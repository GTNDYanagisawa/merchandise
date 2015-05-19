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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract class for order converters. Conversion methods should be implemented in inheriting class.
 * 
 */
public abstract class AbstractOrderPopulator<SOURCE extends AbstractOrderModel, TARGET extends AbstractOrderData> implements
		Populator<SOURCE, TARGET>
{
	private ModelService modelService;
	private PromotionsService promotionsService;
	private PriceDataFactory priceDataFactory;
	private CommonI18NService commonI18NService;
	private TypeService typeService;

	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	private Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter;
	private Converter<PromotionResultModel, PromotionResultData> promotionResultConverter;
	private Converter<PrincipalModel, PrincipalData> principalConverter;

	private final Map<String, PriceData> priceData = new HashMap<String, PriceData>();

	protected Map<String, PriceData> getPriceData()
	{
		return priceData;
	}


	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}


	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return this.orderEntryConverter;
	}

	@Required
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> converter)
	{
		this.orderEntryConverter = converter;
	}

	protected PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	@Required
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> getCreditCardPaymentInfoConverter()
	{
		return creditCardPaymentInfoConverter;
	}

	@Required
	public void setCreditCardPaymentInfoConverter(
			final Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter)
	{
		this.creditCardPaymentInfoConverter = creditCardPaymentInfoConverter;
	}

	protected Converter<DeliveryModeModel, DeliveryModeData> getDeliveryModeConverter()
	{
		return deliveryModeConverter;
	}

	@Required
	public void setDeliveryModeConverter(final Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter)
	{
		this.deliveryModeConverter = deliveryModeConverter;
	}

	protected Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> getZoneDeliveryModeConverter()
	{
		return zoneDeliveryModeConverter;
	}

	@Required
	public void setZoneDeliveryModeConverter(final Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter)
	{
		this.zoneDeliveryModeConverter = zoneDeliveryModeConverter;
	}

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	protected Converter<PromotionResultModel, PromotionResultData> getPromotionResultConverter()
	{
		return promotionResultConverter;
	}

	@Required
	public void setPromotionResultConverter(final Converter<PromotionResultModel, PromotionResultData> promotionResultConverter)
	{
		this.promotionResultConverter = promotionResultConverter;
	}

	protected Converter<PrincipalModel, PrincipalData> getPrincipalConverter()
	{
		return principalConverter;
	}

	@Required
	public void setPrincipalConverter(final Converter<PrincipalModel, PrincipalData> principalConverter)
	{
		this.principalConverter = principalConverter;
	}

	protected void addCommon(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		prototype.setCode(source.getCode());
		if (source.getSite() != null)
		{
			prototype.setSite(source.getSite().getUid());
		}
		if (source.getStore() != null)
		{
			prototype.setStore(source.getStore().getUid());
		}
		prototype.setTotalItems(calcTotalItems(source));
		prototype.setNet(Boolean.TRUE.equals(source.getNet()));
		prototype.setGuid(source.getGuid());
		prototype.setCalculated(Boolean.TRUE.equals(source.getCalculated()));
	}

	protected Integer calcTotalItems(final AbstractOrderModel source)
	{
		return Integer.valueOf(source.getEntries().size());
	}

	protected void addEntries(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		prototype.setEntries(Converters.convertAll(source.getEntries(), getOrderEntryConverter()));
	}

	protected void addDeliveryAddress(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		if (source.getDeliveryAddress() != null)
		{
			prototype.setDeliveryAddress(getAddressConverter().convert(source.getDeliveryAddress()));
		}
	}

	protected void addDeliveryMethod(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		final DeliveryModeModel deliveryMode = source.getDeliveryMode();
		if (deliveryMode != null)
		{
			DeliveryModeData deliveryModeData;
			if (deliveryMode instanceof ZoneDeliveryModeModel)
			{
				deliveryModeData = getZoneDeliveryModeConverter().convert((ZoneDeliveryModeModel) deliveryMode);
			}
			else
			{
				deliveryModeData = getDeliveryModeConverter().convert(deliveryMode);
			}

			if (source.getDeliveryCost() != null)
			{
				deliveryModeData.setDeliveryCost(getPriceDataFactory().create(PriceDataType.BUY,
						BigDecimal.valueOf(source.getDeliveryCost().doubleValue()), source.getCurrency().getIsocode()));
			}
			prototype.setDeliveryMode(deliveryModeData);
		}
	}

	protected void addPaymentInformation(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		final PaymentInfoModel paymentInfo = source.getPaymentInfo();
		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{
			final CCPaymentInfoData paymentInfoData = getCreditCardPaymentInfoConverter().convert(
					(CreditCardPaymentInfoModel) paymentInfo);
			prototype.setPaymentInfo(paymentInfoData);
		}
	}


	/*
	 * Adds applied and potential promotions.
	 */
	protected void addPromotions(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		addPromotions(source, getPromotionsService().getPromotionResults(source), prototype);
	}

	protected void addPromotions(final AbstractOrderModel source, final PromotionOrderResults promoOrderResults,
			final AbstractOrderData prototype)
	{
		if (promoOrderResults != null)
		{
			final double productsDiscountsAmount = getProductsDiscountsAmount(source);
			final double orderDiscountsAmount = getOrderDiscountsAmount(source);

			prototype.setProductDiscounts(createPrice(source, Double.valueOf(productsDiscountsAmount)));
			prototype.setOrderDiscounts(createPrice(source, Double.valueOf(orderDiscountsAmount)));
			prototype.setTotalDiscounts(createPrice(source, Double.valueOf(productsDiscountsAmount + orderDiscountsAmount)));
			prototype.setAppliedOrderPromotions(getPromotions(promoOrderResults.getAppliedOrderPromotions()));
			prototype.setAppliedProductPromotions(getPromotions(promoOrderResults.getAppliedProductPromotions()));
		}
	}

	protected double getProductsDiscountsAmount(final AbstractOrderModel source)
	{
		double discounts = 0.0d;

		final List<AbstractOrderEntryModel> entries = source.getEntries();
		if (entries != null)
		{
			for (final AbstractOrderEntryModel entry : entries)
			{
				final List<DiscountValue> discountValues = entry.getDiscountValues();
				if (discountValues != null)
				{
					for (final DiscountValue dValue : discountValues)
					{
						discounts += dValue.getAppliedValue();
					}
				}
			}
		}
		return discounts;
	}

	protected double getOrderDiscountsAmount(final AbstractOrderModel source)
	{
		double discounts = 0.0d;
		final List<DiscountValue> discountList = source.getGlobalDiscountValues(); // discounts on the cart itself
		if (discountList != null && !discountList.isEmpty())
		{
			for (final DiscountValue discount : discountList)
			{
				final double value = discount.getAppliedValue();
				if (value > 0.0d)
				{
					discounts += value;
				}
			}
		}

		return discounts;
	}

	/*
	 * Extracts (and converts to POJOs) promotions from given results.
	 */
	protected List<PromotionResultData> getPromotions(final List<PromotionResult> promotionsResults)
	{
		final ArrayList<PromotionResultModel> promotionResultModels = getModelService().getAll(promotionsResults,
				new ArrayList<PromotionResultModel>());
		return Converters.convertAll(promotionResultModels, getPromotionResultConverter());
	}

	protected PriceData createPrice(final AbstractOrderModel source, final Double val)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("source order must not be null");
		}

		final CurrencyModel currency = source.getCurrency();
		if (currency == null)
		{
			throw new IllegalArgumentException("source order currency must not be null");
		}

		// Get double value, handle null as zero
		final double priceValue = val != null ? val.doubleValue() : 0d;

		return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(priceValue), currency);
	}

	protected void addTotals(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		prototype.setTotalPrice(createPrice(source, source.getTotalPrice()));
		prototype.setTotalTax(createPrice(source, source.getTotalTax()));
		prototype.setSubTotal(createPrice(source, source.getSubtotal()));
		prototype.setDeliveryCost(source.getDeliveryMode() != null ? createPrice(source, source.getDeliveryCost()) : null);
		prototype.setTotalPriceWithTax((createPrice(source, calcTotalWithTax(source))));
	}

	protected PriceData createZeroPrice()
	{
		final String key = getCommonI18NService().getCurrentCurrency().getIsocode();
		if (getPriceData().containsKey(key))
		{
			return getPriceData().get(key);
		}
		else
		{
			final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.ZERO,
					getCommonI18NService().getCurrentCurrency());
			getPriceData().put(key, priceData);
			return priceData;
		}
	}

	protected Double calcTotalWithTax(final AbstractOrderModel source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("source order must not be null");
		}
		double totalPrice = source.getTotalPrice() == null ? 0.0d : source.getTotalPrice().doubleValue();

		// Add the taxes to the total price if the cart is net; if the total was null taxes should be null as well
		if (Boolean.TRUE.equals(source.getNet()) && totalPrice != 0.0d)
		{
			totalPrice += source.getTotalTax() == null ? 0.0d : source.getTotalTax().doubleValue();
		}

		return Double.valueOf(totalPrice);
	}

	protected Integer calcTotalUnitCount(final AbstractOrderModel source)
	{
		int totalUnitCount = 0;
		for (final AbstractOrderEntryModel orderEntryModel : source.getEntries())
		{
			totalUnitCount += orderEntryModel.getQuantity().intValue();
		}
		return Integer.valueOf(totalUnitCount);
	}

	protected void checkForGuestCustomer(final OrderModel source, final OrderData target)
	{
		if (CustomerType.GUEST.equals(((CustomerModel) source.getUser()).getType()))
		{
			target.setGuestCustomer(true);
		}
	}

	protected void addDeliveryStatus(final OrderModel source, final OrderData target)
	{
		target.setDeliveryStatus(source.getDeliveryStatus());
		if (source.getDeliveryStatus() != null)
		{
			target.setDeliveryStatusDisplay(getTypeService().getEnumerationValue(source.getDeliveryStatus()).getName());
		}
	}

	protected void addPrincipalInformation(final AbstractOrderModel source, final AbstractOrderData target)
	{
		target.setUser(getPrincipalConverter().convert(source.getUser()));
	}
}
