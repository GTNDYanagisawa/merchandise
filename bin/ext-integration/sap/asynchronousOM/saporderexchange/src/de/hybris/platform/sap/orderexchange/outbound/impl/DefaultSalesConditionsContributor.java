/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SalesConditionCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SaporderexchangeConstants;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Builds the Row map for the CSV files for the Sales Conditions in an Order
 */
public class DefaultSalesConditionsContributor implements RawItemContributor<OrderModel>
{
	// Header conditions
	private static final int CONDITION_COUNTER_DELIVERY_COST = 1;
	private static final int CONDITION_COUNTER_PAYMENT_COST = 2;
	private static final int CONDITION_COUNTER_TAX = 10;

	// Item conditions
	private static final int CONDITION_COUNTER_GROSS_PRICE = 1;
	private static final int CONDITION_COUNTER_START_DISCOUNT = 10;

	private String tax1;
	private String grossPrice;
	private String deliveryCosts;
	private String paymentCosts;

	private int conditionCounterDeliveryCost = CONDITION_COUNTER_DELIVERY_COST;
	private int conditionCounterPaymentCost = CONDITION_COUNTER_PAYMENT_COST;
	private int conditionCounterTax = CONDITION_COUNTER_TAX;
	private int conditionCounterGrossPrice = CONDITION_COUNTER_GROSS_PRICE;
	private int conditionCounterStartDiscount = CONDITION_COUNTER_START_DISCOUNT;

