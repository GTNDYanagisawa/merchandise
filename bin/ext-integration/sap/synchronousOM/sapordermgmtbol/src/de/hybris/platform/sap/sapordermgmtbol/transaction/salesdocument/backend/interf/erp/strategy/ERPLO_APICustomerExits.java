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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LrdFieldExtension;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;

import java.util.Map;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;


/**
 * Provides possible interfaces (in the sense of customer exits) for ECO ERP sales document processing. <br>
 * Stability of those interfaces is not guaranteed by a declaration in this class but depends on public DC part where
 * this interface occurs.
 * 
 */
public interface ERPLO_APICustomerExits
{

	/**
	 * Customer exit which is executed directly before the JCO call to ERP. <br>
	 * {@link GetAllStrategy} <br>
	 * 
	 * @param salesDocument
	 *           BO representation of sales document
	 * @param function
	 *           JCO function, including all importing, exporting and table parameters
	 * @param connection
	 *           connection to ERP system
	 * @param logging
	 *           if new tracing/logging is needed
	 */
	public void customerExitBeforeGetAll(SalesDocument salesDocument, JCoFunction function, JCoConnection connection,
			Log4JWrapper logging);

	/**
	 * Customer exit which is executed directly after the JCO call to ERP for reading a sales document. <br>
	 * {@link GetAllStrategy} <br>
	 * 
	 * @param salesdocument
	 *           BO representation of sales document
	 * @param function
	 *           JCO function, including importing, exporting, table parameters
	 * @param connection
	 *           connection to ERP
	 * @param logging
	 *           if additional tracing/logging is needed
	 */
	public void customerExitAfterGetAllJCOCall(SalesDocument salesdocument, JCoFunction function, JCoConnection connection,
			Log4JWrapper logging);


	/**
	 * Customer exit which is executed after the data returned from the JCO call has been processed <br>
	 * {@link GetAllStrategy} <br>
	 * 
	 * @param salesdocument
	 *           BO representation of sales document
	 * @param function
	 *           JCO function, including importing, exporting, table parameters
	 * @param connection
	 *           connection to ERP
	 * @param logging
	 *           if additional tracing/logging is needed
	 */
	public void customerExitAfterGetAll(SalesDocument salesdocument, JCoFunction function, JCoConnection connection,
			Log4JWrapper logging);

	/**
	 * Use this exit, if you want to expose your LO-API field extensions also to the Web Channel.
	 * 
	 * @return map containing all extended fields
	 */
	public Map<LrdFieldExtension.FieldType, LrdFieldExtension> customerExitGetExtensionFields();

	/**
	 * This customer exit is called before <br>
	 * {@link ConstantsR3Lrd#FM_LO_API_DO_ACTIONS} or <br>
	 * {@link ConstantsR3Lrd#FM_LO_API_LOAD} <br>
	 * are executed.
	 * 
	 * @param salesDoc
	 *           BO representation of sales document
	 * @param function
	 *           JCO function
	 * @param connection
	 *           connection to ERP system
	 * @param log
	 *           if additional tracing/logging is necessary
	 */
	public void customerExitBeforeLoad(SalesDocument salesDoc, JCoFunction function, JCoConnection connection, Log4JWrapper log);

	/**
	 * This customer exit is called after <br>
	 * {@link ConstantsR3Lrd#FM_LO_API_DO_ACTIONS} or <br>
	 * {@link ConstantsR3Lrd#FM_LO_API_LOAD} <br>
	 * are executed.
	 * 
	 * @param salesDoc
	 *           BO representation of sales document
	 * @param function
	 *           JCO function
	 * @param connection
	 *           connection to ERP system
	 * @param log
	 *           if additional tracing/logging is necessary
	 */
	public void customerExitAfterLoad(SalesDocument salesDoc, JCoFunction function, JCoConnection connection, Log4JWrapper log);





