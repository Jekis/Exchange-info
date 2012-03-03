package com.android.jekis.exchangeinfo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.util.Log;

public abstract class ExchangeSiteParser extends SiteParser {
    /**
     * Bank name if was not matched in a row.
     */
    protected static final String BANK_NONAME = "Unknown bank";

    protected static Context context = Exchangeinfo.getContext();

    /**
     * Storage for Exchange page content.
     */
    protected String exchangePageContent = "";

    /**
     * Storage for the exchange page uri.
     */
    protected String exchangePageUri;

    /**
     * Array of currencies, that are presented on the page to be parsed.
     */
    protected String[] currencies;

    /**
     * Constructor
     * 
     * @param server
     *        Server's name with protocol (http://example.com).
     * @param exchangePageUri
     *        Exchange page uri (page.php?param=value).
     * @param currencies
     *        Array of currencies, that are presented on the page to be
     *        parsed.
     */
    public ExchangeSiteParser(String server, String exchangePageUri, String[] currencies) {
        super(server);
        this.exchangePageUri = exchangePageUri;
        this.currencies = currencies;
    }

    /**
     * Getter for exchangePageContent.
     */
    public String getExchangePage() {
        return this.getExchangePage(false);
    }

    /**
     * Refresh page content and return it.
     */
    public String getExchangePage(boolean refresh) {
        if (refresh) {
            this.exchangePageContent = this.getPageContent(this.exchangePageUri);
        }
        return this.exchangePageContent;
    }

    /**
     * Derrived class should return array of rows from content, that contain all
     * bank information (name, prices).
     * 
     * @param content
     *        Exchange page content.
     */
    public abstract String[] extractBankRows(String content);

    /**
     * Derrived class should return bank name from row.
     * 
     * @param row
     *        Contains bank's data.
     */
    public abstract String extractBankName(String row);

    /**
     * Derrived class should return array of prices for the currency.
     * 
     * @param row
     *        Contains bank's data.
     * @param currencyCode
     *        Currency code.
     * @return prices[0] - currency buy price prices[1] - currency sell price
     */
    public abstract String[] extractBankCurrencyPrices(String row, String currencyCode);

    /**
     * Create XML without refresh.
     */
    public String createXml() {
        return this.createXml(false);
    }

    /**
     * Build string, that represents XML file content.
     * 
     * @param refresh
     *        Determines whether page content will be refreshed or not.
     * @return XML with all exchange data.
     */
    public String createXml(boolean refresh) {
        if (refresh || this.exchangePageContent.length() < 1) {
            this.getExchangePage(true);
        }

        // Start to build an XML string
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        // Add date timestamp.
        Date now = new Date();
        long nowTime = now.getTime();
        xml.append("<banks date=\"" + Long.toString(nowTime) + "\">\n");

        for (String bankRow : this.extractBankRows(this.exchangePageContent)) {
            String bankName = this.extractBankName(bankRow);
            xml.append("  <bank name=\"" + bankName.replaceAll("\"", "'") + "\">\n");

            for (String code : this.currencies) {
                String[] prices = this.extractBankCurrencyPrices(bankRow, code);
                xml.append("    <currency code=\"" + code + "\" buy=\"" + prices[0].replaceAll(",", ".") + "\" sell=\"" + prices[1].replaceAll(",", ".")
                        + "\" />\n");
            }

            xml.append("  </bank>\n");
        }

        xml.append("</banks>\n");

        return xml.toString();
    }

    /**
     * Saves xml as a file.
     * 
     * @param xml
     */
    public void saveXml(String xml) {
        Cache.set(context, this.uniqueFileName("Banks.xml"), xml);
    }

    /**
     * @see getXml(refresh)
     */
    public String getXml() {
        return this.getXml(false);
    }

