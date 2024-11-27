package com.argus.api.controller;

import com.argus.api.dto.ReservasDTO;
import com.argus.api.service.ReservasService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReservasControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservasService reservasService;

    @InjectMocks
    private ReservasController reservasController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reservasController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testReservarArea_Sucesso() throws Exception {
        ReservasDTO reservaDTO = new ReservasDTO(1L, "Área A", LocalDate.now());

        when(reservasService.reservarArea(any(ReservasDTO.class))).thenReturn(reservaDTO);

        mockMvc.perform(post("/reservas/reservar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.areaNome").value("Área A"));

        verify(reservasService, times(1)).reservarArea(any(ReservasDTO.class));
    }

    @Test
    void testReservarArea_Erro() throws Exception {
        ReservasDTO reservaDTO = new ReservasDTO(1L, "Área A", LocalDate.now());

        when(reservasService.reservarArea(any(ReservasDTO.class)))
                .thenThrow(new RuntimeException("Erro na reserva"));

        mockMvc.perform(post("/reservas/reservar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaDTO)))
                .andExpect(status().isBadRequest());

        verify(reservasService, times(1)).reservarArea(any(ReservasDTO.class));
    }

    @Test
    void testListarTodasReservas() throws Exception {
        List<ReservasDTO> reservas = Arrays.asList(
                new ReservasDTO(1L, "Área A", LocalDate.now()),
                new ReservasDTO(2L, "Área B", LocalDate.now().plusDays(1))
        );

        when(reservasService.listarTodasReservas()).thenReturn(reservas);

        mockMvc.perform(get("/reservas/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].areaNome").value("Área A"))
                .andExpect(jsonPath("$[1].areaNome").value("Área B"));

        verify(reservasService, times(1)).listarTodasReservas();
    }

    @Test
    void testExcluirReserva_Sucesso() throws Exception {
        Long idReserva = 1L;

        when(reservasService.excluirReserva(idReserva)).thenReturn("Reserva excluída com sucesso");

        mockMvc.perform(delete("/reservas/excluir/{id}", idReserva))
                .andExpect(status().isOk())
                .andExpect(content().string("Reserva excluída com sucesso"));

        verify(reservasService, times(1)).excluirReserva(idReserva);
    }

    @Test
    void testExcluirReserva_Erro() throws Exception {
        Long idReserva = 1L;

        when(reservasService.excluirReserva(idReserva))
                .thenThrow(new RuntimeException("Reserva não encontrada"));

        mockMvc.perform(delete("/reservas/excluir/{id}", idReserva))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Reserva não encontrada"));

        verify(reservasService, times(1)).excluirReserva(idReserva);
    }
}