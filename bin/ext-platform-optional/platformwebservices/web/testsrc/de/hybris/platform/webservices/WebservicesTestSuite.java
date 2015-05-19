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
package de.hybris.platform.webservices;

import de.hybris.platform.webservices.functional.Case1Test;
import de.hybris.platform.webservices.objectgraphtransformer.AttributeSelectorTest;
import de.hybris.platform.webservices.objectgraphtransformer.YGraphTransformerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
{ AddressResourceTest.class, //
		AddressesResourceTest.class, //
		AdvancePaymentInfoResourceTest.class, //
		AttributeSelectorTest.class, //
		CartEntriesResourceTest.class, //
		CartEntryResourceTest.class, //
		CatalogsResourceTest.class, //
		CatalogsResourceSelectorTest.class, //
		CatalogResourceTest.class,//
		CatalogVersionResourceTest.class, //
		CategoryResourceTest.class, //
		CompaniesResourceTest.class, //
		CompanyResourceTest.class, //
		CountryResourceTest.class, //
		CountriesResourceTest.class, //
		CreditCardPaymentInfoResourceTest.class, //
		CronJobResourceTest.class, //		
		CustomerResourceTest.class, //
		DebitPaymentInfoResourceTest.class, //
		DeliveryModeResourceTest.class, //
		DeliveryModesResourceTest.class, //
		DynamicComparatorTest.class, //
		EnumerationResourceTest.class, //
		InvoicePaymentInfoResourceTest.class, //
		KeywordResourceTest.class, //
		KeywordsResourceTest.class, //
		LanguageResourceTest.class, //
		LanguagesResourceTest.class, //
		LoginResourceTest.class, //
		MediaResourceTest.class, //
		PaymentInfosResourceTest.class, //
		PaymentModeResourceTest.class, //
		PaymentModesResourceTest.class, //
		PriceResourceTest.class, //
		ProductResourceTest.class, //
		RegionResourceTest.class, //
		RegionsResourceTest.class, //
		RestJerseyTest.class, //
		RetrievePasswordResourceTest.class, //
		UnitResourceTest.class, //
		UnitsResourceTest.class, //
		UsersResourceSelectorTest.class, //
		UsersResourceTest.class,//
		Case1Test.class,//
		YGraphTransformerTest.class,//
		AccessManagerSecurityStrategyTest.class //
})
public class WebservicesTestSuite
{
	//


}
