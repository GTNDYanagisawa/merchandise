/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.checkout;


import de.hybris.platform.chinaaccelerator.services.enums.DeliveryTimeslot;
import de.hybris.platform.chinaaccelerator.services.model.invoice.InvoiceModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.InvoiceData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;


//import de.hybris.platform.chinaacceleratorservices.core.enums.DeliveryTimeslot;


public interface ChinaCheckoutFacade
{

	public InvoiceData getInvoiceData(final InvoiceModel invoiceModel);

	public CartModel getCart();

	public void mergeCart(final CartModel cartModel);

	public void saveInvoice(final InvoiceModel invoice);

	public void removeInvoice(final InvoiceModel invoice);

	public CartData convertCart(final CartModel cartModel);

	public void setPaymentMode(PaymentModeModel paymentMode);

	public void setDeliveryTimeslot(DeliveryTimeslot deliveryTimeslot);
}
