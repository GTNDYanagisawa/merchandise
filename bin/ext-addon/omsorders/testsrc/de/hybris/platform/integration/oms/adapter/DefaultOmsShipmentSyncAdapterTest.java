/**
 * 
 */
package de.hybris.platform.integration.oms.adapter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.oms.OrderLineQuantityWrapper;
import de.hybris.platform.integration.oms.OrderWrapper;
import de.hybris.platform.integration.oms.ShipmentWrapper;
import de.hybris.platform.integration.oms.mapping.OmsHybrisEnumMappingStrategy;
import de.hybris.platform.omsorders.notification.ModelChangeNotifier;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.hybris.oms.domain.order.Order;
import com.hybris.oms.domain.order.OrderLine;
import com.hybris.oms.domain.order.OrderLineQuantity;
import com.hybris.oms.domain.shipping.Shipment;


/**
 * 
 */
public class DefaultOmsShipmentSyncAdapterTest
{

	private static final String SHIPMENT_ID_CHANGINGSTATUS = "ShipmentIdChangingStatus";
	private static final String ORDER_ID = "orderId";
	private static final Integer ENTRYNUMBER = Integer.valueOf(123);
	private static final String OL_ID = ORDER_ID + "_" + ENTRYNUMBER;


	@InjectMocks
	private final OmsSyncAdapter<ShipmentWrapper, ConsignmentModel> omsShipmentSyncAdapter = new DefaultOmsShipmentSyncAdapter();
	@Mock
	private Converter<Shipment, ConsignmentModel> omsShipmentReverseConverter;
	@Mock
	private OmsSyncAdapter<OrderLineQuantityWrapper, ConsignmentEntryModel> omsOlqWrapperSyncAdapter;
	@Mock
	private OmsHybrisEnumMappingStrategy<ConsignmentStatus, Shipment> consignmentStatusMappingStrategy;
	@Mock
	private ModelService modelService;
	@Mock
	private ModelChangeNotifier<ConsignmentModel> consignmentProcessNotifier;


	final Date updateDate = new Date();
	private OrderModel orderModel;
	private ConsignmentModel newConsignment;
	private ConsignmentModel existingConsignment;

	private Shipment statusShippedShipment;
	private Shipment statusPickedUpShipment;
	private Shipment statusNotReadyShipment;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		orderModel = new OrderModel();
		orderModel.setConsignments(new HashSet<ConsignmentModel>());

		existingConsignment = new ConsignmentModel();
		existingConsignment.setCode(SHIPMENT_ID_CHANGINGSTATUS);
		existingConsignment.setStatus(ConsignmentStatus.READY_FOR_PICKUP);
		orderModel.getConsignments().add(existingConsignment);


		statusPickedUpShipment = new Shipment();
		statusPickedUpShipment.setShipmentId(SHIPMENT_ID_CHANGINGSTATUS);
		statusShippedShipment = new Shipment();
		statusShippedShipment.setShipmentId("statusShippedShipment");
		statusNotReadyShipment = new Shipment();
		statusNotReadyShipment.setShipmentId("statusNotReadyShipment");


		BDDMockito.when(consignmentStatusMappingStrategy.getHybrisEnumFromDto(statusNotReadyShipment)).thenReturn(
				ConsignmentStatus.WAITING);
		BDDMockito.when(consignmentStatusMappingStrategy.getHybrisEnumFromDto(statusShippedShipment)).thenReturn(
				ConsignmentStatus.SHIPPED);
		BDDMockito.when(consignmentStatusMappingStrategy.getHybrisEnumFromDto(statusPickedUpShipment)).thenReturn(
				ConsignmentStatus.PICKUP_COMPLETE);



