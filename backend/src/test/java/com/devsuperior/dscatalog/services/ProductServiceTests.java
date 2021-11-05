package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;
    @Mock
    private ProductRepository repository;
    @Mock
    private CategoryRepository categoryRepository;
    private Long idExistente;
    private Long idNaoExistente;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idNaoExistente = 1000L;
        //Comportamento simulado do DELETE sem exception
        Mockito.doNothing().when(repository).deleteById(idExistente);
        //Comportamento simulado do DELETE com exception
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(idNaoExistente);
    }

    @Test
    public void delete_DeveriaNaoFazerNada_QuandoIdExistir() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(idExistente);
        });
        Mockito.verify(repository,Mockito.times(1)).deleteById(idExistente);
    }
}
