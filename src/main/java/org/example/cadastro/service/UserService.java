package org.example.cadastro.service;

import org.example.cadastro.exception.EmailAlreadyExistsException;
import org.example.cadastro.model.User;
import org.example.cadastro.repository.UserRepository;

import java.util.regex.Pattern;

public class UserService {
    private final UserRepository repo;

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9_+&*-]+(?:\\.[A-Za-z0-9_+&*-]+)*@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,7}$");

    public UserService(UserRepository repo) { this.repo = repo; }

    public User register(String name, String email) {
        validate(name, email);
        if (repo.findByEmail(email).isPresent()) throw new EmailAlreadyExistsException(email);
        return repo.save(new User(name.trim(), email.trim()));
    }

    private void validate(String name, String email) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Nome é obrigatório");
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("E-mail é obrigatório");
        if (!EMAIL.matcher(email).matches()) throw new IllegalArgumentException("E-mail inválido");
    }
}
