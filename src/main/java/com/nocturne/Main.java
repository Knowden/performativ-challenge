package com.nocturne;

import com.alibaba.fastjson.JSON;
import com.nocturne.entity.*;
import com.nocturne.service.PerformativApiService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = "src/main/resources/tech-challenge-2024-positions.json"; // 替换为你的文件路径

        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        List<PositionEntity> entities = JSON.parseArray(content, PositionEntity.class);

        PerformativApiService apiService = new PerformativApiService();

        Map<Integer, List<PositionMetricsInOneDayEntity>> oneDayMetricsMap = new HashMap<>();

        List<Date> dates = new ArrayList<>();

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 10);

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Date date = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            dates.add(date);

            for (PositionEntity entity : entities) {
                PositionMetricsInOneDayEntity metrics = entity.calculateMetrics(date, apiService);
                oneDayMetricsMap.computeIfAbsent(metrics.getId(), (k) -> new ArrayList<>()).add(metrics);
            }

            currentDate = currentDate.plusDays(1);
        }

        Map<String, PositionMetricsEntity> positionMetricsMap = new HashMap<>();
        for (Map.Entry<Integer, List<PositionMetricsInOneDayEntity>> entry : oneDayMetricsMap.entrySet()) {
            positionMetricsMap.put(String.valueOf(entry.getKey()), new PositionMetricsEntity(entry.getValue()));
        }

        BasketMetricsEntity basketMetrics = new BasketMetricsEntity(new ArrayList<>(positionMetricsMap.values()));

        MetricsResultEntity metricsResult = new MetricsResultEntity(positionMetricsMap, basketMetrics, dates);
        System.out.println(JSON.toJSONString(metricsResult));
    }

}