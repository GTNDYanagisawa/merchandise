// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.populators;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.chinaaccelerator.facades.data.CityData;
import de.hybris.platform.chinaaccelerator.services.enums.InvoiceCategory;
import de.hybris.platform.chinaaccelerator.services.model.invoice.InvoiceModel;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;


public class AccExtCartPopulatorIntegrationTest extends ServicelayerTransactionalTest
{
	private static final Logger LOG = Logger.getLogger(AccExtCartPopulatorIntegrationTest.class);

	@Resource
	private CartService cartService;

	@Resource
	private ChinaCartPopulator cartPopulator;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private ModelService modelService;

	@Resource
	ChinaAddressReversePopulator chinaAddressReversePopulator;

	@Resource
	CityPopulator cityPopulator;

	@Ignore
	@Test
	public void testCartPopulates()
	{
		final CartModel source = cartService.getSessionCart();
		final CartData target = new CartData();

		final InvoiceModel invoice = new InvoiceModel();
		invoice.setCategory(InvoiceCategory.FOOD);
		source.setInvoice(invoice);


		cartPopulator.populate(source, target);

		assertEquals(target.getInvoice().getInvoicedCategory(), InvoiceCategory.FOOD.getCode());
	}


	@Test
	public void testCityPopulates()
	{

		final CityModel cityModel = modelService.create(CityModel.class); //new CityModel();
		cityModel.setActive(Boolean.TRUE);
		cityModel.setCode("TEST-CITY-CODE");
		cityModel.setName("TEST-CITY-NAME"); // if just new CityModel(), then [mjava.lang.IllegalStateException: there is no LocaleProvider for (detached) model de.hybris.platform.servicelayer.model.ItemModelContextImpl@6b6955aa

		final RegionModel regionModel = modelService.create(RegionModel.class);
		regionModel.setActive(Boolean.TRUE);
		//regionModel.setName("TEST-REGION-NAME", Locale.ENGLISH);
		regionModel.setName("TEST-REGION-NAME");
		regionModel.setIsocode("TEST-REGION-CODE");
		final CountryModel countryModel = modelService.create(CountryModel.class);
		countryModel.setActive(Boolean.TRUE);
		countryModel.setName("TEST-COUNTRY-NAME-EN");
		countryModel.setName("TEST-COUNTRY-NAME-ZH");
		countryModel.setIsocode("TEST-COUNTRY-CODE");
		modelService.save(countryModel);
		regionModel.setCountry(countryModel);
		modelService.save(regionModel);
		cityModel.setRegion(regionModel);
		modelService.save(cityModel);

		final CityData target = new CityData();
		cityPopulator.populate(cityModel, target);
		assertEquals(target.getName(), cityModel.getName());

	}

	@Test
	public void testAddressPopulates()
	{
		final de.hybris.platform.commercefacades.user.data.AddressData addressData = new de.hybris.platform.commercefacades.user.data.AddressData();
		addressData.setBillingAddress(false);
		addressData.setCellphone("13434534567");


		final DistrictModel districtModel = modelService.create(DistrictModel.class); //new DistrictModel();
		districtModel.setActive(Boolean.TRUE);
		districtModel.setCode("TEST-DISTRICT-CODE");
		districtModel.setName("TEST-DISTRICT-NAME"); // if just new DistrictModel(), then here: [mjava.lang.IllegalStateException: there is no LocaleProvider for (detached) model de.hybris.platform.servicelayer.model.ItemModelContextImpl@6b6955aa


		final CityModel cityModel = modelService.create(CityModel.class); //new DistrictModel();
		cityModel.setActive(Boolean.TRUE);
		cityModel.setCode("TEST-CITY-CODE");
		cityModel.setName("TEST-CITY-NAME");
		modelService.save(cityModel);
		modelService.save(districtModel);
		final CityData citydata = new CityData();
		cityPopulator.populate(cityModel, citydata);
		addressData.setCityData(citydata);

		//		final RegionModel regionModel = new RegionModel();
		//		regionModel.setIsocode("TEST-REGION-ISOCODE");
		//		regionModel.setActive(Boolean.TRUE);
		//		regionModel.setName("TEST-REGION-NAME-EN", Locale.ENGLISH);
		//		regionModel.setName("TEST-REGION-NAME-ZH", Locale.CHINESE);
		//
		//		cityModel.setRegion(regionModel);
		final AddressModel addressModel = new AddressModel();
		chinaAddressReversePopulator.populate(addressData, addressModel);
		assertEquals(addressData.getCityData().getCode(), addressModel.getCity().getCode());
	}
}