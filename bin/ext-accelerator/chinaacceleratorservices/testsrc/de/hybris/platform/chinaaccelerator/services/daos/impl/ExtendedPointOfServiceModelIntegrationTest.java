/**
 * 
 */
package de.hybris.platform.chinaaccelerator.services.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import javax.annotation.Resource;

import org.junit.Test;


public class ExtendedPointOfServiceModelIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Test
	public void extendAttributesTest()
	{
		final SearchResult<PointOfServiceModel> result = (SearchResult) flexibleSearchService
				.search("select {pk} from {PointOfService} ");


		final PointOfServiceModel pos1 = new PointOfServiceModel();
		pos1.setName("p1");
		pos1.setType(PointOfServiceTypeEnum.STORE);
		pos1.setSortOrder(20);
		modelService.save(pos1);

		final PointOfServiceModel pos2 = new PointOfServiceModel();
		pos2.setName("p2");
		pos2.setType(PointOfServiceTypeEnum.STORE);
		pos2.setSortOrder(10);
		modelService.save(pos2);

		final PointOfServiceModel pos3 = new PointOfServiceModel();
		pos3.setName("p3");
		pos3.setType(PointOfServiceTypeEnum.STORE);
		modelService.save(pos3);

		final SearchResult<PointOfServiceModel> result2 = (SearchResult) flexibleSearchService
				.search("select {pk} from {PointOfService}  order by {sortOrder} DESC  ");

		final SearchResult<PointOfServiceModel> result3 = (SearchResult) flexibleSearchService
				.search("select {pk} from {PointOfService} where {sortOrder} is not null order by {sortOrder} DESC  ");

		assertNotNull(result2);
		assertTrue(result2.getCount() == result.getCount() + 3);

		assertEquals(result3.getResult().get(0), pos1);
		assertEquals(result3.getResult().get(1), pos2);
	}
}
