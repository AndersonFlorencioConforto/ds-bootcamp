package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.devsuperior.dscatalog.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TokenUtil tokenUtil;

    private Long idExistente;
    private Long idNaoExistente;
    private Long totalDeProdutos;
    private String username;
    private String password;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idNaoExistente = 1000L;
        totalDeProdutos = 25L;
        username = "maria@gmail.com";
        password = "123456";
    }

    @Test //FindAllPage ordenado por nome
    public void findAll_DeveriaRetornarSortedPaged_QuandoSortByName() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products?page=0&size=12&sort=name,asc")
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.totalElements").value(totalDeProdutos));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test //UPDATE
    public void update_DeveriaRetornarProductDTO_QuandoIdExistir() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

        ProductDTO productDto = Factory.novoProdutoDTO();
        String jsonBody = objectMapper.writeValueAsString(productDto);

        String expectedName = productDto.getName();
        String expectedDescription = productDto.getDescription();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", idExistente)
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(idExistente));
        result.andExpect(jsonPath("$.name").value(expectedName));
        result.andExpect(jsonPath("$.description").value(expectedDescription));
    }

    @Test //UPDATE QUANDO ID NÃO EXISTIR
    public void update_DeveriaRetornarThrowNotFound_QuandoIdNaoExistir() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        ProductDTO productDto = Factory.novoProdutoDTO();
        String jsonBody = objectMapper.writeValueAsString(productDto);


        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", idNaoExistente)
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
}
