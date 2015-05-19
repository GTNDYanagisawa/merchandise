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

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.impl.erp.BasketERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.impl.erp.OrderERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.BaseStrategyERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.SalesDocumentBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.AdditionalPricing;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ItemBuffer;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.GetAllStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.LrdActionsStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.LrdCloseStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ProductConfigurationStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.SetStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.GetAllReadParameters;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl.ItemHierarchy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Common base class for ECO ERP sales documents. See {@link OrderERP}, {@link BasketERP}
 * 
 */
public abstract class SalesDocumentERP extends IsaBackendBusinessObjectBaseSalesERP implements SalesDocumentBackend
{

	private LrdCloseStrategy closeStrategy;

	/**
	 * constant naming the key referenced in factory-config.xml where the strategy class is specified
	 */
	public static final String STRATEGY_FACTORY_ERP = "STStrategyFactoryERP";

	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(SalesDocumentERP.class.getName());

	/** UI controller relevant constant for 'OrderItemStructure' */
	public static final String ORDER_ITEM_STRUCTURE = "ORDER_ITEM";
	/** UI controller relevant constant for 'OrderHeaderStructure' */
	public static final String ORDER_HEADER_STRUCTURE = "ORDER_HEADER";

	private ItemBuffer itemBuffer;

	/**
	 * @return the itemBuffer
	 */
	public ItemBuffer getItemBuffer()
	{
		return itemBuffer;
	}

	/**
	 * @param itemBuffer
	 *           the itemBuffer to set
	 */
	public void setItemBuffer(final ItemBuffer itemBuffer)
	{
		this.itemBuffer = itemBuffer;
	}

	private final Map<TechKey, Item> erroneousItems = new HashMap<TechKey, Item>();

	/**
	 * Warning message in case UME is disabled. Then we only have one session in ERP, and we cannot fully support
	 * multiple documents. UME disabling will not be possible in productive environments.
	 */
	protected boolean isUMEDisabledWarningNecessary = false;


	/**
	 * Strategy pattern to support various LO-API versions
	 */
	protected GetAllStrategy getAllStrategy = null;

	/**
	 * @return the getAllStrategy
	 */
	public GetAllStrategy getGetAllStrategy()
	{
		return getAllStrategy;
	}

	/**
	 * @param getAllStrategy
	 *           the getAllStrategy to set
	 */
	public void setGetAllStrategy(final GetAllStrategy getAllStrategy)
	{
		this.getAllStrategy = getAllStrategy;
	}

	/**
	 * @return the lrdActionsStrategy
	 */
	public LrdActionsStrategy getLrdActionsStrategy()
	{
		return lrdActionsStrategy;
	}

	/**
	 * @param lrdActionsStrategy
	 *           the lrdActionsStrategy to set
	 */
	public void setLrdActionsStrategy(final LrdActionsStrategy lrdActionsStrategy)
	{
		this.lrdActionsStrategy = lrdActionsStrategy;
	}

	/**
	 * @return the setStrategy
	 */
	public SetStrategy getSetStrategy()
	{
		return setStrategy;
	}

	/**
	 * @param setStrategy
	 *           the setStrategy to set
	 */
	public void setSetStrategy(final SetStrategy setStrategy)
	{
		this.setStrategy = setStrategy;
	}

	protected LrdActionsStrategy lrdActionsStrategy = null;

	protected SetStrategy setStrategy = null;


	// store variant info for itemSalesDocs
	protected HashMap<String, String> itemVariantMap = new HashMap<String, String>(0);

	// flag if inline config display isd enabled
	protected boolean showInlineConfig = true;

	// The condition type which should be used to determine the freight value
	protected String headerCondTypeFreight = "";

	// flag if incompletion log is requested
	protected boolean isIncompletionLogRequested = false;

	// store delivery Priority coming from UI as ERP Backend does not support it
	// on header level
	protected String deliveryPriority = null;

