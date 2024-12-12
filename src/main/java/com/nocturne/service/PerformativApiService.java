package com.nocturne.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nocturne.utils.HttpsUtil;
import com.nocturne.value.FxRateVO;
import com.nocturne.value.PriceVO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerformativApiService {

    private static final String FX_RATE_QUERY_URL = "https://api.challenges.performativ.com/fx-rates";
    private static final String PRICE_QUERY_URL = "https://api.challenges.performativ.com/prices";

    private static final String PAIRS = "pairs";
    private static final String INSTRUMENT_ID = "instrument_id";
    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";

    private static final String QUERY_END_DATE_LIMIT = "20241110";

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyyMMdd");

    private static final String AUTH_KEY_NAME = "x-api-key";
    private static final String AUTH_KEY_VALUE = "FSPkaSbQA55Do0nXhSZkH9eKWVlAMmNP7OKlI2oA";

    public Map<Date, FxRateVO> queryFxRate(String fromCurrency, String targetCurrency, Date startDate, Date endDate) {
        Map<String, String> param = new HashMap<>();
        param.put(PAIRS, fromCurrency + targetCurrency);
        param.put(START_DATE, FORMATTER.format(startDate));
        param.put(END_DATE, endDate == null ? QUERY_END_DATE_LIMIT : FORMATTER.format(endDate));

        JSONObject jsonObject = HttpsUtil.get(FX_RATE_QUERY_URL, param, JSONObject.class, buildAuthHeader());
        JSONArray jsonArray = jsonObject.getJSONArray(fromCurrency + targetCurrency);

        Map<Date, FxRateVO> result = new HashMap<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            FxRateVO fxRateVO = jsonArray.getObject(i, FxRateVO.class);
            result.put(fxRateVO.getDate(), fxRateVO);
        }

        return result;
    }

    public Map<Date, PriceVO> queryPrice(Integer instrumentId, Date startDate, Date endDate) {
        Map<String, String> param = new HashMap<>();
        param.put(INSTRUMENT_ID, String.valueOf(instrumentId));
        param.put(START_DATE, FORMATTER.format(startDate));
        param.put(END_DATE, endDate == null ? QUERY_END_DATE_LIMIT : FORMATTER.format(endDate));

        JSONObject jsonObject = HttpsUtil.get(PRICE_QUERY_URL, param, JSONObject.class, buildAuthHeader());
        JSONArray jsonArray = jsonObject.getJSONArray(String.valueOf(instrumentId));

        Map<Date, PriceVO> result = new HashMap<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            PriceVO priceVO = jsonArray.getObject(i, PriceVO.class);
            result.put(priceVO.getDate(), priceVO);
        }

        return result;
    }

    private Map<String, String> buildAuthHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTH_KEY_NAME, AUTH_KEY_VALUE);
        return headers;
    }

}
