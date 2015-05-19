package de.hybris.platform.sap.sapcarintegration.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public interface CarConnectionService {

	public abstract HttpURLConnection createConnection(String absoluteUri, String contentType,
			String httpMethod) throws IOException, MalformedURLException;

}