package com.android.jekis.exchangeinfo;

public class SiteParser {

    /**
     * Server name.
     * 
     * @example "http://example.com"
     */
    protected String server;

    /**
     * Constructor.
     */
    public SiteParser(String server) {
        this.server = server;
    }

    /**
     * Gets page html form server front page.
     */
    public String getPageContent() {
        return this.getPageContent("");
    }

    /**
     * Gets page html.
     * 
     * @param uri
     *        Page uri (index.php?foo=bar).
     */
    public String getPageContent(String uri) {
        String url = this.server;
        if (uri.length() > 0) {
            url += "/" + uri;
        }
        SimpleHttpRequest request = new SimpleHttpRequest();
        return request.get(url);
    }
}
