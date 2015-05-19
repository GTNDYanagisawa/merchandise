/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.hybris.instore.widgets.variantselector;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.zkoss.zk.device.AjaxDevice;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.ui.Component;
import org.zkoss.zml.device.XmlDevice;
import org.zkoss.zul.Div;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;


@DeclaredInput(socketType = ProductData.class, value = VariantSelectorController.SOCKET_IN_PRODUCT)
public class VariantSelectorControllerTest extends AbstractWidgetUnitTest<VariantSelectorController>
{
	private static final String TEST_CODE = "testCode";

	@InjectMocks
	private final VariantSelectorController variantSelectorController = new VariantSelectorController();

	@SuppressWarnings("unused")
	@Mock
	protected Div variantSelectorComponent;

	@SuppressWarnings("unused")
	@Mock
	private ProductFacade productFacade;

	@Override
	protected VariantSelectorController getWidgetController()
	{
		return variantSelectorController;
	}

	@Before
	public void setUp()
	{
		// ZK device setup
		if (!Devices.exists("ajax"))
		{
			Devices.add("ajax", AjaxDevice.class);
		}
		if (!Devices.exists("xml"))
		{
			Devices.add("xml", XmlDevice.class);
		}
	}

	@Test
	public void testNonVariantInput()
	{
		final ProductData nonVariant = Mockito.mock(ProductData.class);
		Mockito.when(nonVariant.getCode()).thenReturn(TEST_CODE);

		Mockito.when(productFacade.getProductForCodeAndOptions(Mockito.eq(TEST_CODE), Mockito.anyCollection())).thenReturn(
				nonVariant);

		final List childrenMock = Mockito.mock(List.class);
		Mockito.when(variantSelectorComponent.getChildren()).thenReturn(childrenMock);

		variantSelectorController.renderVariantSelector(nonVariant);

		// Verify that container is cleared
		Mockito.verify(childrenMock, Mockito.atLeastOnce()).clear();
		// Verify that no component has been added to the container
		Mockito.verify(variantSelectorComponent, Mockito.never()).appendChild(Mockito.any(Component.class));
	}

	@Test
	public void testVariantInput()
	{
		variantSelectorController.getWidgetSettings().put(VariantSelectorController.SETTING_SMARTRENDER, Boolean.FALSE);
		final ProductData variant = Mockito.mock(ProductData.class);
		Mockito.when(variant.getCode()).thenReturn(TEST_CODE);


		final BaseOptionData option1 = Mockito.mock(BaseOptionData.class);
		final BaseOptionData option2 = Mockito.mock(BaseOptionData.class);

		final List<BaseOptionData> baseOptionList = Arrays.asList(option1, option2);
		final List<VariantOptionData> variantOptionList = Arrays.asList(Mockito.mock(VariantOptionData.class),
				Mockito.mock(VariantOptionData.class), Mockito.mock(VariantOptionData.class));

		Mockito.when(option1.getOptions()).thenReturn(variantOptionList);
		Mockito.when(option2.getOptions()).thenReturn(variantOptionList);


		Mockito.when(variant.getBaseOptions()).thenReturn(baseOptionList);
		Mockito.when(variant.getVariantOptions()).thenReturn(variantOptionList);

		Mockito.when(productFacade.getProductForCodeAndOptions(Mockito.eq(TEST_CODE), Mockito.anyCollection())).thenReturn(variant);

		final List childrenMock = Mockito.mock(List.class);
		Mockito.when(variantSelectorComponent.getChildren()).thenReturn(childrenMock);

		variantSelectorController.renderVariantSelector(variant);

		// Verify that container is cleared
		Mockito.verify(childrenMock, Mockito.atLeastOnce()).clear();

		// Verify that three components were added to the container (one combo for variant options, two for baseoptions)
		Mockito.verify(variantSelectorComponent, Mockito.times(3)).appendChild(Mockito.any(Component.class));
	}

	@Test
	public void selectVariantTest()
	{
		/*
		 * Controller should send null to output if product is not found for given code
		 */
		Mockito.when(productFacade.getProductForCodeAndOptions(Mockito.eq(TEST_CODE), Mockito.anyCollection())).thenReturn(null);
		// Execute method
		variantSelectorController.selectVariant(TEST_CODE);
		// Verify
		Mockito.verify(this.widgetInstanceManager, Mockito.atLeastOnce()).sendOutput(
				Mockito.eq(VariantSelectorController.SOCKET_OUT_VARIANT), Mockito.isNull());


		/*
		 * Controller should send a productData object to output if it is found for given code
		 */
		final ProductData variant = Mockito.mock(ProductData.class);
		Mockito.when(variant.getCode()).thenReturn(TEST_CODE);
		Mockito.when(productFacade.getProductForCodeAndOptions(Mockito.eq(TEST_CODE), Mockito.anyCollection())).thenReturn(variant);
		// Execute method
		variantSelectorController.selectVariant(TEST_CODE);
		// Verify that output has been sent once
		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(VariantSelectorController.SOCKET_OUT_VARIANT), Mockito.eq(variant));


