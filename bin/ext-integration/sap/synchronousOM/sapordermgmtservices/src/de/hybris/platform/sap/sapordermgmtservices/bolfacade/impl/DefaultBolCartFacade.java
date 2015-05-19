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
package de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapcommonbol.constants.SapcommonbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.interf.BasketBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.interaction.interf.CreateOrder;
import de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.interf.InitBasket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderBackend;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Facade for accessing the cart entity via the BOL
 */
public class DefaultBolCartFacade implements BolCartFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultBolCartFacade.class);

	private GenericFactory genericFactory;

	private SapPartnerService sapPartnerService;


	/**
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * @param genericFactory
	 *           the genericFactory to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.BolCartAccess#getCart()
	 */
	@Override
	public Basket getCart()
	{
		return (Basket) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.BolCartAccess#hasCart()
	 */
	@Override
	public Boolean hasCart()
	{
		final Basket cart = getCart();
		return Boolean.valueOf(cart.isInitialized());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.BolCartAccess#getAllowedDeliveryTypes()
	 */
	@Override
	public Map<String, String> getAllowedDeliveryTypes()
	{
		final TransactionConfiguration transactionConfiguration = getConfiguration();
		try
		{
			return transactionConfiguration.getAllowedDeliveryTypes(false);
		}
		catch (final CommunicationException e)
		{
			throw new ApplicationBaseRuntimeException("Could not fetch delivery types", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtservices.BolCartAccess#createAddress()
	 */
	@Override
	public Address createAddress()
	{
		return (Address) genericFactory.getBean(SapcommonbolConstants.ALIAS_BO_ADDRESS);
	}

	@Override
	public Basket createCart()
	{


		final BasketImpl cart = (BasketImpl) getCart();

		final InitBasket initBasketAction = getInteractionObjectInitBasket();

		final String soldToId = sapPartnerService.getCurrentSapCustomerId();
		final String contactId = sapPartnerService.getCurrentSapContactId();

		try
		{
			initBasketAction.init(cart, soldToId, contactId);
		}
		catch (final BusinessObjectException e)
		{
			throw new ApplicationBaseRuntimeException("create cart not possible", e);
		}


		return cart;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.sap.sapordermgmtservices.BolCartAccess#placeOrderFromCart(de.hybris.platform.sap.sapordermgmtbol
	 * .transaction.businessobject.interf.Basket)
	 */
	@Override
	public Order placeOrderFromCart(final Basket cart)
	{
		checkCartExists();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("placeOrderFromCart for cart: " + cart.getHandle());
		}
		final Order order = getOrder();

		final CreateOrder createOrderInteraction = getInteractionObjectCreateOrder();
		final TransactionConfiguration config = null;

		try
		{
			createOrderInteraction.createOrderFromBasket(cart, order, false, config);
			order.update();
			order.saveAndCommit();
			order.read();
			return order;
		}
		catch (final CommunicationException e)
		{
			throw new ApplicationBaseRuntimeException("placeOrder failed", e);
		}
		catch (final BusinessObjectException e)
		{
			throw new ApplicationBaseRuntimeException("placeOrder failed", e);
		}
	}


	TransactionConfiguration getConfiguration()
	{
		return (TransactionConfiguration) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_TRANSACTION_CONFIGURATION);
	}

	@Override
	public Item getCartItem(final int itemNumber)
	{
		final Basket cart = getCart();
		for (final Item currentItem : cart.getItemList())
		{
			if (currentItem.getNumberInt() == itemNumber)
			{
				return currentItem;
			}
		}
		return null;
	}

	/**
	 * Fetches cart item per product id
	 * 
	 * @param code
	 *           Product ID
	 * @return BOL representation of cart item
	 */
	protected Item getCartItem(final String code)
	{
		final Basket cart = getCart();
		for (final Item item : cart.getItemList())
		{
			if (item.getProductId().equals(code))
			{
				return item;
			}
		}
		return null;
	}

	Converter getConverter()
	{
		return genericFactory.getBean(SapcommonbolConstants.ALIAS_BO_CONVERTER);
	}

	/**
	 * @return The BOL representation of the order
	 */
	public Order getOrder()
	{
		return (Order) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_ORDER);
	}

	/**
	 * @return The cart backend object which establishes the connection to the ERP system
	 */
	public BasketBackend getCartBE()
	{
		return (BasketBackend) genericFactory.getBean(SapordermgmtbolConstants.BEAN_ID_BE_CART_ERP);
	}

	OrderBackend getOrderBE()
	{
		return (OrderBackend) genericFactory.getBean(SapordermgmtbolConstants.BEAN_ID_BE_ORDER_ERP);
	}

	CreateOrder getInteractionObjectCreateOrder()
	{
		return (CreateOrder) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_INT_CREATE_ORDER);
	}

	InitBasket getInteractionObjectInitBasket()
	{
		return (InitBasket) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_INT_INITBASKET);
	}



	/**
	 * Filters the list of messages attached to the cart and just returns the error ones.
	 * 
	 * @return Error messages related to the cart
	 */
	protected MessageList getCartErrors()
	{
		final MessageList cartErrors = new MessageList();
		for (final Message message : getCart().getMessageList())
		{
			if (message.isError())
			{
				cartErrors.add(message);
			}
		}
		return cartErrors;
	}


	@Override
	public void updateCart()
	{
		checkCartExists();
		try
		{
			final Basket currentCart = getCart();
			currentCart.update();
			currentCart.read();
		}
		catch (final CommunicationException e)
		{
			throw new ApplicationBaseRuntimeException("updateSessionCart failed", e);
		}
	}

	@Override
	public Item addToCart(final String code, final long quantity)
	{
		Item newItem;
		final String handle = createNewItem(code, quantity, null, null);

		updateCart();

		final Basket currentCart = getCart();
		newItem = currentCart.getItemList().get(new TechKey(handle));
		if (newItem == null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("item for handle not found, has been merged");
			}
			//find item via product code, as item has been merged
			newItem = getCartItem(code);
			if (newItem == null)
			{
				throw new ApplicationBaseRuntimeException("Item not found for handle/code: " + handle + "/ " + code);
			}
		}
		return newItem;
	}

	/**
	 * Creates a new BOL cart item from the attributes provided. Creates the cart if this has not happened yet.
	 * 
	 * @param code
	 *           Product code (SAP material number)
	 * @param quantity
	 *           Quantity
	 * @param handle
	 *           Handle to be set into new item. Can be provided as null, in this case a new handle will be created
	 * @param configModel
	 *           Configuration runtime representation
	 * @return handle of newly created item
	 */
	protected String createNewItem(final String code, final long quantity, final String handle, final ConfigModel configModel)
	{
		if (!hasCart().booleanValue())
		{
			createCart();
		}

		final String resultingHandle = createNewItem(code, quantity, handle, getCart(), configModel);
		return resultingHandle;
	}

	/**
	 * Creates a new BOL cart item from the attributes provided.
	 * 
	 * @param code
	 *           Product code (SAP material number)
	 * @param quantity
	 *           Quantity
	 * @param handle
	 *           Handle to be set into new item. Can be provided as null, in this case a new handle will be created
	 * @param currentCart
	 *           The BOL cart object the items will be attached to
	 * @param configModel
	 *           Configuration runtime representation
	 * @return handle of newly created item
	 */
	protected String createNewItem(final String code, final long quantity, final String handle, final Basket currentCart,
			final ConfigModel configModel)
	{
		final Item newItem = currentCart.createItem();
		newItem.setProductId(formatProductIdForBOL(code));
		newItem.setQuantity(new BigDecimal(quantity));
		if (handle != null)
		{
			newItem.setHandle(handle);
		}
		newItem.setProductConfiguration(configModel);

		final String resultingHandle = newItem.getHandle();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("addToCart for: " + code + ", handle: " + resultingHandle);
		}

		currentCart.addItem(newItem);
		return resultingHandle;
	}


	@Override
	public void releaseCart()
	{
		final Basket sessionCart = getCart();
		if (sessionCart.isInitialized())
		{
			//initializing a new cart will release the existing carts' contents
			try
			{
				sessionCart.releaseConfigurationSessions();
				sessionCart.release();
			}
			catch (final CommunicationException e)
			{
				throw new ApplicationBaseRuntimeException("Could not release cart", e);
			}

		}

	}

	void checkCartExists()
	{
		if (!hasCart().booleanValue())
		{
			throw new ApplicationBaseRuntimeException("Cart is not initialzed yet");
		}
	}

	/**
	 * @param sapPartnerService
	 *           the sapPartnerService to set
	 */
	public void setSapPartnerService(final SapPartnerService sapPartnerService)
	{
		this.sapPartnerService = sapPartnerService;
	}


	@Override
	public MessageList validateCart()
	{
		try
		{
			getCart().validate();
			return getCartErrors();
		}
		catch (final CommunicationException e)
		{
			throw new ApplicationBaseRuntimeException("Could not validate cart", e);
		}

	}


	@Override
	public String addConfigurationToCart(final ConfigModel configModel)
	{
		final String productId = getProductIdFromConfigModel(configModel);
		final String handle = createNewItem(productId, 1, null, configModel);

		updateCart();

		return handle;
	}

	/**
	 * Retrieves the SAP product ID from a configuration runtime representation. The product ID is taken from the root
	 * instance.
	 * 
	 * @param configModel
	 * @return SAP product ID (code of hybris product model)
	 */
	protected String getProductIdFromConfigModel(final ConfigModel configModel)
	{
		final InstanceModel rootInstance = configModel.getRootInstance();
		if (rootInstance == null)
		{
			throw new ApplicationBaseRuntimeException("No root instance");
		}

		return rootInstance.getName();
	}


	@Override
	public void updateConfigurationInCart(final String key, final ConfigModel configModel)
	{
		final Item item = getCart().getItem(new TechKey(key));
		if (item == null)
		{
			throw new ApplicationBaseRuntimeException("Item not found for key: " + key);
		}
		item.setProductConfiguration(configModel);
		item.setProductConfigurationDirty(true);
		updateCart();

	}



	String addLeadingZeros(final String input, final String fillString)
	{

		if (input == null)
		{
			return "";
		}

		if (input.length() > fillString.length())
		{
			return input;
		}

		try
		{
			new BigInteger(input);
		}
		catch (final NumberFormatException ex)
		{
			// $JL-EXC$
			// This exception might occur if the method is
			// called for non-numerical inputs
			return input.trim();
		}
		catch (final NullPointerException ex)
		{
			// $JL-EXC$
			// This exception might occur if the method is
			// called for non-numerical inputs
			return "";
		}

		return fillString.substring(input.length()) + input;
	}

	/**
	 * Formats product ID taken from hybris catalog and prepares it for sending to LO-API. Adds leading zeros.
	 * 
	 * @param input
	 * @return Formatted product ID
	 */
	protected String formatProductIdForBOL(final String input)
	{
		return addLeadingZeros(input, "000000000000000000");
	}


	@Override
	public void addItemsToCart(final List<Item> items)
	{
		for (final Item item : items)
		{
			createNewItem(item.getProductId(), item.getQuantity().longValue(), item.getHandle(), item.getProductConfiguration());

		}
		updateCart();


	}


	@Override
	public boolean isBackendDown()
	{
		return getCart().isBackendDown();
	}




}
