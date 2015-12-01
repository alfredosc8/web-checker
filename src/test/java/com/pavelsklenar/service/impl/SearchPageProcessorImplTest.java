package com.pavelsklenar.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.pavelsklenar.domain.SearchPage;
import com.pavelsklenar.domain.SearchResult;

import static com.github.tomakehurst.wiremock.client.WireMock.*;



public class SearchPageProcessorImplTest {

	private SearchPageProcessorImpl searchPageProcessor;
	private static final Logger LOG = LoggerFactory
			.getLogger(SearchPageProcessorImplTest.class);
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089);

	@Before
	public void setUp() throws Exception {
		searchPageProcessor = new SearchPageProcessorImpl();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSeznam() throws Exception {
		SearchPage searchPage = new SearchPage("sreality",
				"http://www.sreality.cz/hledani/prodej/domy/ceske-budejovice");
		searchPage
				.setXpathToListOfResults(".//*[contains(concat(' ',normalize-space(@class),' '),' property ')]");
		searchPage
				.setXpathToImage(".//*[contains(concat(' ',normalize-space(@class),' '),' img ')]");
		searchPage
				.setXpathToDescription(".//*[contains(concat(' ',normalize-space(@class),' '),' locality ')]");
		searchPage
				.setXpathToUrl(".//*[contains(concat(' ',normalize-space(@class),' '),' title ')]");
		searchPage
				.setXpathToTitle(".//*[contains(concat(' ',normalize-space(@class),' '),' title ')]");
		searchPage
				.setXpathToPrice(".//*[contains(concat(' ',normalize-space(@class),' '),' price ')]");
		searchPage.setOmitClassesInSearchResult("paging");
		List<SearchResult> processed = searchPageProcessor
				.processSearch(searchPage);

		LOG.info("Number of processed result from page {}: {}",
				searchPage.getUrl(), processed.size());

		for (SearchResult searchResult : processed) {
			LOG.info("Found result: {}", searchResult);
		}
	}

	@Test
	public void testJihoceskeReality() throws Exception {
		SearchPage searchPage = new SearchPage(
				"jihoceskereality",
				"http://jiho.ceskereality.cz/prodej/rodinne-domy/ceske-budejovice/ceskobudejovicko/nejnovejsi");
		searchPage
				.setXpathToListOfResults(".//*[contains(concat(' ',normalize-space(@class),' '),' div_nemovitost ')]");
		searchPage.setXpathToImage("div[1]/div/a/img[1]");
		searchPage.setXpathToDescription("div[2]/div/small");
		searchPage
				.setXpathToUrl(".//*[contains(concat(' ',normalize-space(@class),' '),' nemo ')]");
		searchPage
				.setXpathToTitle(".//*[contains(concat(' ',normalize-space(@class),' '),' nemo ')]");
		searchPage
				.setXpathToPrice(".//*[contains(concat(' ',normalize-space(@class),' '),' cena ')]");
		searchPage
				.setOmitClassesInSearchResult("topovana, list_navigation, top_vypis2");
		List<SearchResult> processed = searchPageProcessor
				.processSearch(searchPage);

		LOG.info("Number of processed result from page {}: {}",
				searchPage.getUrl(), processed.size());

		for (SearchResult searchResult : processed) {
			LOG.info("Found result: {}", searchResult);
		}

	}

	@Test
	public void testRemax() throws Exception {
		
		stubFor(get(urlEqualTo("/Remax2.html"))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "text/html; charset=UTF-8")
	                .withBody(readFileFromClassPathAsString("/pages/Remax2.html").getBytes())));
		
		
		
		SearchPage searchPage = new SearchPage(
				"Remax",
				//"http://localhost:8089/Remax.html");
				"http://localhost:8089/Remax2.html");
		//"http://www.remax-czech.cz/reality/vyhledavani/?price_to=4700000&regions[35][3301]=on&regions[35][3308]=on&sale=1&types[6]=on&order_by_published_date=0");
		searchPage
				.setXpathToListOfResults("/html/body/div[1]/div/div/div[1]/div[1]/div/ul/li");
		searchPage
				.setXpathToImage(".//*[contains(concat(' ',normalize-space(@class),' '),' img-rounded ')]");

		searchPage
				.setXpathToDescription(".//*[contains(concat(' ',normalize-space(@class),' '),' estate-address ')]");
		searchPage.setXpathToUrl("div[1]/h3/a");
		searchPage.setXpathToTitle("div[1]/h3/a");
		searchPage
				.setXpathToPrice(".//*[contains(concat(' ',normalize-space(@class),' '),' price ')]");
		searchPage.setOmitClassesInSearchResult("btn-item");
		
		List<SearchResult> processed = searchPageProcessor
				.processSearch(searchPage);
		
		for (SearchResult searchResult : processed) {
			LOG.info("Found result: {}", searchResult);
		}

		LOG.info("Number of processed result from page {}: {}",
				searchPage.getUrl(), processed.size());
		
		Assert.assertEquals(20, processed.size());
	}

	@Test
	public void testCentury21() throws Exception {
		SearchPage searchPage = new SearchPage(
				"Century21",
				"http://www.century21.cz/nemovitosti?search[ptype]=house&search[price_to]=5000000&search[locality][0]=36&search[locality][1]=29");
		searchPage
				.setXpathToListOfResults(".//*[contains(concat(' ',normalize-space(@class),' '),' item ')]");
		searchPage.setXpathToImage("div[1]/a/img");

		searchPage.setXpathToDescription("div[2]/p[1]");
		searchPage.setXpathToUrl("div[2]/h4/a");
		searchPage
				.setXpathToTitle(".//*[contains(concat(' ',normalize-space(@class),' '),' title ')]");
		searchPage
				.setXpathToPrice(".//*[contains(concat(' ',normalize-space(@class),' '),' amount ')]");
		// searchPage
		// .setOmitClassesInSearchResult("btn-item");
		List<SearchResult> processed = searchPageProcessor
				.processSearch(searchPage);

		LOG.info("Number of processed result from page {}: {}",
				searchPage.getUrl(), processed.size());

		for (SearchResult searchResult : processed) {
			LOG.info("Found result: {}", searchResult);
		}

	}

	@Test
	public void testRkStejskal() throws Exception {
		SearchPage searchPage = new SearchPage(
				"RkStejskal",
				"http://rkstejskal.cz/nemovitosti/?cena_do=5000000&kraj=kraj35&okres=okres3301&typ=1&druh=2&radit=datum");
		searchPage
				.setXpathToListOfResults(".//*[contains(concat(' ',normalize-space(@class),' '),' list-group-item ')]");
		searchPage.setXpathToImage("div/div[1]/div/a/img");

		searchPage
				.setXpathToDescription(".//*[contains(concat(' ',normalize-space(@class),' '),' city ')]");
		searchPage.setXpathToUrl("div/div[2]/table/tbody/tr[2]/td[1]/a");
		searchPage
				.setXpathToTitle(".//*[contains(concat(' ',normalize-space(@class),' '),' title ')]");
		searchPage
				.setXpathToPrice(".//*[contains(concat(' ',normalize-space(@class),' '),' price ')]");
		// searchPage
		// .setOmitClassesInSearchResult("btn-item");
		List<SearchResult> processed = searchPageProcessor
				.processSearch(searchPage);

		LOG.info("Number of processed result from page {}: {}",
				searchPage.getUrl(), processed.size());

		for (SearchResult searchResult : processed) {
			LOG.info("Found result: {}", searchResult);
		}

	}
	
	/**
	 * Read content of file on classpath in to String
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	private String readFileFromClassPathAsString(String file) throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(this.getClass()
					.getResourceAsStream(file)));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

}
