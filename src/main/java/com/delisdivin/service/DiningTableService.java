package com.delisdivin.service;

import com.delisdivin.dto.DiningTableDTO;
import com.delisdivin.entity.TableStatus;
import java.util.List;

public interface DiningTableService {
    DiningTableDTO createTable(DiningTableDTO tableDTO);
    DiningTableDTO updateTable(Long id, DiningTableDTO tableDTO);
    DiningTableDTO getTableById(Long id);
    List<DiningTableDTO> getTablesByRestaurant(Long restaurantId);
    DiningTableDTO updateTableStatus(Long id, TableStatus status);
    void updateTablePositions(List<DiningTableDTO> tables);
    void deleteTable(Long id);
}
