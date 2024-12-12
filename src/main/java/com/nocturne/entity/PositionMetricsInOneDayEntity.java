package com.nocturne.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class PositionMetricsInOneDayEntity {

    private Integer id;

    private Date date;

    private Integer IsOpen;

    private BigDecimal Price;

    private BigDecimal Value;

    private BigDecimal ValueStart;

    private BigDecimal ReturnPerPeriod;

    private BigDecimal ReturnPerPeriodPercentage;
}
