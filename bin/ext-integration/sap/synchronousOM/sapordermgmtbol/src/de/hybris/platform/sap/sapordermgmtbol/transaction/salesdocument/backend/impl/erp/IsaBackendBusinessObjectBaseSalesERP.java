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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp;

import de.hybris.platform.sap.core.bol.backend.jco.BackendBusinessObjectBaseJCo;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.interf.FreeGoodSupportBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sap.conn.jco.JCoTable;


/**
 * Superclass of R3Lrd documents, containing common functionality
 * 
 */
public abstract class IsaBackendBusinessObjectBaseSalesERP extends BackendBusinessObjectBaseJCo implements BackendState
{

	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(IsaBackendBusinessObjectBaseSalesERP.class.getName());

	/**
	 * Map of price attribute maps for items
	 */
	protected Map<String, Map<String, String>> itemsPriceAttribMap = null;

	/**
	 * Map of price attributes for the header
	 */
	protected HashMap<String, String> headerPriceAttribs = null;

	/**
	 * Map of price properties for items
	 */
	protected HashMap<String, String> itemsPropMap = null;

	/**
	 * Map of price properties for the header
	 */
	protected HashMap<String, String> headerPropMap = null;

	/**
	 * store configuration changeable info for configurable itemSalesDocs
	 */
	protected HashMap<String, Boolean> itemConfigChangeableMap = null;

	/**
	 * flag if price attributes are needed, to show IPC prices for configurable products
	 */
	protected boolean setIpcPriceAttributes = false;

	/**
	 * store shipTo info for itemSalesDocs
	 */
	protected HashMap<String, ShipTo> shipToMap = new HashMap<String, ShipTo>(0);

	// store shipTo info of the predecessor item, e.g. template

	/**
	 * 
	 */
	protected HashMap<Object, ShipTo> predecessorShipTo = new HashMap<Object, ShipTo>(0);

	// Lord Load state
	/**
	 * 
	 */
	protected LoadOperation loadState = new LoadOperation();

	// reference to the shop, needed for IPC issues
	// whenever the object is created or initialized, it must be made sure,
	// that the shop is set
	// protected ShopData shop = null;

	/**
	 * List of saved items of the order. In order to be able to distinguish between saved items and new items in case of
	 * order change, the handles of the saved items are added to this list when the order is read the first time from the
	 * backend * *
	 */
	protected Set<String> savedItemsMap = new HashSet<String>(0);

	@Override
	public Set<String> getSavedItemsMap()
	{
		return savedItemsMap;
	}

	/**
	 * Due to performance reasons the docflow of header and items should only be read once. Therefore the docflow data
	 * are cached after the first call or ERP_LORD_GETALL.
	 */
	protected boolean docflowRead = false;

	/**
	 * JCO table: item document flow
	 */
	protected JCoTable itemDocFlow;

	/**
	 * JCO table: header
	 */
	protected JCoTable headerDocFlow;

	@Override
	public void initBackendObject() throws BackendException
	{

		loadState.setLoadOperation(LoadOperation.display);
		super.initBackendObject();
	}

	@Override
	public HashMap<String, String> getHeaderPriceAttribs()
	{

		sapLogger.entering("getHeaderPriceAttribs");

		if (headerPriceAttribs == null)
		{
			sapLogger.debug("Create Header price Attribute Map");
			headerPriceAttribs = new HashMap<String, String>(5);
		}

		sapLogger.exiting();

		return headerPriceAttribs;
	}

	@Override
	public Map<String, Map<String, String>> getItemsPriceAttribMap()
	{

		sapLogger.entering("getItemsPriceAttribMap");

		if (itemsPriceAttribMap == null)
		{
			sapLogger.debug("Create Item price Attribute Map");
			itemsPriceAttribMap = new HashMap<String, Map<String, String>>(5);
		}

		sapLogger.exiting();
		return itemsPriceAttribMap;
	}

