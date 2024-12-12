package com.nocturne.value;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FxRateVO {

    private Date date;

    private BigDecimal rate;
}
