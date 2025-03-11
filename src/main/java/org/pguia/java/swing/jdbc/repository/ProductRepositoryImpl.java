package org.pguia.java.swing.jdbc.repository;

import org.pguia.java.swing.jdbc.db.ConnectionDataBase;
import org.pguia.java.swing.jdbc.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements IProductRepository{
    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try(Connection conn = ConnectionDataBase.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

            while(rs.next()) {
                Product product = new Product(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getInt("quantity"),
                        rs.getString("category"),
                        rs.getString("supplier"),
                        rs.getString("status"),
                        rs.getString("description"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public Product findById(Long id) {
        Product product = null;
        try (Connection conn = ConnectionDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products WHERE id=?")) {
            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    product = new Product(rs.getLong("id"),
                            rs.getString("name"),
                            rs.getInt("price"),
                            rs.getInt("quantity"),
                            rs.getString("category"),
                            rs.getString("supplier"),
                            rs.getString("status"),
                            rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    @Override
    public Product save(Product product) {
        String sql = "";
        if(product.getId() != null && product.getId() > 0) {
            sql = "UPDATE products SET name=?, price=?, quantity=?, category=?, supplier=?, status=?, description=? WHERE id=?";
        } else {
            sql = "INSERT INTO products(name, price, quantity, category, supplier, status, description) VALUES(?,?,?,?,?,?,?)";
        }

        try(Connection conn = ConnectionDataBase.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setString(4, product.getCategory());
            stmt.setString(5, product.getSupplier());
            stmt.setString(6,product.getStatus());
            stmt.setString(7, product.getDescription());
            if(product.getId() != null && product.getId() > 0) {
                stmt.setLong(8, product.getId());
            }
            int affectedRow = stmt.executeUpdate();
            if(affectedRow > 0 && (product.getId() == null || product.getId() == 0)) {
                try(ResultSet rs = stmt.getGeneratedKeys()) {
                    if(rs.next()) {
                        product.setId(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    @Override
    public void delete(Long id) {
        try (Connection conn = ConnectionDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id=?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
