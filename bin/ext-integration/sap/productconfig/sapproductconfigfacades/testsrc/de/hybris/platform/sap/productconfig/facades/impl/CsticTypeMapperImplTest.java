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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImplTest.DummyPriceDataFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.impl.DefaultBaseStoreService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CsticTypeMapperImplTest
{
	/**
	 *
	 */
	private static final String SAP_CHECKBOX_SIMPLE = "SAP_CHECKBOX_SIMPLE_X";

	private CsticTypeMapperImpl typeMapper;

	private static final String GROUP_NAME = "GENERAL";

	private static String CHBOX_VALUE_NAME = "X";

	@Mock
	private ClassificationSystemService classificationService;
	@Mock
	private DefaultBaseStoreService baseStoreService;

	@Mock
	private ClassificationAttributeModel attribute;

	@Mock
	private ClassificationAttributeValueModel attributeValue;

	@Mock
	private BaseStoreModel baseStore;

	private final ClassificationSystemVersionModel classificationVersion = new ClassificationSystemVersionModel();

	@Mock
	private CatalogModel catalogModel;

	private static final String CHARACTERISTIC_CODE = "SAP_STRING_SIMPLE";

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		typeMapper = new CsticTypeMapperImpl();
		final UiTypeFinder uiTypeFinder = new UiTypeFinderImpl();
		typeMapper.setUiTypeFinder(uiTypeFinder);
		final ValueFormatTranslator valueFormatTranslater = new ValueFormatTranslatorImpl();
		typeMapper.setValueFormatTranslater(valueFormatTranslater);
		final ConfigPricingImpl configPicingFactory = new ConfigPricingImpl();
		typeMapper.setPricingFactory(configPicingFactory);
		final DummyPriceDataFactory dummyFactory = new DummyPriceDataFactory();
		configPicingFactory.setPriceDataFactory(dummyFactory);

		typeMapper.setBaseStoreService(baseStoreService);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);

		typeMapper.setClassificationService(classificationService);
		given(classificationService.getAttributeForCode(eq(classificationVersion), anyString())).willReturn(null);
		given(classificationService.getAttributeValueForCode(eq(classificationVersion), anyString())).willReturn(null);
	}

	@Test
	public void testDeltaPrice_noPrice()
	{
		final CsticModel model = ConfigurationTestData.createRadioButtonCstic();
		final CsticValueModel value = model.getAssignableValues().get(0);
		final CsticValueData valueDTO = typeMapper.createDomainValue(model, value, "");
		assertSame("delta price mappend incorrect", ConfigPricing.NO_PRICE, valueDTO.getDeltaPrice());
	}


	@Test
	public void testDeltaPrice()
	{
		final CsticModel model = ConfigurationTestData.createRadioButtonCstic();
		final CsticValueModel value = model.getAssignableValues().get(0);
		final PriceModel deltaPrice = new PriceModelImpl();
		deltaPrice.setPriceValue(new BigDecimal("125.99"));
		deltaPrice.setCurrency("EUR");
		value.setDeltaPrice(deltaPrice);
		final CsticValueData valueDTO = typeMapper.createDomainValue(model, value, "");
		assertTrue("delta price mappend incorrect", new BigDecimal("125.99").compareTo(valueDTO.getDeltaPrice().getValue()) == 0);
		assertEquals("EUR", valueDTO.getDeltaPrice().getCurrencyIso());
	}

	@Test
	public void testCstiModelToDatacMapping() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getTypeLength(), data.getMaxlength());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.STRING, data.getType());
		assertEquals(UiValidationType.NONE, data.getValidationType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));
		assertEquals(Integer.valueOf(model.getConflicts().size()), Integer.valueOf(data.getConflicts().size()));
	}

	@Test
	public void testLangaugeFallbackForEmptyString() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		model.setLanguageDependentName("");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getTypeLength(), data.getMaxlength());
		assertEquals("[" + model.getName() + "]", data.getLangdepname());
	}

	@Test
	public void testLangaugeFallbackForNullValue() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		model.setLanguageDependentName(null);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getTypeLength(), data.getMaxlength());
		assertEquals("[" + model.getName() + "]", data.getLangdepname());
	}

	@Test
	public void testCstiModelToDatacMappingForLastValidValue() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		model.setSingleValue("A value");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals("A value", data.getValue());
		assertEquals("A value", data.getLastValidValue());
	}

	@Test
	public void testCstiModelToDatacMappingForLastValidValueNotNullForNumeric() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createIntegerCstic();
		model.setSingleValue(null);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertNotNull("value may not be null", data.getValue());
		assertNotNull("last valid value may not be null", data.getLastValidValue());
	}

	@Test
	public void testReadOnlyCstiModelToDatacMapping() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createReadOnlyCstic();
		model.setSingleValue("aaaa");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getTypeLength(), data.getMaxlength());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.READ_ONLY, data.getType());
		assertEquals(UiValidationType.NONE, data.getValidationType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));
		assertEquals(0, data.getDomainvalues().size());

	}

	@Test
	public void testReadOnlyDomainValue() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		model.getAssignableValues().get(0).setAuthor(CsticTypeMapper.READ_ONLY_AUTHOR);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());

		assertTrue(data.getDomainvalues().get(0).isReadonly());
	}


	@Test
	public void testDomainValueLangName() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		model.getAssignableValues().get(0).setAuthor(CsticTypeMapper.READ_ONLY_AUTHOR);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getAssignableValues().get(0).getLanguageDependentName(), data.getDomainvalues().get(0).getLangdepname());
	}

	@Test
	public void testDomainValueLangNameFallbackEmpty() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		model.getAssignableValues().get(0).setLanguageDependentName("");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals("[" + model.getAssignableValues().get(0).getName() + "]", data.getDomainvalues().get(0).getLangdepname());
	}

	@Test
	public void testDomainValueLangNameFallbackNull() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		model.getAssignableValues().get(0).setLanguageDependentName(null);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals("[" + model.getAssignableValues().get(0).getName() + "]", data.getDomainvalues().get(0).getLangdepname());
	}


	@Test
	public void testCstiModelToDatacMappingForSimpleCheckBoxTrue() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		model.setSingleValue("X");
		CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.CHECK_BOX, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals(1, data.getDomainvalues().size());
		assertEquals(Boolean.TRUE, Boolean.valueOf(data.getDomainvalues().get(0).isSelected()));

	}

	@Test
	public void testCstiModelToDatacMappingForSimpleCheckBoxFalse() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		model.setSingleValue("");
		CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.CHECK_BOX, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals(1, data.getDomainvalues().size());
		assertEquals(Boolean.FALSE, Boolean.valueOf(data.getDomainvalues().get(0).isSelected()));

	}

	@Test
	public void testCstiModelToDatacMappingForSimpleRadioButton() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createRadioButtonCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.RADIO_BUTTON, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		checkModelContainsDomainValue(model, data);


	}

	@Test
	public void testCstiModelToDatacMappingForRadioButtonWithAdditionalValues() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createRadioButtonWithAddValueCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.RADIO_BUTTON_ADDITIONAL_INPUT, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		checkModelContainsDomainValue(model, data);

	}

	@Test
	public void testCsticLongText()
	{
		final String longText = "lorem ipsum";

		final CsticModel model = ConfigurationTestData.createDropDownCstic();
		model.setLongText(longText);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals(longText, data.getLongText());
		assertFalse(data.isLongTextHTMLFormat());
	}


	@Test
	public void testCsticLongText_HTML()
	{
		final String longText = "<b>lorem ipsum</b>";

		final CsticModel model = ConfigurationTestData.createDropDownCstic();
		model.setLongText(longText);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals(longText, data.getLongText());
		assertTrue(data.isLongTextHTMLFormat());
	}

	@Test
	public void testCsticLongTextInWasNull()
	{
		final String longText = null;

		final CsticModel model = ConfigurationTestData.createDropDownCstic();
		model.setLongText(longText);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNull(data.getLongText());
	}

	@Test
	public void testCsticLongTextInWasEmptyString()
	{
		final String longText = "";

		final CsticModel model = ConfigurationTestData.createDropDownCstic();
		model.setLongText(longText);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNull(data.getLongText());
	}

	@Test
	public void testAdditionalInputAlwaysEmpty()
	{
		final CsticModel model = ConfigurationTestData.createDropDownCstic();
		model.setAllowsAdditionalValues(true);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertTrue("Additioanl value should be empty!", data.getAdditionalValue().isEmpty());
	}


	@Test
	public void testCstiModelToDatacMappingForSimpleDropDown() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createDropDownCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.DROPDOWN, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		checkModelContainsDomainValue(model, data);

	}

	@Test
	public void testCstiModelToDatacMappingForDropDownWithAddValue() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createDropDownWithAddValueCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.DROPDOWN_ADDITIONAL_INPUT, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		checkModelContainsDomainValue(model, data);

	}

	private void checkModelContainsDomainValue(final CsticModel model, final CsticData data)
	{
		for (final CsticValueModel modelValue : model.getAssignableValues())
		{
			boolean found = false;
			for (final CsticValueData dataValue : data.getDomainvalues())
			{
				if (modelValue.getName().equals(dataValue.getName()))
				{
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
	}

	@Test
	public void testCstiModelToDataMappingForUndefinedNotImplemented() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createUndefinedCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.NOT_IMPLEMENTED, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));



	}

	@Test
	public void testCsticMappingFloatToSimpleNumeric() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createFloatCstic();
		model.setNumberScale(23);
		model.setTypeLength(42);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals("Map float type to UIType NUMERIC", UiType.NUMERIC, data.getType());
		assertEquals("Map float type to UiValidationType NUMERIC", UiValidationType.NUMERIC, data.getValidationType());
		assertEquals("Wrong number scale", 23, data.getNumberScale());
		assertEquals("Wrong type length", 42, data.getTypeLength());
	}

	@Test
	public void testCsticMappingNumericMaxLengthWithFraction() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createFloatCstic();
		//format 1.23
		model.setNumberScale(2);
		model.setTypeLength(3);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals("Wrong max length", 4, data.getMaxlength());
	}

	@Test
	public void testCsticMappingNumericMaxLengthWith3DigitsOnly() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createFloatCstic();
		//format 123
		model.setNumberScale(0);
		model.setTypeLength(3);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals("Wrong max length", 3, data.getMaxlength());
	}

	@Test
	public void testCsticMappingNumericMaxLengthWitGrouping() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createFloatCstic();
		//format 123
		model.setNumberScale(0);
		model.setTypeLength(6);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals("Wrong max length", 7, data.getMaxlength());
	}

	@Test
	public void testCsticMappingNumericMaxLengthLong() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createFloatCstic();
		//format 1,234,567,890.12
		model.setNumberScale(2);
		model.setTypeLength(12);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals("Wrong max length", 16, data.getMaxlength());
	}


	@Test
	public void testCsticMappingFloatToAdditionalValueNumeric() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createRadioButtonCstic();
		model.setValueType(CsticModel.TYPE_FLOAT);
		model.setNumberScale(23);
		model.setTypeLength(42);
		model.setAllowsAdditionalValues(true);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals(UiType.RADIO_BUTTON_ADDITIONAL_INPUT, data.getType());
		assertEquals("Map float type to UiValidationType NUMERIC", UiValidationType.NUMERIC, data.getValidationType());
		assertEquals("Wrong number scale", 23, data.getNumberScale());
		assertEquals("Wrong type length", 42, data.getTypeLength());
	}

	@Test
	public void testCsticMappingIntegerToSimpleNumeric() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createIntegerCstic();
		model.setTypeLength(42);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals("Map integer type to UIType NUMERIC", UiType.NUMERIC, data.getType());
		assertEquals("Map integer type to UiValidationType NUMERIC", UiValidationType.NUMERIC, data.getValidationType());
		assertEquals("Number scale is 0 for Integers", 0, data.getNumberScale());
		assertEquals("Wrong type length", 42, data.getTypeLength());
	}


	@Test
	public void testUpdateCsticModelValuesFromDataSingleValueInitial()
	{

		final CsticData data = new CsticData();
		final CsticModel model = new CsticModelImpl();
		data.setValue("aValue");
		assertEquals(0, model.getAssignedValues().size());
		typeMapper.updateCsticModelValuesFromData(data, model);
		assertEquals(1, model.getAssignedValues().size());
		assertEquals("aValue", model.getAssignedValues().get(0).getName());
	}




	@Test
	public void testUpdateCsticModelValuesFromDataSingleValueUpdate()
	{

		final CsticData data = new CsticData();
		final CsticModel model = new CsticModelImpl();
		data.setValue("aValue");
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("anotherValue");
		model.setAssignedValuesWithoutCheckForChange(Collections.singletonList(value));

		assertEquals(1, model.getAssignedValues().size());
		typeMapper.updateCsticModelValuesFromData(data, model);
		assertEquals(1, model.getAssignedValues().size());
		assertEquals("aValue", model.getAssignedValues().get(0).getName());

	}

	@Test
	public void testUpdateCstiModelValueFromDataSimpleCheckBoxSelect() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		model.setSingleValue("");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		data.getDomainvalues().get(0).setSelected(true);

		typeMapper.updateCsticModelValuesFromData(data, model);

		assertEquals(1, model.getAssignedValues().size());
		assertEquals(model.getAssignableValues().get(0).getName(), model.getSingleValue());


	}

	@Test
	public void testUpdateCstiModelValueFromDataSimpleCheckBoxUnSelect() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		model.setSingleValue("X");
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		data.getDomainvalues().get(0).setSelected(false);

		typeMapper.updateCsticModelValuesFromData(data, model);

		assertEquals(0, model.getAssignedValues().size());
		assertNull(model.getSingleValue());


	}

	@Test
	public void testUpdateCstiModelValueCheckBoxListNotChanged() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		model.setChangedByFrontend(false);

		typeMapper.updateCsticModelValuesFromData(data, model);

		assertFalse("DTO was not changed", model.isChangedByFrontend());
	}

	@Test
	public void testCstiModelToDatacMappingForCheckBoxList() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());
		assertEquals(UiType.CHECK_BOX_LIST, data.getType());
		assertEquals(Boolean.valueOf(model.isVisible()), Boolean.valueOf(data.isVisible()));

		checkModelContainsDomainValue(model, data);
		checkAssignedValuesAreSelected(model, data);

	}


	private void checkAssignedValuesAreSelected(final CsticModel model, final CsticData data)
	{

		for (final CsticValueModel modelValue : model.getAssignableValues())
		{
			final boolean isAssigned = model.getAssignedValues().contains(modelValue);
			boolean found = false;
			for (final CsticValueData dataValue : data.getDomainvalues())
			{
				if (modelValue.getName().equals(dataValue.getName()))
				{
					if (isAssigned)
					{
						assertTrue(dataValue.getName() + " should be selected", dataValue.isSelected());
					}
					else
					{
						assertFalse(dataValue.getName() + " should NOT be selected", dataValue.isSelected());
					}
					found = true;
					break;
				}
			}
			assertTrue(found);
		}

	}

	@Test
	public void testUpdateCsticModelValuesFromDataCheckBoxList()
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		// value 1 is pre-selected
		data.getDomainvalues().get(0).setSelected(false);
		data.getDomainvalues().get(1).setSelected(true);
		data.getDomainvalues().get(2).setSelected(true);
		typeMapper.updateCsticModelValuesFromData(data, model);

		assertEquals("number assigned values wrong", 2, model.getAssignedValues().size());
		assertEquals("wrong value assigned", model.getAssignableValues().get(1), model.getAssignedValues().get(0));
		assertEquals("wrong value assigned", model.getAssignableValues().get(2), model.getAssignedValues().get(1));

	}

	@Test
	public void testUpdateCsticModelValuesFromDataDropDownListNullValue()
	{
		final CsticModel model = ConfigurationTestData.createDropDownCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		data.setValue("NULL_VALUE");

		typeMapper.updateCsticModelValuesFromData(data, model);

		assertNull("null value was not set", model.getSingleValue());
	}

	@Test
	public void testUpdateCsticModelValuesFromDataDropDownListWithAddValue_NullValue()
	{

		final CsticModel model = ConfigurationTestData.createDropDownCstic();
		model.setAllowsAdditionalValues(true);
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		data.setValue("NULL_VALUE");

		typeMapper.updateCsticModelValuesFromData(data, model);

		assertNull("null value was not set", model.getSingleValue());

	}

	@Test
	public void testUpdateCsticModelValuesFromDataDropDownListWithAddValue_customerValue()
	{

		final CsticModel model = ConfigurationTestData.createDropDownWithAddValueCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		data.setAdditionalValue("CUSTOM_VALUE");
		typeMapper.updateCsticModelValuesFromData(data, model);

		assertEquals(1, model.getAssignedValues().size());
		assertEquals("CUSTOM_VALUE", model.getAssignedValues().get(0).getName());
	}

	@Test
	public void testUpdateCsticModelValuesFromDataDropDownListWithAddValue_domainValue()
	{

		final CsticModel model = ConfigurationTestData.createDropDownWithAddValueCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		data.setValue("VAL1");
		typeMapper.updateCsticModelValuesFromData(data, model);

		assertEquals(1, model.getAssignedValues().size());
		assertEquals("VAL1", model.getAssignedValues().get(0).getName());
	}


	@Test
	public void testMapFieldMask() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createFloatCstic();
		final String mask = "-____";
		model.setEntryFieldMask(mask);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals("Field mask missing", mask, data.getEntryFieldMask());
	}

	@Test
	public void testMapFieldMaskNull() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createFloatCstic();
		final String mask = null;
		model.setEntryFieldMask(mask);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals("Field mask should not be null", "", data.getEntryFieldMask());
	}

	@Test
	public void testReadOnlyForSelectable() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createCheckBoxListCsticWithValue2Assigned();
		model.getAssignableValues().get(0).setSelectable(false);
		model.getAssignableValues().get(1).setSelectable(true);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertTrue("Should be mapped to readonly", data.getDomainvalues().get(0).isReadonly());
		assertFalse("Should NOT be mapped to readonly", data.getDomainvalues().get(1).isReadonly());
	}

	@Test
	public void testGenerateUniqueKeyCtsic()
	{
		final CsticModel model = new CsticModelImpl();
		model.setName("csticName");
		final String key = typeMapper.generateUniqueKey(model, GROUP_NAME);

		assertEquals("root." + GROUP_NAME + ".csticName", key);
	}

	@Test
	public void testGenerateUniqueKeyCtsicValue()
	{
		final CsticModel model = new CsticModelImpl();
		model.setName("csticName");
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("valueName");
		final String key = typeMapper.generateUniqueKey(model, value, GROUP_NAME);

		assertEquals("root." + GROUP_NAME + ".csticName.valueName", key);
	}

	@Test
	public void testCstiModelToDataKeyNotNull() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createRadioButtonCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertNotNull(data.getKey());

		assertNotNull(data.getDomainvalues().get(0));
		assertNotNull(data.getDomainvalues().get(0).getKey());

	}

	@Test
	public void testCstiModelToDatacConflictMapping() throws Exception
	{
		final CsticModel model = ConfigurationTestData.createSTRCsticWithConflicts();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(Integer.valueOf(model.getConflicts().size()), Integer.valueOf(data.getConflicts().size()));
		assertEquals("CStic with conflict should have 'Warning Status'", CsticStatusType.WARNING, data.getCsticStatus());
	}


	@Test
	public void testAssignedValueNotAssignable()
	{

		final CsticModel model = ConfigurationTestData.createRadioButtonCstic();
		model.setSingleValue("NOT ASSIGNABLE");

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertEquals("NOT ASSIGNABLE", data.getValue());
		boolean found = false;
		for (final CsticValueData value : data.getDomainvalues())
		{
			if ("NOT ASSIGNABLE".equals(value.getName()))
			{
				found = true;
				assertTrue("assigned value not selected", value.isSelected());
				break;
			}
		}
		assertTrue("assigned value is not in domain values", found);
	}

	@Test
	public void testModifiedFlagTrue()
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		model.setAuthor(CsticModel.AUTHOR_USER);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals("user assigned cstic should be marked as finished", CsticStatusType.FINISHED, data.getCsticStatus());
	}

	@Test
	public void testModifiedFlagFalse()
	{
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		model.setAuthor(CsticModel.AUTHOR_SYSTEM);

		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);
		assertEquals("system assigned cstic should be marked as not-finished", CsticStatusType.DEFAULT, data.getCsticStatus());
	}

	@Test
	public void testHarmonizeDeltaPrices_noPrices()
	{
		final List<CsticValueData> domainValues = new ArrayList<CsticValueData>();
		CsticValueData domainValue = new CsticValueData();
		domainValue.setName("A");
		domainValue.setDeltaPrice(ConfigPricing.NO_PRICE);
		domainValues.add(domainValue);
		domainValue = new CsticValueData();
		domainValue.setName("B");
		domainValue.setDeltaPrice(ConfigPricing.NO_PRICE);
		domainValues.add(domainValue);

		typeMapper.harmonizeDeltaPricing(domainValues);
		for (final CsticValueData harmonizedDomainValue : domainValues)
		{
			assertSame(ConfigPricing.NO_PRICE, harmonizedDomainValue.getDeltaPrice());
		}
	}

	@Test
	public void testHarmonizeDeltaPrices_oneValueWithPrice()
	{
		final List<CsticValueData> domainValues = new ArrayList<CsticValueData>();
		CsticValueData domainValue = new CsticValueData();
		domainValue.setName("A");
		final PriceData price = new PriceData();
		price.setValue(BigDecimal.ONE);
		price.setCurrencyIso("USD");
		domainValue.setDeltaPrice(price);
		domainValues.add(domainValue);
		domainValue = new CsticValueData();
		domainValue.setName("B");
		domainValue.setDeltaPrice(ConfigPricing.NO_PRICE);
		domainValues.add(domainValue);

		typeMapper.harmonizeDeltaPricing(domainValues);

		assertTrue(0 == BigDecimal.ONE.compareTo(domainValues.get(0).getDeltaPrice().getValue()));
		assertTrue(0 == BigDecimal.ZERO.compareTo(domainValues.get(1).getDeltaPrice().getValue()));
		assertFalse(ConfigPricing.NO_PRICE == domainValues.get(1).getDeltaPrice());

	}

	@Test
	public void testMapCsticModelToData_CsticDescriptionInHybris()
	{

		final String langDepName = "My Hybris Description";
		given(attribute.getName()).willReturn(langDepName);
		given(classificationService.getAttributeForCode(any(ClassificationSystemVersionModel.class), eq(CHARACTERISTIC_CODE)))
				.willReturn(attribute);
		prepareCatalogAndClassification();
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(langDepName, data.getLangdepname());

	}

	@Test
	public void testMapCsticModelToData_noCsticDescriptionInHybris()
	{

		final String langDepName = "My Hybris Description";
		given(attribute.getName()).willReturn(langDepName);
		final Throwable throwables = new UnknownIdentifierException(CHARACTERISTIC_CODE);
		given(classificationService.getAttributeForCode(any(ClassificationSystemVersionModel.class), eq(CHARACTERISTIC_CODE)))
				.willThrow(throwables);
		prepareCatalogAndClassification();
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLanguageDependentName(), data.getLangdepname());

	}


	@Test
	public void testMapCsticModelToData_csticLongTextInHybris()
	{

		final String longText = "My Hybris LongText";
		given(attribute.getDescription()).willReturn(longText);
		given(classificationService.getAttributeForCode(any(ClassificationSystemVersionModel.class), eq(CHARACTERISTIC_CODE)))
				.willReturn(attribute);
		prepareCatalogAndClassification();
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(longText, data.getLongText());

	}

	@Test
	public void testMapCsticModelToData_EmptyCsticLongTextInHybris()
	{

		final String longText = "";
		given(attribute.getDescription()).willReturn(longText);
		given(classificationService.getAttributeForCode(any(ClassificationSystemVersionModel.class), eq(CHARACTERISTIC_CODE)))
				.willReturn(attribute);
		prepareCatalogAndClassification();
		final CsticModel model = ConfigurationTestData.createSTRCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getLongText(), data.getLongText());

	}

	@Test
	public void testMapCsticModelToData_ValueDescriptionInHybris()
	{

		final String langDepName = "My Hybris Value Description";
		given(attributeValue.getName()).willReturn(langDepName);
		given(classificationService.getAttributeValueForCode(any(ClassificationSystemVersionModel.class), eq(SAP_CHECKBOX_SIMPLE)))
				.willReturn(attributeValue);
		prepareCatalogAndClassification();
		final CsticModel model = ConfigurationTestData.createCheckBoxCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(langDepName, data.getDomainvalues().get(0).getLangdepname());

	}

	@Test
	public void testMapCsticModelToData_noValueDescriptionInHybris()
	{

		final String langDepName = "My Hybris Description";
		given(attributeValue.getName()).willReturn(langDepName);
		final Throwable throwables = new UnknownIdentifierException(SAP_CHECKBOX_SIMPLE);
		given(classificationService.getAttributeValueForCode(any(ClassificationSystemVersionModel.class), eq(SAP_CHECKBOX_SIMPLE)))
				.willThrow(throwables);
		prepareCatalogAndClassification();
		final CsticModel model = ConfigurationTestData.createRadioButtonCstic();
		final CsticData data = typeMapper.mapCsticModelToData(model, GROUP_NAME);

		assertNotNull(data);
		assertEquals(model.getName(), data.getName());
		assertEquals(model.getAssignableValues().get(0).getLanguageDependentName(), data.getDomainvalues().get(0).getLangdepname());

	}

	private void prepareCatalogAndClassification()
	{
		final List<CatalogModel> catalogs = new ArrayList();
		catalogs.add(catalogModel);
		given(baseStore.getCatalogs()).willReturn(catalogs);

		final String catalogId = "Catalog Id";
		final String catalogVersion = "Catalog Version";
		given(catalogModel.getId()).willReturn(catalogId);
		given(catalogModel.getVersion()).willReturn(catalogVersion);

		final ClassificationSystemVersionModel classificationSystemVersionModel = new ClassificationSystemVersionModel();
		given(classificationService.getSystemVersion(eq(catalogId), eq(catalogVersion))).willReturn(
				classificationSystemVersionModel);
	}

	@Test
	public void testContainsHTML_false()
	{
		final String longText = "This is a plain text string \n\r with some newlines\n";
		final boolean containsHTML = typeMapper.containsHTML(longText);
		assertFalse("HTML detected, but not expected, in: " + longText, containsHTML);
	}

	@Test
	public void testContainsHTML_true()
	{
		final String longText = "This is a HTMLtext string <br> with some newlines<br>";
		final boolean containsHTML = typeMapper.containsHTML(longText);
		assertTrue("HTML not detected, but expected, in: " + longText, containsHTML);
	}

	@Test
	public void testContainsHTML_true_table()
	{
		final String longText = "<table><tr><td>Cell <b>1A</b></td><td>Cell <i>1B</i></td></tr></table>";
		final boolean containsHTML = typeMapper.containsHTML(longText);
		assertTrue("HTML not detected, but expected, in: " + longText, containsHTML);
	}

}
