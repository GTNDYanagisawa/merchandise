/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.checkout.impl;

import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.chinaaccelerator.alipay.enums.ServiceType;
import de.hybris.platform.chinaaccelerator.facades.converters.AlipayPaymentInfoConverter;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.AlipayPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.UUID;


public class AlipayCheckoutFacadeImpl extends DefaultAcceleratorCheckoutFacade
{
	private AlipayPaymentInfoConverter alipayPaymentInfoConverter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#setPaymentDetails(java.lang.String)
	 */
	@Override
	public boolean setPaymentDetails(final String paymentInfoId)
	{
		final CartModel cartModel = getCart();
		if (checkIfCurrentUserIsTheCartUser())
		{
			final AlipayPaymentInfoModel alipayPaymentInfoModel = createAlipayPaymentInfoModel(paymentInfoId);
			return getCommerceCheckoutService().setPaymentInfo(cartModel, alipayPaymentInfoModel);
		}
		return false;
	}

	/**
	 * @param paymentInfoId
	 * @return AlipayPaymentInfoModel
	 */
	protected AlipayPaymentInfoModel createAlipayPaymentInfoModel(final String paymentInfoId)
	{
		final AlipayPaymentInfoModel paymentInfo = new AlipayPaymentInfoModel();
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();

		setDefaultDetails(paymentInfo, currentUser);
		paymentInfo.setServiceType(ServiceType.DIRECTPAY);
		getModelService().save(paymentInfo);

		//currentUser.setDefaultPaymentInfo(paymentInfo);
		//getModelService().save(currentUser);

		return paymentInfo;
	}

	protected void setDefaultDetails(final PaymentInfoModel paymentInfo, final CustomerModel currentUser)
	{
		paymentInfo.setCode(currentUser.getUid() + "_" + UUID.randomUUID());
		paymentInfo.setUser(currentUser);
		//paymentInfo.setBillingAddress(currentUser.getDefaultPaymentAddress());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#getCheckoutCart()
	 */
	@Override
	protected CCPaymentInfoData getPaymentDetails()
	{
		final CartModel cart = getCart();
		if (cart != null)
		{
			final PaymentInfoModel paymentInfo = cart.getPaymentInfo();
			if (paymentInfo instanceof CreditCardPaymentInfoModel)
			{
				return getCreditCardPaymentInfoConverter().convert((CreditCardPaymentInfoModel) paymentInfo);
			}
			else if (paymentInfo instanceof AlipayPaymentInfoModel)
			{
				return getAlipayPaymentInfoConverter().convert((AlipayPaymentInfoModel) paymentInfo);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#authorizePayment(java.lang.String)
	 */
	@Override
	public boolean authorizePayment(final String securityCode)
	{
		return true;
	}

	/**
	 * @return the alipayPaymentInfoConverter
	 */
	public AlipayPaymentInfoConverter getAlipayPaymentInfoConverter()
	{
		return alipayPaymentInfoConverter;
	}

	/**
	 * @param alipayPaymentInfoConverter
	 *           the alipayPaymentInfoConverter to set
	 */
	public void setAlipayPaymentInfoConverter(final AlipayPaymentInfoConverter alipayPaymentInfoConverter)
	{
		this.alipayPaymentInfoConverter = alipayPaymentInfoConverter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#prepareCartForCheckout()
	 */
	@Override
	public void prepareCartForCheckout()
	{
		super.prepareCartForCheckout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade#setDeliveryModeIfAvailable()
	 */
	@Override
	public boolean setDeliveryModeIfAvailable()
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			// validate delivery mode if already exists
			getCommerceCheckoutService().validateDeliveryMode(cartModel);

			if (cartModel.getDeliveryMode() == null)
			{
				boolean flag = false;
				final DeliveryModeModel deliveryModeModel = getDeliveryService().getDeliveryModeForCode("standard-gross");
				if (deliveryModeModel != null)
				{
					flag = getCommerceCheckoutService().setDeliveryMode(cartModel, deliveryModeModel);
				}
				getModelService().save(cartModel);
				return flag;
			}
			return true;
		}
		return false;
	}

}
