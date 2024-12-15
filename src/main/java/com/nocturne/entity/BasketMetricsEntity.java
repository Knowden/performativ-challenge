package com.nocturne.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class BasketMetricsEntity {

    @JSONField(name = "IsOpen")
    private List<Integer> IsOpen = new ArrayList<>();

    @JSONField(name = "Price")
    private List<BigDecimal> Price = new ArrayList<>();

    @JSONField(name = "Value")
    private List<BigDecimal> Value = new ArrayList<>();

    @JSONField(name = "ReturnPerPeriod")
    private List<BigDecimal> ReturnPerPeriod = new ArrayList<>();

    @JSONField(name = "ReturnPerPeriodPercentage")
    private List<BigDecimal> ReturnPerPeriodPercentage = new ArrayList<>();

    public BasketMetricsEntity(List<PositionMetricsEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        int size = entities.get(0).getIsOpen().size();

        for (int i = 0; i < size; i++) {
            this.IsOpen.add(calculateIsOpen(entities, i));
            this.Price.add(calculatePrice(entities, i));
            this.Value.add(calculateValue(entities, i));
            this.ReturnPerPeriod.add(calculateReturnPerPeriod(entities, i));
            this.ReturnPerPeriodPercentage.add(calculateReturnPerPeriodPercentage(entities, i));
        }
    }

    private Integer calculateIsOpen(List<PositionMetricsEntity> entities, int index) {
        for (PositionMetricsEntity entity : entities) {
            if (Objects.equals(1, entity.getIsOpen().get(index))) {
                return 1;
            }
        }
        return 0;
    }

    private BigDecimal calculatePrice(List<PositionMetricsEntity> entities, int index) {
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateValue(List<PositionMetricsEntity> entities, int index) {
        BigDecimal value = BigDecimal.ZERO;
        for (PositionMetricsEntity entity : entities) {
            value = value.add(entity.getValue().get(index));
        }
        return value;
    }

    private BigDecimal calculateReturnPerPeriod(List<PositionMetricsEntity> entities, int index) {
        BigDecimal returnPerPeriod = BigDecimal.ZERO;
        for (PositionMetricsEntity entity : entities) {
            returnPerPeriod = returnPerPeriod.add(entity.getReturnPerPeriod().get(index));
        }
        return returnPerPeriod;
    }

    private BigDecimal calculateReturnPerPeriodPercentage(List<PositionMetricsEntity> entities, int index) {
        BigDecimal returnPerPeriod = this.calculateReturnPerPeriod(entities, index);

        BigDecimal valueStart = BigDecimal.ZERO;
        for (PositionMetricsEntity entity : entities) {
            valueStart = valueStart.add(entity.getValueStart().get(index));
        }

        if (valueStart.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return returnPerPeriod.divide(valueStart, 7, RoundingMode.CEILING);
    }
}
