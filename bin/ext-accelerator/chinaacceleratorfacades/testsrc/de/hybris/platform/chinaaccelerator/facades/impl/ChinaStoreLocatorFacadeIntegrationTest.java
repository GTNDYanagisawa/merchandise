// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.chinaaccelerator.facades.StoreData;
import de.hybris.platform.chinaaccelerator.facades.data.CityData;
import de.hybris.platform.chinaaccelerator.facades.storefinder.ChinaStoreLocatorFacade;
import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;


public class ChinaStoreLocatorFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private ChinaStoreLocatorFacade storeLocatorFacade;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Test
	public void testGetCitiesOnlyWithStores()
	{
		final BaseStoreModel baseStoreModel = this.modelService.create(BaseStoreModel.class);
		baseStoreModel.setUid("xxxxxxxxxxx");

		final int count = storeLocatorFacade.getCitiesOnlyWithStores().size();
		final PointOfServiceModel pos1 = this.modelService.create(PointOfServiceModel.class); // new PointOfServiceModel();
		pos1.setName("p1");
		pos1.setType(PointOfServiceTypeEnum.STORE);
		pos1.setSortOrder(9020);
		if (baseStoreModel != null)
		{
			pos1.setBaseStore(baseStoreModel);
		}


		final CityModel c1 = this.modelService.create(CityModel.class); //new CityModel();
		c1.setName("c1");
		c1.setSortOrder(9010);
		c1.setCode("TEST-CITY-CODE-1");

		final AddressModel addr1 = new AddressModel();
		addr1.setLine1("Room 3001 building 299 Tongren road Shanghai");
		addr1.setCity(c1);
		addr1.setOwner(pos1);
		pos1.setAddress(addr1);
		modelService.save(addr1);

		final PointOfServiceModel pos2 = this.modelService.create(PointOfServiceModel.class); //new PointOfServiceModel();
		pos2.setName("p2");
		pos2.setType(PointOfServiceTypeEnum.STORE);
		if (baseStoreModel != null)
		{
			pos2.setBaseStore(baseStoreModel);
		}
		pos2.setSortOrder(9010);

		final CityModel c2 = this.modelService.create(CityModel.class); //new CityModel();
		c2.setName("c2");
		c2.setSortOrder(9020);
		c2.setCode("TEST-CITY-CODE-2");

		final AddressModel addr2 = new AddressModel();
		addr2.setLine1("Room 3002 building 299 Tongren road Shanghai");
		addr2.setCity(c2);
		addr2.setOwner(pos2);
		pos2.setAddress(addr2);
		modelService.save(addr2);

		final PointOfServiceModel pos3 = this.modelService.create(PointOfServiceModel.class); // new PointOfServiceModel();
		pos3.setName("p3");
		pos3.setType(PointOfServiceTypeEnum.STORE);
		pos3.setSortOrder(9030);
		if (baseStoreModel != null)
		{
			pos3.setBaseStore(baseStoreModel);
		}

		final AddressModel addr3 = new AddressModel();
		addr3.setLine1("Room 3003 building 299 Tongren road Shanghai");
		addr3.setCity(c2);
		addr3.setOwner(pos3);
		pos3.setAddress(addr3);
		modelService.save(addr3);

		final CityModel c3 = this.modelService.create(CityModel.class); // new CityModel();
		c3.setName("c3");
		c3.setSortOrder(9030);
		c3.setCode("TEST-CITY-CODE-3");
		modelService.save(c3);

		final List<CityData> result = storeLocatorFacade.getCitiesOnlyWithStores();
		assertNotNull(result);

		assertEquals(count + 2, result.size());

		assertEquals(c2.getPk().getLong(), result.get(0).getCityPK());
		assertEquals(c2.getName(), result.get(0).getCityName());

		assertEquals(c1.getPk().getLong(), result.get(1).getCityPK());
		assertEquals(c1.getName(), result.get(1).getCityName());
	}


	@Test
	public void testGetAllCities()
	{
		final int count = storeLocatorFacade.getAllCities().size();
		final PointOfServiceModel pos1 = this.modelService.create(PointOfServiceModel.class); // new PointOfServiceModel();
		pos1.setName("p1");
		pos1.setType(PointOfServiceTypeEnum.STORE);
		pos1.setSortOrder(9020);

		final CityModel c1 = this.modelService.create(CityModel.class); // new CityModel();
		c1.setName("c1");
		c1.setSortOrder(9010);
		c1.setCode("TEST-CITY-CODE-11");

		final AddressModel addr1 = new AddressModel();
		addr1.setLine1("Room 3001 building 299 Tongren road Shanghai");
		addr1.setCity(c1);
		addr1.setOwner(pos1);
		pos1.setAddress(addr1);
		modelService.save(addr1);

		final PointOfServiceModel pos2 = this.modelService.create(PointOfServiceModel.class); // new PointOfServiceModel();
		pos2.setName("p2");
		pos2.setType(PointOfServiceTypeEnum.STORE);
		pos2.setSortOrder(9010);

		final CityModel c2 = this.modelService.create(CityModel.class); //  new CityModel();
		c2.setName("c2");
		c2.setSortOrder(9030);
		c2.setCode("TEST-CITY-CODE-22");

		final AddressModel addr2 = new AddressModel();
		addr2.setLine1("Room 3002 building 299 Tongren road Shanghai");
		addr2.setCity(c2);
		addr2.setOwner(pos2);
		pos2.setAddress(addr2);
		modelService.save(addr2);

		final PointOfServiceModel pos3 = this.modelService.create(PointOfServiceModel.class); // new PointOfServiceModel();
		pos3.setName("p3");
		pos3.setType(PointOfServiceTypeEnum.STORE);
		pos3.setSortOrder(9030);

		final AddressModel addr3 = new AddressModel();
		addr3.setLine1("Room 3003 building 299 Tongren road Shanghai");
		addr3.setCity(c2);
		addr3.setOwner(pos3);
		pos3.setAddress(addr3);
		modelService.save(addr3);

		final CityModel c3 = this.modelService.create(CityModel.class); //new CityModel();
		c3.setName("c3");
		c3.setSortOrder(9020);
		c3.setCode("TEST-CITY-CODE-33");
		modelService.save(c3);


		modelService.saveAll();

		final List<CityData> result = storeLocatorFacade.getAllCities();
		assertNotNull(result);
		assertEquals(count + 3, result.size());

		// check City2 is ranked 1st
		assertEquals(c2.getPk().getLong(), result.get(0).getCityPK()); // if PK is different, check sortOrder of imported city in impex files as null is desc-ordered on top
		assertEquals(c2.getName(), result.get(0).getCityName());

		assertEquals(2, result.get(0).getStoreCount().intValue());

		// check City3 is ranked 2nd
		assertEquals(c3.getPk().getLong(), result.get(1).getCityPK());
		assertEquals(c3.getName(), result.get(1).getCityName());

		assertEquals(0, result.get(1).getStoreCount().intValue());

		// check City1 is ranked 3rd
		assertEquals(c1.getPk().getLong(), result.get(2).getCityPK());
		assertEquals(c1.getName(), result.get(2).getCityName());

		assertEquals(1, result.get(2).getStoreCount().intValue());
	}

	@Test
	public void testGetStoresByCity()
	{

		final BaseStoreModel baseStoreModel = this.modelService.create(BaseStoreModel.class);
		baseStoreModel.setUid("yyyyyyyyyyyy");
		final PointOfServiceModel pos1 = this.modelService.create(PointOfServiceModel.class); //new PointOfServiceModel();
		pos1.setName("p1");
		pos1.setType(PointOfServiceTypeEnum.STORE);
		pos1.setSortOrder(20);
		if (baseStoreModel != null)
		{
			pos1.setBaseStore(baseStoreModel);
		}

		final CityModel c1 = this.modelService.create(CityModel.class); // new CityModel();
		c1.setName("c1");
		c1.setSortOrder(10);
		c1.setCode("TEST-CITY-CODE-111");

		final AddressModel addr1 = new AddressModel();
		addr1.setLine1("Room 3001 building 299 Tongren road Shanghai");
		addr1.setCity(c1);
		addr1.setOwner(pos1);
		pos1.setAddress(addr1);
		modelService.save(addr1);

		final PointOfServiceModel pos2 = this.modelService.create(PointOfServiceModel.class); //new PointOfServiceModel();
		pos2.setName("p2");
		pos2.setType(PointOfServiceTypeEnum.STORE);
		pos2.setSortOrder(10);
		if (baseStoreModel != null)
		{
			pos2.setBaseStore(baseStoreModel);
		}

		final CityModel c2 = this.modelService.create(CityModel.class); //new CityModel();
		c2.setName("c2");
		c2.setSortOrder(30);
		c2.setCode("TEST-CITY-CODE-222");

		final AddressModel addr2 = new AddressModel();
		addr2.setLine1("Room 3002 building 299 Tongren road Shanghai");
		addr2.setCity(c2);
		addr2.setOwner(pos2);
		pos2.setAddress(addr2);
		modelService.save(addr2);

		final PointOfServiceModel pos3 = this.modelService.create(PointOfServiceModel.class); //new PointOfServiceModel();
		pos3.setName("p3");
		pos3.setType(PointOfServiceTypeEnum.STORE);
		pos3.setSortOrder(30);
		pos3.setBaseStore(baseStoreModel);

		final AddressModel addr3 = new AddressModel();
		addr3.setLine1("Room 3003 building 299 Tongren road Shanghai");
		addr3.setCity(c2);
		addr3.setOwner(pos3);
		pos3.setAddress(addr3);
		modelService.save(addr3);

		final CityModel c3 = this.modelService.create(CityModel.class); // new CityModel();
		c3.setName("c3");
		c3.setSortOrder(20);
		c3.setCode("TEST-CITY-CODE-333");
		modelService.save(c3);

		final List<StoreData> result = storeLocatorFacade.getStoresByCities(c2.getPk().getLong());
		assertNotNull(result);

		assertEquals(2, result.size());



	}
}
