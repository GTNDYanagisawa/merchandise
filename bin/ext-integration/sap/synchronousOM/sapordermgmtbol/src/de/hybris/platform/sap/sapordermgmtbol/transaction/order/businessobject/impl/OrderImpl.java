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
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl;

import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectHelper;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationRuntimeException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.SalesDocumentBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.businessobject.impl.SalesDocumentImpl;


/**
 * Class representing the BOL order.
 * 
 */
@BackendInterface(OrderBackend.class)
public class OrderImpl extends SalesDocumentImpl implements Order
{

	private TechKey basketId;
	private final boolean invalid = false;
	/**
	 * Backend service / guest scenario
	 */
	protected SalesDocumentBackend backendServiceGuest;

	/**
	 * reference to SAP logging API
	 */
	public static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(OrderImpl.class.getName());

	/**
	 * Returns the TechKey of the basket used for creating this order.
	 * 
	 * @return TechKey of Basket
	 */
	@Override
	public TechKey getBasketId()
	{
		try
		{
			if (invalid)
			{
				read();
			}
			return basketId;
		}
		catch (final CommunicationException e)
		{
			throw new CommunicationRuntimeException("Failed to read order", e);
		}
	}

	/**
	 * Sets the TechKey of basket used for creating this order
	 * 
	 * @param basketId
	 *           TechKey of the basket
	 */
	@Override
	public void setBasketId(final TechKey basketId)
	{
		this.basketId = basketId;
	}

	/**
	 * Saves the order in the backend.
	 */
	@Override
	public boolean saveAndCommit() throws CommunicationException
	{

		sapLogger.entering("saveAndCommit()");
		return saveOrderAndCommit();
	}

	/**
	 * Saves the order in the backend.
	 */
	@Override
	public boolean saveOrderAndCommit() throws CommunicationException
	{

		sapLogger.entering("saveOrderAndCommit()");

		if (isUpdateMissing())
		{
			update();
		}

		boolean saveWasSuccessful = false;
		try
		{
			saveWasSuccessful = ((OrderBackend) getBackendService()).saveInBackend(this, true);
			setDirty(true);
			getHeader().setDirty(true);
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		finally
		{
			sapLogger.exiting();
		}

		if (saveWasSuccessful)
		{
			setPersistentInBackend(true);
		}

		return saveWasSuccessful;

	}

	/*
	 * Destroy the data of the document (items and header) in the backend representation. After a call to this method,
	 * the object is in an undefined state. You have to call init() before you can use it again.<br> This method is
	 * normally called before removing the BO-representation of the object using the BOM.
	 */
	@Override
	public void destroyContent() throws CommunicationException
	{

		sapLogger.entering("destroyContent()");
		try
		{
			setDirty(true);
			getHeader().setDirty(true);
			getBackendService().emptyInBackend(this);
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		finally
		{
			sapLogger.exiting();
		}
	}


	@Override
	protected SalesDocumentBackend getBackendService() throws BackendException
	{
		if (backendService == null)
		{
			backendService = (SalesDocumentBackend) getBackendBusinessObject();
		}
		return backendService;
	}

	/**
	 * This method can be used to set a fixed back end service (e.g. mock it)
	 * 
	 * @param service
	 *           back end service to be used by this order
	 */
	public void setBackendService(final OrderBackend service)
	{
		backendService = service;
	}




	@Override
	public void setLoadStateCreate() throws BusinessObjectException
	{
		sapLogger.entering("setLoadStateCreate()");
		try
		{
			getBackendService().setLoadStateCreate();
		}
		catch (final BackendException e)
		{
			BusinessObjectHelper.splitException(e);
		}
		sapLogger.exiting();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	@Override
	public void destroy()
	{
		basketId = null;
		super.destroy();
	}



	@Override
	public boolean isCancelable() throws CommunicationException
	{
		{
			try
			{
				return ((OrderBackend) getBackendService()).isCancelable(this);
			}
			catch (final BackendException ex)
			{
				BusinessObjectHelper.splitException(ex);
			}
		}
		return false;
	}

}
