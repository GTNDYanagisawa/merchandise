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
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.impl.erp;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ProcessingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderBaseBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.SalesDocumentERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesDocumentType;

import java.util.List;


/**
 * Back end Object representing an ERP Order document
 */
public abstract class OrderBaseERP extends SalesDocumentERP implements OrderBaseBackend
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(OrderBaseERP.class.getName());

	/**
	 * When the order is created with a reference to a quotation, the following reads of the order do not return docflow
	 * entries as long as the order was not saved. Thus we remember the Predecessor Quotation here, until the order has
	 * been saved, so we can properly fill the predecessor list for the reads before the order was saved.
	 */
	protected ConnectedDocument quotationPredecessorDocument = null;




	/**
	 * Saves the order in the backend.<br>
	 * The order will be saved in the backend.
	 * 
	 * @param ordr
	 *           The order to be saved
	 * @param commit
	 *           The transaction should also be commited
	 */
	@Override
	public boolean saveInBackend(final Order ordr, final boolean commit) throws BackendException
	{

		final String METHOD_NAME = "saveInBackend(order, commit = '" + Boolean.toString(commit) + "')";
		sapLogger.entering(METHOD_NAME);
		if (checkForInitializationError(ordr))
		{
			sapLogger.exiting();
			return false;
		}

		final boolean saveWasSuccessful = saveOrder(ordr, commit);

		sapLogger.exiting();
		return saveWasSuccessful;
	}

	/**
	 * Saves the order in the backend and returns, if it was successufull.
	 * 
	 * @param ordr
	 * @param commit
	 * @return true, if save was successfull, false otherwise
	 * @throws BackendException
	 */
	protected boolean saveOrder(final Order ordr, final boolean commit) throws BackendException
	{

		final String METHOD_NAME = "saveOrder(order, commit = '" + Boolean.toString(commit) + "')";
		sapLogger.entering(METHOD_NAME);

		boolean success = true;

		// get JCOConnection
		final JCoConnection aJCoCon = getDefaultJCoConnection();

		final BackendCallResult retVal = lrdActionsStrategy.executeLrdSave(ordr, commit, aJCoCon);

		if (retVal.isFailure())
		{
			ordr.setInvalid(); // order could not be saved
			success = false;
		}

		// Call the lord load
		if (success)
		{
			// after the order was saved the docflow entries will be returned by
			// the following reads
			quotationPredecessorDocument = null;
			// Now we are in display mode again
			loadState.setLoadOperation(LoadOperation.display);
			clearItemBuffer();
			success = callLordLoad(ordr, aJCoCon);
		}

		sapLogger.exiting();

		return success;
	}

	/**
	 * Calls the ERP_LORD_LOAD function module.
	 * 
	 * @param ordr
	 * @param aJCoCon
	 * @throws BackendException
	 */
	private boolean callLordLoad(final Order ordr, final JCoConnection aJCoCon) throws BackendException
	{

		boolean success = true;

		loadState.setLoadOperation(LoadOperation.display);
		final BackendCallResult retValLordLoad = lrdActionsStrategy.executeLrdLoad(ordr, this, aJCoCon, loadState);

		// Exit here, if there are errors
		if (retValLordLoad.isFailure())
		{
			sapLogger.debug("Errors in R3LrdLoad -> returning");
			success = false;
		}
		else
		{
			setDocflowRead(false);
		}

		return success;
	}

	/**
	 * Copies the basket object to the order object
	 * 
	 * @param ordr
	 *           Order document
	 * @param commit
	 *           Flag controlling DB commit in back end
	 * @return 0, if success, otherwise -1
	 * @throws BackendException
	 *            in case of a back-end error
	 */
	public int saveOrderInBackend(final Order ordr, final boolean commit) throws BackendException
	{

		final boolean success = saveOrder(ordr, commit);
		if (success)
		{
			return 0;
		}
		return -1;
	}

	/**
	 * Sets the Load State of LO-API to "create" mode. This is needed in case a basket gets copied into an order. Only
	 * for ERP backend.
	 */
	@Override
	public void setLoadStateCreate()
	{
		loadState.setLoadOperation(LoadOperation.create);
	}

	@Override
	public SalesDocumentType getSalesDocumentType()
	{
		return SalesDocumentType.ORDER;
	}


	/**
	 * The ERP order can be canceled if the overall status is opened or if the overall status is in process , but not
	 * shipped, not billed and no delivery created for the any position can be cancelled. This can happened if the credit
	 * check is switched off for instance. The credit check is performed for the order and set order status "in process".
	 * But order can be cancelled in this case
	 */
	@Override
	public boolean isCancelable(final Order order)
	{
		boolean isCancelable = true;
		final Header header = order.getHeader();
		final OverallStatus overallStatus = header.getOverallStatus();
		if (overallStatus != null)
		{
			final BillingStatus billingStatus = (BillingStatus) header.getBillingStatus();
			final ShippingStatus shippingStatus = (ShippingStatus) header.getShippingStatus();

			isCancelable = overallStatus.isNotProcessed()
					|| (overallStatus.isPartiallyProcessed() && (null == billingStatus || billingStatus.isNotProcessed()) && (null == shippingStatus || shippingStatus
							.isNotProcessed()));

		}
		isCancelable = isCancelable && noItemIsFullyProcessed(order);
		return isCancelable;
	}

	private boolean noItemIsFullyProcessed(final Order order)
	{
		boolean noItemIsFullyProcessed = true;
		final ItemList itemList = order.getItemList();
		for (final Item item : itemList)
		{
			final ProcessingStatus processingStatus = item.getProcessingStatus();
			if (processingStatus.isProcessed())
			{
				noItemIsFullyProcessed = false;
				break;
			}
		}
		return noItemIsFullyProcessed;
	}



	@Override
	public void updateInBackend(final SalesDocument salesDocument, final TransactionConfiguration shop) throws BackendException
	{
		super.updateInBackend(salesDocument, shop);

		if (additionalPricing.isPricingCallOrder())
		{
			executeAdditionalPricing(salesDocument, shop);
		}
	}

	@Override
	public void readFromBackend(final SalesDocument salesDocument, final TransactionConfiguration transConf,
			final boolean directRead) throws BackendException
	{
		super.readFromBackend(salesDocument, transConf, directRead);

		//handle product configuration
		final List<String> configurableItems = determineConfigurableItems(salesDocument);
		if (!configurableItems.isEmpty())
		{
			getProductConfigurationStrategy().readConfiguration(getDefaultJCoConnection(), salesDocument, configurableItems);
		}
	}



}
