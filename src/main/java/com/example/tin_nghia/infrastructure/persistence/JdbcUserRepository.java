package com.example.tin_nghia.infrastructure.persistence;

import com.example.tin_nghia.application.port.UserRepository;
import com.example.tin_nghia.domain.Role;
import com.example.tin_nghia.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    public JdbcUserRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Optional<User> findByUserName(String userName){
        String sql = """
                select UserID , UserName , PasswordHash , Role
                from dbo.Users
                where UserName = ?;
                """;
        return jdbcTemplate.query(sql , (rs , rowNum)->new User(
                rs.getInt("UserID"),
                rs.getString("UserName"),
                rs.getString("PasswordHash"),
                Role.valueOf(rs.getString("Role"))
        ),userName).stream().findFirst();
    }
}
