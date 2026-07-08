package com.delisdivin.service;

import com.delisdivin.dto.ProductCategoryDTO;
import com.delisdivin.dto.ProductDTO;
import java.util.List;

public interface ProductService {
    // Categories
    ProductCategoryDTO createCategory(ProductCategoryDTO categoryDTO);
    ProductCategoryDTO updateCategory(Long id, ProductCategoryDTO categoryDTO);
    ProductCategoryDTO getCategoryById(Long id);
    List<ProductCategoryDTO> getCategoriesByRestaurant(Long restaurantId);
    List<ProductCategoryDTO> getActiveCategoriesByRestaurant(Long restaurantId);
    void deleteCategory(Long id);

    // Products
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    ProductDTO getProductById(Long id);
    List<ProductDTO> getProductsByRestaurant(Long restaurantId);
    List<ProductDTO> getActiveProductsByRestaurant(Long restaurantId);
    List<ProductDTO> getProductsByCategory(Long categoryId);
    List<ProductDTO> getBeverages(Long restaurantId);
    List<ProductDTO> getDesserts(Long restaurantId);
    List<ProductDTO> getLowStockProducts(Long restaurantId, Integer threshold);
    ProductDTO updateStock(Long id, Integer quantity);
    void deleteProduct(Long id);
}
