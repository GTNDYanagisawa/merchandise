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
 * Represents the characteristic value model.
 */
public interface CsticValueModel extends BaseModel
{

	public String TRUE = "Y";

	public final static String AUTHOR_SYSTEM = "S";

	public final static String AUTHOR_USER = "U";

	/**
	 * @return the characteristic value name
	 */
	public String getName();

	/**
	 * @param name
	 *           characteristic value name
	 */
	public void setName(String name);

	/**
	 * @return the language dependent characteristic value name
	 */
	public String getLanguageDependentName();

	/**
	 * @param languageDependentName
	 *           language dependent characteristic value name
	 */
	public void setLanguageDependentName(String languageDependentName);

	/**
	 * @return true if the value is a domain value
	 */
	public boolean isDomainValue();

	/**
	 * @param domainValue
	 *           flag indicating whether this value is a domain value
	 */
	public void setDomainValue(boolean domainValue);

	/**
	 * @return cloned <code>CsticValueModel</code>
	 */
	@Override
	public CsticValueModel clone();

	/**
	 * @param author
	 *           characteristic value author
	 */
	public void setAuthor(String author);

	/**
	 * @return the characteristic value author
	 */
	public String getAuthor();

	/**
	 * @return true if the value is selectable
	 */
	public boolean isSelectable();

	/**
	 * @param selectable
	 *           flag indicating whether this value is selectable
	 */
	public void setSelectable(boolean selectable);

	/**
	 * @param authorExternal
	 *           external characteristic value author - engine representation
	 */
	public void setAuthorExternal(String authorExternal);

	/**
	 * @return the external characteristic value author - engine representation
	 */
	public String getAuthorExternal();


	/**
	 * @return the delta price for this option, compared to the selected option
	 */
	public PriceModel getDeltaPrice();

	/**
	 * @param deltaPrice
	 *           delta price for this option, compared to the selected option
	 */
	public void setDeltaPrice(PriceModel deltaPrice);
}
