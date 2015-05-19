package com.sap.hybris.reco.be.cei;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.hybris.reco.be.RecommendationModelTypeManager;
import com.sap.hybris.reco.common.util.HMCConfigurationReader;
import com.sap.hybris.reco.common.util.ODataClientService;


/**
 * 
 */
public class RecommendationModelTypeManagerCEI implements RecommendationModelTypeManager
{
	protected ODataClientService clientService;
	protected HMCConfigurationReader configuration;

	@Override
	public void initBackendObject() throws BackendException
	{
		// TODO Auto-generated method stub
		configuration.loadPRIConfiguration();

	}

	@Override
	public void destroyBackendObject()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public ODataFeed getModelTypes(String entityName, String expand, String select, String filter, String orderby)
			throws ODataException, URISyntaxException, IOException
	{
		configuration.loadPRIConfiguration();
		this.clientService.createService(configuration.getUser(), configuration.getPassword());

		ODataFeed feed = this.clientService.readFeed(configuration.getPath(), ODataClientService.APPLICATION_XML, entityName,
				expand, select, filter, orderby);

		return feed;
	}

	@Override
	public ODataEntry getModelType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ODataClientService getClientService()
	{
		return clientService;
	}

	public void setClientService(ODataClientService clientService)
	{
		this.clientService = clientService;
	}

	public HMCConfigurationReader getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(HMCConfigurationReader configuration)
	{
		this.configuration = configuration;
	}
}
