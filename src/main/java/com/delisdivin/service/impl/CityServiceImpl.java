package com.delisdivin.service.impl;

import com.delisdivin.dto.CityDTO;
import com.delisdivin.entity.City;
import com.delisdivin.exception.BadRequestException;
import com.delisdivin.exception.ResourceNotFoundException;
import com.delisdivin.mapper.AppMapper;
import com.delisdivin.repository.CityRepository;
import com.delisdivin.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final AppMapper mapper;

    @Override
    @Transactional
    public CityDTO createCity(CityDTO cityDTO) {
        if (cityRepository.existsByNameIgnoreCase(cityDTO.getName())) {
            throw new BadRequestException("City with name " + cityDTO.getName() + " already exists.");
        }
        City city = mapper.toEntity(cityDTO);
        City saved = cityRepository.save(city);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public CityDTO updateCity(Long id, CityDTO cityDTO) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with ID: " + id));
        
        city.setName(cityDTO.getName());
        city.setPostalCode(cityDTO.getPostalCode());
        city.setCountry(cityDTO.getCountry());
        city.setActive(cityDTO.isActive());
        
        City updated = cityRepository.save(city);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CityDTO getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with ID: " + id));
        return mapper.toDto(city);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityDTO> getAllActiveCities() {
        return cityRepository.findByActiveTrue().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with ID: " + id));
        cityRepository.delete(city);
    }
}
