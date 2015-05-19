package com.hybris.oms.rest.client;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.hybris.oms.api.inventory.OmsInventory;
import com.hybris.oms.domain.address.Address;
import com.hybris.oms.domain.basestore.BaseStore;
import com.hybris.oms.domain.inventory.ItemStatus;
import com.hybris.oms.domain.inventory.Location;
import com.hybris.oms.domain.locationrole.LocationRole;
import com.hybris.oms.domain.order.Order;
import com.hybris.oms.domain.order.OrderLine;
import com.hybris.oms.domain.order.OrderLineAttribute;
import com.hybris.oms.domain.order.PaymentInfo;
import com.hybris.oms.domain.shipping.ShippingAndHandling;
import com.hybris.oms.domain.types.Amount;
import com.hybris.oms.domain.types.Contact;
import com.hybris.oms.domain.types.Price;
import com.hybris.oms.domain.types.Quantity;


/**
 * Builder methods for creating test DTOs.
 */
public class TestHelper
{
	private static final String TAX_CATEGORY = "AE514";
	private static final int DELAY_MILLIS = 1000;
	private static final String ON_HAND = "ON_HAND";

	private static final String ORDERLINE_ATTRIBUTE_VALUE = "OrderLine.AttributeValue";
	private static final String ORDERLINE_ATTRIBUTE_ID = "OrderLIne.AttributeId";

	public OmsInventory buildInventory(final String locationId, final int quantity)
	{
		final OmsInventory inventory = new OmsInventory();
		inventory.setSkuId(generateSku());
		inventory.setLocationId(locationId);
		inventory.setStatus(ON_HAND);
		inventory.setQuantity(quantity);
		return inventory;
	}

	public Location buildLocation()
	{
		return buildLocation(generateLocationId(), LocationRole.SHIPPING, null);
	}

	public Location buildLocation(final String locationId, final LocationRole locationRole, final Set<String> baseStores)
	{
		final Location location = new Location();
		location.setLocationId(locationId);
		location.setDescription("description");
		location.setPriority(1);
		location.setAddress(this.buildAddress());
		location.setActive(true);
		location.setShipToCountriesCodes(Collections.singleton(location.getAddress().getCountryIso3166Alpha2Code()));
		location.setLocationRoles(Collections.singleton(locationRole));
		location.setBaseStores(baseStores);
		return location;
	}

	public Address buildAddress()
	{
		return new Address("350 5th Ave", null, "New York", "NY", "10118", null, null, "US", "United States",
				"Empire State Building", "1 212-736-3100");
	}

	public ItemStatus buildItemStatus(final String statusCode, final String description)
	{
		final ItemStatus itemStatus = new ItemStatus();
		itemStatus.setDescription(description);
		itemStatus.setStatusCode(statusCode);
		return itemStatus;
	}

	public BaseStore buildBaseStore(final String baseStoreName)
	{
		final BaseStore baseStore = new BaseStore();
		baseStore.setName(baseStoreName);
		baseStore.setDescription("description");

		return baseStore;
	}

	public Order buildOrder(final String sku)
	{
		final String orderId = "order_" + this.generateRandomString();
		final Order order = new Order();
		order.setOrderId(orderId);

		order.setUsername("IntegrationTest");
		order.setFirstName("Chuck");
		order.setLastName("Norris");
		order.setEmailid("chuck.norris@hybris.com");
		order.setShippingFirstName("Chuck");
		order.setShippingLastName("Norris");
		order.setShippingTaxCategory("shippingTaxCategory");
		order.setIssueDate(Calendar.getInstance().getTime());
		order.setCurrencyCode("USD");

		final ShippingAndHandling shippingAndHandling = new ShippingAndHandling();
		shippingAndHandling.setOrderId(order.getOrderId());
		shippingAndHandling.setShippingPrice(new Price(new Amount("USD", Double.valueOf(2d)), new Amount("USD", Double
				.valueOf(0.5d)), new Amount("USD", Double.valueOf(0d))));

		order.setShippingAndHandling(shippingAndHandling);

		order.setContact(new Contact(null, "arvato", null, null, "1234567", null, null));
		order.setShippingAddress(buildAddress());
		order.setShippingMethod("DOM.EP");
		order.setPriorityLevelCode("STANDARD");

		final OrderLine orderLine = this.buildOrderLine(UUID.randomUUID().toString(), sku, new Quantity("unit", 2), new Quantity(
				"unit", 2), new Amount("USD", Double.valueOf(1d)), new Amount("USD", Double.valueOf(0.15d)), TAX_CATEGORY,
				LocationRole.SHIPPING, new OrderLineAttribute(ORDERLINE_ATTRIBUTE_VALUE, ORDERLINE_ATTRIBUTE_ID));

		order.setOrderLines(Collections.singletonList((orderLine)));
		final PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.setPaymentInfoType("Visa");
		paymentInfo.setAuthUrl("TEST");
		paymentInfo.setBillingAddress(buildAddress());

		order.setPaymentInfos(Collections.singletonList(paymentInfo));
		return order;
	}

	public OrderLine buildOrderLine(final String orderLineId, final String skuId, final Quantity quantity,
			final Quantity quantityUnassigned, final Amount unitPrice, final Amount unitTax, final String taxCategory,
			final LocationRole locationRole, final OrderLineAttribute orderlineAttribute)
	{
		final OrderLine orderLine = new OrderLine();

		if (orderLineId == null || orderLineId.trim().length() == 0)
		{
			orderLine.setOrderLineId(UUID.randomUUID().toString());
		}
		else
		{
			orderLine.setOrderLineId(orderLineId);
		}

		orderLine.setSkuId(skuId);
		orderLine.setQuantity(quantity);
		orderLine.setQuantityUnassigned(quantityUnassigned);
		orderLine.setUnitPrice(unitPrice);
		orderLine.setUnitTax(unitTax);
		orderLine.setTaxCategory(taxCategory);
		final HashSet<LocationRole> roles = new HashSet<>();
		roles.add(locationRole);
		orderLine.setLocationRoles(roles);
		orderLine.addOrderLineAttribute(orderlineAttribute);

		return orderLine;
	}

	public String generateLongIdAsString()
	{
		final SecureRandom random = new SecureRandom();
		return String.valueOf(random.nextInt(999999));
	}

	public String generateLocationId()
	{
		return "loc_" + this.generateRandomString();
	}

	public String generateBaseStoreId()
	{
		return "base_" + this.generateRandomString();
	}

	public String generateSku()
	{
		return "sku_" + this.generateRandomString();
	}

	public String generateRandomString()
	{
		return UUID.randomUUID().toString();
	}

	public String generateBinCode()
	{
		return "bin_" + this.generateRandomString();
	}

	public void delay()
	{
		try
		{
			Thread.sleep(DELAY_MILLIS);
		}
		catch (final InterruptedException e)
		{
			//
		}
	}

	public void delay(final long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (final InterruptedException e)
		{
			//
		}
	}

}
