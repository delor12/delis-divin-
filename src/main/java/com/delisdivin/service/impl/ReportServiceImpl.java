package com.delisdivin.service.impl;

import com.delisdivin.entity.*;
import com.delisdivin.exception.BadRequestException;
import com.delisdivin.repository.*;
import com.delisdivin.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RestaurantRepository restaurantRepository;
    private final CityRepository cityRepository;
    private final AppUserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] generateSalesReportExcel(Long restaurantId, LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByRestaurantIdAndCreatedAtBetween(restaurantId, start, end);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Rapport Ventes");

            // Header Style
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Columns
            String[] columns = {"ID Commande", "Date", "Client", "Type", "Statut", "Montant (FCFA)"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            double totalSales = 0.0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Order order : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue("#" + order.getId());
                row.createCell(1).setCellValue(order.getCreatedAt().format(formatter));
                row.createCell(2).setCellValue(order.getClientName() != null ? order.getClientName() : "Client Direct");
                row.createCell(3).setCellValue(order.getType().name());
                row.createCell(4).setCellValue(order.getStatus().name());
                row.createCell(5).setCellValue(order.getTotalAmount());
                totalSales += order.getTotalAmount();
            }

            // Total row
            Row totalRow = sheet.createRow(rowIdx + 1);
            Cell totalLabelCell = totalRow.createCell(4);
            totalLabelCell.setCellValue("TOTAL GENERALE:");
            
            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            totalLabelCell.setCellStyle(boldStyle);

            Cell totalValCell = totalRow.createCell(5);
            totalValCell.setCellValue(totalSales);
            totalValCell.setCellStyle(boldStyle);

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate Excel sales report: {}", e.getMessage());
            throw new BadRequestException("Failed to generate Excel report");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateSalesReportCsv(Long restaurantId, LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByRestaurantIdAndCreatedAtBetween(restaurantId, start, end);
        StringBuilder sb = new StringBuilder();
        sb.append("ID Commande;Date;Client;Type;Statut;Montant (FCFA)\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        double totalSales = 0.0;

        for (Order order : orders) {
            sb.append(order.getId()).append(";")
              .append(order.getCreatedAt().format(formatter)).append(";")
              .append(order.getClientName() != null ? order.getClientName() : "Client Direct").append(";")
              .append(order.getType().name()).append(";")
              .append(order.getStatus().name()).append(";")
              .append(order.getTotalAmount()).append("\n");
            totalSales += order.getTotalAmount();
        }

        sb.append(";;;;TOTAL GENERALE;").append(totalSales).append("\n");
        return sb.toString().getBytes();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getRestaurantStats(Long restaurantId) {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime now = LocalDateTime.now();

        List<Order> allOrders = orderRepository.findByRestaurantId(restaurantId);
        List<Order> monthOrders = orderRepository.findByRestaurantIdAndCreatedAtBetween(restaurantId, startOfMonth, now);

        double totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(Order::getTotalAmount)
                .sum();

        double monthRevenue = monthOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(Order::getTotalAmount)
                .sum();

        long totalOrders = allOrders.size();
        long pendingOrders = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long activeOrders = allOrders.stream().filter(o -> o.getStatus() != OrderStatus.COMPLETED && o.getStatus() != OrderStatus.CANCELLED).count();

        // Calculate Top Products
        Map<String, Integer> productQuantities = new HashMap<>();
        for (Order order : allOrders) {
            for (OrderItem item : order.getOrderItems()) {
                String name = item.getProduct().getName();
                productQuantities.put(name, productQuantities.getOrDefault(name, 0) + item.getQuantity());
            }
        }

        List<Map<String, Object>> topProducts = productQuantities.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Map<String, Object> prod = new HashMap<>();
                    prod.put("name", entry.getKey());
                    prod.put("quantity", entry.getValue());
                    return prod;
                })
                .collect(Collectors.toList());

        // Weekly revenue chart data
        List<Double> weeklyRevenue = new ArrayList<>(Collections.nCopies(4, 0.0));
        for (Order order : monthOrders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                int day = order.getCreatedAt().getDayOfMonth();
                int weekIndex = Math.min((day - 1) / 7, 3);
                weeklyRevenue.set(weekIndex, weeklyRevenue.get(weekIndex) + order.getTotalAmount());
            }
        }

        stats.put("totalRevenue", totalRevenue);
        stats.put("monthRevenue", monthRevenue);
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("activeOrders", activeOrders);
        stats.put("topProducts", topProducts);
        stats.put("weeklyRevenue", weeklyRevenue);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalRestaurants = restaurantRepository.count();
        long totalCities = cityRepository.count();
        long totalUsers = userRepository.count();

        List<Order> allOrders = orderRepository.findAll();
        double totalSales = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // Top restaurants
        Map<String, Double> restaurantSales = new HashMap<>();
        for (Order order : allOrders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                String name = order.getRestaurant().getName();
                restaurantSales.put(name, restaurantSales.getOrDefault(name, 0.0) + order.getTotalAmount());
            }
        }

        List<Map<String, Object>> topRestaurants = restaurantSales.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Map<String, Object> rest = new HashMap<>();
                    rest.put("name", entry.getKey());
                    rest.put("revenue", entry.getValue());
                    return rest;
                })
                .collect(Collectors.toList());

        stats.put("totalRestaurants", totalRestaurants);
        stats.put("totalCities", totalCities);
        stats.put("totalUsers", totalUsers);
        stats.put("totalSales", totalSales);
        stats.put("topRestaurants", topRestaurants);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDailySalesReport(Long restaurantId) {
        Map<String, Object> report = new HashMap<>();

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        List<Payment> todayPayments = paymentRepository.findByRestaurantIdAndCreatedAtBetween(restaurantId, startOfDay, endOfDay);

        double totalRevenue = todayPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getAmount)
                .sum();

        long transactionCount = todayPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .count();

        // Breakdowns by payment methods
        Map<String, Double> breakdown = new HashMap<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            breakdown.put(method.name(), 0.0);
        }

        for (Payment payment : todayPayments) {
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                String methodName = payment.getPaymentMethod().name();
                breakdown.put(methodName, breakdown.getOrDefault(methodName, 0.0) + payment.getAmount());
            }
        }

        report.put("totalRevenue", totalRevenue);
        report.put("transactionCount", transactionCount);
        report.put("breakdown", breakdown);
        report.put("payments", todayPayments);
        report.put("date", LocalDateTime.now());

        return report;
    }
}
