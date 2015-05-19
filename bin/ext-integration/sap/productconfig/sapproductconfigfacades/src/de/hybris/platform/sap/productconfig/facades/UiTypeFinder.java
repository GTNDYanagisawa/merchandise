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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;


/**
 * Helper to determine the UI-type for a given Characteristic. It will determine how the characteristic is rendered on
 * the UI.
 */
public interface UiTypeFinder
{
	/**
	 * @param model
	 * @return UIType that decides how the characteristic is rendered on the UI
	 */
	public UiType findUiTypeForCstic(CsticModel model);

	/**
	 * @param csticModel
	 * @return UIValidatioType that decides how the user input for this characteristic is validated
	 */
	public UiValidationType findUiValidationTypeForCstic(CsticModel csticModel);

}