	/**
	 * Customer exit called after JCO call of {@link ConstantsR3Lrd#FM_LO_API_SAVE} is done <br>
	 * 
	 * @param commit
	 *           Specifies if the save should be committed also
	 * @param function
	 *           JCO function
	 * @param cn
	 *           JCO connection
	 * @param log
	 *           if additional tracing/logging is necessary
	 */
	public void customerExitAfterSave(boolean commit, JCoFunction function, JCoConnection cn, Log4JWrapper log);

	/**
	 * Customer exit called before JCO call of {@link ConstantsR3Lrd#FM_LO_API_SAVE} is done <br>
	 * 
	 * @param commit
	 *           Specifies if the save should be committed also
	 * @param function
	 *           JCO function
	 * @param cn
	 *           JCO connection
	 * @param log
	 *           if additional tracing/logging is necessary
	 */
	public void customerExitBeforeSave(boolean commit, JCoFunction function, JCoConnection cn, Log4JWrapper log);

	/**
	 * Customer exit called before JCO call of {@link ConstantsR3Lrd#FM_LO_API_SET_ACTIVE_FIELDS} is done <br>
	 * 
	 * @param function
	 *           JCO function
	 * @param cn
	 *           JCO connection
	 * @param log
	 *           if additional tracing/logging is necessary
	 */
	public void customerExitBeforeSetActiveFields(JCoFunction function, JCoConnection cn, Log4JWrapper log);

	/**
	 * Customer exit called after JCO call of {@link ConstantsR3Lrd#FM_LO_API_SET_ACTIVE_FIELDS} is done <br>
	 * 
	 * @param function
	 *           JCO function
	 * @param cn
	 *           JCO connection
	 * @param log
	 *           if additional tracing/logging is necessary
	 */
	public void customerExitAfterSetActiveFields(JCoFunction function, JCoConnection cn, Log4JWrapper log);




	/**
	 * Customer exit called after JCO call of {@link ConstantsR3Lrd#FM_LO_API_SET} is done <br>
	 * 
	 * @param salesDoc
	 *           sales document which is updated in the back end
	 * @param function
	 *           JCO function
	 * @param cn
	 *           JCO connection
	 * @param log
	 *           if additional tracing/logging is necessary
	 */
	public void customerExitAfterSet(SalesDocument salesDoc, JCoFunction function, JCoConnection cn, Log4JWrapper log);

	/**
	 * Customer exit called before JCO call of {@link ConstantsR3Lrd#FM_LO_API_SET} is done <br>
	 * 
	 * @param salesDoc
	 *           sales document which is updated in the back end
	 * @param function
	 *           JCO function
	 * @param cn
	 *           JCO connection
	 * @param log
	 *           if additional tracing/logging is necessary
	 */
	public void customerExitBeforeSet(SalesDocument salesDoc, JCoFunction function, JCoConnection cn, Log4JWrapper log);

	/**
	 * Customer exit to check whether an item was changed. This exit might be used to take customer fields into account
	 * to decided wehter an item should be considered as changed. <br>
	 * 
	 * @param currentItem
	 *           current item
	 * @param existingItem
	 *           the old item
	 * @return <code>true</code>, only if the items should be considered changed
	 */
	public boolean customerExitIsItemChanged(Item currentItem, Item existingItem);

	/**
	 * This is called after putting price relevant attributes from item table TT_ITEM_COMR to the map of ipc item
	 * attributes.<br>
	 * 
	 * @param ttItemComR
	 *           Table of type TDT_RFC_ITEM_COMR
	 * @param itm
	 *           Sales item
	 * @param ipcPriceAttributes
	 *           Map of attributes
	 */
	public void customerExitAfterGetAllItemRPricingAttributes(JCoTable ttItemComR, Item itm, Map<String, String> ipcPriceAttributes);

	/**
	 * This is called after putting price relevant attributes from TT_ITEM_COMV to the map of ipc item attributes.<br>
	 * 
	 * @param ttItemComV
	 *           Table of type TDT_RFC_ITEM_COMV
	 * @param itm
	 *           Sales item
	 * @param ipcPriceAttributes
	 *           Map of attributes
	 */
	public void customerExitAfterGetAllItemVPricingAttributes(JCoTable ttItemComV, Item itm, Map<String, String> ipcPriceAttributes);

}