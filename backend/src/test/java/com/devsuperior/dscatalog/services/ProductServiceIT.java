package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService service;
    @Autowired
    private ProductRepository repository;
    private Long idExistente;
    private Long idNaoExistente;
    private Long totalDeProdutos;


    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idNaoExistente = 1000L;
        totalDeProdutos = 25L;
    }

    @Test
    public void delete_DeveriaDeletarOProduto_QuandoIdExistir() {
        service.delete(idExistente);
        Assertions.assertEquals(totalDeProdutos - 1, repository.count());
    }

    @Test
    public void delete_DeveriaRetornarResourceThrowNotFoundException_QuandoIdNaoExistir() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(idNaoExistente);
        });
    }

    @Test //FIND ALL PAGES COM ROLLBACK DO BANCO
    public void findAllPaged_DeveriaRetornarUmaPagina_QuandoPage0Size10() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(0L,"",pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(totalDeProdutos, result.getTotalElements());

    }

    @Test //FIND ALL PAGES CASO A PÁGINA SEJA VAZIA
    public void findAllPaged_DeveriaRetornarPaginaVazia_QuandoPaginaNaoExiste() {
        PageRequest pageRequest = PageRequest.of(50, 10);
        Page<ProductDTO> result = service.findAllPaged(0L,"",pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test //FIND ALL PAGES ORDENADO
    public void findAllPaged_DeveriaRetornarPaginaOrdenada_QuandoSortByName() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
        Page<ProductDTO> result = service.findAllPaged(0L,"",pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }

}
