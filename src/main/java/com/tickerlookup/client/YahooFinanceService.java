package com.tickerlookup.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickerlookup.model.StockData;
import com.tickerlookup.util.Constant;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class YahooFinanceService implements StockDataService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Optional<StockData> fetchStockData(String symbol) throws Exception {
        String url = String.format("https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=15m&range=1d", symbol);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", Constant.USER_AGENT)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            return Optional.empty();
        } else if (response.statusCode() != 200) {
            throw new RuntimeException("Error fetching data: " + response.statusCode());
        }

        JsonNode root = mapper.readTree(response.body());
        JsonNode result = root.path("chart").path("result").get(0);
        
        if (result == null || result.isMissingNode()) {
            return Optional.empty();
        }

        JsonNode timestampsNode = result.path("timestamp");
        List<Long> rawTimestamps = new ArrayList<>();
        for (JsonNode ts : timestampsNode) {
            if (ts.isNumber()) rawTimestamps.add(ts.asLong());
        }

        JsonNode quoteNode = result.path("indicators").path("quote").get(0);
        JsonNode closeNode = quoteNode.path("close");
        JsonNode openNode = quoteNode.path("open");
        List<Double> prices = new ArrayList<>();
        List<Long> syncedTimestamps = new ArrayList<>();

        for (int i = 0; i < closeNode.size(); i++) {
            JsonNode price = closeNode.get(i);
            if (price.isNumber() && i < rawTimestamps.size()) {
                prices.add(price.asDouble());
                syncedTimestamps.add(rawTimestamps.get(i));
            }
        }

        if (prices.isEmpty()) return Optional.empty();

        double currentPrice = prices.get(prices.size() - 1);

        // Find the first non-null open price from the intraday open array
        double openPrice = 0;
        for (JsonNode o : openNode) {
            if (o.isNumber()) {
                openPrice = o.asDouble();
                break;
            }
        }

        JsonNode meta = result.path("meta");
        String companyName = meta.path("longName").isMissingNode() ?
                             meta.path("shortName").asText("N/A") :
                             meta.path("longName").asText();

        // Fall back to regularMarketOpen from meta if open array was empty
        if (openPrice == 0) {
            openPrice = meta.path("regularMarketOpen").asDouble(prices.get(0));
        }

        double previousClose = meta.path("chartPreviousClose").asDouble(openPrice);
        double changePercent = ((currentPrice - previousClose) / previousClose) * 100;
        
        long volume = 0;
        JsonNode volumeNode = result.path("indicators").path("quote").get(0).path("volume");
        for (JsonNode v : volumeNode) {
            if (v.isNumber()) volume += v.asLong();
        }

        String high52 = meta.has("fiftyTwoWeekHigh") ? String.format("$%.2f", meta.get("fiftyTwoWeekHigh").asDouble()) : "N/A";
        String low52 = meta.has("fiftyTwoWeekLow") ? String.format("$%.2f", meta.get("fiftyTwoWeekLow").asDouble()) : "N/A";

        return Optional.of(new StockData(
            symbol.toUpperCase(), companyName, currentPrice, changePercent, openPrice, previousClose, volume, high52, low52, prices, syncedTimestamps
        ));
    }
}