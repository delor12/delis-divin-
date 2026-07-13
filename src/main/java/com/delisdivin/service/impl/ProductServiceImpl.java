package com.delisdivin.service.impl;

import com.delisdivin.dto.ProductCategoryDTO;
import com.delisdivin.dto.ProductDTO;
import com.delisdivin.entity.Product;
import com.delisdivin.entity.ProductCategory;
import com.delisdivin.entity.Restaurant;
import com.delisdivin.exception.ResourceNotFoundException;
import com.delisdivin.mapper.AppMapper;
import com.delisdivin.repository.ProductCategoryRepository;
import com.delisdivin.repository.ProductRepository;
import com.delisdivin.repository.RestaurantRepository;
import com.delisdivin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final AppMapper mapper;

    // --- Category Services ---

    @Override
    @Transactional
    public ProductCategoryDTO createCategory(ProductCategoryDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + dto.getRestaurantId()));
        ProductCategory category = mapper.toEntity(dto, restaurant);
        
        if (dto.getParentCategoryId() != null) {
            ProductCategory parent = categoryRepository.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category not found with ID: " + dto.getParentCategoryId()));
            category.setParentCategory(parent);
        }
        
        ProductCategory saved = categoryRepository.save(category);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductCategoryDTO updateCategory(Long id, ProductCategoryDTO dto) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setActive(dto.isActive());
        category.setDisplayOrder(dto.getDisplayOrder());
        category.setSupplierName(dto.getSupplierName());
        category.setSupplierContact(dto.getSupplierContact());

        if (dto.getParentCategoryId() != null) {
            ProductCategory parent = categoryRepository.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category not found with ID: " + dto.getParentCategoryId()));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        ProductCategory updated = categoryRepository.save(category);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategoryDTO getCategoryById(Long id) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return mapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> getCategoriesByRestaurant(Long restaurantId) {
        return categoryRepository.findByRestaurantId(restaurantId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> getActiveCategoriesByRestaurant(Long restaurantId) {
        return categoryRepository.findByRestaurantIdAndActiveTrueOrderByDisplayOrderAsc(restaurantId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        categoryRepository.delete(category);
    }

    // --- Product Services ---

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + dto.getRestaurantId()));
        
        ProductCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        Product product = mapper.toEntity(dto, restaurant, category);
        Product saved = productRepository.save(product);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        
        ProductCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setAvailable(dto.isAvailable());
        product.setBeverage(dto.isBeverage());
        product.setDessert(dto.isDessert());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(category);

        Product updated = productRepository.save(product);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return mapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByRestaurant(Long restaurantId) {
        return productRepository.findByRestaurantId(restaurantId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getActiveProductsByRestaurant(Long restaurantId) {
        return productRepository.findByRestaurantIdAndAvailableTrue(restaurantId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        return productRepository.findByRestaurantIdAndCategoryId(com.delisdivin.tenant.TenantContext.getCurrentTenant(), categoryId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getBeverages(Long restaurantId) {
        return productRepository.findByRestaurantIdAndBeverageTrueAndAvailableTrue(restaurantId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getDesserts(Long restaurantId) {
        return productRepository.findByRestaurantIdAndDessertTrueAndAvailableTrue(restaurantId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts(Long restaurantId, Integer threshold) {
        return productRepository.findByRestaurantIdAndStockQuantityLessThan(restaurantId, threshold).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDTO updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        product.setStockQuantity(quantity);
        Product saved = productRepository.save(product);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        productRepository.delete(product);
    }
}
