package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.models.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    //Spring garante com essa notação que isso é uma transação com o banco de dados.
    //o readOnly = true, faz com que não trave o banco de dados,pois é apenas uma transação de leitura.
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
        Page<Category> list = repository.findAll(pageRequest);
       return list.map(x -> new CategoryDTO(x));
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Id não encontrado"));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO save(CategoryDTO dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO update(Long id,CategoryDTO dto) {
       try {
           Category entity = repository.getOne(id);
           entity.setName(dto.getName());
           entity = repository.save(entity);
           return new CategoryDTO(entity);
       } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id não encontrado " + id);
       }
    }

    public void delete(Long id) {
        try{
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id não encontrado " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Violação de integridade");
        }

    }
}
