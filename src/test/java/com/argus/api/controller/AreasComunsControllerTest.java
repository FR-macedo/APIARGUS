package com.argus.api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.argus.api.dto.AreasComunsDTO;
import com.argus.api.service.AreasComunsService;
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

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AreasComunsControllerTest {

    @Mock
    private AreasComunsService areasComunsService;

    @InjectMocks
    private AreasComunsController areasComunsController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void cadastrarAreaComum_DeveRetornarAreaComumCriada() throws Exception {
        // Arrange
        AreasComunsDTO areaDTO = new AreasComunsDTO(1L, "Piscina", true, "Condomínio Alpha");
        when(areasComunsService.cadastrarAreaComum(any(AreasComunsDTO.class)))
                .thenReturn(areaDTO);

        mockMvc = MockMvcBuilders.standaloneSetup(areasComunsController).build();

        // Act & Assert
        mockMvc.perform(post("/areasComuns/cadastrar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(areaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Piscina"))
                .andExpect(jsonPath("$.disponivel").value(true))
                .andExpect(jsonPath("$.condominioNome").value("Condomínio Alpha"));

        verify(areasComunsService, times(1)).cadastrarAreaComum(any(AreasComunsDTO.class));
    }

    @Test
    void listarTodasAsAreasComuns_DeveRetornarListaDeAreas() throws Exception {
        // Arrange
        List<AreasComunsDTO> areas = Arrays.asList(
                new AreasComunsDTO(1L, "Piscina", true, "Condomínio Alpha"),
                new AreasComunsDTO(2L, "Churrasqueira", false, "Condomínio Beta")
        );
        when(areasComunsService.listarTodasAsAreasComuns()).thenReturn(areas);

        mockMvc = MockMvcBuilders.standaloneSetup(areasComunsController).build();

        // Act & Assert
        mockMvc.perform(get("/areasComuns/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Piscina"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nome").value("Churrasqueira"));

        verify(areasComunsService, times(1)).listarTodasAsAreasComuns();
    }

    @Test
    void atualizarAreaComum_DeveRetornarAreaAtualizada() throws Exception {
        // Arrange
        Long id = 1L;
        AreasComunsDTO areaAtualizada = new AreasComunsDTO(1L, "Piscina Coberta", true, "Condomínio Alpha");
        when(areasComunsService.atualizarAreaComum(eq(id), any(AreasComunsDTO.class)))
                .thenReturn(areaAtualizada);

        mockMvc = MockMvcBuilders.standaloneSetup(areasComunsController).build();

        // Act & Assert
        mockMvc.perform(put("/areasComuns/atualizar/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(areaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Piscina Coberta"))
                .andExpect(jsonPath("$.disponivel").value(true))
                .andExpect(jsonPath("$.condominioNome").value("Condomínio Alpha"));

        verify(areasComunsService, times(1)).atualizarAreaComum(eq(id), any(AreasComunsDTO.class));
    }

    @Test
    void excluirAreaComum_DeveRetornarMensagemDeSucesso() throws Exception {
        // Arrange
        Long id = 1L;
        when(areasComunsService.excluirAreaComum(id))
                .thenReturn(new ResponseEntity<>("Área comum excluída com sucesso", HttpStatus.OK));

        mockMvc = MockMvcBuilders.standaloneSetup(areasComunsController).build();

        // Act & Assert
        mockMvc.perform(delete("/areasComuns/excluir/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Área comum excluída com sucesso"));

        verify(areasComunsService, times(1)).excluirAreaComum(id);
    }
}
