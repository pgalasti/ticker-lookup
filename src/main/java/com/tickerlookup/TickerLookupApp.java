package com.tickerlookup;

import com.tickerlookup.client.StockDataService;
import com.tickerlookup.client.YahooFinanceService;
import com.tickerlookup.model.StockData;
import com.tickerlookup.model.TimePeriod;
import com.tickerlookup.ui.console.ChartRenderer;
import com.tickerlookup.ui.console.ConsoleStockStatsRenderer;
import com.tickerlookup.ui.console.StockStatsRenderer;
import com.tickerlookup.util.Constant;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
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

    @ArgGroup(exclusive = true)
    private PeriodFlags periodFlags = new PeriodFlags();

    /** Mutually-exclusive time-window flags. If none is given, {@link TimePeriod#DAY} is used. */
    static class PeriodFlags {
        @Option(names = "-d", description = "Current day (default).")
        boolean day;
        @Option(names = "-5d", description = "Last 5 days.")
        boolean fiveDay;
        @Option(names = "-30d", description = "Last 30 days.")
        boolean thirtyDay;
        @Option(names = "-mtd", description = "Month to date.")
        boolean mtd;
        @Option(names = "-ytd", description = "Year to date.")
        boolean ytd;

        TimePeriod toPeriod() {
            if (fiveDay) return TimePeriod.FIVE_DAY;
            if (thirtyDay) return TimePeriod.THIRTY_DAY;
            if (mtd) return TimePeriod.MTD;
            if (ytd) return TimePeriod.YTD;
            return TimePeriod.DAY;
        }
    }

    private final StockDataService dataService = new YahooFinanceService();
    private final StockStatsRenderer statsRenderer = new ConsoleStockStatsRenderer();
    private final ChartRenderer chartRenderer = new ChartRenderer();

    @Override
    public Integer call() throws Exception {
        TimePeriod period = periodFlags.toPeriod();
        System.out.println("Looking up " + symbol.toUpperCase() + " (" + period.label() + ")...");

        try {
            var dataOpt = dataService.fetchStockData(symbol, period);

            if (dataOpt.isEmpty()) {
                System.err.println(Constant.ANSI_RED + "Ticker not found" + Constant.ANSI_RESET);
                return 1;
            }

            StockData data = dataOpt.get();

            statsRenderer.render(data, period);

            if ("yes".equalsIgnoreCase(showChart) || "true".equalsIgnoreCase(showChart)) {
                chartRenderer.render(data, period);
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