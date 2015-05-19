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

import java.util.List;


/**
 * Represents the characteristic group model.
 */
public interface CsticGroupModel extends BaseModel
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
	 * @return the list of characteristic names of this group
	 */
	public List<String> getCsticNames();

	/**
	 * @param csticNames
	 *           the list of characteristic names to set
	 */
	public void setCsticNames(final List<String> csticNames);

	/**
	 * @return cloned <code>CsticGroupModel</code>
	 */
	@Override
	public CsticGroupModel clone();

}
