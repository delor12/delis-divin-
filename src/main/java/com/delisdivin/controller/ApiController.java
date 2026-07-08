package com.delisdivin.controller;

import com.delisdivin.dto.*;
import com.delisdivin.entity.OrderStatus;
import com.delisdivin.entity.TableStatus;
import com.delisdivin.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final CityService cityService;
    private final RestaurantService restaurantService;
    private final ProductService productService;
    private final DiningTableService tableService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ReportService reportService;

    // --- Public Platform endpoints ---

    @GetMapping("/public/cities")
    public ResponseEntity<List<CityDTO>> getCities() {
        return ResponseEntity.ok(cityService.getAllActiveCities());
    }

    @GetMapping("/public/restaurants")
    public ResponseEntity<List<RestaurantDTO>> getRestaurants(
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(cityId, search));
    }

    @GetMapping("/public/restaurant/{restaurantId}/menu")
    public ResponseEntity<List<ProductDTO>> getRestaurantMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(productService.getActiveProductsByRestaurant(restaurantId));
    }

    @GetMapping("/public/restaurant/{restaurantId}/categories")
    public ResponseEntity<List<ProductCategoryDTO>> getRestaurantCategories(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(productService.getActiveCategoriesByRestaurant(restaurantId));
    }

    // --- Restaurant Creation (Public for Sign-up) ---

    @PostMapping("/public/restaurants")
    public ResponseEntity<RestaurantDTO> createRestaurant(@Valid @RequestBody RestaurantDTO restaurantDTO) {
        return ResponseEntity.ok(restaurantService.createRestaurant(restaurantDTO));
    }

    // --- Product & Category Management (Restaurant Admin) ---

    @PostMapping("/restaurant/{restaurantId}/categories")
    public ResponseEntity<ProductCategoryDTO> createCategory(
            @PathVariable Long restaurantId,
            @Valid @RequestBody ProductCategoryDTO categoryDTO) {
        categoryDTO.setRestaurantId(restaurantId);
        return ResponseEntity.ok(productService.createCategory(categoryDTO));
    }

    @PutMapping("/restaurant/category/{categoryId}")
    public ResponseEntity<ProductCategoryDTO> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody ProductCategoryDTO categoryDTO) {
        return ResponseEntity.ok(productService.updateCategory(categoryId, categoryDTO));
    }

    @DeleteMapping("/restaurant/category/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        productService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/restaurant/{restaurantId}/products")
    public ResponseEntity<ProductDTO> createProduct(
            @PathVariable Long restaurantId,
            @Valid @RequestBody ProductDTO productDTO) {
        productDTO.setRestaurantId(restaurantId);
        return ResponseEntity.ok(productService.createProduct(productDTO));
    }

    @PutMapping("/restaurant/product/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(productId, productDTO));
    }

    @DeleteMapping("/restaurant/product/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }


    // --- Restaurant specific endpoints ---

    @GetMapping("/restaurant/{restaurantId}/tables")
    public ResponseEntity<List<DiningTableDTO>> getTables(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(tableService.getTablesByRestaurant(restaurantId));
    }

    @PutMapping("/restaurant/{restaurantId}/tables/positions")
    public ResponseEntity<String> updateTablePositions(
            @PathVariable Long restaurantId,
            @RequestBody List<DiningTableDTO> tables) {
        tableService.updateTablePositions(tables);
        return ResponseEntity.ok("Table positions updated successfully.");
    }

    @PutMapping("/restaurant/table/{tableId}/status")
    public ResponseEntity<DiningTableDTO> updateTableStatus(
            @PathVariable Long tableId,
            @RequestParam TableStatus status) {
        return ResponseEntity.ok(tableService.updateTableStatus(tableId, status));
    }

    // --- Orders & Checkout ---

    @PostMapping("/restaurant/{restaurantId}/orders")
    public ResponseEntity<OrderDTO> createOrder(
            @PathVariable Long restaurantId,
            @Valid @RequestBody OrderDTO orderDTO) {
        orderDTO.setRestaurantId(restaurantId);
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @PutMapping("/restaurant/order/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @PutMapping("/restaurant/order/{orderId}/waiter")
    public ResponseEntity<OrderDTO> assignWaiter(
            @PathVariable Long orderId,
            @RequestParam Long waiterId) {
        return ResponseEntity.ok(orderService.assignWaiter(orderId, waiterId));
    }

    @PutMapping("/restaurant/order/{orderId}/delivery-person")
    public ResponseEntity<OrderDTO> assignDeliveryPerson(
            @PathVariable Long orderId,
            @RequestParam Long deliveryPersonId) {
        return ResponseEntity.ok(orderService.assignDeliveryPerson(orderId, deliveryPersonId));
    }

    // --- Payments ---

    @PostMapping("/restaurant/{restaurantId}/payments")
    public ResponseEntity<PaymentDTO> processPayment(
            @PathVariable Long restaurantId,
            @Valid @RequestBody PaymentDTO paymentDTO) {
        paymentDTO.setRestaurantId(restaurantId);
        return ResponseEntity.ok(paymentService.processPayment(paymentDTO));
    }

    // --- Reports & Stats ---

    @GetMapping("/restaurant/{restaurantId}/stats")
    public ResponseEntity<Map<String, Object>> getRestaurantStats(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(reportService.getRestaurantStats(restaurantId));
    }

    @GetMapping("/super-admin/stats")
    public ResponseEntity<Map<String, Object>> getSuperAdminStats() {
        return ResponseEntity.ok(reportService.getGlobalStats());
    }

    @PostMapping("/super-admin/cities")
    public ResponseEntity<CityDTO> createCity(@Valid @RequestBody CityDTO cityDTO) {
        return ResponseEntity.ok(cityService.createCity(cityDTO));
    }

    @DeleteMapping("/super-admin/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/super-admin/restaurants/{restaurantId}/subscribe")
    public ResponseEntity<Void> subscribeRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam String planName,
            @RequestParam Double price,
            @RequestParam Integer durationMonths) {
        restaurantService.subscribe(restaurantId, planName, price, durationMonths);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/public/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            // Save to static directory (for development persistence/git)
            File staticDir = new File("src/main/resources/static/images");
            if (staticDir.exists()) {
                File destFile = new File(staticDir, fileName);
                file.transferTo(destFile);
            } else {
                // Fallback to standard upload dir
                File fallbackDir = new File("./uploads");
                if (!fallbackDir.exists()) {
                    fallbackDir.mkdirs();
                }
                File destFile = new File(fallbackDir, fileName);
                file.transferTo(destFile);
            }

            // Also write to target/classes/static/images so Spring Boot serves it immediately without restart
            File targetDir = new File("target/classes/static/images");
            if (targetDir.exists()) {
                File destFile = new File(targetDir, fileName);
                Files.copy(file.getInputStream(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            return ResponseEntity.ok(Map.of("imageUrl", "/images/" + fileName));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}

