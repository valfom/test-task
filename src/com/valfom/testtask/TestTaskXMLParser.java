package com.valfom.testtask;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TestTaskXMLParser {

	public TestTaskXMLParser() {}

	// Загружает объявления в xml по заданному url
	public String getXmlFromUrl(String url) {

		String xml = null;

		try {
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
			
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		return xml;
	}

	// Представляет xml данные в виде интерфейса Document
	public Document getDomElement(String xml) {

		Document document = null;
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		try {

			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			InputSource inputSource = new InputSource();

			inputSource.setCharacterStream(new StringReader(xml));
			document = documentBuilder.parse(inputSource);
			
		} catch (NullPointerException exception) {
			
			exception.printStackTrace();

		} catch (ParserConfigurationException exception) {
			
			exception.printStackTrace();
			
		} catch (SAXException exception) {
			
			exception.printStackTrace();
			
		} catch (IOException exception) {
			
			exception.printStackTrace();
		}

		return document;
	}

	// Получает значение тега
	public String getValue(Element element, String tag) {

		NodeList nodeList = element.getElementsByTagName(tag);

		return this.getElementValue(nodeList.item(0));
	}

	public final String getElementValue(Node elem) {

		Node child;

		if (elem != null) {

			if (elem.hasChildNodes()) {

				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {

					if (child.getNodeType() == Node.TEXT_NODE) {

						return child.getNodeValue();
					}
				}
			}
		}

		return "";
	}

	public final String getPriceValue(Element element) {

		String price = null;

		if (element != null) {

			NodeList nodeList = element.getElementsByTagName(TestTaskListActivity.TAG_PRICE);

			price = getElementAttribute(nodeList.item(0), TestTaskListActivity.ATTRIBUTE_FREE);

			if (price != null) {

				if (price.equals("true")) price = "Бесплатно";
				
			} else {

				Node node = nodeList.item(0);

				if (node != null) {

					NodeList nl = node.getChildNodes();

					Node nodeValue = nl.item(1);
					Node nodeCurrency = nl.item(3);

					price = nodeValue.getTextContent() + " " + nodeCurrency.getTextContent();
				}
			}
		}

		return price;
	}

	// Получает значение атрибута тега
	public String getAttribute(Element element, String tag, String attribute) {

		NodeList nodeList = element.getElementsByTagName(tag);
		
		int index = 0;
		
		if (tag.equals(TestTaskListActivity.TAG_IMAGE)) index = 2;

		return this.getElementAttribute(nodeList.item(index), attribute);
	}
	
	public final String getElementAttribute(Node node, String attribute) {

		if (node != null) {

			NamedNodeMap attribs = node.getAttributes();

			if (attribs != null) {

				Node attr = attribs.getNamedItem(attribute);

				if (attr != null) return attr.getNodeValue();
			}
		}

		return null;
	}

	// Получает количество фотографий в объявлении
	public String getImagesCount(Element element) {
		
		NodeList nodeList = element.getElementsByTagName(TestTaskListActivity.TAG_IMAGES);
		
		return String.valueOf(nodeList.getLength());
	}
}