package com.devsuperior.dscatalog.tests;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.models.Category;
import com.devsuperior.dscatalog.models.Product;

import java.time.Instant;

public class Factory {

    public static Product novoProduto() {
        Product product = new Product(1L,"Phone","Good Phone",800.0,"img", Instant.parse("2020-10-20T03:00:00Z"));
        product.getCategories().add(novaCategoria());
        return product;
    }

    public static ProductDTO novoProdutoDTO() {
        Product product = novoProduto();
        return new ProductDTO(product,product.getCategories());
    }

    public static Category novaCategoria() {
        return new Category(1L,"Livros");
    }
}
