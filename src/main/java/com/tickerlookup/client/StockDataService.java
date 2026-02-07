package com.tickerlookup.client;

import com.tickerlookup.model.StockData;
import java.util.Optional;

public interface StockDataService {
    Optional<StockData> fetchStockData(String symbol) throws Exception;
}
