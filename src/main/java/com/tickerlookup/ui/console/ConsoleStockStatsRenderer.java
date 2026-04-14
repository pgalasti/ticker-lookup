package com.tickerlookup.ui.console;

import com.tickerlookup.model.StockData;
import com.tickerlookup.util.Constant;

public class ConsoleStockStatsRenderer implements StockStatsRenderer {

    @Override
    public void render(StockData data) {
        String color = data.changePercent() >= 0 ? Constant.ANSI_GREEN : Constant.ANSI_RED;

        System.out.println(data.companyName());
        System.out.printf("%s: %s$%.2f (%.2f%% since prev close)%s\n",
            data.symbol(), color, data.currentPrice(), data.changePercent(), Constant.ANSI_RESET);

        System.out.printf("%15s  $%-10.2f\n", "Open:", data.openPrice());
        System.out.printf("%15s  $%-10.2f\n", "Prev Close:", data.previousClose());
        System.out.printf("%15s  %-11d\n", "Volume:", data.volume());
        System.out.printf("%15s  %-11s\n", "52 Week High:", data.high52());
        System.out.printf("%15s  %-11s\n", "52 Week Low:", data.low52());
    }
}
