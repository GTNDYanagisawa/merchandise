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
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.List;


/**
 * Represents a characteristic group including the group content - the list of <code>CsticModel</code>.
 * 
 */
public interface CsticGroup
{

	/**
	 * @return the characteristic group name
	 */
	public String getName();

	/**
	 * @param name
	 *           the characteristic group name to set
	 */
	public void setName(String name);

	/**
	 * @return the characteristic group description
	 */
	public String getDescription();

	/**
	 * @param description
	 *           the characteristic group description to set
	 */
	public void setDescription(String description);

	/**
	 * @return the characteristic list of this characteristic group
	 */
	public List<CsticModel> getCstics();

	/**
	 * @param cstics
	 *           the characteristic list to set
	 */
	public void setCstics(List<CsticModel> cstics);

}