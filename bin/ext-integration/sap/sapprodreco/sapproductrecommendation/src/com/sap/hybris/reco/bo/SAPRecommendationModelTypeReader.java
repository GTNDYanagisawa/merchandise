/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sap.hybris.reco.bo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.hybris.reco.be.RecommendationModelTypeManager;
import com.sap.hybris.reco.dao.SAPRecommendationModelType;


/**
 * To fetch a list of model types from PRI using OData service
 */
public class SAPRecommendationModelTypeReader
{
	private static final String ENTITY_NAME = "ModelTypes";
	private static final String MODEL_TYPE_ID = "ModelTypeId";
	private static final String MODEL_TYPE_DESCRIPTION = "ModelTypeDescription";

	private static final String SELECT_FIELDS = MODEL_TYPE_ID + "," + MODEL_TYPE_DESCRIPTION;
	private static final String ORDER_BY = "ModelTypeId";
	protected RecommendationModelTypeManager accessBE;



	/**
	 * @return a list of SAPRecommendationModelType
	 * @throws ODataException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public List<SAPRecommendationModelType> getAllRecommendationModelTypes() throws ODataException, URISyntaxException,
			IOException
	{
		final ODataFeed feed = accessBE.getModelTypes(ENTITY_NAME, null, SELECT_FIELDS, null, ORDER_BY);
		final List<ODataEntry> foundEntries = feed.getEntries();
		return extractRecommendationModelTypes(foundEntries);
	}

	/**
	 * 
	 */
	private List<SAPRecommendationModelType> extractRecommendationModelTypes(final List<ODataEntry> foundEnties)
	{
		final List<SAPRecommendationModelType> recommendationModelTypes = new ArrayList<SAPRecommendationModelType>();
		if (foundEnties != null)
		{
			final Iterator<ODataEntry> iter = foundEnties.iterator();

			while (iter.hasNext())
			{
				final ODataEntry entry = iter.next();
				final SAPRecommendationModelType recommendationModelType = extractRecommendationModelType(entry);
				recommendationModelTypes.add(recommendationModelType);
			}
		}
		return recommendationModelTypes;
	}

	/**
	 * 
	 */
	private SAPRecommendationModelType extractRecommendationModelType(final ODataEntry entry)
	{
		final SAPRecommendationModelType recommendationModel = new SAPRecommendationModelType();
		final Map<String, Object> props = entry.getProperties();
		if (!props.equals(null))
		{
			recommendationModel.setId(props.get(MODEL_TYPE_ID).toString());
			recommendationModel.setDescription(props.get(MODEL_TYPE_DESCRIPTION).toString());
		}
		return recommendationModel;
	}

	/**
	 * @return accessBE
	 */
	public RecommendationModelTypeManager getAccessBE()
	{
		return accessBE;
	}

	/**
	 * @param accessBE
	 */
	public void setAccessBE(RecommendationModelTypeManager accessBE)
	{
		this.accessBE = accessBE;
	}

}
