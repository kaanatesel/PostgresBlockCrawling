package com.blogcrawling.crawlingmodul;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.blogcrawling.api.error.ConnectionException;
import com.blogcrawling.api.error.WrongTypeDocumentExpception;

public class Crawler {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	private Document htmlDoc;

	private List<String> crawl(String url) {

		try {
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
			Document htmlDocument = connection.get();
			this.htmlDoc = htmlDocument;

			if (connection.response().statusCode() == 200) {
				System.out.println("\n**Visiting** Received web page at " + url);
			} else {
				System.out.println("**Failure** Connection Error with code = " + connection.response().statusCode());
			}

			if (!connection.response().contentType().contains("text/html")) {
				System.out.println("**Failure** Retrieved something other than HTML");
			}

			Elements linksOnPage = htmlDoc.select("a[href]");
			System.out.println("Found (" + linksOnPage.size() + ") links");

			for (Element link : linksOnPage) {
				this.links.add(link.absUrl("href"));
			}
		} catch (Exception e) {
		}

		return links;
	}

	public List<String> searchParam(String baseUrl, String postgresParam) {
		List<String> urlsWithParam = new LinkedList<String>();

		List<String> links = crawl(baseUrl);
		System.out.println("Searching for the word " + postgresParam + "...");

		try {
			for (String url : links) {
				Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
				Document htmlDocument = connection.get();
				String bodyText = htmlDocument.body().text();
				if (bodyText.toLowerCase().contains(postgresParam.toLowerCase())) {
					urlsWithParam.add(url);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Finished searching.");
		return urlsWithParam;
	}
}
