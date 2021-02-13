package com.assignment.spring;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface WeatherRepository extends CrudRepository<WeatherEntity, Integer> {
}
