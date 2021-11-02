package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.models.Product;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;
    private long idExistente;
    private long idNaoExistente;
    private long totalProdutos;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idNaoExistente = 1000L;
        totalProdutos = 25L;
    }

    @Test
    public void delete_DeveriaDeletarObjeto_QuandoIdExistir() {
        repository.deleteById(idExistente);
        Optional<Product> result = repository.findById(idExistente);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void delete_DeveriaThrowEmptyResultDataAccessException_QuandoIdNaoExistir() {
        Assertions.assertThrows(EmptyResultDataAccessException.class,() -> {
            repository.deleteById(idNaoExistente);
        });
    }

    @Test
    public void save_DeveriaSalvarUmNovoObjetoEAutoIncrementar_QuandoIdForNulo() {
        Product product = Factory.novoProduto();
        product.setId(null);
        product = repository.save(product);
        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(totalProdutos + 1 ,product.getId());
    }

    @Test
    public void findById_DeveriaRetornarUmOptionalProduto_QuandoIdExistir() {
        Optional<Product> product = repository.findById(idExistente);
        Assertions.assertTrue(product.isPresent());
    }

    @Test
    public void findById_DeveriaRetornarUmOptionalVazio_QuandoIdNaoExistir() {
        Optional<Product> product = repository.findById(idNaoExistente);
        Assertions.assertFalse(product.isPresent());
    }
}
