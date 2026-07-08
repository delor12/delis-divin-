package com.delisdivin.service;

import com.delisdivin.dto.CityDTO;
import java.util.List;

public interface CityService {
    CityDTO createCity(CityDTO cityDTO);
    CityDTO updateCity(Long id, CityDTO cityDTO);
    CityDTO getCityById(Long id);
    List<CityDTO> getAllActiveCities();
    List<CityDTO> getAllCities();
    void deleteCity(Long id);
}
