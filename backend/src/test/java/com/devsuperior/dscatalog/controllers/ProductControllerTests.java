package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;
    private PageImpl<ProductDTO> page;
    private ProductDTO productDto;
    private Long idExistente;
    private Long idNaoExistente;
    private Long idDependente;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idNaoExistente = 2L;
        idDependente = 3L;
        productDto = Factory.novoProdutoDTO();
        page = new PageImpl<>(List.of(productDto));

        Mockito.when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productService.findById(idExistente)).thenReturn(productDto);
        Mockito.when(productService.findById(idNaoExistente)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(productService.update(eq(idExistente), ArgumentMatchers.any())).thenReturn(productDto);
        Mockito.when(productService.update(eq(idNaoExistente), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(productService).delete(idExistente);
        Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(idNaoExistente);
        Mockito.doThrow(DataBaseException.class).when(productService).delete(idDependente);

        Mockito.when(productService.insert(ArgumentMatchers.any())).thenReturn(productDto);

    }

    @Test // FIND ALL PAGE
    public void findAll_DeveriaRetornarUmaPagina() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
    }

    @Test //FINDBYID
    public void findById_DeveriaRetornarUmProduto_QuandoIdExistir() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", idExistente)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test //FINDBYID EXCEPTIONS
    public void findById_DeveriaRetornarUmNotFound_QuandoIdNaoExistir() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", idNaoExistente)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test //UPDATE
    public void update_DeveriaRetornarUmProductDTO_QuandoIdExistir() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", idExistente)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test // UPDATE EXCEPTIONS
    public void update_DeveriaRetornarUmNotFound_QuandoNaoExistir() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", idNaoExistente)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test //INSERT || CREATED
    public void insert_DeveriaRetornarUmProdutoDTO() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isCreated());

    }

    @Test //DELETE
    public void delete_DeveriaRetornarNoContent_QuandoIdExistir() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", idExistente)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());

    }

    @Test //DELETE ID NÃO EXISTIR
    public void delete_DeveriaRetornarNotFound_QuandoIdNaoExistir() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", idNaoExistente)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test //DELETE ID FOR DEPENDENTE
    public void delete_DeveriaRetornarBadRequest_QuandoIdForDependente() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", idDependente)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isBadRequest());
    }
}

