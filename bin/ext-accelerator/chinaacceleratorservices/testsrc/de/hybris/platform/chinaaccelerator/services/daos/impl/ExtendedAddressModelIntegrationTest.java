/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.daos.impl;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import javax.annotation.Resource;

import org.junit.Test;


//import de.hybris.platform.core.model.user.CityModel;


public class ExtendedAddressModelIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Test
	public void extendAttributesTest()
	{/*
	  * final int addrOriginCount = flexibleSearchService.search("select {pk} from {Address} ").getCount(); final int
	  * posOriginCount = flexibleSearchService.search("select {pk} from {PointOfService} ").getCount(); final int
	  * cityOriginCount = flexibleSearchService.search("select {pk} from {City} ").getCount();
	  * 
	  * final PointOfServiceModel pos1 = new PointOfServiceModel(); pos1.setName("p1");
	  * pos1.setType(PointOfServiceTypeEnum.STORE); pos1.setSortOrder(20);
	  * 
	  * final CityModel c1 = new CityModel(); c1.setName("c1", Locale.CHINESE); c1.setCode("c1");
	  * 
	  * 
	  * final AddressModel addr1 = new AddressModel(); addr1.setLine1("Room 3001 building 299 Tongren road Shanghai");
	  * addr1.setCity(c1); addr1.setOwner(pos1); modelService.save(addr1);
	  * 
	  * final PointOfServiceModel pos2 = new PointOfServiceModel(); pos2.setName("p2");
	  * pos2.setType(PointOfServiceTypeEnum.STORE); pos2.setSortOrder(10);
	  * 
	  * final CityModel c2 = new CityModel(); c2.setName("c2", Locale.CHINESE); c2.setCode("c2");
	  * 
	  * final AddressModel addr2 = new AddressModel(); addr2.setLine1("Room 3002 building 299 Tongren road Shanghai");
	  * addr2.setCity(c2); addr2.setOwner(pos2); modelService.save(addr2);
	  * 
	  * final PointOfServiceModel pos3 = new PointOfServiceModel(); pos3.setName("p3");
	  * pos3.setType(PointOfServiceTypeEnum.STORE); pos3.setSortOrder(20);
	  * 
	  * final AddressModel addr3 = new AddressModel(); addr3.setLine1("Room 3003 building 299 Tongren road Shanghai");
	  * addr3.setCity(c2); addr3.setOwner(pos3); modelService.save(addr3);
	  * 
	  * final SearchResult<AddressModel> addrResult = (SearchResult)
	  * flexibleSearchService.search("select {pk} from {Address} "); final SearchResult<CityModel> cityResult =
	  * (SearchResult) flexibleSearchService.search("select {pk} from {City} "); final SearchResult<PointOfServiceModel>
	  * posResult = (SearchResult) flexibleSearchService .search("select {pk} from {PointOfService} ");
	  * 
	  * assertNotNull(addrResult); assertEquals(addrOriginCount + 3, addrResult.getCount());
	  * 
	  * assertNotNull(cityResult); assertEquals(cityOriginCount + 2, cityResult.getCount());
	  * 
	  * assertNotNull(posResult); assertEquals(posOriginCount + 3, posResult.getCount());
	  */
	}
}
