package com.tickerlookup.ui.console;

import com.tickerlookup.model.StockData;
import com.tickerlookup.util.Constant;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartRenderer {

    public void render(StockData data) {
        List<Double> prices = data.prices();
        List<Long> timestamps = data.timestamps();
        
        if (prices.size() < 2) return;

        int width = Constant.CHART_WIDTH;
        int height = Constant.CHART_HEIGHT;

        List<Double> plotData = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            double virtualIndex = (double) i * (prices.size() - 1) / (width - 1);
            int index = (int) virtualIndex;
            double fraction = virtualIndex - index;
            if (index >= prices.size() - 1) {
                plotData.add(prices.get(prices.size() - 1));
            } else {
                double interpolatedPrice = prices.get(index) + fraction * (prices.get(index + 1) - prices.get(index));
                plotData.add(interpolatedPrice);
            }
        }

        double min = Collections.min(plotData);
        double max = Collections.max(plotData);
        if (max == min) max = min + 1.0;

        int[] yPositions = new int[width];
        for (int x = 0; x < width; x++) {
            yPositions[x] = (int) ((plotData.get(x) - min) / (max - min) * (height - 1));
        }

        char[][] chart = new char[height][width];
        String[][] colors = new String[height][width];
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                chart[i][j] = ' ';
                colors[i][j] = Constant.ANSI_RESET;
            }
        }

        for (int x = 0; x < width; x++) {
            int y = yPositions[x];
            int row = height - 1 - y;
            
            char symbol;
            String color;

            if (x < width - 1) {
                int nextY = yPositions[x + 1];
                if (nextY > y) {
                    symbol = '/';
                    color = Constant.ANSI_GREEN;
                } else if (nextY < y) {
                    symbol = '\\';
                    color = Constant.ANSI_RED;
                } else {
                    symbol = '-';
                    color = Constant.ANSI_GREEN;
                }
            } else {
                symbol = '•';
                color = (y >= yPositions[x - 1]) ? Constant.ANSI_GREEN : Constant.ANSI_RED;
            }

            chart[row][x] = symbol;
            colors[row][x] = color;
        }

        System.out.println("\nPrice Movement (Last 24h):");
        for (int i = 0; i < height; i++) {
            double priceAtLevel = max - (i * (max - min) / (height - 1));
            System.out.printf("%8.2f | ", priceAtLevel);
            for (int j = 0; j < width; j++) {
                if (chart[i][j] != ' ') {
                    System.out.print(colors[i][j] + chart[i][j] + Constant.ANSI_RESET);
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        System.out.println("         +" + "-".repeat(width + 1));
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
        String startLabel = timeFormatter.format(Instant.ofEpochSecond(timestamps.get(0)));
        String midLabel = timeFormatter.format(Instant.ofEpochSecond(timestamps.get(timestamps.size() / 2)));
        String endLabel = timeFormatter.format(Instant.ofEpochSecond(timestamps.get(timestamps.size() - 1)));

        int totalSpace = width - startLabel.length() - endLabel.length();
        int leftPadding = (totalSpace - midLabel.length()) / 2;
        int rightPadding = totalSpace - midLabel.length() - leftPadding;

        System.out.println("          " + startLabel + " ".repeat(leftPadding) + midLabel + " ".repeat(rightPadding) + endLabel);
    }
}