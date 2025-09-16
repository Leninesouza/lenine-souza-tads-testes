package org.example.cadastro.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.example.cadastro.model.User;

import java.sql.*;
import java.util.Optional;

public class UserRepository {
    private final HikariDataSource ds;
    public UserRepository(HikariDataSource ds) { this.ds = ds; }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email FROM users WHERE email = ?";
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário", e);
        }
    }

    public User save(User u) {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (Connection c = ds.getConnection();
             PreparedStatement st = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, u.name());
            st.setString(2, u.email());
            st.executeUpdate();
            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new User(id, u.name(), u.email());
                }
                throw new IllegalStateException("Sem chave gerada");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }
}
