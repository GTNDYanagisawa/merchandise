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
 */

package de.hybris.platform.entitlementatddtests.keywords;

import static de.hybris.platform.atddengine.xml.XmlAssertions.assertXPathEvaluatesTo;
import static org.junit.Assert.fail;

import de.hybris.platform.atddengine.keywords.AbstractKeywordLibrary;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.entitlementatddtests.converters.ObjectXStreamAliasConverter;
import de.hybris.platform.entitlementfacades.data.EntitlementData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


public class EntitlementKeywordLibrary extends AbstractKeywordLibrary
{
	private static final Logger LOG = Logger.getLogger(EntitlementKeywordLibrary.class);

	@Autowired
	private ProductFacade productFacade;

	@Autowired
	private ObjectXStreamAliasConverter xStreamAliasConverter;

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify product xml</i>
	 * <p>
	 * 
	 * @param productCode
	 *           code the code of the product to verify
	 * @param xpath
	 *           the XPath expression to evaluate
	 * @param expectedXml
	 *           the expected XML
	 */
	public void verifyProductXml(final String productCode, final String xpath, final String expectedXml)
	{
		try
		{
			final ProductData product = productFacade.getProductForCodeAndOptions(productCode,
					Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));
			final String productXml = xStreamAliasConverter.getXStreamXmlFromObject(product);

			assertXPathEvaluatesTo("The product XML does not match the expectations:", productXml, xpath, expectedXml,
					"transformation/IgnoreEntitlementIdsAndEmptyLists.xsl");
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.error("Product with code " + productCode + " does not exist", e);
			fail("Product with code " + productCode + " does not exist");
		}
		catch (final IllegalArgumentException e)
		{
			LOG.error("Either the expected XML is malformed or the product code is null", e);
			fail("Either the expected XML is malformed or the product code is null");
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify entitlement multiple</i>
	 * <p>
	 * 
	 * @param productCode
	 */
	public void verifyEntitlementMultiple(final String productCode)
	{
		final ProductData product = productFacade.getProductForCodeAndOptions(productCode,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));

		final List<EntitlementData> entitlements = product.getEntitlements();

		if (entitlements == null)
		{
			final String errorMessage = "entitlements are null";
			LOG.error(errorMessage);
			fail(errorMessage);
		}

		final HashMap<String, Integer> entitlementTypes = new HashMap<>();

		for (final EntitlementData entitlement : entitlements)
		{
			final String name = entitlement.getName();
			if (entitlementTypes.containsKey(name))
			{
				final Integer value = entitlementTypes.get(name);
				if (value != null)
				{
					entitlementTypes.put(name, value + 1);
				}

			}
			else
			{
				entitlementTypes.put(name, 1);
			}
		}

		for (final String entType : entitlementTypes.keySet())
		{
			if (entitlementTypes.get(entType) > 1)
			{
				LOG.info(entitlementTypes.get(entType).toString() + " entitlements of type " + entType + " found!");
			}
		}
	}
}
