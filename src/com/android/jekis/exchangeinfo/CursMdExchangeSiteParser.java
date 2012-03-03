package com.android.jekis.exchangeinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CursMdExchangeSiteParser extends ExchangeSiteParser {

    // Match piece of html with bank rows.
    private static final String ROWS_PIECE = "<table[^>]+tabelBankValute[^>]+>.+?<\\/table>";
    // Match for bank row in 'bank rows' context.
    private static final String ROW = "<tr[^>]*>.+?<\\/tr>";
    // Match bank's name in context of a row.
    private static final String BANK_NAME = "bank_name\"><a[^>]*>([^<]+)<";
    // Match currency price.
    private static final String CURRENCY_PRICE = "<td>([^<]+)<\\/td>";

    /**
     * Constructor
     */
    public CursMdExchangeSiteParser() {
        super("http://curs.md/ru", "curs_valutar_banci?CotDate=02.12.2011", new String[] { "USD", "EUR", "RON", "RUB", "UAH" });
    }

    @Override
    public String[] extractBankRows(String content) {
        // Match a piece of html containing bank rows.
        Pattern pattern = Pattern.compile(ROWS_PIECE, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        matcher.find();
        String rowsPiece = matcher.group();

        // Match rows and store them to the array.
        pattern = Pattern.compile(ROW, Pattern.DOTALL);
        matcher = pattern.matcher(rowsPiece);

        ArrayList<String> rowsList = new ArrayList<String>();
        int counter = 0;
        while (matcher.find()) {
            // Skip tables's head row and National Banc row.
            if (counter > 2) {
                rowsList.add(matcher.group());
            }
            counter++;
        }
        // Convert ArrayList to Array.
        String[] rows = new String[rowsList.size()];
        return rowsList.toArray(rows);
    }

    @Override
    public String extractBankName(String row) {
        Pattern pattern = Pattern.compile(BANK_NAME);
        Matcher matcher = pattern.matcher(row);
        if (matcher.find()) {
            return matcher.group(1);
        }
        // Return default bank name if missing.
        return BANK_NONAME;
    }

    @Override
    public String[] extractBankCurrencyPrices(String row, String currencyCode) {
        // Define column number for currency buy price.
        Map<String, Integer> currencyBuyPriceCol = new HashMap<String, Integer>();
        currencyBuyPriceCol.put("EUR", 1);
        currencyBuyPriceCol.put("USD", 3);
        currencyBuyPriceCol.put("RON", 5);
        currencyBuyPriceCol.put("RUB", 7);
        currencyBuyPriceCol.put("UAH", 9);
        int buyColIndex = currencyBuyPriceCol.get(currencyCode) - 1;

        Pattern pattern = Pattern.compile(CURRENCY_PRICE);
        Matcher matcher = pattern.matcher(row);

        String[] prices = new String[2];
        // Skip columns that are before.
        while (buyColIndex > 0) {
            matcher.find();
            buyColIndex--;
        }
        // Read prices from currency columns.
        // Buy price.
        matcher.find();
        prices[0] = matcher.group(1);
        if (!prices[0].matches("[0-9.,]+")) {
            prices[0] = "0";
        }
        // Sell price.
        matcher.find();
        prices[1] = matcher.group(1);
        if (!prices[1].matches("[0-9.,]+")) {
            prices[1] = "0";
        }

        return prices;
    }
}
