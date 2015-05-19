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
package de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl;

import java.math.BigDecimal;
import java.util.Iterator;

import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectHelper;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.interf.BasketBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.modulemgmt.interf.BasketOrderConsistency;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.SalesDocumentBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.businessobject.impl.SalesDocumentImpl;

/**
 * Shopping basket BO implementation. The well known methaphor of a shopping
 * basket is implemented using this class. The basket consist of several items,
 * and header information.
 * 
 */
@BackendInterface(BasketBackend.class)
public class BasketImpl extends SalesDocumentImpl implements Basket {

	private static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(BasketImpl.class.getName());
	/**
	 * Ensures consistency between basket and order
	 */
	protected BasketOrderConsistency basketOrderConsistency;

	@Override
	public boolean isUpdateMissing() {
		final boolean updateMissing = (basketOrderConsistency != null && basketOrderConsistency
				.isUpdateMissing()) || super.isUpdateMissing();
		return updateMissing;
	}

	@Override
	public void setUpdateMissing(final boolean updateMissing) {
		if ((basketOrderConsistency != null) && updateMissing) {
			basketOrderConsistency.setBasketUpdateMissing();
		}
		super.setUpdateMissing(updateMissing);
	}

	/**
	 * Callback method for the <code>BusinessObjectManager</code> to tell the
	 * object that life is over and that it has to release all ressources.
	 */
	@Override
	public void destroy() {
		super.destroy();
		backendService = null;
		alreadyInitialized = false;
	}

	/**
	 * Method retrieving the backend object for the object.
	 * 
	 * @return Backend object to be used
	 */
	@Override
	protected SalesDocumentBackend getBackendService() throws BackendException {

		if (backendService == null) {

			backendService = (SalesDocumentBackend) getBackendBusinessObject();

		}

		return backendService;
	}

	/**
	 * Saves the order in the backend.
	 */
	@Override
	public boolean saveAndCommit() throws CommunicationException {

		sapLogger.entering("saveAndCommit()");

		boolean saveWasSuccessful = false;
		try {
			saveWasSuccessful = ((BasketBackend) getBackendService())
					.saveInBackend(this, true);
			setDirty(true);
			getHeader().setDirty(true);
		} catch (final BackendException ex) {
			BusinessObjectHelper.splitException(ex);
		} finally {
			sapLogger.exiting();
		}

		return saveWasSuccessful;
	}

	/**
	 * This method can be used to set a fixed back end service (e.g. mock it)
	 * 
	 * @param service
	 *            back end service to be used by this basket
	 */
	public void setBackendService(final BasketBackend service) {
		backendService = service;
	}

	@Override
	public void setBasketOrderConsistency(
			final BasketOrderConsistency basketOrderConsistency) {
		this.basketOrderConsistency = basketOrderConsistency;

	}

	/**
	 * Overwrites the super implementation.<br>
	 * Checks if basket and order are consistent. If the data was not consistent
	 * a update is triggered before the read
	 * 
	 * @param force
	 *            If true, then read even if not considered as necessary
	 * @throws CommunicationException
	 *             in case back-end error
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.businessobject.impl.SalesDocumentImpl#read(boolean)
	 */
	@Override
	public void read(final boolean force) throws CommunicationException {
		assureConsistencyAndUpdate();
		super.read(force);
	}

	private void assureConsistencyAndUpdate() throws CommunicationException {
		final boolean isUpdateNeeded = assureConsistency();
		if (isUpdateNeeded) {
			update();
		}
	}

	private boolean assureConsistency() {
		if (basketOrderConsistency == null) {
			sapLogger.debug("No consistency object injected");
			return false;
		}

		final boolean isUpdateNeeded = basketOrderConsistency
				.assureConsistentData(this);
		return isUpdateNeeded;
	}

	@Override
	public void readForUpdate(final boolean force)
			throws CommunicationException {
		assureConsistencyAndUpdate();
		super.readForUpdate(force);
	}

	@Override
	public void update() throws CommunicationException {
		removeEmptyItemsFromItemList();

		super.update();

	}

	/**
	 * Removes empty items (blank product ID) from item list
	 */
	protected void removeEmptyItemsFromItemList() {
		final Iterator<Item> items = getItemList().iterator();

		while (items.hasNext()) {
			final Item item = items.next();
			final String product = item.getProductId();
			final TechKey guid = item.getTechKey();
			if (isEmptyProduct(product) && TechKey.isEmpty(guid)) {
				items.remove();
			}
		}

	}

	private boolean isEmptyProduct(final String product) {
		return (null == product) || product.isEmpty();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(
				"Clone is not supported for this business object");
	}

	@Override
	public BigDecimal calculateTotalQuantity() {
		BigDecimal totalQuantity = BigDecimal.ZERO;
		for (final Item item : getItemList()) {
			if (!isEmptyProduct(item.getProductId())) {
				final BigDecimal quantity = item.getQuantity();
				totalQuantity = totalQuantity.add(quantity);
			}
		}
		return totalQuantity;
	}

	@Override
	protected boolean mergeIdenticalProductsIfRequired()
			throws CommunicationException {
		boolean changed = false;
		if (getTransactionConfiguration().isMergeIdenticalProducts()) {
			changed = mergeIdenticalProducts();
		}
		return changed;
	}

	@Override
	public void release() throws CommunicationException {
		try {
			getBackendService().closeBackendSession();
		} catch (final BackendException e1) {
			BusinessObjectHelper.splitException(e1);
		}
		clearHeader();
		clearItems();
		clearMessages();
		setInitialized(false);
		// release backend object via retrieving a newly initialized backend
		// object
		try {
			setBackendService((BasketBackend) getBackendBusinessObject(true));
		} catch (final BackendException e) {
			BusinessObjectHelper.splitException(e);
		}

	}

}
