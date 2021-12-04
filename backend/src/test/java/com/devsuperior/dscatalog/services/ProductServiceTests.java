package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.models.Category;
import com.devsuperior.dscatalog.models.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

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
    private Long idDependente;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO productDto;
    private Category category;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idNaoExistente = 1000L;
        idDependente = 20L;
        product = Factory.novoProduto();
        productDto = Factory.novoProdutoDTO();
        category = Factory.novaCategoria();
        page = new PageImpl<>(List.of(product));
        //Comportamento simulado do DELETE sem exception
        Mockito.doNothing().when(repository).deleteById(idExistente);
        //Comportamento simulado do DELETE com exception
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(idNaoExistente);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idDependente);
        //Comportamento simulado do findAll passando um page e retornando uma página de um produto
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        //Comportamento simulado do save,retornando um produto
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
        //Comportamento simulado do findById,retornando um produto optional
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(product));
        //Comportamento simulado do findById,retornando vazio
        Mockito.when(repository.findById(idNaoExistente)).thenReturn(Optional.empty());
        //Comportamento simulado do getOne retornando uma exception
        Mockito.when(repository.getOne(idNaoExistente)).thenThrow(EntityNotFoundException.class);
        //Comportamento simulado do getOne retornando um produto
        Mockito.when(repository.getOne(idExistente)).thenReturn(product);
        //Comportamento simulado do getOne retornando uma categoria
        Mockito.when(categoryRepository.getOne(idExistente)).thenReturn(category);
        //Comportamento simulado do getOne retornando uma exception
        Mockito.when(categoryRepository.getOne(idNaoExistente)).thenThrow(EntityNotFoundException.class);

    }

    @Test
    public void delete_DeveriaNaoFazerNada_QuandoIdExistir() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(idExistente);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(idExistente);
    }

    @Test
    public void delete_DeveriaThrowResourceNotFoundException_QuandoNaoIdExistir() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(idNaoExistente);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(idNaoExistente);
    }

    @Test
    public void delete_DeveriaThrowDataBaseException_QuandoIdForDependente() {
        Assertions.assertThrows(DataBaseException.class, () -> {
            service.delete(idDependente);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(idDependente);
    }

    @Test
    public void findAll_DeveriaRetornarUmaPagina() {
        Pageable page = PageRequest.of(0, 10);
        //Page<ProductDTO> result = service.findAllPaged(page);
        //Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(page);
    }

    @Test
    public void findById_DeveriaRetornarUmProductDTO_QuandoIdExistir() {
        ProductDTO result = service.findById(idExistente);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findById(idExistente);
    }

    @Test
    public void findById_DeveriaThrowResourceNotFoundException_QuandoIdNaoExistir() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(idNaoExistente);
        });
        Mockito.verify(repository, Mockito.times(1)).findById(idNaoExistente);
    }

    @Test
    public void update_DeveriaRetornarProductDTO_QuandoIdExistir() {
        ProductDTO result = service.update(idExistente,productDto);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).save(product);
    }

    @Test
    public void update_DeveriaThrowResourceNotFoundException_QuandoIdNaoExistir() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(idNaoExistente, productDto);
        });
    }
}
