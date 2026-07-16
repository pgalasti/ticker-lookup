package com.tickerlookup.ui.console;

import com.tickerlookup.model.StockData;
import com.tickerlookup.model.TimePeriod;
import com.tickerlookup.util.Constant;

public class ConsoleStockStatsRenderer implements StockStatsRenderer {

    @Override
    public void render(StockData data, TimePeriod period) {
        String color = data.changePercent() >= 0 ? Constant.ANSI_GREEN : Constant.ANSI_RED;
        boolean isDay = period == TimePeriod.DAY;
        String changeLabel = isDay ? "since prev close" : "over " + period.label().toLowerCase();
        String openLabel = isDay ? "Open:" : "Period Open:";
        String prevCloseLabel = isDay ? "Prev Close:" : "Prior Close:";

        System.out.println(data.companyName());
        System.out.printf("%s: %s$%.2f (%.2f%% %s)%s\n",
            data.symbol(), color, data.currentPrice(), data.changePercent(), changeLabel, Constant.ANSI_RESET);

        System.out.printf("%15s  $%-10.2f\n", openLabel, data.openPrice());
        System.out.printf("%15s  $%-10.2f\n", prevCloseLabel, data.previousClose());
        System.out.printf("%15s  %-11d\n", "Volume:", data.volume());
        System.out.printf("%15s  %-11s\n", "52 Week High:", data.high52());
        System.out.printf("%15s  %-11s\n", "52 Week Low:", data.low52());
    }
}
