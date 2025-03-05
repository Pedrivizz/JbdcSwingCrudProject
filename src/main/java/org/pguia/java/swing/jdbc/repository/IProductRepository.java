package org.pguia.java.swing.jdbc.repository;

import org.pguia.java.swing.jdbc.model.Product;

import java.util.List;

public interface IProductRepository {
    List<Product> findAll();
    Product findById(Long id);
    Product save(Product product);
    void delete(Long id);
}
