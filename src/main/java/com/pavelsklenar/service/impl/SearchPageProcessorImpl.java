package com.pavelsklenar.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.pavelsklenar.domain.SearchPage;
import com.pavelsklenar.domain.SearchResult;
import com.pavelsklenar.service.SearchPageProcessor;

@Component
public class SearchPageProcessorImpl implements SearchPageProcessor {

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(SearchPageProcessorImpl.class);

	public SearchPageProcessorImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pavelsklenar.service.impl.SearchPageProcessor#process(com.pavelsklenar
	 * .domain.SearchPage)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pavelsklenar.service.impl.SearchPageProcessor#processSearch(com.
	 * pavelsklenar.domain.SearchPage)
	 */
	public List<SearchResult> processSearch(SearchPage searchPageToProcess)
			throws Exception {
		List<SearchResult> result = new ArrayList<SearchResult>();
		if (searchPageToProcess.isEnabled()) {
			WebDriver driver = new HtmlUnitDriver(
					searchPageToProcess.isJavascriptEnabled());
			URI siteBase = new URI(searchPageToProcess.getUrl());
			try {
				driver.get(siteBase.toString());
				LOG.debug("Page found {}", driver.getPageSource());
				List<WebElement> searchResultsElements = driver.findElements(By
						.xpath(searchPageToProcess.getXpathToListOfResults()));
				LOG.info("Number of result found: {}",
						searchResultsElements.size());
				SearchResult searchResult = null;
				List<String> omitClassesInSearchResultAsList = searchPageToProcess
						.getOmitClassesInSearchResultAsList();
				for (WebElement webElement : searchResultsElements) {
					if (shouldBeOmitted(webElement,
							omitClassesInSearchResultAsList)) {
						LOG.info("Element will omitted due to the present element.");
						continue;
					}
					searchResult = new SearchResult(searchPageToProcess);
					LOG.debug("Trying to parse this element: {}",
							webElement.getText());

					searchResult.setPrice(getTextIfExistsFromElement(
							webElement, searchPageToProcess.getXpathToPrice()));
					searchResult.setTitle(getTextIfExistsFromElement(
							webElement, searchPageToProcess.getXpathToTitle()));
					searchResult.setDescription(getTextIfExistsFromElement(
							webElement,
							searchPageToProcess.getXpathToDescription()));
					searchResult.setUrl(getAttributeIfExistsFromElement(
							webElement, searchPageToProcess.getXpathToUrl(),
							"href"));
					searchResult.setImageUrl(getAttributeIfExistsFromElement(
							webElement, searchPageToProcess.getXpathToImage(),
							"src"));
					if (searchResult.getUrl() == null) {
						LOG.info(
								"SearchResult will be skipped due to the empty url: {}",
								searchResult);
					} else {
						LOG.info("Found element: {}", searchResult.toString());
						result.add(searchResult);
					}

				}
			} catch (Exception e) {
				throw e;
			} finally {
				driver.close();
			}
		}
		return result;
	}

	private boolean shouldBeOmitted(WebElement webElement,
			List<String> omitClassesInSearchResult) {
		String attribute = webElement.getAttribute("class");
		LOG.debug("Search result element contains these class: {}", attribute);
		if (attribute != null && !attribute.isEmpty()) {
			List<String> classAttributesList = Arrays.asList(attribute
					.split(" "));
			for (String existingAttribute : classAttributesList) {
				if (omitClassesInSearchResult.contains(existingAttribute)) {
					LOG.debug(
							"Existing class attribute {} was found in class attributes to be omitted.",
							existingAttribute);
					return true;
				}
			}
		}
		return false;
	}

	private String getTextIfExistsFromElement(WebElement webElement,
			String xpathToElement) {
		if (webElement == null || xpathToElement == null) {
			return null;
		}
		try {
			return webElement.findElement(By.xpath(xpathToElement)).getText();
		} catch (Exception e) {
			LOG.warn("Xpath {} cannot be located in element {}",
					xpathToElement, webElement);
			return null;
		}
	}
	
	private String getAttributeIfExistsFromElement(WebElement webElement,
			String xpathToElement, String attribute) {
		if (webElement == null || attribute == null || xpathToElement == null) {
			return null;
		}
		try {
			return webElement.findElement(By.xpath(xpathToElement))
					.getAttribute(attribute);
		} catch (Exception e) {
			LOG.warn("Xpath {} cannot be located in element {}",
					xpathToElement, webElement);
			return null;
		}
	}

}