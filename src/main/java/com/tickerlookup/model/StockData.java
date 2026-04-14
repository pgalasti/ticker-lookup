package com.tickerlookup.model;

import java.util.List;

public record StockData(
    String symbol,
    String companyName,
    double currentPrice,
    double changePercent,
    double openPrice,
    double previousClose,
    long volume,
    String high52,
    String low52,
    List<Double> prices,
    List<Long> timestamps
) {}