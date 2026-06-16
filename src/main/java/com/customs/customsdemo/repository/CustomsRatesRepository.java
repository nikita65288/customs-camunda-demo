package com.customs.customsdemo.repository;

import com.customs.customsdemo.entity.CustomsRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomsRatesRepository extends JpaRepository<CustomsRate, String> {
}
