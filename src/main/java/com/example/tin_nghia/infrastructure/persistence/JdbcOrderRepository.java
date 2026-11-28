package com.example.tin_nghia.infrastructure.persistence;

import com.example.tin_nghia.application.dto.OrderItem;
import com.example.tin_nghia.application.dto.OrderSummary;
import com.example.tin_nghia.application.exception.InsufficientStockException;
import com.example.tin_nghia.application.exception.ProductVariantNotFoundException;
import com.example.tin_nghia.application.exception.UserNotFoundException;
import com.example.tin_nghia.application.port.OrderRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcOrderRepository implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    public JdbcOrderRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public int CreateOrder(int userId , int variantId , int quantity){
        String sql = """
               EXEC dbo.CreateOrder ?,?,?
                """;
        try{
            Integer orderId = jdbcTemplate.queryForObject(
                    sql,
                    Integer.class,
                    userId , variantId , quantity
            );
            if(orderId == null){
                throw new IllegalStateException("Order tạo thành công nhưng không nhận được mã đơn!");
            }
            return orderId;
        }
        catch(DataAccessException ex){
            for(Throwable cause = ex ; cause !=null ; cause = cause.getCause()){
                if(cause instanceof SQLException sqlException ){
                    switch (sqlException.getErrorCode()){
                        case 50000 :
                            throw new IllegalArgumentException(sqlException.getMessage());
                        case 50001:
                            throw new InsufficientStockException(variantId);
                        case 50002:
                            throw new ProductVariantNotFoundException(variantId);
                        case 50003:
                            throw new UserNotFoundException(userId , ex);
                    }
                }
            }
            throw ex;
        }
    }
    @Override
    public List<OrderSummary> findOrderByUserId(int userId){
        String sql = """
                select OD.OrderID , O.OrderDate , SUM(OD.Quantity*OD.UnitPrice*(1-OD.Discount)) as TotalAmount
                from Orders as O
                inner join dbo.OrderDetails as OD on O.OrderID = OD.OrderID
                where O.UserID = ?
                Group By OD.OrderID , O.OrderDate
                Order By O.OrderDate DESC , OD.OrderID DESC;
                """;
        return jdbcTemplate.query(sql,(rs,rowNum)->new OrderSummary(
                rs.getInt("OrderID"),
                rs.getTimestamp("OrderDate").toLocalDateTime(),
                rs.getBigDecimal("TotalAmount")
        ),userId);
    }
    @Override
    public List<OrderItem> findOrderItemById(int orderId , int userId){
        String sql = """
                select P.ProductName ,
                PV.VariantName ,
                VI.ImageID ,
                OD.UnitPrice,
                OD.Quantity*OD.UnitPrice as OriginAmount ,
                OD.Quantity*OD.UnitPrice*(1-OD.Discount) as FinalAmount,
                OD.Quantity
                from dbo.Orders as O
                inner join dbo.OrderDetails as OD on O.OrderID = OD.OrderID
                inner join dbo.ProductVariants as PV on PV.VariantID = OD.VariantID
                inner join dbo.Products as P on P.ProductID = PV.ProductID
                OUTER APPLY (
                    select Top(1) img.ImageID
                    from dbo.VariantImages as img
                    where img.VariantID = OD.VariantID
                    Order By img.Position ASC
                ) as VI
                where O.OrderID = ? And O.UserID = ?;
                """;
        return jdbcTemplate.query(sql , (rs , rowNum)->new OrderItem(
                rs.getString("ProductName"),
                rs.getString("VariantName"),
                rs.getObject("ImageID", Integer.class),
                rs.getBigDecimal("UnitPrice"),
                rs.getBigDecimal("OriginAmount"),
                rs.getBigDecimal("FinalAmount"),
                rs.getInt("Quantity")
        ),orderId, userId);
    }
    @Override
    public Optional<OrderSummary> findOrderByOrderId(int orderId, int userId){
        String sql = """
                select OD.OrderID , O.OrderDate , SUM(OD.Quantity*OD.UnitPrice*(1-OD.Discount)) as TotalAmount
                from Orders as O
                inner join dbo.OrderDetails as OD on O.OrderID = OD.OrderID
                where O.OrderID = ? and O.UserID = ?
                Group By OD.OrderID , O.OrderDate;
                """;
        return jdbcTemplate.query(sql , (rs,rowNum)->new OrderSummary(
                rs.getInt("OrderID"),
                rs.getTimestamp("OrderDate").toLocalDateTime(),
                rs.getBigDecimal("TotalAmount")
        ),orderId,userId).stream().findFirst();
    }
}
