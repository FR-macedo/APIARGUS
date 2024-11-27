package com.argus.api.controller;

import com.argus.api.dto.SessaoVotacaoDTO;
import com.argus.api.service.SessaoVotacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    @InjectMocks
    private SessaoVotacaoController sessaoVotacaoController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sessaoVotacaoController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCriarSessaoVotacao_Sucesso() throws Exception {
        // Dados da sessão de votação
        SessaoVotacaoDTO sessaoInput = new SessaoVotacaoDTO(
                null,
                "Reforma do Playground",
                "Aprovação de reforma no playground do condomínio",
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                "Condomínio Central"
        );

        SessaoVotacaoDTO sessaoResponse = new SessaoVotacaoDTO(
                1L,
                "Reforma do Playground",
                "Aprovação de reforma no playground do condomínio",
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                "Condomínio Central"
        );

        // Configurando o mock
        when(sessaoVotacaoService.criarSessao(any(SessaoVotacaoDTO.class)))
                .thenReturn(sessaoResponse);

        // Executando o teste
        mockMvc.perform(post("/sessaoVotacao/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessaoInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.proposta").value("Reforma do Playground"))
                .andExpect(jsonPath("$.condominioNome").value("Condomínio Central"));

        // Verificando a chamada do serviço
        verify(sessaoVotacaoService, times(1)).criarSessao(any(SessaoVotacaoDTO.class));
    }

    @Test
    void testCriarSessaoVotacao_Falha_CamposInvalidos() throws Exception {
        // Sessão de votação com campos em branco
        SessaoVotacaoDTO sessaoInvalida = new SessaoVotacaoDTO(
                null,
                "",
                "",
                null,
                null,
                ""
        );

        // Executando o teste
        mockMvc.perform(post("/sessaoVotacao/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessaoInvalida)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListarTodasSessoes() throws Exception {
        // Criando lista de sessões
        List<SessaoVotacaoDTO> listaSessoes = Arrays.asList(
                new SessaoVotacaoDTO(
                        1L,
                        "Reforma do Playground",
                        "Descrição 1",
                        LocalDate.now(),
                        LocalDate.now().plusDays(7),
                        "Condomínio Central"
                ),
                new SessaoVotacaoDTO(
                        2L,
                        "Troca de Portão",
                        "Descrição 2",
                        LocalDate.now(),
                        LocalDate.now().plusDays(10),
                        "Condomínio Central"
                )
        );

        // Configurando o mock
        when(sessaoVotacaoService.listarTodasSessoes())
                .thenReturn(listaSessoes);

        // Executando o teste
        mockMvc.perform(get("/sessaoVotacao/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].proposta").value("Reforma do Playground"))
                .andExpect(jsonPath("$[1].proposta").value("Troca de Portão"));

        // Verificando a chamada do serviço
        verify(sessaoVotacaoService, times(1)).listarTodasSessoes();
    }

    @Test
    void testDeletarSessaoVotacao_Sucesso() throws Exception {
        Long idSessao = 1L;

        // Não espera retorno do serviço, apenas verificar chamada
        doNothing().when(sessaoVotacaoService).deletarSessao(idSessao);

        // Executando o teste
        mockMvc.perform(delete("/sessaoVotacao/deletar/{id}", idSessao))
                .andExpect(status().isOk())
                .andExpect(content().string("Sessão de votação foi deletada com sucesso."));

        // Verificando a chamada do serviço
        verify(sessaoVotacaoService, times(1)).deletarSessao(idSessao);
    }
}