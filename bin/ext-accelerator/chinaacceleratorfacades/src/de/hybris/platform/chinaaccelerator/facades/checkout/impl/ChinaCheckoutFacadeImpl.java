// CHINAACC_NEWFILE

/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.checkout.impl;


import de.hybris.platform.chinaaccelerator.facades.checkout.ChinaCheckoutFacade;
import de.hybris.platform.chinaaccelerator.services.enums.DeliveryTimeslot;
import de.hybris.platform.chinaaccelerator.services.model.invoice.InvoiceModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.InvoiceData;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;


public class ChinaCheckoutFacadeImpl implements ChinaCheckoutFacade
{

	/**
	 * @return the invoiceConverter
	 */
	public Converter<InvoiceModel, InvoiceData> getInvoiceConverter()
	{
		return invoiceConverter;
	}


	/**
	 * @param invoiceConverter
	 *           the invoiceConverter to set
	 */
	public void setInvoiceConverter(final Converter<InvoiceModel, InvoiceData> invoiceConverter)
	{
		this.invoiceConverter = invoiceConverter;
	}

	private Converter<InvoiceModel, InvoiceData> invoiceConverter;

	@Resource(name = "cartConverter")
	private Converter<CartModel, CartData> cartConverter;

	@Autowired
	private CartFacade cartFacade;

	@Autowired
	private CartService cartService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private UserService userService;

	@Autowired
	CommerceCartService commerceCartService;

	@Autowired
	BaseSiteService baseSiteService;

	public InvoiceData getInvoiceData(final InvoiceModel invoiceModel)
	{
		return invoiceConverter.convert(invoiceModel);
	}


	public CartModel getCart()
	{
		if (cartFacade.hasSessionCart())
		{
			return cartService.getSessionCart();
		}

		return null;
	}

	public void mergeCart(final CartModel cartModel)
	{
		cartService.saveOrder(cartModel);
	}

	public void saveInvoice(final InvoiceModel invoice)
	{
		modelService.save(invoice);
	}

	public void removeInvoice(final InvoiceModel invoice)
	{
		modelService.remove(invoice);
	}

	public CartData convertCart(final CartModel cartModel)
	{
		return cartConverter.convert(cartModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.ychinaaccelerator.facades.ChinaCheckoutFacade#setPaymentMode(de.hybris.platform.core.model.
	 * order.payment.PaymentModeModel)
	 */
	@Override
	public void setPaymentMode(final PaymentModeModel paymentMode)
	{
		if (getCart() != null)
		{
			this.getCart().setPaymentMode(paymentMode);
			modelService.save(this.getCart());
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.ychinaaccelerator.facades.ChinaCheckoutFacade#setDeliveryTimeslot(de.hybris.platform.
	 * ychinaaccelerator.core.enums.DeliveryTimeslot)
	 */
	@Override
	public void setDeliveryTimeslot(final DeliveryTimeslot deliveryTimeslot)
	{
		if (getCart() != null)
		{
			this.getCart().setDeliveryTimeslot(deliveryTimeslot);
			modelService.save(this.getCart());
		}

	}

}