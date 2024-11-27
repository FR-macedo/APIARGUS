package com.argus.api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.argus.api.domain.TipoDoUsuario;
import com.argus.api.domain.model.Condominio;
import com.argus.api.domain.model.Usuarios;
import com.argus.api.dto.UsuarioDTO;
import com.argus.api.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuariosControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuariosController usuariosController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createUser_DeveRetornarUsuarioCriado() throws Exception {
        // Arrange
        TipoDoUsuario tipoDoUsuario = TipoDoUsuario.MORADOR; // Enum correspondente ao tipo do usuário
        Condominio condominio = new Condominio(
                1L,
                "Condomínio Alpha",
                "Rua X, 123",
                LocalDateTime.now(), // createdAt
                LocalDateTime.now()  // updatedAt
        );
        // Mock de exemplo para o condomínio

        Usuarios usuario = new Usuarios(
                1L,                         // id
                "João",                     // nome
                "12345678900",              // cpf
                "senha123",                 // senha
                "123456789",                // telefone
                tipoDoUsuario,              // tipoDoUsuario
                'A',                        // bloco
                101,                        // apartamento
                condominio,                 // condominio
                null,                       // createdAt
                null                        // updatedAt
        );

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                1L,
                "João",
                "123456789",
                "Morador",
                'A',
                101,
                "Condomínio Alpha",
                "Rua X, 123"
        );

        when(usuarioService.createUser(any(Usuarios.class))).thenReturn(usuario);
        when(usuarioService.convertToDTO(any(Usuarios.class))).thenReturn(usuarioDTO);

        mockMvc = MockMvcBuilders.standaloneSetup(usuariosController).build();

        // Act & Assert
        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João"));

        verify(usuarioService, times(1)).createUser(any(Usuarios.class));
        verify(usuarioService, times(1)).convertToDTO(any(Usuarios.class));
    }


    @Test
    void getUsers_DeveRetornarListaDeUsuarios() throws Exception {
        // Arrange
        List<UsuarioDTO> usuarios = Arrays.asList(
                new UsuarioDTO(1L, "João", "123456789", "Morador", 'A', 101, "Condomínio Alpha", "Rua X, 123"),
                new UsuarioDTO(2L, "Maria", "987654321", "Síndica", 'B', 202, "Condomínio Beta", "Rua Y, 456")
        );

        when(usuarioService.getAllUsers()).thenReturn(usuarios);

        mockMvc = MockMvcBuilders.standaloneSetup(usuariosController).build();

        // Act & Assert
        mockMvc.perform(get("/usuarios/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("João"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nome").value("Maria"));

        verify(usuarioService, times(1)).getAllUsers();
    }

    @Test
    void findUserById_DeveRetornarUsuarioSeExistir() throws Exception {
        // Arrange
        Long id = 1L;
        UsuarioDTO usuarioDTO = new UsuarioDTO(1L, "João", "123456789", "Morador", 'A', 101, "Condomínio Alpha", "Rua X, 123");

        when(usuarioService.findUserById(id)).thenReturn(Optional.of(usuarioDTO));

        mockMvc = MockMvcBuilders.standaloneSetup(usuariosController).build();

        // Act & Assert
        mockMvc.perform(get("/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João"));

        verify(usuarioService, times(1)).findUserById(id);
    }

    @Test
    void updateUser_DeveRetornarUsuarioAtualizado() throws Exception {
        // Arrange
        Long id = 1L;

        TipoDoUsuario tipoDoUsuario = TipoDoUsuario.MORADOR; // Enum correspondente ao tipo do usuário
        Condominio condominio = new Condominio(
                1L,
                "Condomínio Alpha",
                "Rua X, 123",
                LocalDateTime.now(), // createdAt
                LocalDateTime.now()  // updatedAt
        );// Mock de exemplo para o condomínio

        Usuarios usuario = new Usuarios(
                id,                          // id
                "João Atualizado",           // nome
                "12345678900",               // cpf
                "novaSenha123",              // senha
                "123456789",                 // telefone
                tipoDoUsuario,               // tipoDoUsuario
                'A',                         // bloco
                101,                         // apartamento
                condominio,                  // condominio
                null,                        // createdAt
                null                         // updatedAt
        );

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                id,
                "João Atualizado",
                "123456789",
                "Morador",
                'A',
                101,
                "Condomínio Alpha",
                "Rua X, 123"
        );

        when(usuarioService.updateUser(eq(id), any(Usuarios.class))).thenReturn(usuario);
        when(usuarioService.convertToDTO(any(Usuarios.class))).thenReturn(usuarioDTO);

        mockMvc = MockMvcBuilders.standaloneSetup(usuariosController).build();

        // Act & Assert
        mockMvc.perform(put("/usuarios/atualizar/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Atualizado"));

        verify(usuarioService, times(1)).updateUser(eq(id), any(Usuarios.class));
        verify(usuarioService, times(1)).convertToDTO(any(Usuarios.class));
    }


    @Test
    void deleteUser_DeveRetornarMensagemDeSucesso() throws Exception {
        // Arrange
        Long id = 1L;
        UsuarioDTO usuarioDTO = new UsuarioDTO(1L, "João", "123456789", "Morador", 'A', 101, "Condomínio Alpha", "Rua X, 123");

        when(usuarioService.deleteUser(id)).thenReturn(usuarioDTO);

        mockMvc = MockMvcBuilders.standaloneSetup(usuariosController).build();

        // Act & Assert
        mockMvc.perform(delete("/usuarios/deletar/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("João Foi Deletado Com Sucesso!"));

        verify(usuarioService, times(1)).deleteUser(id);
    }
}
