package de.hybris.platform.sap.sappricingbol.backend.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.sap.sappricingbol.constants.SappricingbolConstants;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;
import de.hybris.platform.sap.sappricingbol.converter.ConversionTools;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;


public class SapPricingItemMapper extends SapPricingBaseMapper
{
	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(SapPricingItemMapper.class.getName());
	private static final int PRODUCT_CODE_LENGTH = 18;

	public void fillImportParameters(final JCoParameterList importParameters, final List<ProductModel> productModels,
			ConversionService conversionService)
	{

		final JCoTable itItem = importParameters.getTable("IT_ITEM");
		fillImportParameters(itItem, productModels, conversionService);
		importParameters.setValue("IT_ITEM", itItem);
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("sappricingbol - RFC Item Parameters: ");
			sapLogger.debug(itItem.toString());
		}

	}

	protected void fillImportParameters(final JCoTable itItem, final List<ProductModel> productModels,
			ConversionService conversionService)
	{

		for (int i = 0; i < productModels.size(); i++)
		{
			itItem.appendRow();
			itItem.setValue("KPOSN", i + 1);
			itItem.setValue("MATNR", formatProductCode(productModels.get(i).getCode()));
			itItem.setValue("MGAME", SappricingbolConstants.MGAME);
			String unit = conversionService.getSAPUnitforISO(productModels.get(i).getUnit().getCode());
			if (unit == null)
			{
				unit = SappricingbolConstants.NO;
			}
			itItem.setValue("VRKME", unit);
			itItem.setValue("CALLER_DATA", SappricingbolConstants.NO);

		}

	}

	protected void fillImportParameters(final JCoTable itItem, final AbstractOrderEntryModel orderEntryModel,
			ConversionService conversionService)
	{
		itItem.appendRow();
		itItem.setValue("KPOSN", orderEntryModel.getEntryNumber() + 1);
		itItem.setValue("MATNR", formatProductCode(orderEntryModel.getProduct().getCode()));
		itItem.setValue("MGAME", orderEntryModel.getQuantity());
		String unit = conversionService.getSAPUnitforISO(orderEntryModel.getUnit().getCode());
		itItem.setValue("VRKME", unit);
		itItem.setValue("CALLER_DATA", SappricingbolConstants.NO);

	}

	public void fillImportParameters(final AbstractOrderModel order, final JCoParameterList importParameters,
			ConversionService conversionService)
	{

		final JCoTable itItem = importParameters.getTable("IT_ITEM");

		for (final AbstractOrderEntryModel orderEntryModel : order.getEntries())
		{
			fillImportParameters(itItem, orderEntryModel, conversionService);
		}

		importParameters.setValue("IT_ITEM", itItem);
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("sappricingbol - RFC Checkout Item Parameters: ");
			sapLogger.debug(itItem.toString());
		}
	}

	protected String formatProductCode(String code)
	{
		return ConversionTools.addLeadingZerosToNumericID(code, PRODUCT_CODE_LENGTH);

	}

	/**
	 * 
	 * @param prices
	 * @param itemTable
	 * @return List<PriceInformation>
	 */
	public List<PriceInformation> readPrices(final JCoTable resultTable)
	{
		if (!resultTable.isEmpty())
		{
			final JCoTable itemTable = resultTable.getTable("ITEM");

			if (itemTable != null && !itemTable.isEmpty())
			{
				List<PriceInformation> result = new ArrayList<PriceInformation>(itemTable.getNumRows());

				for (int i = 0; i < itemTable.getNumRows(); i++)
				{
					itemTable.setRow(i);
					final double priceBeforeDiscount = Math.abs(Double.parseDouble(itemTable
							.getString(getProperty(SappricingbolConstants.CONF_PROP_PRICE_SUBTOTAL))));

					final double discount = Math.abs(Double.parseDouble(itemTable
							.getString(getProperty(SappricingbolConstants.CONF_PROP_DISCOUNTS_SUBTOTAL))));

					// calculate discounted price
					final double price = priceBeforeDiscount - discount;

					final PriceValue priceValue = new PriceValue(resultTable.getString("WAERK"), price, true);
					final PriceInformation priceInformation = new PriceInformation(priceValue);
					result.add(priceInformation);

				}

				return result;
			}
		}

		return Collections.emptyList();
	}

	public void readPrices(AbstractOrderModel order, final JCoTable resultTable, ConversionService conversionService)
	{
		if (resultTable != null && !resultTable.isEmpty())
		{
			JCoTable itemTable = resultTable.getTable("ITEM");

			if (itemTable != null && !itemTable.isEmpty())
			{

				final String currencyIsoCode = resultTable.getString("WAERK");

				double deliveryCosts = 0.00;
				double paymentCosts = 0.00;

				for (final AbstractOrderEntryModel orderEntryModel : order.getEntries())
				{
					long quantity = orderEntryModel.getQuantity();

					// calculate base price
					double entryPrice = itemTable.getDouble(getProperty(SappricingbolConstants.CONF_PROP_PRICE_SUBTOTAL));
					orderEntryModel.setBasePrice(entryPrice / quantity);

					// calculate discounts 
					double entryDiscount = itemTable.getDouble(getProperty(SappricingbolConstants.CONF_PROP_DISCOUNTS_SUBTOTAL));
					double discount = Math.abs(entryDiscount / quantity);
					final DiscountValue discountValue = new DiscountValue(generateCode("DISC", orderEntryModel, order), discount,
							true, discount, currencyIsoCode);
					orderEntryModel.setDiscountValues(Arrays.asList(discountValue));


					// calculate taxes
					double entryTax = itemTable.getDouble(getProperty(SappricingbolConstants.CONF_PROP_TAXES_SUBTOTAL));
					double tax = entryTax / quantity;
					final TaxValue taxValue = new TaxValue(generateCode("TAX", orderEntryModel, order), tax, true, tax,
							currencyIsoCode);
					orderEntryModel.setTaxValues(Arrays.asList(taxValue));

					// calculate delivery costs - TODO add the delivery cost parameter for calculation
					// retrieve delivery costs only if delivery mode is selected
					double deliveryCost = itemTable.getDouble(getProperty(SappricingbolConstants.CONF_PROP_DELIVERY_SUBTOTAL));
					// sum all entries delivery costs
					deliveryCosts += deliveryCost;
					if (order.getDeliveryMode() != null)
					{
						order.setDeliveryCost(deliveryCosts);
					}

					// calculate payment costs
					double paymentCost = itemTable.getDouble(getProperty(SappricingbolConstants.CONF_PROP_PAYMENT_COST_SUBTOTAL));
					paymentCosts += paymentCost;
					order.setPaymentCost(paymentCosts);

					// add SAP pricing conditions to the Order Model entries 
					if (order instanceof OrderModel)
					{
						orderEntryModel.setSapPricingConditions(readSapPricingConditions(itemTable.getTable("COND"), order.getCode(),
								conversionService));
					}

					itemTable.nextRow();
				}
			}

		}

	}

	/**
	 * generate a code string from order number and order entry number
	 * 
	 * @return
	 */
	protected String generateCode(String prefix, AbstractOrderEntryModel entry, AbstractOrderModel order)
	{
		return prefix + entry.getEntryNumber() + order.getCode();

	}

	protected Set<SAPPricingConditionModel> readSapPricingConditions(JCoTable conds, String orderCode,
			ConversionService conversionService)
	{

		Set<SAPPricingConditionModel> sapPricingConditionModelSet = null;

		if (conds != null && !conds.isEmpty())
		{

			sapPricingConditionModelSet = new HashSet<SAPPricingConditionModel>();

			for (int i = 0; i < conds.getNumRows(); i++)
			{

				conds.setRow(i);

				SAPPricingConditionModel sapPricingConditionModel = new SAPPricingConditionModel();

				sapPricingConditionModel.setStepNumber(conds.getString("STUNR"));
				sapPricingConditionModel.setConditionCounter(conds.getString("ZAEHK"));
				sapPricingConditionModel.setConditionType(conds.getString("KSCHL"));
				sapPricingConditionModel.setCurrencyKey(conds.getString("WAERS"));
				sapPricingConditionModel.setConditionPricingUnit(conds.getString("KPEIN"));
				String sapUnit = conds.getString("KMEIN");
				if (sapUnit != null)
				{
					String unit = conversionService.getISOUnitforSAP(sapUnit);
					if (unit != null)
					{
						sapPricingConditionModel.setConditionUnit(unit);
					}
				}
				String calculationType = conds.getString("KRECH");
				sapPricingConditionModel.setConditionCalculationType(calculationType);
				if (calculationType.equals(SappricingbolConstants.PERCENTAGE_CALCULATION_TYPE))
				{
					BigDecimal conditionRate = new BigDecimal(conds.getString("KBETR"));
					BigDecimal adjusted = conditionRate.movePointLeft(1).setScale(2);
					sapPricingConditionModel.setConditionRate(adjusted.toPlainString());
					if (sapLogger.isDebugEnabled())
					{
						sapLogger.debug("Calculation Type is Percentage, Rate set to: " + adjusted.toPlainString());
					}
				}
				else
				{
					sapPricingConditionModel.setConditionRate(conds.getString("KBETR"));
				}


				sapPricingConditionModel.setConditionValue(conds.getString("KWERT"));
				sapPricingConditionModel.setOrder(orderCode);


				sapPricingConditionModelSet.add(sapPricingConditionModel);

			}
		}

		return sapPricingConditionModelSet;
	}


}
