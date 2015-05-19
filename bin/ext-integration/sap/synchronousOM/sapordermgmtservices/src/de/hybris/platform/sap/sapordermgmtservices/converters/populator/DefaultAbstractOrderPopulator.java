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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Populates the hybris representation of an abstract order (concrete cart or concrete order) from the SAP BOL
 * representation
 * 
 * @param <SOURCE>
 *           BOL representation of abstract order
 * @param <TARGET>
 *           hybris representation of abstract order
 */
public class DefaultAbstractOrderPopulator<SOURCE extends SalesDocument, TARGET extends AbstractOrderData> implements
		Populator<SOURCE, TARGET>
{
	private static final Logger LOG = Logger.getLogger(DefaultAbstractOrderPopulator.class);



	private BolCartFacade bolCartFacade;
	private BaseSiteService baseSiteService;
	private PriceDataFactory priceFactory;
	private SapPartnerService sapPartnerService;

	private Converter<Item, OrderEntryData> cartItemConverter;
	private Converter<AddressModel, AddressData> addressConverter;


	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		populateHeader(source, target);
		populateDeliveryAddress(source, target);

		Collections.sort(source.getItemList(), new ItemComparator());

		Integer totalCount = Integer.valueOf(0);

		final List<OrderEntryData> orderEntryList = new ArrayList<OrderEntryData>();
		for (final Item item : source.getItemList())
		{
			//Sub items will not be converted into hybris representation of order entries
			if (TechKey.isEmpty(item.getParentId()))
			{
				final OrderEntryData orderEntry = cartItemConverter.convert(item);
				orderEntryList.add(orderEntry);

				totalCount = Integer.valueOf(totalCount.intValue() + item.getQuantity().intValue());
			}
		}

		target.setEntries(orderEntryList);

		target.setTotalItems(totalCount);
		target.setDeliveryItemsQuantity(Long.valueOf(orderEntryList.size()));
	}

	/**
	 * Maps an abstract order header in BOL representation into an hybris abstract order
	 * 
	 * @param source
	 *           BOL representation of abstract order
	 * @param target
	 *           hybris representation of abstract order
	 */
	protected void populateHeader(final SOURCE source, final TARGET target)
	{
		final Header header = source.getHeader();

		target.setSubTotal(priceFactory.create(PriceDataType.BUY, header.getNetValueWOFreight(), header.getCurrency()));

		target.setDeliveryCost(priceFactory.create(PriceDataType.BUY, header.getFreightValue(), header.getCurrency()));

		target.setTotalTax(priceFactory.create(PriceDataType.BUY, header.getTaxValue(), header.getCurrency()));

		target.setTotalPrice(priceFactory.create(PriceDataType.BUY, header.getGrossValue(), header.getCurrency()));

		final DeliveryModeData deliveryMode = determineDeliveryModeSAPToHybris(header.getShipCond());
		target.setDeliveryMode(deliveryMode);
		if (baseSiteService != null)
		{
			target.setSite(baseSiteService.getCurrentBaseSite().getUid());
		}
		target.setCalculated(true);

		if (LOG.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder("SD price data on header level:");
			debugOutput.append("\nNet Value: " + header.getNetValueWOFreight() + " / Freight Value: " + header.getFreightValue());
			debugOutput.append("\nTax Value: " + header.getTaxValue() + " / Total Value: " + header.getGrossValue());
			LOG.debug(debugOutput);
		}
	}

	/**
	 * Determines a hybris model from the SAP delivery mode code (i.e. the shipping condition in SD terms)
	 * 
	 * @param deliveryModeCode
	 *           SAP delivery mode code
	 * 
	 * @return hybris delivery mode data
	 */
	protected DeliveryModeData determineDeliveryModeSAPToHybris(final String deliveryModeCode)
	{
		final Map<String, String> allowedDeliveryTypes = bolCartFacade.getAllowedDeliveryTypes();
		final DeliveryModeData deliveryModeData = new DeliveryModeData();
		convertDeliveryModeSAPToHybris(deliveryModeCode, allowedDeliveryTypes, deliveryModeData);

		return deliveryModeData;
	}

	/**
	 * Compiles the hybris representation of the delivery mode from an SAP code and the list of possible SAP codes. This
	 * list contains the language dependent descriptions which we need for the facade layer or UI
	 * 
	 * @param shipCond
	 *           SAP delivery mode code
	 * @param availableShipConds
	 *           List of possible SAP delivery mode codes
	 * @param deliveryModeData
	 *           hybris delivery mode data
	 */
	protected void convertDeliveryModeSAPToHybris(final String shipCond, final Map<String, String> availableShipConds,
			final DeliveryModeData deliveryModeData)
	{
		deliveryModeData.setCode(shipCond);
		String deliveryModeText = "";
		if (availableShipConds != null)
		{
			deliveryModeText = availableShipConds.get(shipCond);
		}
		deliveryModeData.setName(deliveryModeText);
	}


	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the cartItemConverter
	 */
	public Converter<Item, OrderEntryData> getCartItemConverter()
	{
		return cartItemConverter;
	}

	/**
	 * @param cartItemConverter
	 *           the cartItemConverter to set
	 */
	public void setCartItemConverter(final Converter<Item, OrderEntryData> cartItemConverter)
	{
		this.cartItemConverter = cartItemConverter;
	}

	/**
	 * @return the priceFactory
	 */
	public PriceDataFactory getPriceFactory()
	{
		return priceFactory;
	}

	/**
	 * @param priceFactory
	 *           the priceFactory to set
	 */
	public void setPriceFactory(final PriceDataFactory priceFactory)
	{
		this.priceFactory = priceFactory;
	}

	/**
	 * @return the bolCartFacade
	 */
	public BolCartFacade getBolCartFacade()
	{
		return bolCartFacade;
	}

	/**
	 * @param bolCartFacade
	 *           the bolCartFacade to set
	 */
	public void setBolCartFacade(final BolCartFacade bolCartFacade)
	{
		this.bolCartFacade = bolCartFacade;
	}

	/**
	 * @param sapPartnerService
	 *           the sapPartnerService to set
	 */
	public void setSapPartnerService(final SapPartnerService sapPartnerService)
	{
		this.sapPartnerService = sapPartnerService;
	}


	/**
	 * Converts delivery addresses from BOL into hybris representation. On BOL level, the address is identified through
	 * the SAP key of the ship-to party which is attached to {@link Header}. <br>
	 * As a result of this method, {@link TARGET#setDeliveryAddress(AddressData)} will be called on the hybris abstract
	 * order
	 * 
	 * @param source
	 *           BOL representation of abstract order
	 * @param target
	 *           hybris representation of abstract order
	 */
	protected void populateDeliveryAddress(final SOURCE source, final TARGET target)
	{

		final ShipTo shipTo = source.getHeader().getShipTo();
		if (shipTo != null)
		{
			final String sapCustomerId = shipTo.getId();
			final AddressModel addressModel = sapPartnerService.getHybrisAddressForSAPCustomerId(sapCustomerId);

			if (addressModel != null)
			{
				final AddressData addressData = addressConverter.convert(addressModel);
				target.setDeliveryAddress(addressData);
			}
		}
		else
		{
			LOG.debug("No shipTo available");
		}

	}

	/**
	 * @param addressConverter
	 *           the addressConverter to set
	 */
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	/**
	 * @return the sapPartnerService
	 */
	public SapPartnerService getSapPartnerService()
	{
		return sapPartnerService;
	}
}
