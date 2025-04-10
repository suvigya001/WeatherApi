package com.klm.weather.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klm.weather.model.Weather;
import com.klm.weather.repository.WeatherRepository;
import com.klm.weather.service.SequenceGeneratorService;
import com.mongodb.DuplicateKeyException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/weather")
public class WeatherApiRestController {

	private static final String SEQUENCE_NAME = "id_sequence";
	@Autowired
	private WeatherRepository weatherRepository;
	
	@Autowired
	private SequenceGeneratorService sequenceGeneratorService;
    
    
    @PostMapping
    public ResponseEntity<?> addDetail(@Valid @RequestBody Weather weather) {
    	try {
    		weather.setId(sequenceGeneratorService.generateSequence(SEQUENCE_NAME));
    		Weather savedDetail = weatherRepository.save(weather);
            return ResponseEntity.status(HttpStatus.CREATED)
            		.body(savedDetail);
    	}catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("An entry with this ID already exists.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    	
    }
    
    @GetMapping
    public List<Weather> getAllDetails(@RequestParam(required = false)
    						@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date){
    	if(date !=null) {
    		return weatherRepository.findByDateBetween(
                    getStartOfDay(date),
                    getEndOfDay(date));
    	}else {
    		return weatherRepository.findAllByOrderByIdAsc();
    	}
    }

	@GetMapping("/{id}")
    public ResponseEntity<?> getDetailById(@PathVariable int id){
    	Optional<Weather> value = weatherRepository.findById(id);
    	if(value.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not record found");
    	}
    	else {
    		return ResponseEntity.status(HttpStatus.OK).body(value);
    	}
    		
    }
	
	private Date getStartOfDay(Date date) {
        return new Date(date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
    }

    private Date getEndOfDay(Date date) {
        return new Date(date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .atTime(23, 59, 59, 999000000)
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
    }
    
}
