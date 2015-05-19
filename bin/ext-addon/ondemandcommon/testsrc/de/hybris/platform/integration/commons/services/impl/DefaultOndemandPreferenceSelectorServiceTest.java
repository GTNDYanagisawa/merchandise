package de.hybris.platform.integration.commons.services.impl;


import com.hybris.commons.tenant.TenantContextService;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integration.commons.model.OndemandBaseStorePreferenceModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import java.util.List;
import javax.annotation.Resource;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultOndemandPreferenceSelectorServiceTest extends ServicelayerTest
{
	@Resource
	private BaseStoreService baseStoreService;
	@Resource
	private TenantContextService tenantContextService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private DefaultOndemandPreferenceSelectorService defaultOndemandPreferenceSelectorService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/ondemandcommon/test/onDemandTenantPrefSelector.impex", "utf-8");
		final BaseSiteModel baseSiteForUID = baseSiteService.getBaseSiteForUID("test-uid");
		baseSiteService.setCurrentBaseSite(baseSiteForUID, false);
	}

	@Test
	public void testGetCurrentOndemandTenantPreference()
	{
		Assert.assertNotNull(defaultOndemandPreferenceSelectorService.getCurrentOndemandPreference());
	}

	@Test
	public void testGetAllOndemandTenantPreferences()
	{
		final List<OndemandBaseStorePreferenceModel> ondemandTenantBaseStorePreferenceModelList = defaultOndemandPreferenceSelectorService
				.getAllOndemandPreferences();
		Assert.assertEquals(1, ondemandTenantBaseStorePreferenceModelList.size());

	}

	@Test
	public void testGetOndemandTenantPreferenceForStore()
	{
		final OndemandBaseStorePreferenceModel ondemandTenantBaseStorePreferenceModel = defaultOndemandPreferenceSelectorService
				.getOndemandTenantPreferenceForStore(baseStoreService.getCurrentBaseStore());
		Assert.assertNotNull(ondemandTenantBaseStorePreferenceModel);
	}

	@Test
	public void testIsExternalCallsEnabledForSite()
	{
		Assert.assertEquals(true, defaultOndemandPreferenceSelectorService.isExternalCallsEnabledForSite());
	}
}
