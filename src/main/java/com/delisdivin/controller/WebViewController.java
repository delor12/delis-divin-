package com.delisdivin.controller;

import com.delisdivin.dto.*;
import com.delisdivin.entity.OrderStatus;
import com.delisdivin.entity.Role;
import com.delisdivin.security.UserDetailsImpl;
import com.delisdivin.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebViewController {

    private final CityService cityService;
    private final RestaurantService restaurantService;
    private final ProductService productService;
    private final DiningTableService tableService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final ReportService reportService;
    private final BackupService backupService;

    // --- Public routes ---

    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) Long cityId, @RequestParam(required = false) String search) {
        model.addAttribute("cities", cityService.getAllActiveCities());
        model.addAttribute("selectedCityId", cityId);
        model.addAttribute("searchQuery", search);
        
        List<RestaurantDTO> restaurants = restaurantService.searchRestaurants(cityId, search);
        model.addAttribute("restaurants", restaurants);
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("cities", cityService.getAllActiveCities());
        return "register";
    }

    // --- Client Space ---

    @GetMapping("/restaurant/{id}")
    public String restaurantSpace(@PathVariable Long id, Model model) {
        RestaurantDTO restaurant = restaurantService.getRestaurantById(id);
        List<ProductCategoryDTO> categories = productService.getActiveCategoriesByRestaurant(id);
        List<ProductDTO> products = productService.getActiveProductsByRestaurant(id);
        List<DiningTableDTO> tables = tableService.getTablesByRestaurant(id);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("tables", tables);
        return "restaurant_space";
    }

    // --- Super Admin Space ---

    @GetMapping("/super-admin/dashboard")
    public String superAdminDashboard(Model model) {
        Map<String, Object> stats = reportService.getGlobalStats();
        model.addAllAttributes(stats);
        model.addAttribute("cities", cityService.getAllCities());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("backups", backupService.getBackups());
        
        List<UserDTO> deliverers = userService.getAllUsers().stream()
                .filter(u -> u.getRole() == com.delisdivin.entity.Role.DELIVERY)
                .toList();
        model.addAttribute("deliverers", deliverers);
        
        return "super_admin_dashboard";
    }

    @PostMapping("/super-admin/backup/trigger")
    public String triggerBackup() {
        backupService.backupDatabase();
        return "redirect:/super-admin/dashboard";
    }

    // --- Restaurant Admin Space ---

    @GetMapping("/restaurant-admin/dashboard")
    public String restaurantAdminDashboard(@AuthenticationPrincipal UserDetailsImpl user, Model model) {
        Long restaurantId = user.getRestaurantId();
        Map<String, Object> stats = reportService.getRestaurantStats(restaurantId);
        
        model.addAllAttributes(stats);
        model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId));
        model.addAttribute("products", productService.getProductsByRestaurant(restaurantId));
        model.addAttribute("categories", productService.getCategoriesByRestaurant(restaurantId));
        model.addAttribute("employees", userService.getUsersByRestaurant(restaurantId));
        model.addAttribute("orders", orderService.getOrdersByRestaurant(restaurantId));
        return "restaurant_admin_dashboard";
    }

    // --- Kitchen Space ---

    @GetMapping("/kitchen/dashboard")
    public String kitchenDashboard(@AuthenticationPrincipal UserDetailsImpl user, Model model) {
        Long restaurantId = user.getRestaurantId();
        model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId));
        model.addAttribute("orders", orderService.getActiveOrdersByRestaurant(restaurantId));
        model.addAttribute("cookedToday", orderService.getCookedOrdersToday(restaurantId));
        return "kitchen_dashboard";
    }

    // --- Server Space ---

    @GetMapping("/server/dashboard")
    public String serverDashboard(@AuthenticationPrincipal UserDetailsImpl user, Model model) {
        Long restaurantId = user.getRestaurantId();
        model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId));
        model.addAttribute("tables", tableService.getTablesByRestaurant(restaurantId));
        model.addAttribute("products", productService.getActiveProductsByRestaurant(restaurantId));
        return "server_dashboard";
    }

    // --- Cashier Space ---

    @GetMapping("/cashier/dashboard")
    public String cashierDashboard(@AuthenticationPrincipal UserDetailsImpl user, Model model) {
        Long restaurantId = user.getRestaurantId();
        model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId));
        model.addAttribute("orders", orderService.getActiveOrdersByRestaurant(restaurantId));
        model.addAttribute("payments", paymentService.getPaymentsByRestaurant(restaurantId));
        model.addAttribute("products", productService.getProductsByRestaurant(restaurantId));
        model.addAttribute("categories", productService.getCategoriesByRestaurant(restaurantId));
        model.addAttribute("salesReport", reportService.getDailySalesReport(restaurantId));
        return "cashier_dashboard";
    }

    // --- Delivery Space ---

    @GetMapping("/delivery/dashboard")
    public String deliveryDashboard(@AuthenticationPrincipal UserDetailsImpl user, Model model) {
        model.addAttribute("deliveries", orderService.getOrdersByDeliveryPerson(user.getId()));
        model.addAttribute("availableOrders", orderService.getAvailableDeliveryOrders(user.getId()));
        model.addAttribute("currentUser", userService.getUserById(user.getId()));
        return "delivery_dashboard";
    }

    // --- PDF Invoice Download ---

    @GetMapping("/invoice/pdf/{orderId}")
    @ResponseBody
    public ResponseEntity<Resource> downloadInvoice(@PathVariable Long orderId) {
        BillDTO bill = paymentService.generateInvoice(orderId);
        if (bill.getPdfPath() == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(bill.getPdfPath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    // --- Excel Report Download ---

    @GetMapping("/restaurant-admin/report/excel")
    @ResponseBody
    public ResponseEntity<byte[]> downloadExcelReport(@AuthenticationPrincipal UserDetailsImpl user) {
        Long restaurantId = user.getRestaurantId();
        LocalDateTime start = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now();
        byte[] excelBytes = reportService.generateSalesReportExcel(restaurantId, start, end);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sales_report.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}
