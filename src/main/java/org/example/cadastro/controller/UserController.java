package org.example.cadastro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import org.example.cadastro.exception.EmailAlreadyExistsException;
import org.example.cadastro.model.User;
import org.example.cadastro.repository.UserRepository;
import org.example.cadastro.service.UserService;

import java.util.Map;

public class UserController {
    private final UserService service;
    private final ObjectMapper om = new ObjectMapper();

    public UserController(HikariDataSource ds) {
        this.service = new UserService(new UserRepository(ds));
    }

    public void configure(Javalin app) {
        app.post("/api/users", ctx -> {
            try {
                var dto = om.readValue(ctx.body(), UserCreateDto.class);
                User saved = service.register(dto.name(), dto.email());
                ctx.status(HttpStatus.CREATED).json(Map.of(
                        "id", saved.id(), "name", saved.name(), "email", saved.email()));
            } catch (IllegalArgumentException e) {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", e.getMessage()));
            } catch (EmailAlreadyExistsException e) {
                ctx.status(HttpStatus.CONFLICT).json(Map.of("error", e.getMessage()));
            } catch (Exception e) {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("error", "Erro interno"));
            }
        });
    }

    private record UserCreateDto(String name, String email) {}
}
