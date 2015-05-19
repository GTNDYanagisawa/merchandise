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

import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.store.services.BaseStoreService;


/**
 * Populator like helper object to map a single characteristic and all child objects, such as domain values, from the
 * product configuration model to the corresponding DAOs and vice versa.
 */
public interface CsticTypeMapper
{
	/**
	 * Characteristics that are assigned by the system author may not be chnaged by the front-end user, henc ethey should
	 * be considered read-only.
	 */
	public static final String READ_ONLY_AUTHOR = "S";

	/**
	 * Maps a single characteristic. Model -> DTO.
	 * 
	 * @param model
	 *           source - characteristic Model
	 * @param groupName
	 *           name of the group, this characteristic belongs to
	 * @return target - characteristic DTO
	 */
	public CsticData mapCsticModelToData(CsticModel model, String groupName);

	/**
	 * Updates a single characteristic. DTO -> Model.
	 * 
	 * @param data
	 *           source - characteristic DTO
	 * @param model
	 *           target - characteristic Model
	 */
	public void updateCsticModelValuesFromData(CsticData data, CsticModel model);

	/**
	 * Injects the UiTypeFinder.
	 * 
	 * @param uiTypeFinder
	 *           decides which UI-Type is used to render this characteristic
	 */
	public void setUiTypeFinder(UiTypeFinder uiTypeFinder);

	/**
	 * Injects the ValueFormatTranslator
	 * 
	 * @param valueFormatTranslater
	 *           helper object for formatting values, for example numbers
	 */
	public void setValueFormatTranslater(ValueFormatTranslator valueFormatTranslater);

	/**
	 * Injects the ConfigPricingFactory
	 * 
	 * @param pricingFactory
	 *           helper object for formatting prices
	 */
	public void setPricingFactory(ConfigPricing pricingFactory);

	/**
	 * Generates a key that identifies this characteristic value uniquely within this configuration.
	 * 
	 * @param model
	 *           characteristic model
	 * @param value
	 *           characteristic value model
	 * @return unique key
	 */
	public String generateUniqueKey(CsticModel model, CsticValueModel value, String groupName);

	/**
	 * Generates a key that identifies this characteristic uniquely within this configuration.
	 * 
	 * @param model
	 *           characteristic model
	 * 
	 * @return unique key
	 */
	public String generateUniqueKey(CsticModel model, String groupName);

	/**
	 * @param classificationService
	 */
	public void setClassificationService(ClassificationSystemService classificationService);

	/**
	 * @param baseStoreService
	 */
	public void setBaseStoreService(BaseStoreService baseStoreService);


}
