package com.argus.api.controller;

import com.argus.api.domain.model.Condominio;
import com.argus.api.dto.CondominioDTO;
import com.argus.api.service.CondominioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CondominioController.class)
public class CondominioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CondominioService condominioService;

    @Autowired
    private ObjectMapper objectMapper;

    private CondominioDTO condominioDTO;
    private CondominioDTO validCondominioDTO;
    private CondominioDTO invalidCondominioDTO;
    private Condominio condominio;

    @BeforeEach
    public void setup() {
        // DTO válido
        validCondominioDTO = new CondominioDTO("Condomínio Teste", "Rua Exemplo, 123");
        invalidCondominioDTO = new CondominioDTO("", "");

        // Criando um objeto Condominio de exemplo
        condominio = new Condominio();
        condominio.setId(1L);
        condominio.setNome("Condomínio Teste");
        condominio.setEndereco("Rua Exemplo, 123");
    }

    @Test
    public void testCreateCondominio() throws Exception {
        // Substitua doNothing() por when() para métodos que retornam algo
        when(condominioService.createCondominio(any(CondominioDTO.class))).thenReturn(condominio);

        // Realizando a requisição POST
        mockMvc.perform(post("/condominio/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCondominioDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Condomínio Teste criado com sucesso!")));

        // Verificando se o método do serviço foi chamado
        verify(condominioService, times(1)).createCondominio(any(CondominioDTO.class));
    }

    @Test
    public void testCreateCondominio_InvalidInput() throws Exception {
        // Realizando a requisição POST com entrada inválida
        mockMvc.perform(post("/condominio/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCondominioDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Nome do condomínio não pode ser vazio")));

        // Verificando que o serviço não foi chamado
        verify(condominioService, never()).createCondominio(any(CondominioDTO.class));
    }

    @Test
    public void testGetAllCondominios() throws Exception {
        // Criando uma lista de condominios
        List<Condominio> condominios = Arrays.asList(condominio);

        // Configurando o comportamento do serviço
        when(condominioService.getAllCondominios()).thenReturn(condominios);

        // Realizando a requisição GET
        mockMvc.perform(get("/condominio/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Condomínio Teste"))
                .andExpect(jsonPath("$[0].endereco").value("Rua Exemplo, 123"));

        // Verificando se o método do serviço foi chamado
        verify(condominioService, times(1)).getAllCondominios();
    }

    @Test
    public void testGetCondominioById() throws Exception {
        // Configurando o comportamento do serviço
        when(condominioService.getCondominioById(anyLong())).thenReturn(condominio);

        // Realizando a requisição GET
        mockMvc.perform(get("/condominio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Condomínio Teste"))
                .andExpect(jsonPath("$.endereco").value("Rua Exemplo, 123"));

        // Verificando se o método do serviço foi chamado
        verify(condominioService, times(1)).getCondominioById(anyLong());
    }

    @Test
    public void testUpdateCondominio() throws Exception {
        // Configurando o comportamento do serviço
        when(condominioService.updateCondominio(anyLong(), any(CondominioDTO.class)))
                .thenReturn(condominio);

        // Realizando a requisição PUT
        mockMvc.perform(put("/condominio/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(condominioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Condomínio Teste"))
                .andExpect(jsonPath("$.endereco").value("Rua Exemplo, 123"));

        // Verificando se o método do serviço foi chamado
        verify(condominioService, times(1)).updateCondominio(anyLong(), any(CondominioDTO.class));
    }

    @Test
    public void testDeleteCondominio() throws Exception {
        // Configurando o comportamento do serviço
        doNothing().when(condominioService).deleteCondominio(anyLong());

        // Realizando a requisição DELETE
        mockMvc.perform(delete("/condominio/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Condomínio 1 excluído.")));

        // Verificando se o método do serviço foi chamado
        verify(condominioService, times(1)).deleteCondominio(anyLong());
    }

}