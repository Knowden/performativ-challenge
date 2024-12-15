package com.nocturne.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.nocturne.service.PerformativApiService;
import com.nocturne.value.FxRateVO;
import com.nocturne.value.PriceVO;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Setter
@ToString
public class PositionEntity {

    private Integer id;

    @JSONField(name = "open_date")
    private Date openDate;

    @JSONField(name = "close_date")
    private Date closeDate;

    @JSONField(name = "open_price")
    private BigDecimal openPrice;

    @JSONField(name = "close_price")
    private BigDecimal closePrice;

    private BigDecimal quantity;

    @JSONField(name = "transaction_costs")
    private BigDecimal transactionCosts;

    @JSONField(name = "instrument_id")
    private Integer instrumentId;

    @JSONField(name = "instrument_currency")
    private String instrumentCurrency;

    @JSONField(name = "open_transaction_type")
    private String openTransactionType;

    @JSONField(name = "close_transaction_type")
    private String closeTransactionType;

    private static final String TARGET_CURRENCY = "USD";

    private static final Date START_DATE;
    private static final Date END_DATE;

    static {
        LocalDate localDate = LocalDate.of(2023, 1, 1);
        START_DATE = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        localDate = LocalDate.of(2024, 11, 10);
        END_DATE = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    public PositionMetricsInOneDayEntity calculateMetrics(Date date, PerformativApiService apiService) {
        return PositionMetricsInOneDayEntity.builder()
                .id(id)
                .date(date)
                .IsOpen(calculateIsOpen(date))
                .Price(calculatePrice(date, apiService))
                .Value(calculateValue(date, apiService))
                .ValueStart(calculateValueStart(date, apiService))
                .ReturnPerPeriod(calculateReturnPerPeriod(date, apiService))
                .ReturnPerPeriodPercentage(calculateReturnPerPeriodPercentage(date, apiService))
                .build();
    }

    private Integer calculateIsOpen(Date date) {
        if (date.before(openDate)) {
            return 0;
        }
        if (closeDate != null && !date.before(closeDate)) {
            return 0;
        }
        return 1;
    }

    private BigDecimal calculatePrice(Date date, PerformativApiService apiService) {
        BigDecimal localPrice = getLocalPriceInDate(date, apiService);

        BigDecimal rate = getFxRateInDate(date, apiService);

        return localPrice.multiply(rate);
    }

    private BigDecimal calculateValue(Date date, PerformativApiService apiService) {
        BigDecimal localPriceInDate = getLocalPriceInDate(date, apiService);
        BigDecimal quantityInDate = getQuantityInDate(date);
        BigDecimal localValue = localPriceInDate.multiply(quantityInDate);
        BigDecimal rate = getFxRateInDate(date, apiService);
        return localValue.multiply(rate);
    }

    private BigDecimal getLocalPriceInDate(Date date, PerformativApiService apiService) {
        if (date.before(openDate)) {
            return BigDecimal.ZERO;
        }

        Map<Date, PriceVO> priceRecords = apiService.queryPrice(instrumentId, this.openDate, this.closeDate);
        PriceVO priceVO = priceRecords.get(date);

        if (priceVO == null) {
            return BigDecimal.ZERO;
        }
        return priceVO.getPrice();
    }

    private BigDecimal getFxRateInDate(Date date, PerformativApiService apiService) {
        if (Objects.equals(this.instrumentCurrency, TARGET_CURRENCY)) {
            return BigDecimal.ONE;
        }

        Map<Date, FxRateVO> fxRateRecords = apiService.queryFxRate(this.instrumentCurrency, TARGET_CURRENCY, this.openDate, this.closeDate);
        FxRateVO fxRateVO = fxRateRecords.get(date);

        if (fxRateVO == null) {
            return BigDecimal.ZERO;
        }
        return fxRateVO.getRate();
    }

    private BigDecimal getQuantityInDate(Date date) {
        if (date.before(openDate)) {
            return BigDecimal.ZERO;
        }
        if (closeDate != null && !date.before(closeDate)) {
            return BigDecimal.ZERO;
        }
        return this.quantity;
    }

    private BigDecimal calculateReturnPerPeriod(Date date, PerformativApiService apiService) {
        if (date.before(openDate) || (closeDate != null && date.after(closeDate))) {
            return BigDecimal.ZERO;
        }

        BigDecimal valueStart = calculateValueStart(date, apiService);

        BigDecimal valueEnd;
        if (closeDate == null || date.before(closeDate)) {
            valueEnd = calculateValue(date, apiService);
        } else {
            valueEnd = calculateCloseValue(date, apiService);
        }

        return valueEnd.subtract(valueStart);
    }

    private BigDecimal calculateValueStart(Date date, PerformativApiService apiService) {
        BigDecimal valueStart;
        if (!Objects.equals(date, openDate) && Objects.equals(date, START_DATE)) {
            valueStart = calculateValue(date, apiService);
        } else if (Objects.equals(date, openDate)) {
            valueStart = calculateOpenValue(date, apiService);
        } else if (date.after(openDate) && date.after(START_DATE)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterday = calendar.getTime();
            valueStart = calculateValue(yesterday, apiService);
        } else {
            return BigDecimal.ZERO;
        }
        return valueStart;
    }

    private BigDecimal calculateOpenValue(Date date, PerformativApiService apiService) {
        if (date.before(openDate)) {
            return BigDecimal.ZERO;
        }

        return this.openPrice.multiply(getFxRateInDate(this.openDate, apiService)).multiply(this.quantity);
    }

    private BigDecimal calculateCloseValue(Date date, PerformativApiService apiService) {
        if (closeDate == null || date.before(closeDate)) {
            return BigDecimal.ZERO;
        }

        return this.closePrice.multiply(getFxRateInDate(this.closeDate, apiService)).multiply(this.quantity);
    }

    private BigDecimal calculateReturnPerPeriodPercentage(Date date, PerformativApiService apiService) {
        BigDecimal valueStart = this.calculateValueStart(date, apiService);
        if (BigDecimal.ZERO.compareTo(valueStart) == 0) {
            return BigDecimal.ZERO;
        }

        return calculateReturnPerPeriod(date, apiService).divide(valueStart, 7, RoundingMode.CEILING);
    }
}
