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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConflictData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


public class CsticTypeMapperImpl implements CsticTypeMapper
{
	/**
	 *
	 */
	private static final String EMPTY = "";

	private UiTypeFinder uiTypeFinder;

	private ClassificationSystemService classificationService;

	private BaseStoreService baseStoreService;

	private final static Logger LOG = Logger.getLogger(CsticTypeMapperImpl.class);

	@Autowired
	private ValueFormatTranslator valueFormatTranslator;

	@Autowired
	private ConfigPricing pricingFactory;

	private ClassificationSystemVersionModel classificationSystemVersionModel;

	@Override
	public CsticData mapCsticModelToData(final CsticModel model, final String prefix)
	{
		final CsticData data = new CsticData();
		data.setAdditionalValue(EMPTY);

		data.setKey(generateUniqueKey(model, prefix));
		String langDepName = model.getLanguageDependentName();
		final String name = model.getName();
		langDepName = getDisplayName(langDepName, name);
		data.setLangdepname(langDepName);
		data.setName(name);
		final String longText = getLongText(model);
		data.setLongText(longText);
		final boolean longTextHTMLFormat = containsHTML(longText);
		data.setLongTextHTMLFormat(longTextHTMLFormat);
		data.setVisible(model.isVisible());
		data.setRequired(model.isRequired());

		final int maxLength = model.getTypeLength();
		data.setMaxlength(maxLength);
		final String entryFieldMask = model.getEntryFieldMask();
		data.setEntryFieldMask(emptyIfNull(entryFieldMask));

		final List<CsticValueData> domainValues = createDomainValues(model, prefix);
		data.setDomainvalues(domainValues);

		final List<ConflictData> conflicts = extractConflicts(model);
		data.setConflicts(conflicts);
		final boolean hasConflicts = conflicts != null && !conflicts.isEmpty();
		if (hasConflicts)
		{
			data.setCsticStatus(CsticStatusType.WARNING);
		}
		else if (CsticModel.AUTHOR_USER.equals(model.getAuthor()))
		{
			data.setCsticStatus(CsticStatusType.FINISHED);
		}
		else
		{
			data.setCsticStatus(CsticStatusType.DEFAULT);
		}

		final UiType uiType = uiTypeFinder.findUiTypeForCstic(model);
		data.setType(uiType);
		final UiValidationType validationType = uiTypeFinder.findUiValidationTypeForCstic(model);
		data.setValidationType(validationType);

		final String singleValue = model.getSingleValue();
		final String formattedValue = valueFormatTranslator.format(uiType, singleValue);
		data.setValue(formattedValue);
		data.setLastValidValue(formattedValue);

		if (UiValidationType.NUMERIC == validationType)
		{
			mapNumericSpecifics(model, data);
		}

		if (LOG.isTraceEnabled())
		{
			LOG.trace("map CsticModel to CsticData [CSTIC_NAME='" + model.getName() + "';CSTIC_UI_KEY='" + data.getKey()
					+ "';CSTIC_UI_TYPE='" + data.getType() + "';CSTIC_VALUE='" + data.getValue() + "']");
		}

		return data;
	}

	private String getLongText(final CsticModel model)
	{
		String longText = null;

		final ClassificationAttributeModel attribute = getClassificationAttribute(model.getName());
		if (attribute != null)
		{
			longText = attribute.getDescription();
			longText = useNullValueForEmptyText(longText);
		}
		if (null == longText)
		{
			longText = model.getLongText();
		}

		longText = useNullValueForEmptyText(longText);
		return longText;
	}

	protected String useNullValueForEmptyText(String longText)
	{
		if (isNullOrEmpty(longText))
		{
			longText = null;
		}
		return longText;
	}

	protected boolean isNullOrEmpty(final String longText)
	{
		return longText == null || longText.isEmpty();
	}

	private String getDisplayName(String langDepName, final String name)
	{

		final ClassificationAttributeModel attribute = getClassificationAttribute(name);
		if (attribute != null && attribute.getName() != null && !attribute.getName().isEmpty())
		{
			langDepName = attribute.getName();
		}

		if (isNullOrEmpty(langDepName))
		{
			langDepName = "[" + name + "]";
		}

		return langDepName;
	}

	protected ClassificationAttributeModel getClassificationAttribute(final String name)
	{
		final ClassificationSystemVersionModel systemVersion = getSystemVersion();

		ClassificationAttributeModel attribute = null;
		if (systemVersion != null)
		{
			try
			{

				attribute = classificationService.getAttributeForCode(systemVersion, name);

			}
			catch (final UnknownIdentifierException e)
			{
				LOG.debug("The attribute is not found for the name '" + name + "'");
			}
		}
		return attribute;
	}

