package com.nocturne.entity;

import lombok.Data;

import java.util.List;

@Data
public class PositionMetricsEntity {

    private Integer id;

    private List<Integer> IsOpen;

    private List<Double> Price;

    private List<Double> Value;

    private List<Double> ReturnPerPeriod;

    private List<Double> ReturnPerPeriodPercentage;

}
