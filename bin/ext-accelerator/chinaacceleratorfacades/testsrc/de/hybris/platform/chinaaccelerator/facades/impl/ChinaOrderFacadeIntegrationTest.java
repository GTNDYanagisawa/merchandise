// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.chinaaccelerator.facades.order.impl.ChinaOrderFacadeImpl;
import de.hybris.platform.commercefacades.order.converters.populator.OrderHistoryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Matchers;
import org.mockito.Mockito;


public class ChinaOrderFacadeIntegrationTest extends ServicelayerTransactionalTest
{

	private static final Logger LOG = Logger.getLogger(ChinaOrderFacadeIntegrationTest.class);

	@Resource
	private Converter<OrderModel, OrderHistoryData> orderHistoryConverter;
	@Resource
	private OrderHistoryPopulator orderHistoryPopulator;

	@Test
	public void testGetPagedOrderHistoryForStatuses()
	{

		//final List<OrderHistoryData> orderHistoryList = Mockito.mock(List.class);


		final SearchStateData searchStateData = Mockito.mock(SearchStateData.class);
		final PageableData pageableData = Mockito.mock(PageableData.class);
		final OrderStatus statuses = Mockito.mock(OrderStatus.class);

		final BaseStoreModel baseStoreModel = Mockito.mock(BaseStoreModel.class);
		final CustomerModel currentCustomer = Mockito.mock(CustomerModel.class);
		final SearchPageData<OrderModel> source = Mockito.mock(SearchPageData.class);


		final List<SortData> sortllist = new ArrayList<SortData>();
		final SortData sdata = new SortData();
		sdata.setCode("c");
		sortllist.add(sdata);

		BDDMockito.when(source.getSorts()).thenReturn(sortllist);


		final List<OrderModel> orderCollect = new ArrayList<OrderModel>();
		final OrderModel order = new OrderModel();
		order.setCode("TEST-ORDER-CODE");
		orderCollect.add(order);
		BDDMockito.when(source.getResults()).thenReturn(orderCollect);


		final de.hybris.platform.store.services.BaseStoreService baseStoreService = Mockito
				.mock(de.hybris.platform.store.services.BaseStoreService.class);
		final CustomerAccountService customerAccountService = Mockito.mock(CustomerAccountService.class);
		final UserService userService = Mockito.mock(UserService.class);

		BDDMockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		BDDMockito.when(userService.getCurrentUser()).thenReturn(currentCustomer);
		BDDMockito.when(
				customerAccountService.getOrderList(Matchers.any(CustomerModel.class), Matchers.any(BaseStoreModel.class),
						Matchers.any(de.hybris.platform.core.enums.OrderStatus[].class), Matchers.any(PageableData.class))).thenReturn(
				source);
		final ChinaOrderFacadeImpl extOrderFacade = new ChinaOrderFacadeImpl();
		extOrderFacade.setBaseStoreService(baseStoreService);
		extOrderFacade.setCustomerAccountService(customerAccountService);
		extOrderFacade.setUserService(userService);


		extOrderFacade.setOrderHistoryConverter(orderHistoryConverter);
		final FacetSearchPageData<SearchStateData, OrderHistoryData> result = extOrderFacade.getPagedOrderHistoryForStatuses(
				searchStateData, pageableData, statuses);
		assertNotNull(result);
		assertNotNull(result.getSorts());
		assertEquals(result.getSorts().size(), 1);
		assertEquals(result.getSorts().get(0).getCode(), sdata.getCode());


		assertEquals(result.getResults().size(), 1);
		assertEquals(result.getResults().get(0).getCode(), "TEST-ORDER-CODE");
	}

}