		newConsignment = new ConsignmentModel();
		newConsignment.setShippingAddress(new AddressModel());
		BDDMockito.when(modelService.create(ConsignmentModel.class)).thenReturn(newConsignment);

	}

	@Test
	public void testOrderSyncAdapterForNewConsignment() throws Exception
	{

		final Order order = new Order();
		order.setOrderId(ORDER_ID);
		order.setOrderLines(Lists.newArrayList(buildUniqueShipmentReadyOrderLine()));

		final ShipmentWrapper shipmentWrapper = new OrderWrapper(order).getShipments().get(0);
		final ConsignmentModel updatedConsignment = omsShipmentSyncAdapter.update(shipmentWrapper, orderModel);

		assertEquals(newConsignment, updatedConsignment);
		assertEquals(orderModel, updatedConsignment.getShippingAddress().getOwner());
		verify(modelService).save(newConsignment);


	}

	@Test
	public void testOrderSyncAdapterExistingNotReadyConsignment() throws Exception
	{

		final Order order = new Order();
		order.setOrderId(ORDER_ID);
		order.setOrderLines(Lists.newArrayList(buildUniqueShipmentNotReadyOrderLine()));

		final ShipmentWrapper shipmentWrapper = new OrderWrapper(order).getShipments().get(0);
		final ConsignmentModel updatedConsignment = omsShipmentSyncAdapter.update(shipmentWrapper, orderModel);

		assertEquals(null, updatedConsignment);

	}


	@Test
	public void testOrderSyncAdapterExistingChangingStatusConsignment() throws Exception
	{

		final Order order = new Order();
		order.setOrderId(ORDER_ID);
		order.setOrderLines(Lists.newArrayList(buildChangingStatusOrderLine()));

		final ShipmentWrapper shipmentWrapper = new OrderWrapper(order).getShipments().get(0);
		final ConsignmentModel updatedConsignment = omsShipmentSyncAdapter.update(shipmentWrapper, orderModel);

		assertEquals(existingConsignment, updatedConsignment);
		assertEquals(ConsignmentStatus.PICKUP_COMPLETE, updatedConsignment.getStatus());

	}






	protected OrderLine buildUniqueShipmentNotReadyOrderLine()
	{
		final OrderLine orderLine = new OrderLine();
		final List<OrderLineQuantity> lstOLQ = new ArrayList<OrderLineQuantity>();
		orderLine.setOrderLineId(OL_ID);

		lstOLQ.add(getOrderLineQuantity("1", statusNotReadyShipment));
		lstOLQ.add(getOrderLineQuantity("2", statusNotReadyShipment));

		orderLine.setOrderLineQuantities(lstOLQ);

		return orderLine;
	}


	protected OrderLine buildUniqueShipmentReadyOrderLine()
	{
		final OrderLine orderLine = new OrderLine();
		final List<OrderLineQuantity> lstOLQ = new ArrayList<OrderLineQuantity>();
		orderLine.setOrderLineId(OL_ID);

		lstOLQ.add(getOrderLineQuantity("1", statusShippedShipment));
		lstOLQ.add(getOrderLineQuantity("2", statusShippedShipment));

		orderLine.setOrderLineQuantities(lstOLQ);

		return orderLine;
	}


	protected OrderLine buildChangingStatusOrderLine()
	{
		final OrderLine orderLine = new OrderLine();
		final List<OrderLineQuantity> lstOLQ = new ArrayList<OrderLineQuantity>();
		orderLine.setOrderLineId(OL_ID);

		lstOLQ.add(getOrderLineQuantity("1", statusPickedUpShipment));

		lstOLQ.add(getOrderLineQuantity("2", statusShippedShipment));
		lstOLQ.add(getOrderLineQuantity("3", statusShippedShipment));

		orderLine.setOrderLineQuantities(lstOLQ);
		return orderLine;

	}

	/**
	 * @param string
	 * @return
	 */
	protected OrderLineQuantity getOrderLineQuantity(final String id, final Shipment shipment)
	{
		final OrderLineQuantity olq = new OrderLineQuantity();
		olq.setShipment(shipment);

		return olq;

	}



}
