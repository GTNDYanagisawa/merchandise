package com.hybris.datahub.core.rest.client;

import de.hybris.bootstrap.annotations.IntegrationTest;

import java.util.HashMap;

import org.junit.Test;


@IntegrationTest
public class ImpexDataImportClientIntegrationTest
{
	private static final String sContentDisposition = "attachment; filename=\"noFile\"";
	private static final String tenantID = "master";

	private final ImpexDataImportClient impexDataImportClient = new ImpexDataImportClient();

	@Test(expected = IllegalStateException.class)
	public void testCallBackWithInvalidURL()
	{
		impexDataImportClient.readData("fake_url", requestHeaders());
	}

	private HashMap<String, String> requestHeaders()
	{
		final HashMap<String, String> headers = new HashMap<>();
		headers.put("Content-Disposition", sContentDisposition);
		headers.put("tenantID", tenantID);
		return headers;
	}
}