	private String getDisplayValueName(String langDepName, final String csticName, final String name)
	{

		final ClassificationSystemVersionModel systemVersion = getSystemVersion();

		ClassificationAttributeValueModel attributeValue = null;
		final String attributeName = csticName + '_' + name;
		if (systemVersion != null)
		{
			try
			{

				attributeValue = classificationService.getAttributeValueForCode(systemVersion, attributeName);

			}
			catch (final UnknownIdentifierException e)
			{
				LOG.debug("The attribute is not found for the name '" + name + "'");
			}
		}

		if (attributeValue != null && attributeValue.getName() != null && !attributeValue.getName().isEmpty())
		{
			langDepName = attributeValue.getName();
		}

		if (isNullOrEmpty(langDepName))
		{
			langDepName = "[" + name + "]";
		}

		return langDepName;
	}

	/**
	 * @return classification system Version model
	 */
	private ClassificationSystemVersionModel getSystemVersion()
	{
		if (classificationSystemVersionModel == null)
		{
			final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
			if (baseStore == null)
			{
				throw new IllegalStateException("No base store available");
			}

			final List<CatalogModel> catalogs = baseStore.getCatalogs();
			for (final CatalogModel catalog : catalogs)
			{
				try
				{
					return classificationSystemVersionModel = classificationService.getSystemVersion(catalog.getId(),
							catalog.getVersion());

				}
				catch (final UnknownIdentifierException e)
				{
					LOG.debug("No classification sytem found for Id '" + catalog.getId() + "'");
					continue;
				}
			}
		}
		return classificationSystemVersionModel;
	}

	private List<ConflictData> extractConflicts(final CsticModel model)
	{
		final List<ConflictData> conflicts = new ArrayList<>();

		for (final ConflictModel conflict : model.getConflicts())
		{
			final ConflictData conflictData = new ConflictData();
			conflictData.setText(conflict.getText());
			conflicts.add(conflictData);
		}
		return conflicts;
	}

	private String emptyIfNull(final String value)
	{
		return (value == null) ? EMPTY : value;
	}



	protected void mapNumericSpecifics(final CsticModel model, final CsticData data)
	{
		final int numFractionDigits = model.getNumberScale();
		final int typeLength = model.getTypeLength();
		data.setNumberScale(numFractionDigits);
		data.setTypeLength(typeLength);

		int maxlength = typeLength;
		if (numFractionDigits > 0)
		{
			maxlength++;
		}
		final int numDigits = typeLength - numFractionDigits;
		final int maxGroupimgSeperators = (numDigits - 1) / 3;
		maxlength += maxGroupimgSeperators;
		data.setMaxlength(maxlength);
	}





	private List<CsticValueData> createDomainValues(final CsticModel model, final String prefix)
	{
		final List<CsticValueData> domainValues = new ArrayList<>();

		for (final CsticValueModel csticValue : model.getAssignableValues())
		{
			final CsticValueData domainValue = createDomainValue(model, csticValue, prefix);
			domainValues.add(domainValue);
		}
		if (model.isConstrained())
		{
			for (final CsticValueModel assignedValue : model.getAssignedValues())
			{
				if (!model.getAssignableValues().contains(assignedValue))
				{
					final CsticValueData domainValue = createDomainValue(model, assignedValue, prefix);
					domainValues.add(domainValue);
				}
			}
		}

		harmonizeDeltaPricing(domainValues);

		return domainValues;

	}


	/**
	 * @param domainValues
	 */
	protected void harmonizeDeltaPricing(final List<CsticValueData> domainValues)
	{
		boolean atleastOneValueHasADeltaPrice = false;
		PriceData nonZeroDeltaPrice = null;
		for (final CsticValueData domainValue : domainValues)
		{
			if (domainValue.getDeltaPrice() != ConfigPricing.NO_PRICE)
			{
				atleastOneValueHasADeltaPrice = true;
				nonZeroDeltaPrice = domainValue.getDeltaPrice();
				break;
			}
		}
		if (atleastOneValueHasADeltaPrice)
		{
			for (final CsticValueData domainValue : domainValues)
			{
				if (domainValue.getDeltaPrice() == ConfigPricing.NO_PRICE)
				{
					final PriceModel priceModel = new PriceModelImpl();
					priceModel.setCurrency(nonZeroDeltaPrice.getCurrencyIso());
					priceModel.setPriceValue(BigDecimal.ZERO);
					final PriceData priceData = pricingFactory.getPriceData(priceModel);
					domainValue.setDeltaPrice(priceData);
				}
			}
		}

	}