	@Override
	public Set<String> getColumns()
	{
		return new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER,
				SalesConditionCsvColumns.CONDITION_CODE, SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE,
				SalesConditionCsvColumns.CONDITION_VALUE, SalesConditionCsvColumns.ABSOLUTE,
				SalesConditionCsvColumns.CONDITION_UNIT_CODE, SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY,
				SalesConditionCsvColumns.CONDITION_COUNTER));
	}

	protected void setConditionTypes(final OrderModel order)
	{
		final SAPConfigurationModel sapConfiguration = order.getStore().getSAPConfiguration();
		if (sapConfiguration != null)
		{
			setGrossPrice(sapConfiguration.getSaporderexchange_itemPriceConditionType());
			setDeliveryCosts(sapConfiguration.getSaporderexchange_deliveryCostConditionType());
			setPaymentCosts(sapConfiguration.getSaporderexchange_paymentCostConditionType());
		}
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		return syncPricingInactive(entries) ? createRowsHybrisPricing(order, entries) : createRowsSyncPricing(order, entries);
	}

	protected boolean syncPricingInactive(final List<AbstractOrderEntryModel> entries)
	{
		return entries.get(0).getSapPricingConditions() == null || entries.get(0).getSapPricingConditions().isEmpty();
	}

	private List<Map<String, Object>> createRowsHybrisPricing(final OrderModel order, final List<AbstractOrderEntryModel> entries)
	{
		final List<Map<String, Object>> result = new ArrayList<>();
		setConditionTypes(order);

		for (final AbstractOrderEntryModel entry : entries)
		{
			createGrossPriceRow(order, result, entry);
			createTaxRows(order, result, entry);
			createDiscountRows(order, result, entry);
		}

		createDeliveryCostRow(order, result);
		createPaymentCostRow(order, result);
		return result;
	}

	private List<Map<String, Object>> createRowsSyncPricing(final OrderModel order, final List<AbstractOrderEntryModel> entries)
	{
		final List<Map<String, Object>> result = new ArrayList<>();

		for (final AbstractOrderEntryModel entry : entries)
		{
			final Iterator<SAPPricingConditionModel> it = entry.getSapPricingConditions().iterator();
			while (it.hasNext())
			{
				final SAPPricingConditionModel condition = it.next();
				final Map<String, Object> row = new HashMap<>();

				row.put(OrderCsvColumns.ORDER_ID, order.getCode());
				row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());

				row.put(SalesConditionCsvColumns.CONDITION_CODE, condition.getConditionType());
				row.put(SalesConditionCsvColumns.CONDITION_VALUE, condition.getConditionRate());
				row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, condition.getConditionUnit());
				row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, condition.getConditionPricingUnit());
				row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, condition.getCurrencyKey());
				row.put(SalesConditionCsvColumns.CONDITION_COUNTER, condition.getConditionCounter());
				result.add(row);
			}
		}
		return result;
	}

	protected void createPaymentCostRow(final OrderModel order, final List<Map<String, Object>> result)
	{
		final Map<String, Object> row = new HashMap<>();
		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, SaporderexchangeConstants.HEADER_ENTRY);
		row.put(SalesConditionCsvColumns.CONDITION_CODE, paymentCosts);
		row.put(SalesConditionCsvColumns.CONDITION_VALUE, order.getPaymentCost());
		row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
		row.put(SalesConditionCsvColumns.CONDITION_COUNTER, getConditionCounterPaymentCost());
		row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
		result.add(row);
	}

	protected void createDeliveryCostRow(final OrderModel order, final List<Map<String, Object>> result)
	{
		final Map<String, Object> row = new HashMap<>();
		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, SaporderexchangeConstants.HEADER_ENTRY);
		row.put(SalesConditionCsvColumns.CONDITION_CODE, deliveryCosts);
		row.put(SalesConditionCsvColumns.CONDITION_VALUE, order.getDeliveryCost());
		row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
		row.put(SalesConditionCsvColumns.CONDITION_COUNTER, getConditionCounterDeliveryCost());
		row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
		result.add(row);
	}

	protected void createDiscountRows(final OrderModel order, final List<Map<String, Object>> result,
			final AbstractOrderEntryModel entry)
	{
		final List<DiscountValue> discountList = entry.getDiscountValues();
		int conditionCounter = getConditionCounterStartDiscount();
		for (final DiscountValue disVal : discountList)
		{
			final Map<String, Object> row = new HashMap<>();
			row.put(OrderCsvColumns.ORDER_ID, order.getCode());
			row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
			if (disVal.isAbsolute())
			{
				row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
				row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, entry.getUnit().getCode());
				row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, entry.getProduct().getPriceQuantity());
				row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
			}
			else
			{
				row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.FALSE);
			}
			row.put(SalesConditionCsvColumns.CONDITION_CODE, disVal.getCode());
			row.put(SalesConditionCsvColumns.CONDITION_VALUE, disVal.getValue() * -1);
			row.put(SalesConditionCsvColumns.CONDITION_COUNTER, conditionCounter++);
			result.add(row);
		}
	}

	protected void createTaxRows(final OrderModel order, final List<Map<String, Object>> result,
			final AbstractOrderEntryModel entry)
	{
		final Iterator<TaxValue> taxIterator = entry.getTaxValues().iterator();
		while (taxIterator.hasNext())
		{
			final TaxValue next = taxIterator.next();
			final Map<String, Object> row = new HashMap<>();
			row.put(OrderCsvColumns.ORDER_ID, order.getCode());
			row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
			row.put(SalesConditionCsvColumns.CONDITION_CODE, tax1);
			row.put(SalesConditionCsvColumns.CONDITION_VALUE, next.getValue());
			row.put(SalesConditionCsvColumns.CONDITION_COUNTER, getConditionCounterTax());

			if (next.isAbsolute())
			{
				row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
				row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
				row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, entry.getUnit().getCode());
				row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, entry.getProduct().getPriceQuantity());
			}
			else
			{
				row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.FALSE);
			}
			result.add(row);
			break; // Currently only the first entry is used
		}
	}

	protected void createGrossPriceRow(final OrderModel order, final List<Map<String, Object>> result,
			final AbstractOrderEntryModel entry)
	{
		final Map<String, Object> row = new HashMap<>();
		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
		row.put(SalesConditionCsvColumns.CONDITION_CODE, grossPrice);
		row.put(SalesConditionCsvColumns.CONDITION_VALUE, entry.getBasePrice());
		row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, entry.getUnit().getCode());
		row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, entry.getProduct().getPriceQuantity());
		row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
		row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
		row.put(SalesConditionCsvColumns.CONDITION_COUNTER, getConditionCounterGrossPrice());

		result.add(row);
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setTax1(final String tax1)
	{
		this.tax1 = tax1;
	}

	@SuppressWarnings("javadoc")
	public void setGrossPrice(final String grossPrice)
	{
		this.grossPrice = grossPrice;
	}

	@SuppressWarnings("javadoc")
	public void setDeliveryCosts(final String deliveryCosts)
	{
		this.deliveryCosts = deliveryCosts;
	}

	@SuppressWarnings("javadoc")
	public void setPaymentCosts(final String paymentCosts)
	{
		this.paymentCosts = paymentCosts;
	}

	@SuppressWarnings("javadoc")
	public int getConditionCounterDeliveryCost()
	{
		return conditionCounterDeliveryCost;
	}

	@SuppressWarnings("javadoc")
	public void setConditionCounterDeliveryCost(final int conditionCounterDeliveryCost)
	{
		this.conditionCounterDeliveryCost = conditionCounterDeliveryCost;
	}

	@SuppressWarnings("javadoc")
	public int getConditionCounterPaymentCost()
	{
		return conditionCounterPaymentCost;
	}

	@SuppressWarnings("javadoc")
	public void setConditionCounterPaymentCost(final int conditionCounterPaymentCost)
	{
		this.conditionCounterPaymentCost = conditionCounterPaymentCost;
	}

	@SuppressWarnings("javadoc")
	public int getConditionCounterTax()
	{
		return conditionCounterTax;
	}

	@SuppressWarnings("javadoc")
	public void setConditionCounterTax(final int conditionCounterTax)
	{
		this.conditionCounterTax = conditionCounterTax;
	}

	@SuppressWarnings("javadoc")
	public int getConditionCounterStartDiscount()
	{
		return conditionCounterStartDiscount;
	}

	@SuppressWarnings("javadoc")
	public void setConditionCounterStartDiscount(final int conditionCounterStartDiscount)
	{
		this.conditionCounterStartDiscount = conditionCounterStartDiscount;
	}

	@SuppressWarnings("javadoc")
	public int getConditionCounterGrossPrice()
	{
		return conditionCounterGrossPrice;
	}

	@SuppressWarnings("javadoc")
	public void setConditionCounterGrossPrice(final int conditionCounterGrossPrice)
	{
		this.conditionCounterGrossPrice = conditionCounterGrossPrice;
	}

}
