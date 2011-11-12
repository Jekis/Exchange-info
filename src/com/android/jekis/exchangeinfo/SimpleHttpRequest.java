/**
 *
 */
package com.android.jekis.exchangeinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

/**
 * @author Jekis
 *
 * Simply executes web server requests.
 */
public class SimpleHttpRequest {

	protected HttpResponse response;

	/**
	 * Creates and executes HTTP GET request.
	 *
	 * Stores response in class property.
	 * */
	protected SimpleHttpRequest executeGetRequest(String url) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		this.response = client.execute(request);
		return this;
	}

	/**
	 * Converts response object to string.
	 * */
	protected String readStringFromResponse() throws IllegalStateException, IOException {
		InputStream in = this.response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		StringBuilder str = new StringBuilder();

		String line;
		while((line = reader.readLine()) != null) {
		    str.append(line);
		}
		in.close();
		return str.toString();
	}

	/**
	 * GET request wrapper.
	 *
	 * @param url
	 * 	Page url.
	 * @return
	 *  Page content.
	 * */
	public String get(String url) {
		String responseString = "";
		try {
			responseString = executeGetRequest(url).readStringFromResponse();
		} catch (Exception e) {
			Log.e("SimpleHttpRequest", e.getMessage());
		}
		return responseString;
	}
}
