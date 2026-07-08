package com.delisdivin.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface ReportService {
    byte[] generateSalesReportExcel(Long restaurantId, LocalDateTime start, LocalDateTime end);
    byte[] generateSalesReportCsv(Long restaurantId, LocalDateTime start, LocalDateTime end);
    Map<String, Object> getRestaurantStats(Long restaurantId);
    Map<String, Object> getGlobalStats();
}
