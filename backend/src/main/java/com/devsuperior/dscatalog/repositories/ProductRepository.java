package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.models.Category;
import com.devsuperior.dscatalog.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
}
