package com.nocturne.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PositionMetricsEntity {

    @JSONField(deserialize = false, serialize = false)
    private Integer id;

    @JSONField(name = "IsOpen")
    private List<Integer> IsOpen = new ArrayList<>();

    @JSONField(name = "Price")
    private List<BigDecimal> Price = new ArrayList<>();

    @JSONField(name = "Value")
    private List<BigDecimal> Value = new ArrayList<>();

    @JSONField(serialize = false, deserialize = false)
    private List<BigDecimal> ValueStart = new ArrayList<>();

    @JSONField(name = "ReturnPerPeriod")
    private List<BigDecimal> ReturnPerPeriod = new ArrayList<>();

    @JSONField(name = "ReturnPerPeriodPercentage")
    private List<BigDecimal> ReturnPerPeriodPercentage = new ArrayList<>();


    public PositionMetricsEntity(List<PositionMetricsInOneDayEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        this.id = entities.get(0).getId();

        for (PositionMetricsInOneDayEntity entity : entities) {
            this.IsOpen.add(entity.getIsOpen());
            this.Price.add(entity.getPrice());
            this.Value.add(entity.getValue());
            this.ValueStart.add(entity.getValueStart());
            this.ReturnPerPeriod.add(entity.getReturnPerPeriod());
            this.ReturnPerPeriodPercentage.add(entity.getReturnPerPeriodPercentage());
        }
    }

}
