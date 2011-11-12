/**
 *
 */
package com.android.jekis.exchangeinfo;

/**
 * @author Jekis
 *
 */
public abstract class ExchangeSiteParser extends SiteParser {

	/**
	 * Storage for Exchange page content.
	 * */
	protected String exchangePageContent = "";

	/**
	 * Storage for the exchange page uri.
	 * */
	protected String exchangePageUri;

	/**
	 * Array of currencies, that are presented on the page to be parsed.
	 * */
	protected String[] currencies;

	/**
	 * Constructor
	 *
	 * @param server
	 * 	Server's name with protocol (http://example.com).
	 * @param exchangePageUri
	 * 	Exchange page uri (page.php?param=value).
	 * @param currencies
	 * 	Array of currencies, that are presented on the page to be parsed.
	 */
	public ExchangeSiteParser(String server, String exchangePageUri, String[] currencies) {
		super(server);
		this.exchangePageUri = exchangePageUri;
		this.currencies = currencies;
	}

	/**
	 * Getter for exchangePageContent.
	 * */
	public String getExchangePage() {
		return getExchangePage(false);
	}

	/**
	 * Refresh page content and return it.
	 * */
	public String getExchangePage(boolean refresh) {
		if (refresh) {
			exchangePageContent = getPageContent(exchangePageUri);
		}
		return exchangePageContent;
	}

	/**
	 * Derrived class should return array of rows from content,
	 * that contain all bank information (name, prices).
	 *
	 * @param content
	 * 	Exchange page content.
	 * */
	public abstract String[] extractBankRows(String content);

	/**
	 * Derrived class should return bank name from row.
	 *
	 * @param row
	 * 	Contains bank's data.
	 * */
	public abstract String extractBankName(String row);

	/**
	 * Derrived class should return array of prices for the currency.
	 *
	 * @param row
	 * 	Contains bank's data.
	 * @param currencyCode
	 *  Currency code.
	 * @return
	 * 	prices[0] - currency buy price
	 * 	prices[1] - currency sell price
	 * */
	public abstract String[] extractBankCurrencyPrices(String row, String currencyCode);

	/**
	 * Create XML without refresh.
	 * */
	public String createXml() {
		return createXml(false);
	}

	/**
	 * Build string, that represents XML file content.
	 *
	 * @param refresh
	 * 	Determines whether page content will be refreshed or not.
	 * @return
	 * XML with all exchange data.
	 * */
	public String createXml(boolean refresh) {
		if (refresh || exchangePageContent.length() < 1) {
			getExchangePage(true);
		}

		// Start to build an XML string
		StringBuilder xml = new StringBuilder();

		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		xml.append("<banks>\n");

		for (String bankRow : extractBankRows(exchangePageContent)) {
			String bankName = extractBankName(bankRow);
			xml.append("\t<bank name=\"" + bankName + "\">\n");

			for (String code : currencies) {
				String[] prices = extractBankCurrencyPrices(bankRow, code);
				xml.append("\t\t<currency code=\"" + code + "\" buy=\"" + prices[0] + "\" sell=\"" + prices[1] + "\" />\n");
			}

			xml.append("\t</bank>\n");
		}

		xml.append("</banks>\n");

		return xml.toString();
	}
}
