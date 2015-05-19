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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import java.util.HashMap;
import java.util.Map;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LrdFieldExtension;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ERPLO_APICustomerExits;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.BackendMessageMapper;

/**
 * Provides customer exit methods for the R3LRD remote function calls.
 * <p>
 * Before and after every remote function call in the WrapperIsaR3Lrd classes, a
 * customer method is called. By default the methods are doing nothing, that
 * means, they have an empty implementation. To implement single customer
 * methods, the <code>ERPLO_APICustomerExits</code> class has to be subclassed
 * and the desired methods have to be overwritten.
 * </p>
 * <p>
 * To activate the customer exit, an entry has to be changed in the
 * <code>factory-config.xml</code> configuration file. The
 * <code>className</code> attribute has to be changed, so that it contains the
 * name of the subclass, mentioned above.
 * </p>
 * 
 * 
 * The <code>reuseInstance</code> attribute makes sure, that a once created
 * instance of the customer exit class is reused in every call (lean singleton).
 */

public class ERPLO_APICustomerExitsImpl implements ERPLO_APICustomerExits {

	/**
	 * Logging instance
	 */
	protected static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(ERPLO_APICustomerExitsImpl.class.getName());
	/**
	 * Map of extension fields
	 */
	protected Map<LrdFieldExtension.FieldType, LrdFieldExtension> extensionFields = null;
	private BackendMessageMapper messageMapper;

	/**
	 * @return Mapper (backend messages -> BOL messages, refer to messages.xml)
	 */
	public BackendMessageMapper getMessageMapper() {
		return messageMapper;
	}

	/**
	 * Sets message mapper (backend messages -> BOL messages, refer to
	 * messages.xml)
	 * 
	 * @param messageMapper
	 */
	public void setMessageMapper(final BackendMessageMapper messageMapper) {
		this.messageMapper = messageMapper;
	}

	/**
	 * Use these exits if you want to use additional parameters for function
	 * modules
	 * <ul>
	 * <li>ERP_LORD_LOAD</li>
	 * <li>ERP_LORD_GETALL</li>
	 * <li>ERP_LORD_SET</li>
	 * </ul>
	 * 
	 * @param salesDoc
	 *            The sales document (e.g. basket, order)
	 * @param func
	 *            The JCO function which allows to access all related JCO data
	 *            for this function module
	 * @param cn
	 *            The JCO connection which would allow to additionally call a
	 *            function module
	 * @param logging
	 *            Allow to log some useful debugging informations
	 */
	@Override
	public void customerExitBeforeLoad(final SalesDocument salesDoc,
			final JCoFunction func, final JCoConnection cn,
			final Log4JWrapper logging) {
		//
	}

	@Override
	public void customerExitAfterLoad(final SalesDocument salesDoc,
			final JCoFunction func, final JCoConnection cn,
			final Log4JWrapper logging) {
		//
	}

	@Override
	public void customerExitBeforeGetAll(
			final SalesDocument businessObjectInterface,
			final JCoFunction func, final JCoConnection cn,
			final Log4JWrapper logging) {
		//
	}

