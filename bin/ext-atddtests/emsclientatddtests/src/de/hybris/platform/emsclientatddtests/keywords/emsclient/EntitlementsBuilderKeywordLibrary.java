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
package de.hybris.platform.emsclientatddtests.keywords.emsclient;

import static de.hybris.platform.atddengine.xml.XmlAssertions.assertXPathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import de.hybris.platform.atddengine.keywords.AbstractKeywordLibrary;
import de.hybris.platform.entitlementatddtests.converters.ObjectXStreamAliasConverter;
import de.hybris.platform.entitlementfacades.CoreEntitlementFacade;
import de.hybris.platform.entitlementfacades.data.EntitlementData;
import de.hybris.platform.entitlementservices.data.EmsGrantData;
import de.hybris.platform.entitlementservices.enums.EntitlementTimeUnit;
import de.hybris.platform.entitlementservices.exception.EntitlementFacadeException;
import de.hybris.platform.entitlementservices.facades.EntitlementFacadeDecorator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.*;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.hybris.services.entitlements.api.GrantData;


public class EntitlementsBuilderKeywordLibrary extends AbstractKeywordLibrary
{
	public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";

	private static final Logger LOG = Logger.getLogger(EntitlementsBuilderKeywordLibrary.class);
	private static final DateTimeFormatter PARSER = ISODateTimeFormat.dateTimeParser();

	private final Random random = new Random();


	@Autowired
	private EntitlementFacadeDecorator entitlementFacadeDecorator;

	@Autowired
	private ObjectXStreamAliasConverter xStreamAliasConverter;

	@Autowired
	@Qualifier("defaultEmsGrantGrantConverter")
	private Converter<EmsGrantData, GrantData> emsGrantGrantConverter;

    @Autowired
    @Qualifier("coreEntitlementFacade")
    private CoreEntitlementFacade coreEntitlementFacade;

	public EmsGrantData buildEmsGrantData(final String entitlementType, final String conditionString, final Integer maxQuantity,
			final String entitlementTimeUnit, final String timeUnitStart, final String timeUnitDuration, final String conditionPath,
			final String conditionGeo, final String dateCreatedAt, String UserId)
	{
		final EmsGrantData emsGrantData = new EmsGrantData();
		emsGrantData.setEntitlementType(entitlementType);
		emsGrantData.setConditionString(conditionString);
		emsGrantData.setMaxQuantity(maxQuantity);
		if (dateCreatedAt != null)
		{
			final Date date = PARSER.parseDateTime(dateCreatedAt).toDate();
			emsGrantData.setCreatedAt(date);
		}
		if (conditionGeo != null)
		{
			emsGrantData.setConditionGeo(new ArrayList<>(Arrays.asList(conditionGeo.split(","))));
		}
		if (entitlementTimeUnit != null)
		{
			emsGrantData.setTimeUnit(EntitlementTimeUnit.valueOf(entitlementTimeUnit));
		}

		emsGrantData.setConditionPath(conditionPath);

		if (timeUnitStart != null)
		{
			emsGrantData.setTimeUnitStart(Integer.parseInt(timeUnitStart));
		}

		if (timeUnitDuration != null)
		{
			emsGrantData.setTimeUnitDuration(Integer.parseInt(timeUnitDuration));
		}

		emsGrantData.setUserId(UserId);
		emsGrantData.setOrderCode(getRandomString("order_code"));
		emsGrantData.setBaseStoreUid(getRandomString("base_store_uid"));
		emsGrantData.setOrderEntryNumber(getRandomString("order_entry_number"));

		return emsGrantData;
	}

	public EmsGrantData buildEmsGrantData(final String entitlementType, final String conditionString, final Integer maxQuantity,
			final String entitlementTimeUnit, final String timeUnitStart, final String timeUnitDuration, final String conditionPath,
			final String conditionGeo, final String dateCreatedAt)
	{
		EmsGrantData emsGrantData = buildEmsGrantData(entitlementType, conditionString, maxQuantity, entitlementTimeUnit, timeUnitStart,
				timeUnitDuration, conditionPath, conditionGeo, dateCreatedAt, null);
		emsGrantData.setUserId(UUID.randomUUID().toString());
		return emsGrantData;
	}



	public EmsGrantData buildEmsGrantData(final String entitlementType, final String conditionString, final Integer maxQuantity)
	{
		return buildEmsGrantData(entitlementType, conditionString, maxQuantity, null, null, null, null, null, null);
	}

	public String grantEmsEntitlement(final EmsGrantData emsGrantData) throws EntitlementFacadeException
	{
		//		final Date createdAtDate = new Date();// TODO parse from createdAt
		return entitlementFacadeDecorator.createEntitlement(emsGrantData);
	}


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
	public void verifyObjectXml(final EmsGrantData emsGrantData, final String xpath, final String expectedXml)
	{
		assertNotNull(emsGrantGrantConverter);
		assertNotNull(xStreamAliasConverter);
		try
		{
			final GrantData grantData = emsGrantGrantConverter.convert(emsGrantData);
			final String grantXml = xStreamAliasConverter.getXStreamXmlFromObject(grantData);
			assertXPathEvaluatesTo("The product XML does not match the expectations:", grantXml, xpath, expectedXml,
					"transformation/IgnoreGrantIds.xsl");
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
     * <i>verify grants xml for user</i>
     * <p>
     *
     * @param userId
     *           the identificator of the user to verify
     * @param expectedXml
     *           the expected XML
     */
    public void verifyGrantsXmlForUser(final String userId, final String expectedXml)
    {
        assertNotNull(coreEntitlementFacade);
        assertNotNull(xStreamAliasConverter);
        try
        {
            final Collection<EntitlementData> userGrants = coreEntitlementFacade.getUserGrants(userId);

            final EntitlementDataList entitlementDataList = new EntitlementDataList();
            entitlementDataList.setEntitlements(userGrants);

            xStreamAliasConverter.getXstream().alias("entitlementsList", EntitlementDataList.class);
            xStreamAliasConverter.getXstream().aliasField("entitlements", EntitlementDataList.class, "entitlements");

            final String entitlementXml = xStreamAliasConverter.getXStreamXmlFromObject(entitlementDataList);
            assertXPathEvaluatesTo("The entitlements XML does not match the expectations:", entitlementXml,
                    "/entitlementsList/entitlements", expectedXml,
                    "transformation/IgnoreGrantIdsAndGrantTime.xsl");
        }
        catch (final IllegalArgumentException e)
        {
            LOG.error("Either the expected XML is malformed or the user id is null", e);
            fail("Either the expected XML is malformed or the user id is null");
        }
    }

	private String getRandomString(final String prefix)
	{
		return prefix + '_' + random.nextInt();
	}

}
