package com.sap.hybris.reco.be;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.hybris.reco.common.util.HMCConfigurationReader;
import com.sap.hybris.reco.common.util.ODataClientService;


/**
 * 
 */
public interface RecommendationModelTypeManager extends BackendBusinessObject
{
	/**
	 * Retrieve the list of model types from a remote server
	 * 
	 * @param entityName
	 * @param expand
	 * @param select
	 * @param filter
	 * @param orderby
	 * 
	 * @return Returns an ODataFeed with the list of model types
	 * @throws ODataException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public ODataFeed getModelTypes(String entityName, String expand, String select, String filter, String orderby)
			throws ODataException, URISyntaxException, IOException;

	/**
	 * Retrieve a single model type
	 * 
	 * @return Return an ODataEntry with a single model type
	 */
	public ODataEntry getModelType();

	/**
	 * @return clientService
	 */
	public ODataClientService getClientService();

	/**
	 * @param clientService
	 */
	public void setClientService(ODataClientService clientService);

	/**
	 * @return HMCConfiguration
	 */
	public HMCConfigurationReader getConfiguration();

	/**
	 * @param configuration
	 */
	public void setConfiguration(HMCConfigurationReader configuration);

}
