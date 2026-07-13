package com.delisdivin.mapper;

import com.delisdivin.dto.*;
import com.delisdivin.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppMapper {

    // --- City Mapping ---
    public CityDTO toDto(City city) {
        if (city == null) return null;
        CityDTO dto = new CityDTO();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setPostalCode(city.getPostalCode());
        dto.setCountry(city.getCountry());
        dto.setActive(city.isActive());
        return dto;
    }

    public City toEntity(CityDTO dto) {
        if (dto == null) return null;
        City city = new City();
        city.setId(dto.getId());
        city.setName(dto.getName());
        city.setPostalCode(dto.getPostalCode());
        city.setCountry(dto.getCountry());
        city.setActive(dto.isActive());
        return city;
    }

    // --- Restaurant Mapping ---
    public RestaurantDTO toDto(Restaurant restaurant) {
        if (restaurant == null) return null;
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setAddress(restaurant.getAddress());
        if (restaurant.getCity() != null) {
            dto.setCityId(restaurant.getCity().getId());
            dto.setCityName(restaurant.getCity().getName());
        }
        dto.setPhone(restaurant.getPhone());
        dto.setEmail(restaurant.getEmail());
        dto.setRating(restaurant.getRating());
        dto.setPriceRange(restaurant.getPriceRange());
        dto.setAveragePrepTime(restaurant.getAveragePrepTime());
        dto.setLogoUrl(restaurant.getLogoUrl());
        dto.setBannerUrl(restaurant.getBannerUrl());
        dto.setLatitude(restaurant.getLatitude());
        dto.setLongitude(restaurant.getLongitude());
        dto.setActive(restaurant.isActive());
        return dto;
    }

    // --- User Mapping ---
    public UserDTO toDto(AppUser user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        if (user.getRestaurant() != null) {
            dto.setRestaurantId(user.getRestaurant().getId());
            dto.setRestaurantName(user.getRestaurant().getName());
        }
        dto.setActive(user.isActive());
        dto.setLatitude(user.getLatitude());
        dto.setLongitude(user.getLongitude());
        dto.setDeclinedOrderIds(user.getDeclinedOrderIds());
        return dto;
    }

    // --- Product Category Mapping ---
    public ProductCategoryDTO toDto(ProductCategory category) {
        if (category == null) return null;
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setId(category.getId());
        if (category.getRestaurant() != null) {
            dto.setRestaurantId(category.getRestaurant().getId());
        }
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setActive(category.isActive());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setSupplierName(category.getSupplierName());
        dto.setSupplierContact(category.getSupplierContact());
        return dto;
    }

    public ProductCategory toEntity(ProductCategoryDTO dto, Restaurant restaurant) {
        if (dto == null) return null;
        ProductCategory category = new ProductCategory();
        category.setId(dto.getId());
        category.setRestaurant(restaurant);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setActive(dto.isActive());
        category.setDisplayOrder(dto.getDisplayOrder());
        category.setSupplierName(dto.getSupplierName());
        category.setSupplierContact(dto.getSupplierContact());
        return category;
    }

    // --- Product Mapping ---
    public ProductDTO toDto(Product product) {
        if (product == null) return null;
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        if (product.getRestaurant() != null) {
            dto.setRestaurantId(product.getRestaurant().getId());
        }
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setAvailable(product.isAvailable());
        dto.setBeverage(product.isBeverage());
        dto.setDessert(product.isDessert());
        dto.setStockQuantity(product.getStockQuantity());
        return dto;
    }

    public Product toEntity(ProductDTO dto, Restaurant restaurant, ProductCategory category) {
        if (dto == null) return null;
        Product product = new Product();
        product.setId(dto.getId());
        product.setRestaurant(restaurant);
        product.setCategory(category);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setAvailable(dto.isAvailable());
        product.setBeverage(dto.isBeverage());
        product.setDessert(dto.isDessert());
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }

    // --- Dining Table Mapping ---
    public DiningTableDTO toDto(DiningTable table) {
        if (table == null) return null;
        DiningTableDTO dto = new DiningTableDTO();
        dto.setId(table.getId());
        if (table.getRestaurant() != null) {
            dto.setRestaurantId(table.getRestaurant().getId());
        }
        dto.setNumber(table.getNumber());
        dto.setCapacity(table.getCapacity());
        dto.setStatus(table.getStatus());
        dto.setXCoordinate(table.getXCoordinate());
        dto.setYCoordinate(table.getYCoordinate());
        return dto;
    }

    public DiningTable toEntity(DiningTableDTO dto, Restaurant restaurant) {
        if (dto == null) return null;
        DiningTable table = new DiningTable();
        table.setId(dto.getId());
        table.setRestaurant(restaurant);
        table.setNumber(dto.getNumber());
        table.setCapacity(dto.getCapacity());
        table.setStatus(dto.getStatus() != null ? dto.getStatus() : TableStatus.FREE);
        table.setXCoordinate(dto.getXCoordinate() != null ? dto.getXCoordinate() : 0.0);
        table.setYCoordinate(dto.getYCoordinate() != null ? dto.getYCoordinate() : 0.0);
        return table;
    }

    // --- Order Item Mapping ---
    public OrderItemDTO toDto(OrderItem item) {
        if (item == null) return null;
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
        }
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setSpecialNotes(item.getSpecialNotes());
        return dto;
    }

    // --- Order Mapping ---
    public OrderDTO toDto(Order order) {
        if (order == null) return null;
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        if (order.getRestaurant() != null) {
            dto.setRestaurantId(order.getRestaurant().getId());
            dto.setRestaurantName(order.getRestaurant().getName());
        }
        if (order.getTable() != null) {
            dto.setTableId(order.getTable().getId());
            dto.setTableNumber(order.getTable().getNumber());
        }
        dto.setClientName(order.getClientName());
        dto.setClientPhone(order.getClientPhone());
        dto.setClientAddress(order.getClientAddress());
        dto.setStatus(order.getStatus());
        dto.setType(order.getType());
        dto.setTotalAmount(order.getTotalAmount());
        if (order.getWaiter() != null) {
            dto.setWaiterId(order.getWaiter().getId());
            dto.setWaiterName(order.getWaiter().getFirstName() + " " + order.getWaiter().getLastName());
        }
        if (order.getDeliveryPerson() != null) {
            dto.setDeliveryPersonId(order.getDeliveryPerson().getId());
            dto.setDeliveryPersonName(order.getDeliveryPerson().getFirstName() + " " + order.getDeliveryPerson().getLastName());
        }
        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream().map(this::toDto).collect(Collectors.toList()));
        } else {
            dto.setOrderItems(Collections.emptyList());
        }
        dto.setPaid(order.isPaid());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    // --- Payment Mapping ---
    public PaymentDTO toDto(Payment payment) {
        if (payment == null) return null;
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        if (payment.getOrder() != null) {
            dto.setOrderId(payment.getOrder().getId());
        }
        if (payment.getRestaurant() != null) {
            dto.setRestaurantId(payment.getRestaurant().getId());
        }
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setTransactionReference(payment.getTransactionReference());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }

    // --- Bill Mapping ---
    public BillDTO toDto(Bill bill) {
        if (bill == null) return null;
        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        if (bill.getOrder() != null) {
            dto.setOrderId(bill.getOrder().getId());
        }
        if (bill.getRestaurant() != null) {
            dto.setRestaurantId(bill.getRestaurant().getId());
        }
        dto.setBillNumber(bill.getBillNumber());
        dto.setSubTotal(bill.getSubTotal());
        dto.setTaxAmount(bill.getTaxAmount());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setIssuedAt(bill.getIssuedAt());
        dto.setPdfPath(bill.getPdfPath());
        return dto;
    }

    // --- Delivery Mapping ---
    public DeliveryDTO toDto(Delivery delivery) {
        if (delivery == null) return null;
        DeliveryDTO dto = new DeliveryDTO();
        dto.setId(delivery.getId());
        if (delivery.getOrder() != null) {
            dto.setOrderId(delivery.getOrder().getId());
        }
        if (delivery.getDeliveryPerson() != null) {
            dto.setDeliveryPersonId(delivery.getDeliveryPerson().getId());
            dto.setDeliveryPersonName(delivery.getDeliveryPerson().getFirstName() + " " + delivery.getDeliveryPerson().getLastName());
        }
        dto.setStatus(delivery.getStatus());
        dto.setGpsLatitude(delivery.getGpsLatitude());
        dto.setGpsLongitude(delivery.getGpsLongitude());
        return dto;
    }
}
