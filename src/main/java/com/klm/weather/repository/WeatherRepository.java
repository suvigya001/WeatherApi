package com.klm.weather.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.klm.weather.model.Weather;

@Repository
public interface WeatherRepository extends MongoRepository<Weather, String> {
	Optional<Weather> findById(Integer id);
	List<Weather> findAllByOrderByIdAsc();
}
