package com.tickerlookup.client;

import com.tickerlookup.model.StockData;
import com.tickerlookup.model.TimePeriod;
import java.util.Optional;

public interface StockDataService {
    Optional<StockData> fetchStockData(String symbol, TimePeriod period) throws Exception;
}
