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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.businessobject.impl;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectHelper;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.SalesDocumentBaseImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesTransactionsFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.AlternativeProduct;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.SalesDocumentBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesDocumentType;
import de.hybris.platform.servicelayer.session.SessionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import sap.hybris.integration.models.constants.SapmodelConstants;

import com.sap.tc.logging.Severity;


/**
 * Common superclass for all sales documents.
 * 
 */
public abstract class SalesDocumentImpl extends SalesDocumentBaseImpl<ItemList, Item, Header> implements SalesDocument
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(SalesDocumentImpl.class.getName());


	protected TransactionConfiguration transactionConfiguration = null;

	protected SalesDocumentBackend backendService = null;
	protected boolean alreadyInitialized = false;
	protected boolean externalToOrder;

	private SessionService sessionService;



	protected boolean checkCatalogNecessary = true;

	/**
	 * redemption value
	 */
	protected String redemptionValue;



	protected boolean determinationRequired;

	protected boolean grossValueAvailable = true;

	protected boolean netValueAvailable = true;

	protected boolean pricesTraced = false;

	/**
	 * This list stores items with alternative products. For these items events should only be fired when a product is
	 * selected (see int msg 385863 2010).
	 */
	protected final List<TechKey> itemsWithAlternativeProductList = new ArrayList<TechKey>();

	private boolean updateMissing = false;

	private SalesTransactionsFactory salesFactory;

	private Converter converter;

	private ConfigurationProviderFactory configurationProviderFactory;


	private final Map<TechKey, String> configurationIDs = new HashMap<TechKey, String>();




	private boolean backendWasUp = false;


	private boolean backendWasDown = false;





	@Override
	public boolean isCheckCatalogNecessary()
	{
		return checkCatalogNecessary;
	}

	@Override
	public void setCheckCatalogNecessary(final boolean checkCatalogNecessary)
	{
		this.checkCatalogNecessary = checkCatalogNecessary;
	}

	/**
	 * Sets the document type on the document header depending on the Java class instance of the Sales Document
	 * (evaluated with <i><code>instanceof</code> </i>).
	 * 
	 * @throws CommunicationException
	 */
	protected void adaptHeaderDocumentType() throws CommunicationException
	{
		final Header header = getHeader();
		final SalesDocumentType type = getDocumentType();

		switch (type)
		{
			case BASKET:
				header.setDocumentType(DocumentType.BASKET);
				break;
			case ORDER:
				header.setDocumentType(DocumentType.ORDER);
				break;
			case QUOTATION:
				header.setDocumentType(DocumentType.QUOTATION);
				break;
			case RFQ:
				header.setDocumentType(DocumentType.RFQ);
				break;
			default:
				header.setDocumentType(null);

		}

	}

	/**
	 * Adds a shipTo to the shipTo list<br>
	 * 
	 * @param shipTo
	 *           shipTo to add
	 */
	public void addShipTo(final ShipTo shipTo)
	{
		shipToList.add(shipTo);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #clearShipTos()
	 */
	@Override
	public void clearShipTos()
	{
		shipToList.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #createBillTo()
	 */
	@Override
	public BillTo createBillTo()
	{
		final SalesTransactionsFactory salesTransactionsFactory = getSalesTransactionsFactory();
		final BillTo billTo = salesTransactionsFactory.createBillTo();

		return billTo;
	}

	/**
	 * Temporary helper method to create a HashMap from incoming Set of key/value-pairs. Method is necessary as long as
	 * getExtensionMap is not available in the BusinessObject-interface to transfer an extension map from one object to
	 * another one.
	 * 
	 * @param set
	 *           Set of keys/values
	 * @return new HashMap with all keys/values of the Set
	 */
	protected HashMap<String, Object> createExtensionMap(final Set<Entry<String, Object>> set)
	{
		if ((set == null) || (set.isEmpty()))
		{
			return new HashMap<String, Object>(0);
		}

		final HashMap<String, Object> newMap = new HashMap<String, Object>();
		final Iterator<Map.Entry<String, Object>> extIter = set.iterator();
		while (extIter.hasNext())
		{
			final Entry<String, Object> extEntry = extIter.next();

			final String key = extEntry.getKey();
			final Object value = extEntry.getValue();

			newMap.put(key, value);
		}
		return newMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.wec.app.common.module.transaction.businessobject.interf. SalesDocumentBase#createItem()
	 */
	@Override
	public Item createItem()
	{
		final SalesTransactionsFactory factory = getSalesTransactionsFactory();
		final Item item = factory.createSalesDocumentItem();

		item.setParentId(TechKey.EMPTY_KEY);

		return item;
	}

	private SalesTransactionsFactory getSalesTransactionsFactory()
	{
		if (null == salesFactory)
		{
			salesFactory = (SalesTransactionsFactory) genericFactory
					.getBean(SapordermgmtbolConstants.ALIAS_BEAN_TRANSACTIONS_FACTORY);
		}

		return salesFactory;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #createShipTo()
	 */
	@Override
	public ShipTo createShipTo()
	{
		final SalesTransactionsFactory salesTransactionsFactory = getSalesTransactionsFactory();
		final ShipTo shipTo = salesTransactionsFactory.createShipTo();

		return shipTo;
	}

	/**
	 * Deletes the item with the given Techkey
	 * 
	 * @param techKey
	 * @throws CommunicationException
	 */
	protected void deleteItemInt(final TechKey techKey) throws CommunicationException
	{
		// now delete the item
		try
		{
			setDirty(true);
			getHeader().setDirty(true);
			getBackendService().deleteItemInBackend(this, techKey);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #deleteItems(com.sap.wec.tc.core.common.TechKey[])
	 */
	@Override
	public void removeItems(final TechKey[] techKeys) throws CommunicationException
	{
		sapLogger.entering("deleteItems()");
		if ((techKeys == null) || (techKeys.length == 0))
		{
			sapLogger.debug("techKeys is null or contains no entries. Exit method");
			return;
		}

		// now delete the items
		try
		{

			setDirty(true);
			getHeader().setDirty(true);
			getBackendService().deleteItemsInBackend(this, techKeys, getTransactionConfiguration());
			releaseConfigurationSession(Arrays.asList(techKeys));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.wec.app.common.module.transaction.businessobject.impl. SalesDocumentBaseImpl#destroy()
	 */
	@Override
	public void destroy()
	{
		setUpdateMissing(false);
		setDirty(true);
		pricesTraced = false;
		if (getHeader() != null)
		{
			getHeader().setDirty(true);
		}
		try
		{
			// deletion of cookie logic removed

			// only destroy the backend object if the service exists
			// otherwise we have a useless call that creates a backend
			// object to destroy itself immediately afterwards...
			if (backendService != null)
			{
				getBackendService().destroyBackendObject();
			}
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Error while destroying SalesDocument", e);
		}
		super.destroy();
		shipToList = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #destroyContent()
	 */
	@Override
	public void destroyContent() throws CommunicationException
	{

		sapLogger.entering("destroyContent()");

		alreadyInitialized = false;
		pricesTraced = false;
		try
		{
			getBackendService().emptyInBackend(this);
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
	}

	private List<TechKey> determineItemsToDelete()
	{

		final List<TechKey> techkeysToDelete = new ArrayList<TechKey>();
		final ItemList items = getItemList();

		for (final Item item : items)
		{
			if (item.isProductEmpty() && !TechKey.isEmpty(item.getTechKey()))
			{
				techkeysToDelete.add(item.getTechKey());
			}
		}
		return techkeysToDelete;

	}

	/**
	 * Resets the entire document to empty state.<br>
	 * 
	 * @throws CommunicationException
	 *            in case of aback-end error
	 */
	public void emptyContent() throws CommunicationException
	{

		sapLogger.entering("emptyContent()");

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("emptyContent(): transactionConfiguration = " + transactionConfiguration);
		}

		try
		{

			alreadyInitialized = false;
			pricesTraced = false;
			getBackendService().emptyInBackend(this);

			// clone the PartnerList as this will be cleared otherwise (call by
			// reference)
			final PartnerList partnerList = getHeader().getPartnerList().clone();
			init(partnerList, getHeader().getProcessType());

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
	}


	/**
	 * Method retrieving the backend object for the object. This method is abstract because every concrete subclass of
	 * <code>SalesDocument</code> may use its own implementation of a backend object.
	 * 
	 * @return Backend object to be used
	 */
	protected abstract SalesDocumentBackend getBackendService() throws BackendException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #getSalesDocumentType()
	 */
	@Override
	public SalesDocumentType getDocumentType() throws CommunicationException
	{
		try
		{
			return getBackendService().getSalesDocumentType();
		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
			return SalesDocumentType.UNKNOWN;
		}
	}




	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #getTransactionConfiguration()
	 */
	@Override
	public TransactionConfiguration getTransactionConfiguration()
	{
		return transactionConfiguration;
	}

	protected boolean applyAlternativeProducts() throws CommunicationException
	{
		boolean updateNeeded = false;

		for (final Item item : getItemList())
		{
			for (final AlternativeProduct prod : item.getAlternativProductList())
			{
				updateNeeded = true;
				applyAlternativeProductToItem(item, prod);
				break;
			}
		}

		return updateNeeded;
	}

	protected void applyAlternativeProductToItem(final Item item, final AlternativeProduct prod) throws CommunicationException
	{
		final TechKey systemProductGUID = prod.getSystemProductGUID();
		final String systemProductID = prod.getSystemProductId();
		final String substitutionReasonId = prod.getSubstitutionReasonId();

		item.applyAlternativeProduct(systemProductGUID, systemProductID);
		item.setSubstitutionReasonId(substitutionReasonId);
	}

	/**
	 * Initializes a sales document
	 * 
	 * @param partnerList
	 *           Partner list
	 * @param processType
	 *           Sales document type
	 * @throws CommunicationException
	 */
	public void init(final PartnerList partnerList, final String processType) throws CommunicationException
	{

		sapLogger.entering("init()");
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("init(): transactionConfiguration = " + transactionConfiguration + ", processtype = " + processType);
		}
		//checkCatalogAvailable();

		clear();
		clearShipTos();

		if (partnerList != null)
		{
			getHeader().setPartnerList(partnerList);
		}
		if (processType != null)
		{
			getHeader().setProcessType(processType);
		}

		// set the correct salesdocument type
		adaptHeaderDocumentType();

		setPersistentInBackend(false);

		if (!alreadyInitialized)
		{
			try
			{
				// let the backend implementation decide if it is dirty. The
				// backend ca reset this flag.
				setDirty(true);
				getHeader().setDirty(true);

				alreadyInitialized = true;
				getBackendService().createInBackend(transactionConfiguration, this);
			}
			catch (final BackendException ex)
			{
				BusinessObjectHelper.splitException(ex);
			}

		}

		sapLogger.exiting();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument#init(de.hybris.platform
	 * .sap.sapordermgmtbol.order.businessobject.interf.PartnerList)
	 */
	@Override
	public void init(final PartnerList partnerList) throws CommunicationException
	{
		final String processType = (String) moduleConfigurationAccess
				.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE);

		init(partnerList, processType);

	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #init(com.sap.hybris.app.esales.module.transaction.businessobject.interf. SalesDocument,
	 * com.sap.wec.app.common.module.businesspartner.businessobject .interf.BusinessPartnerManager, java.lang.String,
	 * boolean)
	 */
	@Override
	public void init(final SalesDocument source, final String processType) throws BusinessObjectException
	{

		sapLogger.entering("init()");

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("init(): posd = " + source + ", transactionConfiguration = " + transactionConfiguration
					+ ", processtype = " + processType);
		}

		//checkCatalogAvailable();
		clear();

		// copy the header (this is a deep copy, so also including partner list,
		// etc)
		setHeader((Header) source.getHeader().clone());

		// Cloning also clones the document type, so the document type needs to
		// be adjusted afterwards.
		adaptHeaderDocumentType();

		if (processType != null)
		{
			getHeader().setProcessType(processType);
		}

		// we have to copy the extension information because it gets lost,
		// when the header is cloned and the new document is initialized.
		final HashMap<String, Object> extCopy = createExtensionMap(source.getHeader().getExtensionDataValues());

		try
		{
			alreadyInitialized = true;
			getBackendService().createInBackend(transactionConfiguration, this);
		}
		catch (final BackendException beEcx)
		{
			// throw BO Exception attaching messages of the BE Exception in
			// order to display them on the UI
			final BusinessObjectException boEcx = new BusinessObjectException();
			final MessageList msgList = beEcx.getMessageList();
			if (msgList != null)
			{
				for (int i = 0; i < msgList.size(); i++)
				{
					boEcx.addMessage(msgList.get(i));
				}
			}
			throw boEcx;
		}

		getHeader().setExtensionMap(extCopy);
		getItemList().clear();

		final int size = source.getItemList().size();

		// Copy the items. Because of the fact, that the CRM has problems
		// dealing with items containing too much data, I do not use the
		// clone() method but copy the relevant fields manually
		StringBuffer debugOutput = null;
		if (sapLogger.isDebugEnabled())
		{
			debugOutput = new StringBuffer("\nitem copy process");
		}
		for (int i = 0; i < size; i++)
		{
			final Item oldItem = source.getItemList().get(i);

			final boolean isMainItem = oldItem.getParentId().isInitial();
			if (isMainItem)
			{

				if (sapLogger.isDebugEnabled())
				{
					debugOutput.append("\n item will be copied");
				}

				final Item newItem = createItem();
				// if camp det should be executed anyway, even for a copied item
				// the execCampDet flag must be set to true
				newItem.setCopiedFromOtherItem(true);
				copyAttributesOfItem(oldItem, newItem);

				// sub-items if any
				Date reqDeliveryDate = null;
				reqDeliveryDate = oldItem.getReqDeliveryDate();
				// set reqDeliveryDate, LatestDlvDate required for main and
				newItem.setReqDeliveryDate(reqDeliveryDate);

				// copy extension informations
				if (oldItem.getExtensionDataValues() != null)
				{
					// we have to copy the extension information because it gets
					// lost, when the header is cloned and the new document is
					// initialized.
					final HashMap<String, Object> extCopy2 = createExtensionMap(oldItem.getExtensionDataValues());
					newItem.setExtensionMap(extCopy2);
				}


				newItem.setHandle(String.valueOf(i + 2));

				getItemList().add(newItem);
			}
			else
			{
				if (sapLogger.isDebugEnabled())
				{
					debugOutput.append("\n item won't be copied");
				}
			}
		}
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug(debugOutput.toString());
		}

		setPersistentInBackend(false);

		sapLogger.exiting();
	}

	@Override
	public boolean isExistingInBackend()
	{
		boolean isExisting = false;
		final Header header = getHeader();
		isExisting = (header.getSalesDocNumber() != null) && !header.getSalesDocNumber().isEmpty();
		return isExisting;
	}

	protected void copyAttributesOfItem(final Item source, final Item destination)
	{

		destination.setProductGuid(source.getProductGuid());
		destination.setProductId(source.getProductId());
		destination.setUnit(source.getUnit());
		destination.setQuantity(source.getQuantity());

		destination.setDeliveryPriority(source.getDeliveryPriority());
		Text text = source.getText();
		if (null != text)
		{
			text = text.clone();
			text.setHandle("");
		}
		destination.setText(text);

		// Need to be passed for Grid items
		destination.setConfigType(source.getConfigType());
		destination.setParentId(source.getParentId());

		ShipTo shipTo = source.getShipTo();
		if (shipTo != null)
		{
			shipTo = shipTo.clone();
		}
		destination.setShipTo(shipTo);

		PartnerList partnerList = source.getPartnerListData();
		if (null != partnerList)
		{
			partnerList = partnerList.clone();
		}
		destination.setPartnerListData(partnerList);

		destination.setConfigurable(source.isConfigurable());

		destination.setItmTypeUsage(source.getItmTypeUsage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.wec.app.common.module.transaction.businessobject.interf. SalesDocumentBase#isDeterminationRequired()
	 */
	@Override
	public boolean isDeterminationRequired()
	{
		return determinationRequired;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #isExternalToOrder()
	 */
	@Override
	public boolean isExternalToOrder()
	{
		return externalToOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #isGrossValueAvailable()
	 */
	@Override
	public boolean isGrossValueAvailable()
	{
		return grossValueAvailable;
	}

	@Override
	public boolean isMultipleAddressesSupported() throws BusinessObjectException
	{
		try
		{
			return getBackendService().isMultipleAddressesSupported();
		}
		catch (final BackendException e)
		{
			BusinessObjectHelper.splitException(e);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #isNetValueAvailable()
	 */
	@Override
	public boolean isNetValueAvailable()
	{
		return netValueAvailable;
	}

	@Override
	public boolean isUpdateMissing()
	{
		return updateMissing;
	}

	/**
	 * Merges identical products (this is told from the product ID) and states whether changes have been done
	 * 
	 * @return did we do changes?
	 * @throws CommunicationException
	 */
	protected boolean mergeIdenticalProducts() throws CommunicationException
	{

		/*
		 * Since the update of ERP backend does not read the updated data from the backend by default, we have to do it
		 * here explicitly because we need the data for the merge
		 */
		read();

		// items that will contain the sum of merging products
		final List<Item> mergeItems = new ArrayList<Item>();
		final List<TechKey> discardedItemKeys = new LinkedList<TechKey>();

		final Map<String, List<Item>> itemMap = getDublicatesForItems();

		/*
		 * Second step: go through the map, find all list with more than one entries and merge them and store in duplicate
		 * list to remove them in step three from the document
		 */
		final Iterator<List<Item>> iterPossDuplicates = itemMap.values().iterator();
		while (iterPossDuplicates.hasNext())
		{

			final List<Item> items = iterPossDuplicates.next();
			if (items.size() > 1)
			{

				Item merged = null; // item which we keep
				double sumQty = 0;
				int discarded = 0;

				final Iterator<Item> iterItems = items.iterator();
				while (iterItems.hasNext())
				{

					final Item item = iterItems.next();
					if (merged == null)
					{
						merged = item;
						sumQty = merged.getQuantity().doubleValue();
					}
					else
					{
						// merge only allowed combination
						// merge only if items do not exceed quantity limit
						final double itemQty = item.getQuantity().doubleValue();
						if (merged.isMergeSupported(item))
						{
							sumQty += itemQty;
							discardedItemKeys.add(item.getTechKey());
							++discarded;
						}
					}

				}

				if (discarded > 0)
				{
					merged.setQuantity(new BigDecimal(sumQty));
					mergeItems.add(merged);
				}
			}
		}

		/*
		 * Third step: remove duplicates from SalesDocument and update the SalesDocument in BE
		 */
		final TechKey[] techKeys = discardedItemKeys.toArray(new TechKey[discardedItemKeys.size()]);
		removeItems(techKeys);

		//		// publish merged items after BE calculations
		//		final Iterator<Item> it = mergeItems.iterator();
		//		while (it.hasNext())
		//		{
		//			final Item merge = it.next();
		//		}
		//
		return !mergeItems.isEmpty();
	}

	protected Map<String, List<Item>> getDublicatesForItems()
	{
		final Map<String, List<Item>> itemMap = new HashMap<String, List<Item>>();
		final Iterator<Item> iterItem = getItemList().iterator();
		// First step: to run through the itemlist and store all items with same
		// productId and unit in lists
		while (iterItem.hasNext())
		{
			final Item toCheck = iterItem.next();
			// Child items do not form duplicates
			if (!TechKey.isEmpty(toCheck.getParentId()))
			{
				continue;
			}
			if (toCheck.isConfigurable())
			{
				continue;
			}

			final String itemKey = createItemKey(toCheck);
			// if the item has no techKey yet, we will not merge anything
			if (null == itemKey)
			{
				continue;
			}

			// toCheck is eligible for duplicates

			List<Item> possibleDuplicates = itemMap.get(itemKey); // find
			if (possibleDuplicates == null)
			{
				possibleDuplicates = new ArrayList<Item>();
				itemMap.put(itemKey, possibleDuplicates);
			}
			possibleDuplicates.add(toCheck);

		}
		return itemMap;
	}

	protected String createItemKey(final Item toCheck)
	{
		final TechKey productGUID = toCheck.getProductGuid();

		if ((null == productGUID) || productGUID.isInitial())
		{
			return null;
		}

		final String itemKey = productGUID.toString() + toCheck.getUnit() + toCheck.getPartnerListData().getAllToString();

		return itemKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #read()
	 */
	@Override
	public void read() throws CommunicationException
	{
		this.read(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #read(boolean)
	 */
	@Override
	public void read(final boolean force) throws CommunicationException
	{
		sapLogger.entering("read(boolean force)");
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("Read is called with force=" + force);
		}

		try
		{
			// new version
			if (isDirty() || getHeader().isDirty() || force)
			{
				getBackendService().readFromBackend(this, transactionConfiguration, force);
				setDirty(false);
				getHeader().setDirty(false);
			}

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



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #readForUpdate()
	 */
	@Override
	public void readForUpdate() throws CommunicationException
	{
		sapLogger.entering("readForUpdate()");
		// for backward compatibility
		this.readForUpdate(false);
		sapLogger.exiting();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #readForUpdate(boolean)
	 */
	@Override
	public void readForUpdate(final boolean force) throws CommunicationException
	{
		sapLogger.entering("readForUpdate(boolean force)");
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("ReadForUpdate is called with force=" + force);
		}

		try
		{

			// read and lock from Backend
			if (isDirty() || getHeader().isDirty() || force)
			{
				getBackendService().readForUpdateFromBackend(this);
				getHeader().setDirty(false);

				//checkAllCatalogDataAvailable();
			}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #
	 * removeItem(com.sap.wec.app.common.module.transaction.item.businessobject. interf.Item)
	 */
	@Override
	public void removeItem(final Item item) throws CommunicationException
	{
		sapLogger.entering("removeItem(Item item)");

		// now delete the item
		try
		{
			setDirty(true);
			getHeader().setDirty(true);
			getBackendService().deleteItemInBackend(this, item.getTechKey());
			releaseConfigurationSession(item.getTechKey());
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


	/** Abstract Dummy implementation of interface method */
	@Override
	abstract public boolean saveAndCommit() throws CommunicationException;

	@Override
	public void setDeterminationRequired(final boolean isDeterminationRequired)
	{
		this.determinationRequired = isDeterminationRequired;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #setExternalToOrder(boolean)
	 */
	@Override
	public void setExternalToOrder(final boolean isExternalToOrder)
	{
		this.externalToOrder = isExternalToOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #setGrossValueAvailable(boolean)
	 */
	@Override
	public void setGrossValueAvailable(final boolean isGrossValueAvailable)
	{
		this.grossValueAvailable = isGrossValueAvailable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #setNetValueAvailable(boolean)
	 */
	@Override
	public void setNetValueAvailable(final boolean isNetValueAvailable)
	{
		this.netValueAvailable = isNetValueAvailable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #setShipToList(java.util.List)
	 */
	@Override
	public void setShipToList(final List<ShipTo> shipToList)
	{
		this.shipToList = shipToList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #setBillToList(java.util.List)
	 */
	@Override
	public void setBillToList(final List<BillTo> billToList)
	{
		this.billToList = billToList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument
	 * #setTransactionConfiguration(com.sap.wec.app.esales.module.transaction.
	 * businessobject.interf.TransactionConfiguration)
	 */
	@Override
	public void setTransactionConfiguration(final TransactionConfiguration transConf)
	{
		transactionConfiguration = transConf;
	}

	@Override
	public void setUpdateMissing(final boolean updateMissing)
	{
		this.updateMissing = updateMissing;
	}

	/**
	 * Returns a string representation of the object
	 * 
	 * @return String representation
	 */
	@Override
	public String toString()
	{
		return super.toString() + " SalesDocumentImpl[shipToList=" + shipToList + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.businessobject.interf.SalesDocument #
	 * update(com.sap.wec.app.common.module.businesspartner.businessobject.interf .BusinessPartnerManager)
	 */
	@Override
	public void update() throws CommunicationException
	{
		sapLogger.entering("update()");

		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("update(): transactionConfiguration = " + transactionConfiguration);
		}



		final List<TechKey> itemsToDelete = determineItemsToDelete();

		try
		{
			/**
			 * Do some checks before the update is happening Update in backend can be skipped Specific BOs might redefine
			 * this method
			 */
			// this is only for compatibility reasons - the BE should
			// determine whether the bo is dirty or not
			setDirty(true);
			getHeader().setDirty(true);
			prepareItemsWithChangedProducts();

			getBackendService().updateInBackend(this, transactionConfiguration, itemsToDelete);
			releaseConfigurationSession(itemsToDelete);

			updateMissing = false;

			final boolean alternativeProductsApplied = applyAlternativeProducts();
			final Map<TechKey, Message> messages = checkQuantityUOM();
			if (alternativeProductsApplied || !messages.isEmpty())
			{
				getBackendService().updateInBackend(this, transactionConfiguration);
			}
			for (final Entry<TechKey, Message> entry : messages.entrySet())
			{
				final Item itm = getItemList().get(entry.getKey());
				final Message message = entry.getValue();
				if (itm != null)
				{
					itm.addMessage(message);
				}
				else
				{
					getHeader().addMessage(message);
				}
			}

		}
		catch (final BackendException ex)
		{
			BusinessObjectHelper.splitException(ex);
		}
		catch (final BusinessObjectException e)
		{
			throw new CommunicationException("Update failed", e);

		}

		final boolean changesDone = mergeIdenticalProductsIfRequired();
		if (changesDone)
		{
			try
			{
				getBackendService().updateInBackend(this, transactionConfiguration);
			}
			catch (final BackendException e)
			{
				BusinessObjectHelper.splitException(e);
			}
			read();
		}
		pricesTraced = false;





		// backend case
		sapLogger.exiting();
	}

	protected Map<TechKey, Message> checkQuantityUOM() throws BusinessObjectException
	{
		Map<TechKey, Message> messages = null;
		for (final Item itm : getItemList())
		{
			final String unit = itm.getUnit();
			final BigDecimal quantity = itm.getQuantity();
			if (unit != null && !unit.isEmpty() && quantity != null)
			{
				final int scale = converter.getUnitScale(unit);
				BigDecimal roundedQuantity = quantity.setScale(scale, BigDecimal.ROUND_HALF_UP);
				final boolean roundingNeeded = (0 != roundedQuantity.compareTo(quantity));
				if (roundingNeeded)
				{
					final BigDecimal minimumScaleValue = converter.getMinimumScaleValue(unit);
					if (roundedQuantity.compareTo(minimumScaleValue) < 0)
					{
						roundedQuantity = minimumScaleValue;
					}
					itm.setQuantity(roundedQuantity);
					final String[] args = new String[1];
					args[0] = converter.convertUnitKey2UnitID(unit);
					// Quantity was rounded because unit of measure PC allows
					// fewer decimal places than unit of measure EA
					final String key = "sapsalestransactions.bo.message.item.quantityUOM";
					final Message message = new Message(Message.WARNING, key, args, "UNIT_CONVERSION");
					if (messages == null)
					{
						messages = new HashMap<TechKey, Message>();
					}
					messages.put(itm.getTechKey(), message);
				}
			}
		}
		if (messages == null)
		{
			messages = Collections.emptyMap();
		}
		return messages;
	}

	protected boolean mergeIdenticalProductsIfRequired() throws CommunicationException
	{
		// by default merge is de-activated for any document. In case of Basket
		// this method is overridden
		return false;
	}

	protected void prepareItemsWithChangedProducts()
	{
		final ItemList itemList = getItemList();
		final Iterator<Item> iterItem = itemList.iterator();

		while (iterItem.hasNext())
		{
			final Item item = iterItem.next();
			prepareItemWithChangedProduct(item);
		}
	}

	protected void prepareItemWithChangedProduct(final Item item)
	{
		if (item.isProductChanged())
		{
			resetItemWithChangedProduct(item);
		}
	}

	private void resetItemWithChangedProduct(final Item item)
	{
		item.setProductGuid(TechKey.EMPTY_KEY);
		item.setUnit(null);
		item.setDescription(null);
		item.setProductChanged(false);
	}

	@Override
	public TechKey getSoldToGuid()
	{
		final PartnerListEntry soldToData = getHeader().getPartnerList().getSoldToData();
		if (null == soldToData)
		{
			return null;
		}
		else
		{
			return soldToData.getPartnerTechKey();
		}
	}

	@Override
	public void setSoldToGuid(final TechKey techKeySoldTo)
	{
		setSoldToGuid(techKeySoldTo, "");
	}

	@Override
	public void setSoldToGuid(final TechKey techKeySoldTo, final String soldToId)
	{
		final PartnerListEntry entry = getHeader().getPartnerList().createPartnerListEntry(techKeySoldTo, soldToId);
		getHeader().getPartnerList().setSoldToData(entry);

	}

	@Override
	public void clearItemBuffer()
	{
		try
		{
			getBackendService().clearItemBuffer();
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Could not establish BackendService", e);

		}
	}


	@Override
	public boolean hasPredecessorOfSpecificType(final DocumentType docType)
	{
		final List<ConnectedDocument> predecessors = getHeader().getPredecessorList();

		for (final ConnectedDocument predecessor : predecessors)
		{
			if (predecessor.getDocType().equals(docType))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public void setInitialized(final boolean b)
	{
		alreadyInitialized = b;

	}

	@Override
	public boolean isInitialized()
	{
		return alreadyInitialized;
	}


	@Override
	public boolean isItemBasedAvailability()
	{
		try
		{
			return getBackendService().isItemBasedAvailability();
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Not handled '" + e.getClass().getName() + "' exception.", e);

		}
	}

	@Override
	public void setConverter(final Converter converter)
	{
		this.converter = converter;
	}


	@Override
	public void validate() throws CommunicationException
	{
		try
		{
			getBackendService().validate(this);
			setDirty(false);
		}
		catch (final BackendException e)
		{
			BusinessObjectHelper.splitException(e);
		}


	}



	/**
	 * Releases a configuration session attached to an item
	 * 
	 * @param key
	 *           Item techKey
	 */
	protected void releaseConfigurationSession(final TechKey key)
	{
		final String configId = getConfigId(key);
		if (configId != null)
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("Release configuration: " + configId);
			}
			configurationProviderFactory.getProvider().releaseSession(configId);
		}
		else
		{
			sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS, "Item handle: " + key
					+ " not found in session, no configuration was released");
		}

	}

	/**
	 * @return Factory which allows to access the configuration provider
	 */
	public ConfigurationProviderFactory getConfigurationProviderFactory()
	{
		return this.configurationProviderFactory;
	}

	/**
	 * @param configurationProviderFactory
	 *           Factory which allows to access the configuration provider
	 */
	public void setConfigurationProviderFactory(final ConfigurationProviderFactory configurationProviderFactory)
	{
		this.configurationProviderFactory = configurationProviderFactory;
	}

	/**
	 * Returns the configuration ID from the session for a given item handle
	 * 
	 * @param key
	 *           Item key
	 * @return Configuration ID
	 */
	protected String getConfigId(final TechKey key)
	{

		return configurationIDs.get(key);
	}


	@Override
	public void setConfigId(final TechKey key, final String configId)
	{
		final Map<TechKey, String> configIds = this.configurationIDs;
		configIds.put(key, configId);

	}

	/**
	 * Releases configuration sessions for all items provided
	 * 
	 * @param itemsToDelete
	 *           List of item TechKeys
	 */
	protected void releaseConfigurationSession(final List<TechKey> itemsToDelete)
	{
		for (final TechKey key : itemsToDelete)
		{
			releaseConfigurationSession(key);
		}

	}

	@Override
	public void releaseConfigurationSessions()
	{
		for (final Item item : getItemList())
		{
			releaseConfigurationSession(item.getTechKey());
		}

	}

	@Override
	public boolean isBackendDown()
	{
		if (isBackendWasDown())
		{
			return checkBackendNeverWasUp();
		}

		try
		{
			final boolean currentlyDown = getBackendService().isBackendDown();
			if (!currentlyDown)
			{
				setBackendWasUp(true);
				return false;
			}
			else
			{
				setBackendWasDown(true);
				return checkBackendNeverWasUp();
			}
		}
		catch (final BackendException e)
		{
			throw new ApplicationBaseRuntimeException("Cannot determine backend availability", e);
		}
	}

	/**
	 * Checks whether backend was up previously and thus a runtime exception needs to be raised
	 * 
	 * @return true if backend was never up before
	 */
	boolean checkBackendNeverWasUp()
	{
		if (isBackendWasUp())
		{
			sessionService.closeCurrentSession();
			throw new ApplicationBaseRuntimeException("Back end went down, session needs to be terminated");
		}
		else
		{
			return true;
		}
	}



	void setBackendWasUp(final boolean b)
	{
		this.backendWasUp = b;
	}


	boolean isBackendWasUp()
	{
		return this.backendWasUp;
	}

	void setBackendWasDown(final boolean b)
	{
		this.backendWasDown = b;
	}


	boolean isBackendWasDown()
	{
		return this.backendWasDown;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}


}
