package com.github.lvpasqualini.ms.pagamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lvpasqualini.ms.pagamento.dto.PagamentoDTO;
import com.github.lvpasqualini.ms.pagamento.entities.Pagamento;
import com.github.lvpasqualini.ms.pagamento.exceptions.ResourceNotFoundException;
import com.github.lvpasqualini.ms.pagamento.service.PagamentoService;
import com.github.lvpasqualini.ms.pagamento.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest(PagamentoController.class)
public class PagamentoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PagamentoService pagamentoService;
    private Pagamento pagamento;
    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = Long.MAX_VALUE;
        pagamento = Factory.createPagamento();
    }

    @Test
    void findAllPagamentosShouldReturnListPagamentoDto() throws Exception {
        //Arrange
        PagamentoDTO inputDto = new PagamentoDTO(pagamento);
        List<PagamentoDTO> list = List.of(inputDto);
        Mockito.when(pagamentoService.findAll()).thenReturn(list);
        //Act + Assert
        ResultActions result = mockMvc.perform(get("/pagamento")
                .accept(MediaType.APPLICATION_JSON));
        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        result.andExpect(jsonPath("$").isArray());
        result.andExpect(jsonPath("$[0].id").value(pagamento.getId()));

        // Verifica se o valor do pagamento está correto
        result.andExpect(jsonPath("$[0].valor").value(pagamento.getValor().doubleValue()));

        // Verifica se o método do service foi chamado
        Mockito.verify(pagamentoService).findAll();

        // Garante que não houve outras interações com o service
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void findPagamentoByIdShouldReturnPagamentoDTOWhenIdExists() throws Exception {
        PagamentoDTO responseDto = new PagamentoDTO(pagamento);
        Mockito.when(pagamentoService.findById(existingId)).thenReturn(responseDto);

        mockMvc.perform(get("/pagamento/{id}",existingId)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.valor").value(pagamento.getValor().doubleValue()))
                .andExpect(jsonPath("$.status").value(pagamento.getStatus().name()))
                .andExpect(jsonPath("$.pedidoId").value(pagamento.getPedidoId()));

        Mockito.verify(pagamentoService).findById(existingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void findPagamentoByIdShouldReturn404WhenIdDoesNotExist() throws Exception {
        // Configuração do Mock (Given)
        Mockito.when(pagamentoService.findById(nonExistingId))
                .thenThrow(new ResourceNotFoundException("Recurso não encontrado. ID: " + nonExistingId));

        // Execução e Verificação do Comportamento (When/Then)
        mockMvc.perform(get("/pagamento/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        // Verificações do Mockito (Verify)
        Mockito.verify(pagamentoService).findById(nonExistingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void createPagamentoShouldReturn201WhenValid() throws Exception {

        // Preparação dos dados (Given)
        PagamentoDTO requestDTO = new PagamentoDTO(Factory.createPagamentoSemId());

        // Bean objectMapper para converter JAVA para JSON
        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);


        PagamentoDTO responseDTO = new PagamentoDTO(pagamento);

        Mockito.when(pagamentoService.save(any(PagamentoDTO.class)))
                .thenReturn(responseDTO);

        // Execução (When)
        mockMvc.perform(post("/pagamento")
                        .contentType(MediaType.APPLICATION_JSON) // request Content-Type
                        .accept(MediaType.APPLICATION_JSON)      // request Accept
                        .content(jsonRequestBody))               // request body
                .andDo(print())

                // Verificações da Resposta (Then)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) // response
                .andExpect(jsonPath("$.id").value(pagamento.getId()))
                .andExpect(jsonPath("$.status").value(pagamento.getStatus().name()))
                .andExpect(jsonPath("$.valor").value(pagamento.getValor().doubleValue()))
                .andExpect(jsonPath("$.pedidoId").value(pagamento.getPedidoId()));

        // Verificações do Mockito
        Mockito.verify(pagamentoService).save(any(PagamentoDTO.class));
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void createPagamentoShouldReturn422WhenInvalid() throws Exception {

        // Preparando um objeto inválido (valor 0 e nome nulo)
        Pagamento pagamentoInvalido = Factory.createPagamentoSemId();
        pagamentoInvalido.setValor(BigDecimal.valueOf(0));
        pagamentoInvalido.setNome(null);

        PagamentoDTO requestDTO = new PagamentoDTO(pagamentoInvalido);
        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);

        PagamentoDTO responseDTO = new PagamentoDTO(pagamentoInvalido);

        // Configurando o Mock (embora neste caso o service nem deva ser chamado)
        Mockito.when(pagamentoService.save(any(PagamentoDTO.class))).thenReturn(responseDTO);

        // Execução
        mockMvc.perform(post("/pagamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andDo(print())

                // Verificação: Espera-se 422 Unprocessable Entity devido às validações
                .andExpect(status().isUnprocessableEntity());

        // Verificação crucial: o service NÃO deve ser chamado se o DTO for inválido
        Mockito.verifyNoInteractions(pagamentoService);
    }

    @Test
    void updatePagamentoShouldReturn404WhenIdDoesNotExist() throws Exception {

        // Preparação dos dados (Given)
        PagamentoDTO requestDTO = new PagamentoDTO(Factory.createPagamentoSemId());
        String jsonRequestBody = objectMapper.writeValueAsString(requestDTO);

        // Configurando o Mock para lançar exceção ao tentar atualizar um ID inexistente
        Mockito.when(pagamentoService.update(eq(nonExistingId), any(PagamentoDTO.class)))
                .thenThrow(new ResourceNotFoundException("Recurso não encontrado. ID: " + nonExistingId));

        // Execução (When)
        mockMvc.perform(put("/pagamento/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))

                // Verificação (Then)
                .andExpect(status().isNotFound())
                .andDo(print());

        // Verificações do Mockito
        Mockito.verify(pagamentoService).update(eq(nonExistingId), any(PagamentoDTO.class));
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void deletePagamentoShouldReturn204WhenIdExists() throws Exception {

        // Configuração do Mock (Given)
        // Para métodos void, usamos doNothing()
        Mockito.doNothing().when(pagamentoService).deleteById(existingId);

        // Execução (When)
        mockMvc.perform(delete("/pagamento/{id}", existingId))

                // Verificação (Then)
                .andExpect(status().isNoContent());

        // Verificações do Mockito
        Mockito.verify(pagamentoService).deleteById(existingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }

    @Test
    void deletePagamentoShouldReturn404WhenIdDoesNotExist() throws Exception {
        // Configuração do Mock (Given)
        // Para métodos void que devem lançar exceção, usamos doThrow
        Mockito.doThrow(new ResourceNotFoundException("Recurso não encontrado. ID: " + nonExistingId))
                .when(pagamentoService).deleteById(nonExistingId);

        // Execução (When)
        mockMvc.perform(delete("/pagamento/{id}", nonExistingId))

                // Verificação (Then)
                .andExpect(status().isNotFound());

        // Verificações do Mockito
        Mockito.verify(pagamentoService).deleteById(nonExistingId);
        Mockito.verifyNoMoreInteractions(pagamentoService);
    }
}
