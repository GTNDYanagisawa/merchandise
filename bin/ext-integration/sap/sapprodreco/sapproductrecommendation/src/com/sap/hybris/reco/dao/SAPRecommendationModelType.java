/**
 * 
 */
package com.sap.hybris.reco.dao;

/**
 * @author Administrator
 * 
 */
public class SAPRecommendationModelType
{

	protected String id;
	protected String description;


	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           the description to set
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}


	@Override
	public String toString()
	{
		return "id: " + id + " description: " + description;
	}

}
