package com.nocturne.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class MetricsResultEntity {

    private Map<String, PositionMetricsEntity> positions;

    private BasketMetricsEntity basket;

}
