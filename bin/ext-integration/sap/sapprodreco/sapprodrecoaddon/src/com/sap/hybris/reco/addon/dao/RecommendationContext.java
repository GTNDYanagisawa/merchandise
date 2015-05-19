package com.sap.hybris.reco.addon.dao;

import java.util.HashMap;


/**
 * A data structure that holds all the information that requires for fetching recommendations from PRI.
 */
public class RecommendationContext
{
	private String appAnchor;

	private String modelType;
	private HashMap<String, String> leadingItems;
	private HashMap<String, String> basketItems;

	//the context for Product Recommendation Data Source Pre-filter Parameters
	private int prefilterParamId;
	private int parentPrefilterParamId;
	private String parameterValue;
	private String datasrouceParamId;

	private String userId;
	private String userType;

	/**
	 * @return appAnchor
	 */
	public String getAppAnchor()
	{
		return appAnchor;
	}

	/**
	 * @param appAnchor
	 */
	public void setAppAnchor(String appAnchor)
	{
		this.appAnchor = appAnchor;
	}


	/**
	 * @return modelType of the recommender
	 */
	public String getModelType()
	{
		return modelType;
	}

	/**
	 * @param modelType
	 */
	public void setModelType(String modelType)
	{
		this.modelType = modelType;
	}

	/**
	 * @return leading items
	 */
	public HashMap<String, String> getLeadingItems()
	{
		return leadingItems;
	}

	/**
	 * @param leadingItems
	 */
	public void setLeadingItems(HashMap<String, String> leadingItems)
	{
		this.leadingItems = leadingItems;
	}

	/**
	 * @return basket items
	 */
	public HashMap<String, String> getBasketItems()
	{
		return basketItems;
	}

	/**
	 * @param basketItems
	 */
	public void setBasketItems(HashMap<String, String> basketItems)
	{
		this.basketItems = basketItems;
	}

	/**
	 * @return PREFILTER_PARAM_ID in PRI
	 */
	public int getContextId()
	{
		return prefilterParamId;
	}

	/**
	 * @param contextId
	 */
	public void setContextId(int contextId)
	{
		this.prefilterParamId = contextId;
	}

	/**
	 * @return PARENT_PREFILTER_PARAM_ID in PRI
	 */
	public int getContextParamId()
	{
		return parentPrefilterParamId;
	}

	/**
	 * @param contextParamId
	 */
	public void setContextParamId(final int contextParamId)
	{
		this.parentPrefilterParamId = contextParamId;
	}

	/**
	 * @return PARAMETER_VALUE in PRI
	 */
	public String getValue()
	{
		return parameterValue;
	}

	/**
	 * @param value
	 */
	public void setValue(String value)
	{
		this.parameterValue = value;
	}

	/**
	 * @return DATASOURCE_PARAM_ID in PRI
	 */
	public String getValueType()
	{
		return datasrouceParamId;
	}

	/**
	 * @param valueType
	 */
	public void setValueType(String valueType)
	{
		this.datasrouceParamId = valueType;
	}

	/**
	 * @return userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @param userId
	 */
	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	/**
	 * @return userType
	 */
	public String getUserType()
	{
		return userType;
	}

	/**
	 * @param userType
	 */
	public void setUserType(String userType)
	{
		this.userType = userType;
	}


}
