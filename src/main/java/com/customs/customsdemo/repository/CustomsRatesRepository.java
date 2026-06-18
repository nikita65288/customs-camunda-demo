package com.customs.customsdemo.repository;

import com.customs.customsdemo.entity.CustomsRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для доступа к данным о ставках пошлин (таблица {@code CUSTOMS_RATES}).
 *
 * Используется делегатом {@link com.customs.customsdemo.delegate.ClassifyGoodsDelegate}
 * для поиска ставки по коду товара.
 */
@Repository
public interface CustomsRatesRepository extends JpaRepository<CustomsRate, String> {
}
