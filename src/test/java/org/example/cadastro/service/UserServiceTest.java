package org.example.cadastro.service;

import org.example.cadastro.exception.EmailAlreadyExistsException;
import org.example.cadastro.model.User;
import org.example.cadastro.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    void nomeVazio_lancaValidacao() {
        var repo = mock(UserRepository.class);
        var svc = new UserService(repo);
        var ex = assertThrows(IllegalArgumentException.class, () -> svc.register("", "a@b.com"));
        assertEquals("Nome é obrigatório", ex.getMessage());
        verifyNoInteractions(repo);
    }

    @Test
    void emailVazio_lancaValidacao() {
        var repo = mock(UserRepository.class);
        var svc = new UserService(repo);
        var ex = assertThrows(IllegalArgumentException.class, () -> svc.register("João", ""));
        assertEquals("E-mail é obrigatório", ex.getMessage());
        verifyNoInteractions(repo);
    }

    @Test
    void emailInvalido_lancaValidacao() {
        var repo = mock(UserRepository.class);
        var svc = new UserService(repo);
        var ex = assertThrows(IllegalArgumentException.class, () -> svc.register("João", "invalido"));
        assertEquals("E-mail inválido", ex.getMessage());
        verifyNoInteractions(repo);
    }

    @Test
    void emailJaCadastrado_lancaConflito() {
        var repo = mock(UserRepository.class);
        when(repo.findByEmail("existente@email.com"))
                .thenReturn(Optional.of(new User(1L, "Alguém", "existente@email.com")));
        var svc = new UserService(repo);
        var ex = assertThrows(EmailAlreadyExistsException.class,
                () -> svc.register("João", "existente@email.com"));
        assertEquals("E-mail já cadastrado: existente@email.com", ex.getMessage());
        verify(repo).findByEmail("existente@email.com");
        verify(repo, never()).save(any());
    }

    @Test
    void dadosValidos_salva() {
        var repo = mock(UserRepository.class);
        when(repo.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(repo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new User(1L, u.name(), u.email());
        });
        var svc = new UserService(repo);
        var saved = svc.register("João", "novo@email.com");
        assertNotNull(saved.id());
        assertEquals("João", saved.name());
        assertEquals("novo@email.com", saved.email());
        verify(repo).findByEmail("novo@email.com");
        verify(repo).save(any(User.class));
    }
}
