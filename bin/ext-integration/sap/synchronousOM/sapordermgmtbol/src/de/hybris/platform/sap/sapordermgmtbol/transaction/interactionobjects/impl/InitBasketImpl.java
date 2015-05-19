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
package de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.impl;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.PartnerFunctionData;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.interf.InitBasket;

/**
 * ** This interaction object is used to initialize a basket.
 * 
 */
public class InitBasketImpl implements InitBasket {

	private GenericFactory genericFactory;

	/**
	 * Sets factory used to access SAP session beans
	 * 
	 * @param genericFactory
	 */
	public void setGenericFactory(final GenericFactory genericFactory) {
		this.genericFactory = genericFactory;
	}

	/**
	 * Logging instance
	 */
	static final protected Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(InitBasketImpl.class.getName());

	@Override
	public void init(final Basket basket, final String soldToId,
			final String contactId) {
		sapLogger.entering("init()");
		try {

			initEmptyBasket(basket, soldToId, contactId);

			// update of cart is not needed as we do an update after first
			// addToCart

		} catch (final BusinessObjectException e) {
			throw new ApplicationBaseRuntimeException(
					"SalesDocument could not be initialized", e);

		} finally {
			sapLogger.exiting();
		}
	}

	private void initEmptyBasket(final Basket basket, final String soldToId,
			final String contactId) throws CommunicationException {
		final PartnerList partnerList = (PartnerList) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST);

		initPartnerList(partnerList, soldToId, contactId);

		basket.init(partnerList);
	}

	@Override
	public void initPartnerList(final PartnerList partnerList,
			final String soldToId, final String contactId) {

		sapLogger.entering("initPartnerList()");

		// Create the PartnerList and add SoldTo to the Partner List
		addPartnerData(partnerList, PartnerFunctionData.SOLDTO, soldToId);

		addPartnerData(partnerList, PartnerFunctionData.CONTACT, contactId);

		sapLogger.exiting();
	}

	/**
	 * Adds partner data to the partnerlist.
	 * 
	 * @param partnerList
	 * @param partnerFunction
	 * @param partnerId
	 */
	protected void addPartnerData(final PartnerList partnerList,
			final String partnerFunction, final String partnerId) {
		sapLogger.entering("addPartnerData()");

		if (sapLogger.isDebugEnabled()) {
			sapLogger.debug("add partner data");
		}
		if (partnerId != null) {
			final PartnerListEntry entry = (PartnerListEntry) genericFactory
					.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST_ENTRY);
			entry.setPartnerTechKey(new TechKey(partnerId));
			entry.setPartnerId(partnerId);

			partnerList.setPartnerData(partnerFunction, entry);
		}

		sapLogger.exiting();
	}

	/**
	 * Ensure that a shipto is set on basket header level
	 * 
	 * @param basket
	 *            BO cart representation
	 */
	protected void assureShipTo(final Basket basket) {
		final Header header = basket.getHeader();
		ShipTo shipTo = header.getShipTo();
		if (null == shipTo) {
			shipTo = basket.createShipTo();
			header.setShipTo(shipTo);
		}
	}

	/**
	 * Ensure that a billto is set on basket header level
	 * 
	 * @param basket
	 *            BO cart representation
	 */
	protected void assureBillTo(final Basket basket) {
		final Header header = basket.getHeader();
		BillTo billTo = header.getBillTo();
		if (null == billTo) {
			billTo = basket.createBillTo();
			header.setBillTo(billTo);
		}
	}

}
