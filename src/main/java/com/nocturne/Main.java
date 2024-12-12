package com.nocturne;

import com.alibaba.fastjson.JSON;
import com.nocturne.entity.PositionEntity;
import com.nocturne.service.PerformativApiService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = "src/main/resources/tech-challenge-2024-positions.json"; // 替换为你的文件路径

        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        List<PositionEntity> entities = JSON.parseArray(content, PositionEntity.class);

        PerformativApiService apiService = new PerformativApiService();

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 10);

        // 遍历日期
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            for (PositionEntity entity : entities) {
                System.out.println(entity.calculateMetrics(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), apiService));
            }
            currentDate = currentDate.plusDays(1); // 增加一天
        }
    }

}