		/*
		 * If widgetSetting WIDGETSETTING_SELECT_DEFAULT is false, controller should send a productData object to output
		 * if it is found for given code, even if it has variantOptions
		 */
		final List<VariantOptionData> variantOptionList = Arrays.asList(Mockito.mock(VariantOptionData.class),
				Mockito.mock(VariantOptionData.class), Mockito.mock(VariantOptionData.class));
		Mockito.when(variant.getVariantOptions()).thenReturn(variantOptionList);
		// Execute method
		variantSelectorController.selectVariant(TEST_CODE);
		// Verify that output has been sent a second time
		Mockito.verify(this.widgetInstanceManager, Mockito.times(2)).sendOutput(
				Mockito.eq(VariantSelectorController.SOCKET_OUT_VARIANT), Mockito.eq(variant));

	}


	@Test
	public void selectDefaultVariantTest()
	{
		/*
		 * If widgetSetting WIDGETSETTING_SELECT_DEFAULT is true, controller should send the productData object specified
		 * at one of the variantOptions
		 */
		final String variantCode = "variant";
		final String variant2Code = "variant2";
		widgetInstanceManager.getWidgetSettings().put(VariantSelectorController.SETTING_SELECT_DEFAULT, Boolean.TRUE);
		widgetInstanceManager.getWidgetSettings().put(VariantSelectorController.SETTING_SMARTRENDER, Boolean.TRUE);

		// Prepare data objects
		final ProductData variant = Mockito.mock(ProductData.class);
		Mockito.when(variant.getCode()).thenReturn(TEST_CODE);
		final ProductData variant1FromOption = Mockito.mock(ProductData.class);
		Mockito.when(variant1FromOption.getCode()).thenReturn(variantCode);
		final ProductData variant2FromOption = Mockito.mock(ProductData.class);
		Mockito.when(variant2FromOption.getCode()).thenReturn(variant2Code);
		final VariantOptionData firstVariantOption = Mockito.mock(VariantOptionData.class);
		final VariantOptionData secondVariantOption = Mockito.mock(VariantOptionData.class);

		final VariantOptionQualifierData variantOption1QualifierData = Mockito.mock(VariantOptionQualifierData.class);
		final VariantOptionQualifierData variantOption2QualifierData = Mockito.mock(VariantOptionQualifierData.class);

		Mockito.when(firstVariantOption.getCode()).thenReturn(variantCode);
		Mockito.when(secondVariantOption.getCode()).thenReturn(variant2Code);

		Mockito.when(firstVariantOption.getVariantOptionQualifiers()).thenReturn(
				Collections.singletonList(variantOption1QualifierData));
		Mockito.when(secondVariantOption.getVariantOptionQualifiers()).thenReturn(
				Collections.singletonList(variantOption2QualifierData));
		Mockito.when(variantOption1QualifierData.getQualifier()).thenReturn("testQualifier");
		Mockito.when(variantOption1QualifierData.getValue()).thenReturn("testValue1");
		Mockito.when(variantOption2QualifierData.getQualifier()).thenReturn("testQualifier");
		Mockito.when(variantOption2QualifierData.getValue()).thenReturn("testValue2");

		final List<VariantOptionData> variantOptionList = Arrays.asList(firstVariantOption, secondVariantOption,
				Mockito.mock(VariantOptionData.class));
		Mockito.when(variant.getVariantOptions()).thenReturn(variantOptionList);

		// Prepare facade product getter
		Mockito.when(productFacade.getProductForCodeAndOptions(Mockito.eq(TEST_CODE), Mockito.anyCollection())).thenReturn(variant);
		Mockito.when(productFacade.getProductForCodeAndOptions(Mockito.eq(variantCode), Mockito.anyCollection())).thenReturn(
				variant1FromOption);
		Mockito.when(productFacade.getProductForCodeAndOptions(Mockito.eq(variant2Code), Mockito.anyCollection())).thenReturn(
				variant2FromOption);

		// Execute method
		variantSelectorController.selectVariant(TEST_CODE);
		// Verify
		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(VariantSelectorController.SOCKET_OUT_VARIANT), Mockito.eq(variant1FromOption));
		Mockito.verify(this.widgetInstanceManager, Mockito.times(0)).sendOutput(
				Mockito.eq(VariantSelectorController.SOCKET_OUT_VARIANT), Mockito.eq(variant2FromOption));


		/*
		 * Test if lastSelectedValue cache works. Same test as above, but the cache holds the optionValueLabel of
		 * secondVariantOption, mimic that the user has selected the option with the value "testValue2" for option
		 * "testName"
		 */
		final String optionkey = variantSelectorController.createOptionKey(secondVariantOption);
		final String optionLabel = variantSelectorController.createOptionLabel(secondVariantOption, true, null);
		// Check if createOptionTitle and createOptionLabel have produced the correct values
		Assert.assertEquals("testQualifier", optionkey);
		Assert.assertEquals("testValue2", optionLabel);
		// prepare the selection cache
		variantSelectorController.lastSelectedValueForOptionTitle.put(optionkey, optionLabel);
		// Execute method
		variantSelectorController.selectVariant(TEST_CODE);
		// Verify
		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(VariantSelectorController.SOCKET_OUT_VARIANT), Mockito.eq(variant1FromOption));
		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(VariantSelectorController.SOCKET_OUT_VARIANT), Mockito.eq(variant2FromOption));
		// cleanup
		variantSelectorController.lastSelectedValueForOptionTitle.clear();
	}
}
