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
 */

package de.hybris.platform.integration.oms.order.populators;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.hybris.oms.domain.address.Address;


@IntegrationTest
@Ignore("the reverse populators for models which dynamic attributes don't work when crating the model via a factory method")
public class OmsAddressReverseConverterIntegrationTest extends ServicelayerTest
{
	@Resource
	private Converter<Address, AddressModel> omsAddressReverseConverter;

	@Resource
	private BaseSiteService baseSiteService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/omsorders/test/testAcceleratorData.csv", "UTF-8");

		final BaseSiteModel site = baseSiteService.getBaseSiteForUID("testSite");
		Assert.assertNotNull("no baseSite with uid 'testSite", site);
		site.setChannel(SiteChannel.B2C);
		baseSiteService.setCurrentBaseSite(site, false);
	}

	@Test
	public void shouldConvertOmsAddress() throws Exception
	{
		final Address omsAddress = new Address();
		omsAddress.setAddressLine1("line1");
		omsAddress.setAddressLine2("line2");
		omsAddress.setCityName("City Name");
		omsAddress.setCountryIso3166Alpha2Code("US");
		omsAddress.setCountrySubentity("US-NY");
		omsAddress.setPostalZone("12345");

		final AddressModel addressModel = omsAddressReverseConverter.convert(omsAddress);
		Assert.assertEquals(omsAddress.getAddressLine1(), addressModel.getLine1());
		Assert.assertEquals(omsAddress.getAddressLine2(), addressModel.getLine2());
		Assert.assertEquals(omsAddress.getCountryIso3166Alpha2Code(), addressModel.getCountry().getIsocode());
		Assert.assertEquals(omsAddress.getCountrySubentity(), addressModel.getRegion().getIsocode());
		Assert.assertEquals(omsAddress.getPostalZone(), addressModel.getPostalcode());
	}
}
