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
package de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.impl.erp;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.interf.BasketBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.SalesDocumentERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesDocumentType;


/**
 * ERP specific basket implementation. Most parts inherited from {@link SalesDocumentERP}
 * 
 */
@BackendType("ERP")
public class BasketERP extends SalesDocumentERP implements BasketBackend
{

	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(BasketERP.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.BasketBackend #
	 * saveInBackend(com.sap.hybris.app.esales.module.transaction.businessobject.interf .Basket, boolean)
	 */
	@Override
	public boolean saveInBackend(final Basket basket, final boolean commit) throws BackendException
	{

		final String METHOD_NAME = "saveInBackend(basket, commit = '" + Boolean.toString(commit) + "')";
		sapLogger.entering(METHOD_NAME);
		boolean saveWasSuccessful = true;

		// get JCOConnection
		final JCoConnection aJCoCon = getDefaultJCoConnection();

		final BackendCallResult retVal = lrdActionsStrategy.executeLrdSave(basket, commit, aJCoCon);

		if (retVal.isFailure())
		{
			basket.setInvalid(); // Basket could not be saved
			saveWasSuccessful = false;
		}

		sapLogger.exiting();
		return saveWasSuccessful;
	}

	@Override
	public SalesDocumentType getSalesDocumentType()
	{
		return SalesDocumentType.BASKET;
	}

	@Override
	public void readFromBackend(final SalesDocument salesDocument, final TransactionConfiguration transConf,
			final boolean directRead) throws BackendException
	{
		super.readFromBackend(salesDocument, transConf, directRead);

		// finally add warning with respect to UME support if
		// necessary.
		if (isUMEDisabledWarningNecessary)
		{
			final Message umeDisabled = new Message(Message.WARNING, "sapsalestransactions.erp.umedisabled");
			salesDocument.getHeader().addMessage(umeDisabled);
		}

	}



	/**
	 * In the BasketERP case the readForUpdateFromBackend calls readFromBackend to avoid calling of ERP_WEC_ORDER_LOAD
	 */
	@Override
	public void readForUpdateFromBackend(final SalesDocument salesDocument) throws BackendException
	{
		readFromBackend(salesDocument, salesDocument.getTransactionConfiguration(), false);

	}

	@Override
	public void updateInBackend(final SalesDocument salesDocument, final TransactionConfiguration shop) throws BackendException
	{
		super.updateInBackend(salesDocument, shop);

		if (additionalPricing.isPricingCallCart())
		{
			if (checkForInitializationError(salesDocument))
			{
				return;
			}
			executeAdditionalPricing(salesDocument, shop);
		}
	}

}
