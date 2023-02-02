package com.devsuperior.bds04.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.CityDTO;
import com.devsuperior.bds04.entities.City;
import com.devsuperior.bds04.repositories.CityRepository;
import com.devsuperior.bds04.services.exceptions.DatabaseException;
import com.devsuperior.bds04.services.exceptions.ResourceNotFoundException;

@Service
public class CityService {

	@Autowired
	private CityRepository repository;
	
	@Transactional(readOnly = true)
	public List<CityDTO> findAll() {
		List<City> list = repository.findAll(Sort.by("name"));
		return list.stream().map(x -> new CityDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public CityDTO findById(Long id) {
		Optional<City> optional = repository.findById(id);
		City city = optional.orElseThrow(() -> new ResourceNotFoundException("Id not found"));
		return new CityDTO(city);
	}
	
	@Transactional
	public CityDTO insert(CityDTO dto) {
		City city = new City();
		city.setName(dto.getName());
		repository.save(city);
		return new CityDTO(city);
	}
	
	@Transactional
	public CityDTO update(Long id, CityDTO dto) {
		try {
			City city = repository.getOne(id);
			city.setName(dto.getName());
			city = repository.save(city);
			return new CityDTO(city);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		}
	}
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
}
