/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model;

/**
 * Represents the configuration model.
 */
public interface ConfigModel extends BaseModel
{

	/**
	 * @return configuration id
	 */
	public String getId();

	/**
	 * @param id
	 *           configuration id
	 */
	public void setId(String id);

	/**
	 * @return configuration name
	 */
	public String getName();

	/**
	 * @param name
	 *           configuration name
	 */
	public void setName(String name);

	/**
	 * @return root instance
	 */
	public InstanceModel getRootInstance();

	/**
	 * @param rootInstance
	 *           root instance
	 */
	void setRootInstance(InstanceModel rootInstance);

	/**
	 * @return true if configuration is consistent
	 */
	public boolean isConsistent();

	/**
	 * @param isConsistent
	 *           flag indicating whether configuration is cosistent
	 */
	public void setConsistent(boolean isConsistent);

	/**
	 * @return true if configuration is complete
	 */
	public boolean isComplete();

	/**
	 * @param isComplete
	 *           flag indicating whether configuration is complete
	 */
	public void setComplete(boolean isComplete);

	/**
	 * @return cloned <code>ConfigModel</code>
	 */
	@Override
	public ConfigModel clone();

	/**
	 * @return configuration base price
	 */
	public PriceModel getBasePrice();

	/**
	 * @param basePrice
	 *           configuration base price
	 */
	public void setBasePrice(PriceModel basePrice);

	/**
	 * @return price of selected options
	 */
	public PriceModel getSelectedOptionsPrice();

	/**
	 * @param selectedOptionsPrice
	 *           price of selected options
	 */
	public void setSelectedOptionsPrice(PriceModel selectedOptionsPrice);

	/**
	 * @return configuration current total price
	 */
	public PriceModel getCurrentTotalPrice();

	/**
	 * @param currentTotalPrice
	 *           configuration current total price
	 */
	public void setCurrentTotalPrice(PriceModel currentTotalPrice);
}
