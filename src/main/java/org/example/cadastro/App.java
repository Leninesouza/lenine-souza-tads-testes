package org.example.cadastro;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import org.flywaydb.core.Flyway;
import org.example.cadastro.controller.UserController;

import java.io.InputStream;
import java.util.Properties;

public class App {
    public static void main(String[] args) {
        Properties p = loadProps();

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(p.getProperty("db.url"));
        cfg.setUsername(p.getProperty("db.username"));
        cfg.setPassword(p.getProperty("db.password"));
        cfg.setMaximumPoolSize(Integer.parseInt(p.getProperty("db.maximumPoolSize", "5")));
        HikariDataSource ds = new HikariDataSource(cfg);

        Flyway.configure().dataSource(ds).locations("classpath:db/migration").load().migrate();

        Javalin app = Javalin.create().start(8080);
        new UserController(ds).configure(app);

        System.out.println("POST http://localhost:8080/api/users");
    }

    private static Properties loadProps() {
        try (InputStream in = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties props = new Properties();
            props.load(in);
            return props;
        } catch (Exception e) {
            throw new RuntimeException("application.properties não encontrado", e);
        }
    }
}
