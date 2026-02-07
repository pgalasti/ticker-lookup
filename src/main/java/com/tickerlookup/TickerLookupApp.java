package com.tickerlookup;

import com.tickerlookup.client.StockDataService;
import com.tickerlookup.client.YahooFinanceService;
import com.tickerlookup.model.StockData;
import com.tickerlookup.ui.console.ChartRenderer;
import com.tickerlookup.ui.console.ConsoleStockStatsRenderer;
import com.tickerlookup.ui.console.StockStatsRenderer;
import com.tickerlookup.util.Constant;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "tl", mixinStandardHelpOptions = true, version = "1.0",
        description = "Looks up stock prices and displays a simple ASCII chart.",
        subcommands = { CommandLine.HelpCommand.class })
public class TickerLookupApp implements Callable<Integer> {

    @Parameters(index = "0", description = "The stock ticker symbol (e.g., AAPL, TSLA).")
    private String symbol;

    @CommandLine.Option(names = {"-sc", "--showChart"}, description = "Whether to show the ASCII chart (yes/no).", defaultValue = "yes")
    private String showChart;

    private final StockDataService dataService = new YahooFinanceService();
    private final StockStatsRenderer statsRenderer = new ConsoleStockStatsRenderer();
    private final ChartRenderer chartRenderer = new ChartRenderer();

    @Override
    public Integer call() throws Exception {
        System.out.println("Looking up " + symbol.toUpperCase() + "...");

        try {
            var dataOpt = dataService.fetchStockData(symbol);

            if (dataOpt.isEmpty()) {
                System.err.println(Constant.ANSI_RED + "Ticker not found" + Constant.ANSI_RESET);
                return 1;
            }

            StockData data = dataOpt.get();
            
            statsRenderer.render(data);
            
            if ("yes".equalsIgnoreCase(showChart) || "true".equalsIgnoreCase(showChart)) {
                chartRenderer.render(data);
            }

        } catch (Exception e) {
            System.err.println(Constant.ANSI_RED + "An error occurred: " + e.getMessage() + Constant.ANSI_RESET);
            return 1;
        }

        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TickerLookupApp()).execute(args);
        System.exit(exitCode);
    }
}