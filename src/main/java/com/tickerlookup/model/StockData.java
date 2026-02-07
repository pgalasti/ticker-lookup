package com.tickerlookup.model;

import java.util.List;

public record StockData(
    String symbol,
    double currentPrice,
    double changePercent,
    double openPrice,
    long volume,
    String high52,
    String low52,
    List<Double> prices,
    List<Long> timestamps
) {}
