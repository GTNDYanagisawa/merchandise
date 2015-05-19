package de.hybris.platform.sap.b2bsappricing.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sap.hybris.integration.models.constants.SapmodelConstants;


/**
 * DefaultB2BSapPartnerService Test
 */
@UnitTest
public class DefaultB2BSapPartnerServiceTest
{

	private DefaultB2BSapPartnerService defaultB2BSapPartnerService;

	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private ModuleConfigurationAccess moduleConfigurationAccess;
	@Mock
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	/**
	 * Set up
	 */
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultB2BSapPartnerService = new DefaultB2BSapPartnerService();
		defaultB2BSapPartnerService.setCommonI18NService(commonI18NService);
		defaultB2BSapPartnerService.setModuleConfigurationAccess(moduleConfigurationAccess);
		defaultB2BSapPartnerService.setB2bUnitService(b2bUnitService);
		defaultB2BSapPartnerService.setB2bCustomerService(b2bCustomerService);
	}

	/**
	 * Test Get B2C Or Anonymous Partner Function
	 */
	@Test
	public void testGetB2COrAnonymousPartnerFunction()
	{
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("USD");

		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("EN");

		when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
		when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
		when(moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_REFERENCE_CUSTOMER)).thenReturn("JV01");


		assertNotNull(defaultB2BSapPartnerService.getPartnerFunction());

		assertEquals("Currency equals", "USD", defaultB2BSapPartnerService.getPartnerFunction().getCurrency());
		assertEquals("Language equals", "EN", defaultB2BSapPartnerService.getPartnerFunction().getLanguage());
		assertEquals("Sold to equals", "JV01", defaultB2BSapPartnerService.getPartnerFunction().getSoldTo());
	}

	/**
	 * Test Get B2B Partner Function
	 */
	@Test
	public void testGetB2BPartnerFunction()
	{
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("USD");

		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("EN");

		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		final B2BUnitModel parent = new B2BUnitModel();
		parent.setUid("B2BUint");


		when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
		when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
		when(b2bCustomerService.getCurrentB2BCustomer()).thenReturn(b2bCustomer);
		when(b2bUnitService.getParent(b2bCustomer)).thenReturn(parent);

		assertNotNull(defaultB2BSapPartnerService.getPartnerFunction());

		assertEquals("Currency equals", "USD", defaultB2BSapPartnerService.getPartnerFunction().getCurrency());
		assertEquals("Language equals", "EN", defaultB2BSapPartnerService.getPartnerFunction().getLanguage());
		assertEquals("Sold to equals", "B2BUint", defaultB2BSapPartnerService.getPartnerFunction().getSoldTo());
	}

}