    /**
     * Gets xml from cache(stored file), or gets data from server.
     * 
     * @param refresh
     *        To force update from server.
     * @return xml string.
     */
    public String getXml(boolean refresh) {
        String xml = Cache.get(context, this.uniqueFileName("Banks.xml"), "null");
        if (xml.equals("null") || refresh) {
            xml = this.createXml(true);
            this.saveXml(xml);
        }
        return xml;
    }

    /**
     * Gets xml with banks data and parses it.
     * 
     * @return Date when data was recieved and unsorted list of bank objects.
     */
    public Map<String, Object> getBanksData() {
        return parseXml(this.getXml());
    }

    /**
     * Gets xml with banks data and parses it.
     * 
     * @return Date when data was recieved and sorted list of bank objects.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getBanksData(String sortCurrency, String sortField) {
        Map<String, Object> banksData = this.getBanksData();
        ArrayList<Bank> sortedBanks = sortBanks((ArrayList<Bank>) banksData.get("banks"), sortCurrency, sortField);
        banksData.put("banks", sortedBanks);
        return banksData;
    }

    protected String uniqueFileName(String fileName) {
        String className = this.getClass().getName();
        String[] classNameParts = className.split("\\.");
        className = classNameParts[classNameParts.length - 1];
        return className + fileName;
    }

    protected static ArrayList<Bank> sortBanks(ArrayList<Bank> banks, final String currencyCharcode, String priceField) {
        if (priceField.equals("buyPrice")) {
            // Sort by buy price DESC.
            Collections.sort(banks, new Comparator<Bank>() {
                @Override
                public int compare(Bank b1, Bank b2) {
                    Float price1 = b1.getCurrency(currencyCharcode).buyPrice;
                    Float price2 = b2.getCurrency(currencyCharcode).buyPrice;
                    return Float.compare(price2, price1);
                }
            });
        } else {
            // Sort by buy price ASC.
            Collections.sort(banks, new Comparator<Bank>() {
                @Override
                public int compare(Bank b1, Bank b2) {
                    Float price1 = b1.getCurrency(currencyCharcode).sellPrice;
                    Float price2 = b2.getCurrency(currencyCharcode).sellPrice;
                    return Float.compare(price1, price2);
                }
            });
        }
        return banks;
    }

    /**
     * Parses xml file and builds map with date and all banks data.
     * 
     * @param xml
     *        XML file content.
     */
    public static Map<String, Object> parseXml(String xml) {
        Map<String, Object> xmlMap = new HashMap<String, Object>();
        long xmlDate = 0L;
        ArrayList<Bank> banks = new ArrayList<Bank>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document dom = builder.parse(is);

            // Get the root element.
            Element rootEle = dom.getDocumentElement();
            xmlDate = new Long(rootEle.getAttribute("date"));

            // Get a nodelist of elements.
            NodeList bankList = rootEle.getElementsByTagName("bank");
            if (bankList != null && bankList.getLength() > 0) {
                for (int i = 0; i < bankList.getLength(); i++) {
                    // Get the bank element.
                    Element bankEl = (Element) bankList.item(i);

                    // Get the bank data.
                    String bankName = bankEl.getAttribute("name");
                    Bank bank = new Bank(bankName);
                    // Get bank currencies.
                    NodeList currList = bankEl.getElementsByTagName("currency");
                    if (currList != null && currList.getLength() > 0) {
                        for (int j = 0; j < currList.getLength(); j++) {
                            Element currEl = (Element) currList.item(j);
                            String charcode = currEl.getAttribute("code");
                            Float buyPrice = new Float(currEl.getAttribute("buy"));
                            Float sellPrice = new Float(currEl.getAttribute("sell"));
                            bank.addCurrency(charcode, buyPrice, sellPrice);
                        }
                    }
                    banks.add(bank);
                }
            }
        } catch (Exception e) {
            Log.d("ExchangeSiteParser", "Esception parsing xml.");
        }
        xmlMap.put("date", xmlDate);
        xmlMap.put("banks", banks);
        return xmlMap;
    }
}
