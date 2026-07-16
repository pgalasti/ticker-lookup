package com.tickerlookup.ui.console;

import com.tickerlookup.model.StockData;
import com.tickerlookup.model.TimePeriod;

public interface StockStatsRenderer {
    void render(StockData data, TimePeriod period);
}
