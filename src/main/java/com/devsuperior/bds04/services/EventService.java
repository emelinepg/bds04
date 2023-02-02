package com.devsuperior.bds04.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.EventDTO;
import com.devsuperior.bds04.entities.Event;
import com.devsuperior.bds04.repositories.CityRepository;
import com.devsuperior.bds04.repositories.EventRepository;
import com.devsuperior.bds04.services.exceptions.DatabaseException;
import com.devsuperior.bds04.services.exceptions.ResourceNotFoundException;

@Service
public class EventService {

	@Autowired
	private EventRepository repository;
	
	@Autowired
	private CityRepository cityRepository;
	
	@Transactional(readOnly = true)
	public Page<EventDTO> findAllPaged(Pageable pageable) {
		Page<Event> list = repository.findAll(pageable);
		return list.map(event -> new EventDTO(event));
	}
	
	@Transactional(readOnly = true)
	public EventDTO findById(Long id) {
		Optional<Event> optional = repository.findById(id);
		Event event = optional.orElseThrow(() -> new ResourceNotFoundException("Id not found"));
		return new EventDTO(event);
	}
	
	@Transactional
	public EventDTO insert(EventDTO dto) {
		Event event = new Event();
		copyDtoToEntity(dto, event);
		event = repository.save(event);
		return new EventDTO(event);
	}
	
	@Transactional
	public EventDTO update(EventDTO dto, Long id) {
		try {
			Event event = repository.getOne(id);
			copyDtoToEntity(dto, event);
			event = repository.save(event);
			return new EventDTO(event);
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
	
	private void copyDtoToEntity(EventDTO dto, Event event) {
		event.setName(dto.getName());
		event.setDate(dto.getDate());
		event.setUrl(dto.getUrl());
		event.setCity(cityRepository.getOne(dto.getCityId()));
	}
}