	// Error state of the document in the backend (ERRKZ)
	protected boolean erroneous = false;

	protected TransactionConfiguration transactionConfiguration;

	protected boolean createProcess;

	private String soldToHandle;

	private String payerHandle;

	private boolean documentInitial;

	private String shippingCondition = null;

	private boolean isRequiredDeliveryDateNeededForInitializing = false;

	private Header erroneousHeader;

	protected AdditionalPricing additionalPricing;

	/**
	 * @param productConfigurationStrategy
	 *           the productConfigurationStrategy to set
	 */
	public void setProductConfigurationStrategy(final ProductConfigurationStrategy productConfigurationStrategy)
	{
		this.productConfigurationStrategy = productConfigurationStrategy;
	}

	ProductConfigurationStrategy productConfigurationStrategy;

	@Override
	public boolean isDocumentInitial()
	{
		return documentInitial;
	}

	protected SalesDocumentERP()
	{
		super();
	}

	@Override
	public void setSoldToHandle(final String soldTo)
	{
		soldToHandle = soldTo;

	}

	@Override
	public void setPayerHandle(final String payer)
	{
		payerHandle = payer;

	}

	@Override
	public String getPayerHandle()
	{
		return payerHandle;
	}

	@Override
	public String getSoldToHandle()
	{
		return soldToHandle;
	}

	@Override
	public void setDocumentInitial(final boolean isInitial)
	{
		documentInitial = isInitial;
	}







	/**
	 * Create back end representation of the object
	 * 
	 * @param salesDocument
	 *           sales document to be created in back end
	 * @throws BackendException
	 *            case of a back-end error
	 */
	public void createInBackend(final SalesDocument salesDocument) throws BackendException
	{
		createInBackend(null, salesDocument);
	}

	/**
	 * Create back end representation of the object without providing information about the user. This may happen in the
	 * B2C scenario where the login may be done later.
	 * 
	 * @param transactionConfiguration
	 *           The current shop
	 * @param salesDocument
	 *           The document to read the data in
	 */
	@Override
	public void createInBackend(final TransactionConfiguration transactionConfiguration, final SalesDocument salesDocument)
			throws BackendException
	{

		sapLogger.entering("createInBackend()");

		// clear ERP buffer, because we are creating a new document in
		// ERP
		itemBuffer.clearERPBuffer();

		// Shop needs to be initialized. Other methods rely on this.
		this.transactionConfiguration = transactionConfiguration;

		// get JCOConnection
		final JCoConnection aJCoCon = getDefaultJCoConnection();

		loadState.setLoadOperation(LoadOperation.create);
		final BackendCallResult retVal = lrdActionsStrategy.executeLrdLoad(salesDocument, this, aJCoCon, loadState);

		// Exit here, if there are errors
		if (retVal.isFailure() && (!salesDocument.isInitialized()))
		{
			salesDocument.setInvalid();
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("Errors in R3LrdLoad -> returning");
			}

			return;
		}
		else if (retVal.isFailure())
		{
			// correct error, error is recoverable
			handleInvalidHeaderAfterUpdate(salesDocument);
			restoreErroneousHeaderAfterSet(salesDocument.getHeader());
			isRequiredDeliveryDateNeededForInitializing = true;
		}
		else
		{
			setErroneousHeader(null);
		}

		// a subsequent read is not needed
		salesDocument.setDirty(false);

		createProcess = true;

		// Set Active Fields List
		lrdActionsStrategy.executeSetActiveFields(aJCoCon);

