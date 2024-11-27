package com.argus.api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.argus.api.domain.model.Ocorrencias;
import com.argus.api.domain.model.Usuarios;
import com.argus.api.domain.model.AreasComuns;
import com.argus.api.dto.OcorrenciasDTO;
import com.argus.api.service.OcorrenciasService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OcorrenciasControllerTest {

    @Mock
    private OcorrenciasService ocorrenciasService;

    @InjectMocks
    private OcorrenciasController ocorrenciasController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(ocorrenciasController).build();
    }

    private Ocorrencias criarOcorrencia() {
        Usuarios usuario = new Usuarios();
        usuario.setId(1L);
        usuario.setNome("João");

        AreasComuns area = new AreasComuns();
        area.setId(1L);
        area.setNome("Área Comum A");

        Ocorrencias ocorrencia = new Ocorrencias();
        ocorrencia.setId(1L);
        ocorrencia.setTitulo("Ocorrência de Teste");
        ocorrencia.setDescricao("Descrição detalhada da ocorrência");
        ocorrencia.setTipo(Ocorrencias.TipoOcorrencia.PROBLEMA_DE_INFRAESTRUTURA);
        ocorrencia.setStatusAprovacao(Ocorrencias.StatusAprovacao.AGUARDANDO);
        ocorrencia.setStatusResolucao(Ocorrencias.StatusResolucao.PENDENTE);
        ocorrencia.setDataCriacao(LocalDateTime.now());
        ocorrencia.setUsuario(usuario);
        ocorrencia.setArea(area);

        return ocorrencia;
    }

    private OcorrenciasDTO criarOcorrenciaDTO() {
        OcorrenciasDTO dto = new OcorrenciasDTO();
        dto.setId(1L);
        dto.setTitulo("Ocorrência de Teste");
        dto.setDescricao("Descrição detalhada da ocorrência");
        dto.setTipo(Ocorrencias.TipoOcorrencia.valueOf("PROBLEMA_DE_INFRAESTRUTURA"));
        dto.setStatusAprovacao(Ocorrencias.StatusAprovacao.valueOf("AGUARDANDO"));
        dto.setStatusResolucao(Ocorrencias.StatusResolucao.valueOf("PENDENTE"));
        dto.setDataCriacao(LocalDateTime.now());
        dto.setIdUsuario(1L);
        dto.setIdArea(1L);
        return dto;
    }

    @Test
    void criarOcorrencia_DeveRetornarOcorrenciaCriada() throws Exception {
        // Arrange
        Ocorrencias ocorrencia = criarOcorrencia();
        OcorrenciasDTO ocorrenciaDTO = criarOcorrenciaDTO();

        when(ocorrenciasService.criarOcorrencia(any(OcorrenciasDTO.class))).thenReturn(ocorrenciaDTO);

        // Act & Assert
        mockMvc.perform(post("/ocorrencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ocorrenciaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Ocorrência de Teste"));

        verify(ocorrenciasService, times(1)).criarOcorrencia(any(OcorrenciasDTO.class));
    }

    @Test
    void listarOcorrencias_DeveRetornarListaDeOcorrencias() throws Exception {
        // Arrange
        List<OcorrenciasDTO> ocorrencias = Arrays.asList(
                criarOcorrenciaDTO(),
                criarOcorrenciaDTO()
        );

        when(ocorrenciasService.listarTodasOcorrencias()).thenReturn(ocorrencias);

        // Act & Assert
        mockMvc.perform(get("/ocorrencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].titulo").value("Ocorrência de Teste"))
                .andExpect(jsonPath("$[1].id").value(1L))
                .andExpect(jsonPath("$[1].titulo").value("Ocorrência de Teste"));

        verify(ocorrenciasService, times(1)).listarTodasOcorrencias();
    }

    @Test
    void buscarOcorrencia_DeveRetornarOcorrenciaQuandoExistir() throws Exception {
        // Arrange
        Long id = 1L;
        OcorrenciasDTO ocorrenciaDTO = criarOcorrenciaDTO();

        when(ocorrenciasService.buscarOcorrenciaPorId(id)).thenReturn(ocorrenciaDTO);

        // Act & Assert
        mockMvc.perform(get("/ocorrencias/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Ocorrência de Teste"));

        verify(ocorrenciasService, times(1)).buscarOcorrenciaPorId(id);
    }

    @Test
    void atualizarOcorrencia_DeveRetornarOcorrenciaAtualizada() throws Exception {
        // Arrange
        Long id = 1L;
        OcorrenciasDTO ocorrenciaDTO = criarOcorrenciaDTO();
        ocorrenciaDTO.setTitulo("Ocorrência Atualizada");

        when(ocorrenciasService.atualizarOcorrencia(eq(id), any(OcorrenciasDTO.class))).thenReturn(ocorrenciaDTO);

        // Act & Assert
        mockMvc.perform(put("/ocorrencias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ocorrenciaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Ocorrência Atualizada"));

        verify(ocorrenciasService, times(1)).atualizarOcorrencia(eq(id), any(OcorrenciasDTO.class));
    }

    @Test
    void deletarOcorrencia_DeveRetornarNoContent() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(ocorrenciasService).deletarOcorrencia(id);

        // Act & Assert
        mockMvc.perform(delete("/ocorrencias/{id}", id))
                .andExpect(status().isNoContent());

        verify(ocorrenciasService, times(1)).deletarOcorrencia(id);
    }
}