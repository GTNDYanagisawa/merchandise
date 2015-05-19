/**
 * 
 */
package de.hybris.platform.integration.oms.adapter;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.integration.oms.OrderWrapper;
import de.hybris.platform.integration.oms.ShipmentWrapper;
import de.hybris.platform.integration.oms.mapping.OmsHybrisEnumMappingStrategy;
import de.hybris.platform.omsorders.services.query.daos.SyncDao;
import de.hybris.platform.ordercancel.OrderCancelCallbackService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.oms.domain.order.Order;


/**
 * 
 */
public class DefaultOmsOrderSyncAdapterTest
{

	private static final String ORDER_ID = "orderId";


	@InjectMocks
	private final OmsSyncAdapter<Order, OrderModel> omsOrderSyncAdapter = new DefaultOmsOrderSyncAdapter();

	@Mock
	private OmsSyncAdapter<ShipmentWrapper, ConsignmentModel> omsShipmentSyncAdapter;
	@Mock
	private ModelService modelService;
	@Mock
	private SyncDao<OrderModel> orderSyncDao;
	@Mock
	private OmsHybrisEnumMappingStrategy<OrderStatus, OrderWrapper> orderStatusMappingStrategy;
	@Mock
	private OrderCancelCallbackService orderCancelCallbackService;

	final Date updateDate = new Date();
	private OrderModel orderModel;
	private Order omsOrder;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		orderModel = new OrderModel();
		orderModel.setEntries(new ArrayList<AbstractOrderEntryModel>());
		BDDMockito.when(orderSyncDao.findById(OrderModel.CODE, ORDER_ID)).thenReturn(orderModel);

		omsOrder = new Order();
		omsOrder.setOrderId(ORDER_ID);
	}


	@Test
	public void testOrderSyncAdapter() throws Exception
	{

		orderModel.setStatus(OrderStatus.CREATED);
		BDDMockito.when(orderStatusMappingStrategy.getHybrisEnumFromDto(Mockito.any(OrderWrapper.class))).thenReturn(
				OrderStatus.COMPLETED);


		omsOrderSyncAdapter.update(omsOrder, updateDate);

		assertEquals(OrderStatus.COMPLETED, orderModel.getStatus());
		assertEquals(updateDate, orderModel.getOrderUpdateRemoteTime());

	}

	@Test
	public void testShouldUpdateCancellingOrder() throws Exception
	{

		BDDMockito.when(orderStatusMappingStrategy.getHybrisEnumFromDto(Mockito.any(OrderWrapper.class))).thenReturn(
				OrderStatus.CANCELLED);

		omsOrderSyncAdapter.update(omsOrder, updateDate);

		//TODO:
		//Mockito.verify(orderCancelCallbackService).onOrderCancelResponse(Mockito.any(OrderCancelResponse.class));

	}


}
