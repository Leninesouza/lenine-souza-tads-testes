package org.example.cadastro.model;

public record User(Long id, String name, String email) {
    public User(String name, String email) { this(null, name, email); }
}
