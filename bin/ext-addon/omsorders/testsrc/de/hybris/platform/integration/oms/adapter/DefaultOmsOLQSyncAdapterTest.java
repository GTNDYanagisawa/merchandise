/**
 * 
 */
package de.hybris.platform.integration.oms.adapter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.integration.oms.OrderLineQuantityWrapper;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.hybris.oms.domain.order.OrderLineQuantity;
import com.hybris.oms.domain.order.OrderLineQuantityStatus;
import com.hybris.oms.domain.shipping.Shipment;
import com.hybris.oms.domain.types.Quantity;


/**
 * 
 */
public class DefaultOmsOLQSyncAdapterTest
{

	private static final String SHIPMENT_ID = "ShipmentId";
	private static final String ORDER_ID = "orderId";
	private static final Integer EXISTING_ENTRYNUMBER = Integer.valueOf(123);


	@InjectMocks
	private final OmsSyncAdapter<OrderLineQuantityWrapper, ConsignmentEntryModel> omsOLQSyncAdapter = new DefaultOmsOLQSyncAdapter();
	@Mock
	private Converter<OrderLineQuantity, ConsignmentEntryModel> omsOLQReverseConverter;
	@Mock
	private ModelService modelService;



	final Date updateDate = new Date();
	private ConsignmentEntryModel existingConsignmentEntry;
	private ConsignmentEntryModel newConsignmentEntry;
	private ConsignmentModel parentConsignment;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		newConsignmentEntry = new ConsignmentEntryModel();
		existingConsignmentEntry = new ConsignmentEntryModel();
		final AbstractOrderEntryModel mockOrderEntry = Mockito.mock(AbstractOrderEntryModel.class);
		existingConsignmentEntry.setOrderEntry(mockOrderEntry);
		BDDMockito.when(existingConsignmentEntry.getOrderEntry().getEntryNumber()).thenReturn(EXISTING_ENTRYNUMBER);

		parentConsignment = new ConsignmentModel();
		parentConsignment.setOrder(new OrderModel());
		parentConsignment.getOrder().setEntries(Lists.newArrayList(mockOrderEntry));

		parentConsignment.setConsignmentEntries(new HashSet<ConsignmentEntryModel>());
		parentConsignment.getConsignmentEntries().add(existingConsignmentEntry);

		BDDMockito.when(modelService.create(ConsignmentEntryModel.class)).thenReturn(newConsignmentEntry);
	}

	@Test
	public void testOrderSyncAdapterForNewConsignmentEntry() throws Exception
	{
		final OrderLineQuantityWrapper olqWrapper = new OrderLineQuantityWrapper(getOrderLineQuantity(), Integer.valueOf(999999));
		final ConsignmentEntryModel updatedConsignment = omsOLQSyncAdapter.update(olqWrapper, parentConsignment);

		assertEquals(newConsignmentEntry, updatedConsignment);
		verify(modelService).save(newConsignmentEntry);


	}

	@Test
	public void testOrderSyncAdapterExistingConsignmentEntry() throws Exception
	{

		final OrderLineQuantityWrapper olqWrapper = new OrderLineQuantityWrapper(getOrderLineQuantity(), EXISTING_ENTRYNUMBER);
		final ConsignmentEntryModel updatedConsignment = omsOLQSyncAdapter.update(olqWrapper, parentConsignment);

		assertEquals(existingConsignmentEntry, updatedConsignment);
		verify(modelService).save(existingConsignmentEntry);

	}

	protected OrderLineQuantity getOrderLineQuantity()
	{
		final Shipment shipment = new Shipment();
		shipment.setShipmentId(SHIPMENT_ID);

		final OrderLineQuantity olq = new OrderLineQuantity();
		olq.setOlqId("id");
		olq.setQuantity(new Quantity("DDT", 2));
		olq.setLocation("locationId");

		final OrderLineQuantityStatus olqs = new OrderLineQuantityStatus();
		olqs.setActive(true);
		olqs.setDescription(shipment.getOlqsStatus());
		olqs.setStatusCode(shipment.getOlqsStatus());

		olq.setStatus(olqs);

		olq.setShipment(shipment);

		return olq;

	}

}