		sapLogger.exiting();
	}

	/**
	 * Document couldn't be created. Now the header needs to be corrected, and the document is again sent to ERP. <br>
	 * 
	 * @throws BackendException
	 */
	protected void handleInvalidHeaderAfterUpdate(final SalesDocument salesDocument) throws BackendException
	{
		final Header header = salesDocument.getHeader();
		final Header copy = (Header) header.clone();
		copy.setReqDeliveryDate(null);
		copy.copyMessages(salesDocument);
		setErroneousHeader(copy);

		fixErroneousHeader(salesDocument);

		setStrategy.execute(this, salesDocument, itemBuffer.getItemsERPState(), transactionConfiguration,
				getDefaultJCoConnection(), null, true);

	}



	/**
	 * Restoring header: Initializing delivery date.<br>
	 * 
	 * @param header
	 */
	protected void restoreErroneousHeaderAfterSet(final Header header)
	{
		header.setReqDeliveryDate(ConstantsR3Lrd.DATE_INITIAL);

	}

	/**
	 * Fixing a header which cannot be digested from ERP. This implementation sets a delivery date because this is the
	 * only error which can be corrected by means of attributes available in WCEM
	 * 
	 */
	protected void fixErroneousHeader(final SalesDocument document)
	{
		final Header header = document.getHeader();
		header.setReqDeliveryDate(new Date(System.currentTimeMillis()));
		header.setText((Text) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_TEXT));

		for (final Item item : document.getItemList())
		{
			item.setText(item.createText());
		}

	}


	/**
	 * Deletes a single item in the backend.
	 */
	@Override
	public void deleteItemInBackend(final SalesDocument salesDocument, final TechKey itemToDelete) throws BackendException
	{

		final String METHOD_NAME = "deleteItemInBackend()";
		sapLogger.entering(METHOD_NAME);

		final TechKey[] itemsToDelete = new TechKey[1];
		itemsToDelete[0] = itemToDelete;
		deleteItemsInBackend(salesDocument, itemsToDelete, null);

		sapLogger.exiting();
	}

	/**
	 * Deletes multiple items in the backend.
	 */
	@Override
	public void deleteItemsInBackend(final SalesDocument salesDocument, final TechKey[] itemsToDelete,
			final TransactionConfiguration transConf) throws BackendException
	{

		final String METHOD_NAME = "deleteItemsInBackend()";
		sapLogger.entering(METHOD_NAME);

		if ((itemsToDelete == null) || (itemsToDelete.length <= 0))
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("No items to delete.");
			}

			sapLogger.exiting();
			return;
		}

		salesDocument.clearMessages();

		// remove item from buffer if it available
		for (final TechKey element : itemsToDelete)
		{
			if (itemBuffer.getItemsERPState() != null)
			{
				itemBuffer.removeItemERPState(element.getIdAsString());
			}
			// remove error information
			removeErroneousItems(itemsToDelete);
		}

		// collect sub items of deleted items and delete them
		final ItemList itemList = salesDocument.getItemList();
		final ItemHierarchy hierarchy = new ItemHierarchy(itemList);
		for (int i = 0; i < itemsToDelete.length; i++)
		{
			final Item itemToDelete = itemList.get(itemsToDelete[i]);
			if (!TechKey.isEmpty(itemToDelete.getParentId()))
			{
				final ItemList allSubItems = hierarchy.getAllLevelSubItems(itemToDelete);
				itemList.removeAll(allSubItems);
			}
		}

		// Try to delete the items
		final JCoConnection aJCoCon = getDefaultJCoConnection();
		final BaseStrategyERP.ReturnValue retVal = lrdActionsStrategy.executeLrdDoActionsDelete(transactionConfiguration,
				salesDocument, aJCoCon, LrdActionsStrategy.ITEMS, itemsToDelete);

		if (!retVal.getReturnCode().equals(ConstantsR3Lrd.BAPI_RETURN_ERROR))
		{



			for (final TechKey element : itemsToDelete)
			{
				salesDocument.getItemList().remove(element);
				itemVariantMap.remove(element.getIdAsString());

				if (itemConfigChangeableMap != null)
				{
					itemConfigChangeableMap.remove(element.getIdAsString());
				}
			}
		}
		else
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("IPC relevant info not deleted because item-deletion has returned an error");
			}

			sapLogger.exiting();
		}
	}

	@Override
	public void removeErroneousItems(final TechKey[] itemsToDelete)
	{
		for (int i = 0; i < itemsToDelete.length; i++)
		{
			erroneousItems.remove(itemsToDelete[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.wec.app.esales.module.transaction.backend.interf.SalesDocumentBackend
	 * #emptyInBackend(com.sap.wec.app.esales.module.transaction.backend.interf. SalesDocument)
	 */
	@Override
	public void emptyInBackend(final SalesDocument salesDocument) throws BackendException
	{

		// re-set change status
		loadState.setLoadOperation(LoadOperation.display);

		itemBuffer.clearERPBuffer();

	}


	/**
	 * Returns the map, to store item variant information
	 * 
	 * @return HashMap, the map to store item varaint information in
	 */
	@Override
	public HashMap<String, String> getItemVariantMap()
	{
		return itemVariantMap;
	}


	protected void fixErroneousItem(final Item item)
	{
		item.setProductId("");
		item.setProductGuid(TechKey.EMPTY_KEY);
	}

	/**
	 * Initializes this Business Object.
	 */
	@Override
	public void initBackendObject() throws BackendException
	{


		super.initBackendObject();

		headerCondTypeFreight = (String) moduleConfigurationAccess
				.getProperty(SapordermgmtbolConstants.CONFIGURATION_PROPERTY_HEADER_CONDITION_TYPE_FREIGHT);

		// in case of create, this will be set to tru by the create method
		createProcess = false;

	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf
	 * .erp.BackendState#isCreateProcess()
	 */
	@Override
	public boolean isCreateProcess()
	{
		return createProcess;
	}

	/**
	 * @return true, if error occured
	 */
	public boolean isErroneous()
	{
		return erroneous;
	}

	@Override
	public boolean isMultipleAddressesSupported()
	{
		return false;
	}


	/**
	 * Read the complete document from backend and reset the dirty flags.
	 */
	@Override
	public void readForUpdateFromBackend(final SalesDocument salesDocument) throws BackendException
	{

		if (checkForInitializationError(salesDocument))
		{
			return;
		}

		setGData(salesDocument, salesDocument.getTransactionConfiguration());

		readFromBackend(salesDocument, transactionConfiguration, false);
		// reset the dirty flags to avoid duplicate rfc calls.
		salesDocument.setDirty(false);
		salesDocument.getHeader().setDirty(false);
	}

	protected boolean checkForInitializationError(final SalesDocument salesDocument)
	{
		if (isInBackend(salesDocument))
		{
			salesDocument.setInitialized(true);
			return false;
		}
		if (!salesDocument.isInitialized())
		{
			salesDocument.getItemList().clear();
			salesDocument.addMessage(new Message(Message.ERROR, "sapsalestransactions.bo.no.create"));
			return true;
		}
		else
		{
			return false;
		}
	}

	protected boolean isInBackend(final SalesDocument salesDocument)
	{
		final String id = salesDocument.getTechKey().getIdAsString();
		return ((id != null) && (!id.isEmpty()) && (id.length() <= 10));
	}

	@Override
	public void readFromBackend(final SalesDocument salesDocument, final TransactionConfiguration transConf,
			final boolean directRead) throws BackendException
	{

		transactionConfiguration = transConf;
		final String METHOD_NAME = "readFromBackend()";
		sapLogger.entering(METHOD_NAME);

		if (checkForInitializationError(salesDocument))
		{
			sapLogger.exiting();
			return;
		}

		final JCoConnection aJCoCon = getDefaultJCoConnection();

		// Set read control parameter
		final GetAllReadParameters readParams = new GetAllReadParameters();
		readParams.headerCondTypeFreight = headerCondTypeFreight;
		readParams.setIpcPriceAttributes = setIpcPriceAttributes;
		readParams.isIncompletionLogRequested = isIncompletionLogRequested;

		// clear document data before being read from backend
		salesDocument.clearHeader();

		handleInvalidItems(salesDocument);
		readItemsFromLOAPI(salesDocument, aJCoCon, readParams);
		handleInvalidHeaderAfterRead(salesDocument);
		handleInvalidItemsAfterRead(salesDocument);


		adjustFreeGoods(salesDocument, transactionConfiguration);

		if (deliveryPriority != null)
		{
			salesDocument.getHeader().setDeliveryPriority(deliveryPriority);
		}

		sapLogger.exiting();
	}

	/**
	 * Actions which are needed for the header after a read step. Messages might need to be restored, because the header
	 * has been temporarily adjusted before. <br>
	 * 
	 */
	protected void handleInvalidHeaderAfterRead(final SalesDocument salesDocument)
	{
		if (getErroneousHeader() != null)
		{
			restoreErroneousHeaderAfterRead(salesDocument);
		}

	}

	/**
	 * Restore invalid header after read step. Restore required delivery date and messages. <br>
	 * 
	 */
	protected void restoreErroneousHeaderAfterRead(final SalesDocument salesDocument)
	{
		final Header header = salesDocument.getHeader();
		header.setReqDeliveryDate(getErroneousHeader().getReqDeliveryDate());
		salesDocument.copyMessages(getErroneousHeader());
	}

	/**
	 * Restores the sales document items after read and adds the information which needed to be removed before.<br>
	 * 
	 * @param salesDocument
	 *           The cart sales document.
	 */
	protected void handleInvalidItemsAfterRead(final SalesDocument salesDocument)
	{
		final Iterator<Entry<TechKey, Item>> iterator = getErroneousItems().entrySet().iterator();
		while (iterator.hasNext())
		{
			final Item itemStateBeforeCorrection = iterator.next().getValue();
			final Item erroneousItem = salesDocument.getItem(itemStateBeforeCorrection.getTechKey());
			restoreErroneousItem(itemStateBeforeCorrection, erroneousItem);
		}
	}

	/**
	 * If an item has been fixed so that LO-API can consume it, we later on need to restore the user inputs, so that the
	 * user sees the data he/she has entered which need to be corrected.<br>
	 * 
	 * @param savedState
	 *           The original, saved state of the item
	 * @param errItm
	 *           State of the erroneous item after LO-API can digest it
	 */
	protected void restoreErroneousItem(final Item savedState, final Item errItm)
	{
		errItm.setProductId(savedState.getProductId());
		errItm.setProductGuid(TechKey.EMPTY_KEY);
		errItm.setQuantity(savedState.getQuantity());
		errItm.setUnit(savedState.getUnit());
		errItm.setReqDeliveryDate(savedState.getReqDeliveryDate());
		errItm.copyMessages(savedState);
	}

	@Override
	public Map<TechKey, Item> getErroneousItems()
	{
		return this.erroneousItems;
	}

	private void readItemsFromLOAPI(final SalesDocument salesDocument, final JCoConnection aJCoCon,
			final GetAllReadParameters readParams) throws BackendException
	{
		salesDocument.clearItems();

		getAllStrategy.execute(this, salesDocument, itemBuffer, readParams, aJCoCon);

	}

	/**
	 * Checks for invalid items and initialises them, so that they can be deleted afterwards. This call is done before
	 * the LO-API is called for reading item information. <br>
	 * The items which cannot be digested by LO-API are fixed, the info needed to restore the document are kept in
	 * {@link SalesDocumentERP#getErroneousItems()}
	 * 
	 * @param salesDocument
	 *           Cart sales document
	 * @return Could we fix the document.
	 * @throws BackendException
	 */
	protected boolean handleInvalidItems(final SalesDocument salesDocument) throws BackendException
	{

		final String METHOD_NAME = "handleInvalidItems()";
		sapLogger.entering(METHOD_NAME);

		final ItemList items = salesDocument.getItemList();
		boolean itemErrorsResolvable = true;

		for (final Item item : items)
		{
			if ((item != null) && item.isErroneous())
			{

				this.addErroneousItem(item);
				fixErroneousItem(item);

				// get JCOConnection
				final JCoConnection aJCoCon = getDefaultJCoConnection();
				setStrategy.execute(this, salesDocument, itemBuffer.getItemsERPState(), transactionConfiguration, aJCoCon, null,
						false);

				// Item is still erroneous
				if (item.isErroneous())
				{
					itemErrorsResolvable = false;
					break;
				}

			}

		}
		return itemErrorsResolvable;

	}

	@Override
	public void clearErroneousItems()
	{
		this.erroneousItems.clear();
	}

	@Override
	public void addErroneousItem(final Item item)
	{
		final Item clonedItem = (Item) item.clone();
		this.erroneousItems.put(item.getTechKey(), clonedItem);
	}



	@Override
	public boolean recoverFromBackend(final SalesDocument salesDoc, final TechKey basketGuid) throws BackendException
	{

		throw new BackendException("Method recoverFromBackend not supported by this backend.");
	}



	@Override
	public void setErroneous(final boolean bool)
	{
		erroneous = bool;
	}

	/**
	 * Call the ERP_LORD_LOAD for edit.
	 */
	@Override
	public void setGData(final SalesDocument salesDocument, final TransactionConfiguration shop) throws BackendException
	{

		itemBuffer.clearERPBuffer();

		transactionConfiguration = shop;
		final String METHOD_NAME = "setGData()";
		sapLogger.entering(METHOD_NAME);
		// get JCOConnection
		final JCoConnection aJCoCon = getDefaultJCoConnection();

		loadState.setLoadOperation(LoadOperation.edit);
		final BackendCallResult retVal = lrdActionsStrategy.executeLrdLoad(salesDocument, this, aJCoCon, loadState);
		// Exit here, if there are errors
		if (retVal.isFailure())
		{
			salesDocument.setInvalid();
			loadState.setLoadOperation(LoadOperation.display);
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("Errors in R3LrdLoad -> returning");
			}
			return;
		}

		// Set Active Fields List
		lrdActionsStrategy.executeSetActiveFields(aJCoCon);


		sapLogger.exiting();
	}

	@Override
	public void setLoadStateCreate()
	{
		//
	}

	@Override
	public void updateInBackend(final SalesDocument salesDocument) throws BackendException
	{
		updateInBackend(salesDocument, transactionConfiguration);
	}

	/**
	 * Update object in the backend by putting the data into the underlying storage.
	 * 
	 * @param salesDocument
	 *           the document to update
	 * @param shop
	 *           the current shop
	 */

	@Override
	public void updateInBackend(final SalesDocument salesDocument, final TransactionConfiguration shop) throws BackendException
	{

		final String METHOD_NAME = "updateInBackend()";
		sapLogger.entering(METHOD_NAME);

		if (checkForInitializationError(salesDocument))
		{
			sapLogger.exiting();
			return;
		}

		// get JCOConnection
		final JCoConnection aJCoCon = getDefaultJCoConnection();


		handleInvalidHeaderBeforeUpdate(salesDocument);
		final BackendCallResult result = setStrategy.execute(this, salesDocument, itemBuffer.getItemsERPState(), shop, aJCoCon,
				null, false);

		if (result.isFailure())
		{
			handleInvalidHeaderAfterUpdate(salesDocument);
		}
		else
		{
			setErroneousHeader(null);
		}

		handleInvalidItemsAfterUpdate(salesDocument);
		if (!isErroneous())
		{
			checkShippingConditions(salesDocument);
		}

		writeConfiguration(salesDocument);

		deliveryPriority = salesDocument.getHeader().getDeliveryPriority();
		sapLogger.exiting();

	}

	/**
	 * Actions which are needed before an update call if error situations occur. Implementation re-initializes the
	 * required delivery date in case nothing was entered<br>
	 * 
	 * @param salesDocument
	 *           Sales document
	 */
	protected void handleInvalidHeaderBeforeUpdate(final SalesDocument salesDocument)
	{
		if (isRequiredDeliveryDateNeededForInitializing && salesDocument.getHeader().getReqDeliveryDate() == null)
		{
			salesDocument.getHeader().setReqDeliveryDate(ConstantsR3Lrd.DATE_INITIAL);
		}
	}

	/**
	 * Clear session storage of erroneous items for those items which are fine after the set call to SD<br>
	 * 
	 * @param salesDocument
	 */
	protected void handleInvalidItemsAfterUpdate(final SalesDocument salesDocument)
	{
		for (final Item item : salesDocument.getItemList())
		{
			if (!item.isErroneous())
			{
				final TechKey[] itemsToDelete = new TechKey[]
				{ item.getTechKey() };
				removeErroneousItems(itemsToDelete);
			}
		}
	}

	/**
	 * Check whether shipping conditions have been changed, and trigger a new pricing if necessary.<br>
	 * 
	 * @param salesDocument
	 * @throws BackendException
	 */
	protected void checkShippingConditions(final SalesDocument salesDocument) throws BackendException
	{
		final Header header = salesDocument.getHeader();
		final String currentShippingCondition = header.getShipCond();
		final String storedShippingCondition = getShippingCondition();

		if (storedShippingCondition != null && !storedShippingCondition.equals(currentShippingCondition))
		{
			lrdActionsStrategy
					.executeLrdDoActionsDocumentPricing(salesDocument, getDefaultJCoConnection(), transactionConfiguration);
			setShippingCondition(currentShippingCondition);
		}
		else if (storedShippingCondition == null)
		{
			setShippingCondition(currentShippingCondition);
		}
	}

	@Override
	public void setShippingCondition(final String currentShippingCondition)
	{
		shippingCondition = currentShippingCondition;

	}

	@Override
	public String getShippingCondition()
	{
		return shippingCondition;
	}

	@Override
	public void updateInBackend(final SalesDocument salesDocument, final TransactionConfiguration transactionConfiguration,
			final List<TechKey> itemsToDelete) throws BackendException
	{

		// delete items. We need to do this via a distinct RFC call
		final TechKey[] array = new TechKey[0];
		deleteItemsInBackend(salesDocument, itemsToDelete.toArray(array), transactionConfiguration);
		updateInBackend(salesDocument, transactionConfiguration);
	}

	@Override
	public void clearItemBuffer()
	{
		itemBuffer.clearERPBuffer();
	}

	@Override
	public Header getErroneousHeader()
	{
		return erroneousHeader;
	}

	@Override
	public void setErroneousHeader(final Header header)
	{
		erroneousHeader = header;
	}


	/**
	 * Execute additional pricing call if document is error free <br>
	 * 
	 * @param salesDocument
	 *           Sales document
	 * @param shop
	 *           Sales configuration
	 * @throws BackendException
	 *            Exception from backend call
	 */
	protected void executeAdditionalPricing(final SalesDocument salesDocument, final TransactionConfiguration shop)
			throws BackendException
	{
		if (!isErroneous())
		{
			lrdActionsStrategy.executeLrdDoActionsDocumentPricing(salesDocument, additionalPricing.getPriceType(),
					getDefaultJCoConnection(), shop);
		}
	}

	@Override
	public boolean isItemBasedAvailability()
	{
		return true;
	}

	@Override
	public ModuleConfigurationAccess getModuleConfigurationAccess()
	{
		return getModuleConfigurationAccess();
	}

	/**
	 * @return the additionalPricing
	 */
	public AdditionalPricing getAdditionalPricing()
	{
		return additionalPricing;
	}

	/**
	 * @param additionalPricing
	 *           the additionalPricing to set
	 */
	public void setAdditionalPricing(final AdditionalPricing additionalPricing)
	{
		this.additionalPricing = additionalPricing;
	}

	/**
	 * @return Stratey for dealing with configurable products
	 */
	public ProductConfigurationStrategy getProductConfigurationStrategy()
	{
		return productConfigurationStrategy;
	}

	/**
	 * Write product configuration data to the back end.
	 * 
	 * @param salesDocument
	 *           Document containing items which might have a configuration attached.
	 */
	protected void writeConfiguration(final SalesDocument salesDocument)
	{
		for (final Item item : salesDocument.getItemList())
		{
			if (!item.isErroneous())
			{
				final ConfigModel productConfiguration = item.getProductConfiguration();
				if (productConfiguration != null && salesDocument.isInitialized() && item.isProductConfigurationDirty())
				{
					item.setProductConfigurationDirty(false);
					productConfigurationStrategy.writeConfiguration(getDefaultJCoConnection(), productConfiguration, item.getHandle(),
							salesDocument);
					salesDocument.setConfigId(item.getTechKey(), productConfiguration.getId());
				}
			}
		}
	}


	@Override
	public void validate(final SalesDocument salesDocument) throws BackendException
	{
		if (!salesDocument.isInitialized())
		{
			return;
		}
		//    The standard implementation just re-reads the order from the back end. 
		//    It's possible to do additional validations or updates at this point 
		//    to retrieve other messages from the back end, which must be attached to the salesDocument

		//Example: 

		//		final ItemList storeItems = prepareForValidation(salesDocument);
		//
		//		updateInBackend(salesDocument);
		//		salesDocument.setItemList(storeItems);
		//		updateInBackend(salesDocument);
		//

		readFromBackend(salesDocument, transactionConfiguration, true);

	}

	/**
	 * Prepares the sales document for the validation call. In this implementation, to be sure to get all warnings
	 * related to order quantity: Changes all item quantities
	 * 
	 * @param salesDocument
	 * @return Item list in its initial state
	 */
	protected ItemList prepareForValidation(final SalesDocument salesDocument)
	{
		itemBuffer.clearERPBuffer();

		final ItemList storeItems = storeItemsBeforeValidate(salesDocument);
		prepareItemsForFirstValidateStep(salesDocument);
		return storeItems;
	}

	ItemList storeItemsBeforeValidate(final SalesDocument salesDocument)
	{
		final ItemList items = new ItemListImpl();
		for (final Item item : salesDocument.getItemList())
		{
			items.add((Item) item.clone());
		}
		return items;
	}

	void prepareItemsForFirstValidateStep(final SalesDocument salesDocument)
	{
		for (final Item item : salesDocument.getItemList())
		{
			final BigDecimal quantity = item.getQuantity();
			if (quantity.compareTo(BigDecimal.ONE) > 0)
			{
				item.setQuantity(quantity.subtract(BigDecimal.ONE));
			}
			else
			{
				item.setQuantity(quantity.add(BigDecimal.ONE));
			}
		}
	}

	/**
	 * Determines the list of item handles for items which are configurable
	 * 
	 * @param salesDocument
	 * @return List of handles
	 */
	protected List<String> determineConfigurableItems(final SalesDocument salesDocument)
	{
		final List<String> result = new ArrayList<>();
		for (final Item item : salesDocument.getItemList())
		{
			if (item.isConfigurable())
			{
				result.add(item.getHandle());
			}
		}
		return result;
	}


	@Override
	public boolean isBackendDown()
	{
		try
		{
			return getDefaultJCoConnection().isBackendOffline();
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Cannot determine backend availability", e);

		}
	}

	/**
	 * @return the closeStrategy
	 */
	protected LrdCloseStrategy getCloseStrategy()
	{
		return closeStrategy;
	}

	/**
	 * @param closeStrategy
	 *           the closeStrategy to set
	 */
	public void setCloseStrategy(final LrdCloseStrategy closeStrategy)
	{
		this.closeStrategy = closeStrategy;
	}

	@Override
	public void closeBackendSession()
	{
		try
		{
			getCloseStrategy().close(getDefaultJCoConnection());
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Could not close session", e);
		}

	}



}
