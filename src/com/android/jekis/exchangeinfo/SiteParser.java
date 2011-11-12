/**
 *
 */
package com.android.jekis.exchangeinfo;

/**
 * @author Jekis
 *
 * Requires SimpleHttpRequest class.
 */
public class SiteParser {

	/**
	 * Server name.
	 *
	 * @example
	 * 	"http://example.com"
	 * */
	protected String server;

	/**
	 * Constructor.
	 * */
	public SiteParser(String server) {
		this.server = server;
	}

	/**
	 * Gets page html form server front page.
	 * */
	public String getPageContent() {
		return getPageContent("");
	}

	/**
	 * Gets page html.
	 *
	 * @param uri
	 * 	Page uri (index.php?foo=bar).
	 * */
	public String getPageContent(String uri) {
		String url = server;
		if (uri.length() > 0) {
			url += "/" + uri;
		}
        SimpleHttpRequest request = new SimpleHttpRequest();
        return request.get(url);
	}
}
