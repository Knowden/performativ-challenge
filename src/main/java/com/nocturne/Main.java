package com.nocturne;

import com.alibaba.fastjson.JSON;
import com.nocturne.entity.PositionEntity;
import com.nocturne.service.PerformativApiService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "src/main/resources/tech-challenge-2024-positions.json"; // 替换为你的文件路径

        PerformativApiService apiService = new PerformativApiService();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            List<PositionEntity> entities = JSON.parseArray(content, PositionEntity.class);
            for (PositionEntity entity : entities) {
                System.out.println(entity.calculateMetrics(Date.from(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()), apiService));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}