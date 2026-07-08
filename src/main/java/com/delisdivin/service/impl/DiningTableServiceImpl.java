package com.delisdivin.service.impl;

import com.delisdivin.dto.DiningTableDTO;
import com.delisdivin.entity.DiningTable;
import com.delisdivin.entity.Restaurant;
import com.delisdivin.entity.TableStatus;
import com.delisdivin.exception.ResourceNotFoundException;
import com.delisdivin.mapper.AppMapper;
import com.delisdivin.repository.DiningTableRepository;
import com.delisdivin.repository.RestaurantRepository;
import com.delisdivin.service.DiningTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiningTableServiceImpl implements DiningTableService {

    private final DiningTableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;
    private final AppMapper mapper;

    @Override
    @Transactional
    public DiningTableDTO createTable(DiningTableDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + dto.getRestaurantId()));
        DiningTable table = mapper.toEntity(dto, restaurant);
        DiningTable saved = tableRepository.save(table);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public DiningTableDTO updateTable(Long id, DiningTableDTO dto) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dining table not found with ID: " + id));
        table.setNumber(dto.getNumber());
        table.setCapacity(dto.getCapacity());
        if (dto.getStatus() != null) {
            table.setStatus(dto.getStatus());
        }
        if (dto.getXCoordinate() != null) {
            table.setXCoordinate(dto.getXCoordinate());
        }
        if (dto.getYCoordinate() != null) {
            table.setYCoordinate(dto.getYCoordinate());
        }
        DiningTable updated = tableRepository.save(table);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public DiningTableDTO getTableById(Long id) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dining table not found with ID: " + id));
        return mapper.toDto(table);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiningTableDTO> getTablesByRestaurant(Long restaurantId) {
        return tableRepository.findByRestaurantIdOrderByNumberAsc(restaurantId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DiningTableDTO updateTableStatus(Long id, TableStatus status) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dining table not found with ID: " + id));
        table.setStatus(status);
        DiningTable updated = tableRepository.save(table);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public void updateTablePositions(List<DiningTableDTO> tables) {
        for (DiningTableDTO dto : tables) {
            DiningTable table = tableRepository.findById(dto.getId()).orElse(null);
            if (table != null) {
                table.setXCoordinate(dto.getXCoordinate());
                table.setYCoordinate(dto.getYCoordinate());
                tableRepository.save(table);
            }
        }
    }

    @Override
    @Transactional
    public void deleteTable(Long id) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dining table not found with ID: " + id));
        tableRepository.delete(table);
    }
}