	@Override
	public HashMap<String, String> getHeaderPropMap()
	{

		sapLogger.entering("getHeaderPropMap");

		if (headerPropMap == null)
		{
			sapLogger.debug("Create Header property Map");
			headerPropMap = new HashMap<String, String>(2);
		}

		sapLogger.exiting();
		return headerPropMap;
	}

	@Override
	public HashMap<String, String> getItemsPropMap()
	{

		sapLogger.entering("getItemsPropMap");

		if (itemsPropMap == null)
		{
			sapLogger.debug("Create Item property Map");
			itemsPropMap = new HashMap<String, String>(1);
		}

		sapLogger.exiting();
		return itemsPropMap;
	}

	@Override
	public abstract HashMap<String, String> getItemVariantMap();

	@Override
	public HashMap<String, ShipTo> getShipToMap()
	{
		return shipToMap;
	}

	/**
	 * sets the map, which stores the header resp. item vs. shipToKey relation
	 * 
	 * @param map
	 *           shipTo map
	 */
	public void setShipToMap(final HashMap<String, ShipTo> map)
	{
		this.shipToMap = map;
	}

	/**
	 * Adjusts the free good related sub items. Forwards to {@link FreeGoodSupportBackend}.
	 * 
	 * @param posd
	 *           the sales document
	 * @param transConf
	 *           Configuration for SAP synchronous order management
	 * @return was there an inclusive FG item?
	 * @throws BackendException
	 *            exception from parsing etc.
	 */

	protected boolean adjustFreeGoods(final SalesDocument posd, final TransactionConfiguration transConf) throws BackendException
	{
		if (transConf == null)
		{
			final BackendException ex = new BackendException("adjustFreeGoods: parameter 'transConf' is null");
			sapLogger.throwing(ex);
			throw ex;
		}

		return FreeGoodSupportBackend.adjustSalesDocument(posd);
	}

	@Override
	public boolean isDocflowRead()
	{
		return docflowRead;
	}

	@Override
	public void setDocflowRead(final boolean docflowread)
	{
		docflowRead = docflowread;
	}

	@Override
	public JCoTable getHeaderDocFlow()
	{

		return headerDocFlow;
	}

	@Override
	public JCoTable getItemDocFlow()
	{
		return itemDocFlow;
	}

	@Override
	public void setHeaderDocFlow(final JCoTable table)
	{
		headerDocFlow = table;
	}

	@Override
	public void setItemDocFlow(final JCoTable table)
	{
		itemDocFlow = table;
	}

	/**
	 * Map of messages.
	 */
	protected HashMap<String, MessageList> messageBufferMap = new HashMap<String, MessageList>(1);

	@Override
	public MessageList getMessageList(final TechKey key)
	{

		MessageList msgList = null;
		String keyStr = "DEFAULT";

		if (key != null && key.getIdAsString().length() > 0)
		{
			keyStr = key.getIdAsString();
		}

		msgList = messageBufferMap.get(keyStr);

		return msgList;
	}

	@Override
	public MessageList getOrCreateMessageList(final TechKey key)
	{

		MessageList msgList = null;
		String keyStr = "DEFAULT";

		if (key != null && key.getIdAsString().length() > 0)
		{
			keyStr = key.getIdAsString();
		}
		if (messageBufferMap != null)
		{
			msgList = messageBufferMap.get(key.getIdAsString());
		}
		if (msgList == null)
		{
			msgList = new MessageList();
			messageBufferMap.put(keyStr, msgList);
		}

		return msgList;
	}

	@Override
	public void removeMessageFromMessageList(final TechKey key, final String resourceKey)
	{

		MessageList msgList = null;
		String keyStr = "DEFAULT";

		if (key != null && key.getIdAsString().length() > 0)
		{
			keyStr = key.getIdAsString();
		}

		msgList = messageBufferMap.get(keyStr);
		if (msgList != null && resourceKey != null)
		{
			msgList.remove(resourceKey);
		}
	}

	@Override
	public LoadOperation getLoadState()
	{
		return loadState;
	}

}
