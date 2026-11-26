package com.example.tin_nghia.infrastructure.persistence;

import com.example.tin_nghia.application.exception.InsufficientStockException;
import com.example.tin_nghia.application.exception.ProductVariantNotFoundException;
import com.example.tin_nghia.application.exception.UserNotFoundException;
import com.example.tin_nghia.application.port.OrderRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

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
}
