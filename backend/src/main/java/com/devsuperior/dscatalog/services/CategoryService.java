package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.models.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    //Spring garante com essa notação que isso é uma transação com o banco de dados.
    //o readOnly = true, faz com que não trave o banco de dados,pois é apenas uma transação de leitura.
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return repository.findAll();
    }
}