	protected CsticValueData createDomainValue(final CsticModel model, final CsticValueModel csticValue, final String prefix)
	{
		final CsticValueData domainValue = new CsticValueData();
		domainValue.setKey(generateUniqueKey(model, csticValue, prefix));
		String langDepName = csticValue.getLanguageDependentName();
		final String name = csticValue.getName();
		langDepName = getDisplayValueName(langDepName, model.getName(), name);
		domainValue.setLangdepname(langDepName);
		domainValue.setName(name);
		final boolean isAssigned = model.getAssignedValues().contains(csticValue);
		domainValue.setSelected(isAssigned);

		final boolean isReadOnly = checkReadonly(csticValue);
		domainValue.setReadonly(isReadOnly);

		final PriceData price = pricingFactory.getPriceData(csticValue.getDeltaPrice());
		domainValue.setDeltaPrice(price);


		return domainValue;
	}



	protected boolean checkReadonly(final CsticValueModel csticValue)
	{
		final boolean isSystemValue = (csticValue.getAuthor() != null && csticValue.getAuthor().equalsIgnoreCase(READ_ONLY_AUTHOR));

		final boolean isSelectable = csticValue.isSelectable();
		return isSystemValue || !isSelectable;
	}

	@Override
	public void setUiTypeFinder(final UiTypeFinder uiTypeFinder)
	{
		this.uiTypeFinder = uiTypeFinder;
	}


	@Override
	public void updateCsticModelValuesFromData(final CsticData data, final CsticModel model)
	{
		final UiType uiType = data.getType();
		if (UiType.CHECK_BOX_LIST == uiType || UiType.CHECK_BOX == uiType)
		{
			//			model.clearValues();
			for (final CsticValueData valueData : data.getDomainvalues())
			{
				final String value = valueData.getName();
				final String parsedValue = valueFormatTranslator.parse(uiType, value);
				if (valueData.isSelected())
				{
					model.addValue(parsedValue);
				}
				else
				{
					model.removeValue(parsedValue);
				}
			}
		}
		else
		{
			final String value = getValueFromCstcData(data);
			final String parsedValue = valueFormatTranslator.parse(uiType, value);
			if ((UiType.DROPDOWN == uiType || UiType.DROPDOWN_ADDITIONAL_INPUT == uiType) && "NULL_VALUE".equals(value))
			{
				model.setSingleValue(null);
			}
			else
			{
				model.setSingleValue(parsedValue);
			}
		}

		if (LOG.isTraceEnabled())
		{
			LOG.trace("update CsticData to CsticModel [CSTIC_NAME='" + model.getName() + "';CSTIC_UI_KEY='" + data.getKey()
					+ "';CSTIC_UI_TYPE='" + data.getType() + "';CSTIC_VALUE='" + data.getValue() + "']");
		}

	}

	/**
	 * @param data
	 * @return value of characteristic
	 */
	protected String getValueFromCstcData(final CsticData data)
	{
		String value = data.getValue();
		final UiType uiType = data.getType();
		if (UiType.RADIO_BUTTON_ADDITIONAL_INPUT == uiType || UiType.DROPDOWN_ADDITIONAL_INPUT == uiType)
		{
			final String additionalValue = data.getAdditionalValue();
			if (additionalValue != null && !additionalValue.isEmpty())
			{
				value = additionalValue;
			}
		}

		return value;
	}



	@Override
	public void setValueFormatTranslater(final ValueFormatTranslator valueFormatTranslater)
	{
		this.valueFormatTranslator = valueFormatTranslater;
	}



	@Override
	public String generateUniqueKey(final CsticModel model, final CsticValueModel value, final String prefix)
	{

		String key = generateUniqueKey(model, prefix);
		key += "." + value.getName();
		return key;
	}


	@Override
	public String generateUniqueKey(final CsticModel model, final String prefix)
	{
		final String key = "root" + "." + prefix + "." + model.getName();
		return key;
	}

	protected ConfigPricing getPricingFactory()
	{
		return pricingFactory;
	}

	@Override
	public void setPricingFactory(final ConfigPricing pricingFactory)
	{
		this.pricingFactory = pricingFactory;
	}


	@Override
	public void setClassificationService(final ClassificationSystemService classificationService)
	{
		this.classificationService = classificationService;
	}


	@Override
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;

	}


	protected boolean containsHTML(final String longText)
	{
		boolean containsHTML = false;
		if (longText != null)
		{
			containsHTML = longText.matches(".*\\<.+?\\>.*");
		}
		return containsHTML;
	}

}
