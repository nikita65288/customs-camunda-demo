package com.customs.customsdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Сущность, представляющая ставку таможенной пошлины для определённого кода товара.
 *
 * Маппится на таблицу {@code CUSTOMS_RATES} в базе данных.
 */
@Entity
@Table(name = "CUSTOMS_RATES")
public class CustomsRate {

    @Id
    @Column(name = "goods_code", length = 20)
    private String goodsCode;

    @Column(name = "duty_rate")
    private double dutyRate;

    public String getGoodsCode() {
        return goodsCode;
    }

    public double getDutyRate() {
        return dutyRate;
    }
}
