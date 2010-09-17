package com.tah.im;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

public class APIUtil {
	
	private static final String API_URL = "http://localhost:9000/api/";
	private static final String API_PASS = "TestPass";
	
	public static void main(String[] args) {
		String kangaroo = "4c2cb43160adf3055c97d061";
//		createConvo("4c2cb43160adf3055c97d061", "NEW22");
		
		createAnswer("4c9338e23d9ebce5dca7d477", kangaroo, null, "Test comment3");
	}
	
	public static String createConvo(String talkerId, String title) {
		NameValuePair[] params = prepareParams("title", title, "talkerId", talkerId);
		GetMethod get = new GetMethod(API_URL+"createconvo");
		get.setQueryString(params);
		
		return executeCall(get);
	}
	
	public static String createAnswer(String convoId, String talkerId, String parentId, String text) {
		NameValuePair[] params = prepareParams("convoId", convoId, "talkerId", talkerId,
				"parentId", parentId, "text", text);
		GetMethod get = new GetMethod(API_URL+"createanswer");
		get.setQueryString(params);
		
		return executeCall(get);
	}
	
	
	private static String executeCall(GetMethod get) {
		try {
			HttpClient client = createClient();
			
			int statusCode = client.executeMethod(get);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Bad response! "+statusCode);
			}
			
			String response = get.getResponseBodyAsString();
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static NameValuePair[] prepareParams(String... values) {
		int size = values.length/2 + 1;
		NameValuePair[] params = new NameValuePair[size];
		
		params[0] = new NameValuePair("pass", API_PASS);
		for (int i=1; i<params.length; i++) {
			params[i] = new NameValuePair(values[2*i-2], values[2*i-1]);
		}
				
		return params;
	}

	//create and initialize Http client
	public static HttpClient createClient() {
		HttpClient client = new HttpClient();
		
		client.getParams().setSoTimeout(15000);
		client.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
		return client;
	}

}