	/**
	 * Use this exit if you want to add additional item pricing parameters. It
	 * is called twice per item, first time for table <code>ttItemComR</code>
	 * and second time for <code>ttItemComV</code>.<br>
	 * <p>
	 * Example:<br>
	 * <code>
	 *   if (ttItemComR != null) { <br>
	 *       &nbsp;itemPriceAttributes.add("<i>attribute from table ttItemComR</i>", ttItemComR.getString("<i>attribute</i>"));<br>
	 *   } <br><br>
	 *   // item's extension data had been set in @see #customerExitAfterGetAll<br>
	 *   String attribValue = item.getExtensionData("<i>your pricing attribute name</i>");<br>
	 *   itemPriceAttributes.add("<i>your pricing attribute name</i>", attribValue);<br>
	 *   </code>
	 * </p>
	 * <p>
	 * Please keep in mind that the new attributes might need to be mapped to
	 * CRM values via function module <code>
	 * CRM_ERP_ECO_CFG_PRC_ATTR</code>.
	 * </p>
	 * 
	 * @param ttItemComR
	 *            Table TT_ITEM_COMR from function module
	 *            <code>ERP_LORD_GET_ALL</code>. Table pointer is already set to
	 *            the current item.<br>
	 *            Will be <code>null</code> if not available!<br>
	 *            <b>IMPORTANT:</b> DON'T change table pointer with commands
	 *            like <code>ttItemComR.firstRow()</code> or so.
	 * @param ttItemComV
	 *            Table TT_ITEM_COMV from function module
	 *            <code>ERP_LORD_GET_ALL</code>. Table pointer is already set to
	 *            the current item.<br>
	 *            Will be <code>null</code> if not available!<br>
	 *            <b>IMPORTANT:</b> DON'T change table pointer with commands
	 *            like <code>ttItemComV.firstRow()</code> or so.
	 * @param item
	 *            The current item
	 * @param itemPriceAttributes
	 *            The item's price attribute map.
	 */
	protected void customerExitAfterGetAllItemPricingAttributes(
			final JCoTable ttItemComR, final JCoTable ttItemComV,
			final Item item,
			final HashMap<String, HashMap<String, String>> itemPriceAttributes) {
		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend
	 * .impl.erp.strategy.ERPLO_APICustomerExits#
	 * customerExitAfterGetAll(com.sap
	 * .hybris.app.esales.module.transaction.businessobject
	 * .interf.SalesDocument, com.sap.conn.jco.JCoFunction,
	 * com.sap.wec.tc.core.backend.sp.jco.JCoConnection,
	 * com.sap.wec.tc.core.common.logging.WCFLocation)
	 */
	@Override
	public void customerExitAfterGetAll(
			final SalesDocument businessObjectInterface,
			final JCoFunction func, final JCoConnection cn,
			final Log4JWrapper logging) {
		//
	}

	@Override
	public void customerExitBeforeSet(final SalesDocument salesDoc,
			final JCoFunction func, final JCoConnection cn,
			final Log4JWrapper logging) {
		//
	}

	@Override
	public void customerExitAfterSet(final SalesDocument salesDoc,
			final JCoFunction func, final JCoConnection cn,
			final Log4JWrapper logging) {
		//
	}

	/**
	 * Use this exit if you want to use additional parameters before function
	 * modules >ERP_LORD_SAVE< ,
	 * 
	 * @param commit
	 *            Indicates if the document should be committed or not
	 * @param function
	 *            The JCO function which allows to access all related JCO data
	 *            for this function module
	 * @param cn
	 *            The JCO connection which would allow to additionally call a
	 *            function module
	 * @param logging
	 *            Allow to log some useful debugging informations
	 */
	@Override
	public void customerExitBeforeSave(final boolean commit,
			final JCoFunction function, final JCoConnection cn,
			final Log4JWrapper logging) {
		//
	}

	@Override
	public void customerExitAfterSave(final boolean commit,
			final JCoFunction function, final JCoConnection cn,
			final Log4JWrapper log) {
		//
	}

	/**
	 * Use this exit if you want to use additional parameters before function
	 * modules >ERP_LORD_SET_ACTIVE_FIELDS< ,
	 * 
	 * @param function
	 *            The JCO function which allows to access all related JCO data
	 *            for this function module
	 * @param cn
	 *            The JCO connection which would allow to additionally call a
	 *            function module
	 * @param logging
	 *            Allow to log some useful debugging informations
	 */
	@Override
	public void customerExitBeforeSetActiveFields(final JCoFunction function,
			final JCoConnection cn, final Log4JWrapper logging) {
		//
	}

	/**
	 * Use this exit if you want to use additional parameters after function
	 * modules >ERP_LORD_SET_ACTIVE_FIELDS< ,
	 * 
	 * @param function
	 *            The JCO function which allows to access all related JCO data
	 *            for this function module
	 * @param cn
	 *            The JCO connection which would allow to additionally call a
	 *            function module
	 * @param log
	 *            Allow to log some useful debugging informations
	 */
	@Override
	public void customerExitAfterSetActiveFields(final JCoFunction function,
			final JCoConnection cn, final Log4JWrapper log) {
		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend
	 * .impl.erp.strategy.ERPLO_APICustomerExits#
	 * customerExitGetExtensionFields()
	 */
	@Override
	public Map<LrdFieldExtension.FieldType, LrdFieldExtension> customerExitGetExtensionFields() {

		if (extensionFields != null) {
			return extensionFields;
		} else {
			extensionFields = new HashMap<LrdFieldExtension.FieldType, LrdFieldExtension>();
		}

		// // Header ComV
		// LrdFieldExtension headerComVExtension = new
		// LrdFieldExtension(LrdFieldExtension.FieldType.HeadComV);
		// headerComVExtension.addField("ZZ_BSTKD_E");
		// headerComVExtension.addField("ZZ_BSTDK_E");
		// extensionFields.put(LrdFieldExtension.FieldType.HeadComV ,
		// headerComVExtension);
		//
		// // Header ComR
		//
		//
		// //Item ComV
		// LrdFieldExtension itemComVExtension = new
		// LrdFieldExtension(LrdFieldExtension.FieldType.ItemComV);
		// itemComVExtension.addField("ZZ_BSTKD_E");
		// itemComVExtension.addField("ZZ_BSTDK_E");
		// extensionFields.put(LrdFieldExtension.FieldType.ItemComV ,
		// itemComVExtension);
		//
		// // Item ComR
		// LrdFieldExtension itemComRExtension = new
		// LrdFieldExtension(LrdFieldExtension.FieldType.ItemComR);
		// itemComRExtension.addField("ZZ_STDAT_R");
		// extensionFields.put(LrdFieldExtension.FieldType.ItemComR ,
		// itemComRExtension);

		return extensionFields;
	}

	/**
	 * With this customer exit it is possible to do further checks if items have
	 * been changed. E.g. if one introduced extension data per item, it's
	 * necessary to check on this data to decide whether this item needs to be
	 * sent to ERP or not. Note that this is relevant only if delta mode is
	 * DELTA_HANDLING_READ_ALL, DELTA_HANDLING_SEND_READ_CHANGED. The method is
	 * called if all other checks indicate that nothing has been changed on item
	 * level!
	 * 
	 * @param currentItem
	 *            Current state of WEC item
	 * @param existingItem
	 *            ERP state of WEC item
	 * @return Were there changes?
	 */
	@Override
	public boolean customerExitIsItemChanged(final Item currentItem,
			final Item existingItem) {
		return false;
	}

	@Override
	public void customerExitAfterGetAllItemRPricingAttributes(
			final JCoTable ttItemComR, final Item itm,
			final Map<String, String> ipcPriceAttributes) {
		//
	}

	@Override
	public void customerExitAfterGetAllItemVPricingAttributes(
			final JCoTable ttItemComV, final Item itm,
			final Map<String, String> ipcPriceAttributes) {
		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend
	 * .interf.erp.strategy.ERPLO_APICustomerExits #
	 * customerExitAfterGetAllJCOCall
	 * (de.hybris.platform.sap.sapordermgmtbol.transaction
	 * .businessobject.interf.SalesDocument , com.sap.conn.jco.JCoFunction,
	 * de.hybris.platform.sap.core.bol.backend.jco.JCoConnection,
	 * de.hybris.platform.sap.core.bol.logging.Log4JWrapper)
	 */
	@Override
	public void customerExitAfterGetAllJCOCall(
			final SalesDocument salesdocument, final JCoFunction function,
			final JCoConnection connection, final Log4JWrapper logging) {

		//
	}

}
