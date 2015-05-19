/**
 * 
 */
package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartAddressException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.ycommercewebservices.exceptions.InvalidPaymentInfoException;
import de.hybris.platform.ycommercewebservices.exceptions.NoCheckoutCartException;
import de.hybris.platform.ycommercewebservices.exceptions.UnsupportedDeliveryModeException;
import de.hybris.platform.ycommercewebservices.populator.options.PaymentInfoOption;
import de.hybris.platform.ycommercewebservices.validator.PlaceOrderCartValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class BaseCommerceController extends BaseController
{
	private final static Logger LOG = Logger.getLogger(BaseCommerceController.class);
	//TODO change commerceWebServicesCartFacade2 to commerceWebServicesCartFacade after removing it in commercefacades
	@Resource(name = "commerceWebServicesCartFacade2")
	protected CartFacade cartFacade;
	@Resource(name = "checkoutFacade")
	protected CheckoutFacade checkoutFacade;
	@Resource(name = "voucherFacade")
	protected VoucherFacade voucherFacade;
	@Resource(name = "deliveryAddressValidator")
	protected Validator deliveryAddressValidator;
	@Resource(name = "httpRequestAddressDataPopulator")
	protected Populator<HttpServletRequest, AddressData> httpRequestAddressDataPopulator;
	@Resource(name = "addressValidator")
	protected Validator addressValidator;
	@Resource(name = "addressDTOValidator")
	protected Validator addressDTOValidator;
	@Resource(name = "userFacade")
	protected UserFacade userFacade;
	@Resource(name = "ccPaymentInfoValidator")
	protected Validator ccPaymentInfoValidator;
	@Resource(name = "paymentDetailsDTOValidator")
	protected Validator paymentDetailsDTOValidator;
	@Resource(name = "httpRequestPaymentInfoPopulator")
	protected ConfigurablePopulator<HttpServletRequest, CCPaymentInfoData, PaymentInfoOption> httpRequestPaymentInfoPopulator;
	@Resource(name = "placeOrderCartValidator")
	private PlaceOrderCartValidator placeOrderCartValidator;

	protected AddressData createAddressInternal(final HttpServletRequest request) throws WebserviceValidationException
	{
		final AddressData addressData = new AddressData();
		httpRequestAddressDataPopulator.populate(request, addressData);

		validate(addressData, "addressData", addressValidator);

		return createAddressInternal(addressData);
	}

	protected AddressData createAddressInternal(final AddressData addressData)
	{
		addressData.setShippingAddress(true);
		addressData.setVisibleInAddressBook(true);
		userFacade.addAddress(addressData);
		if (addressData.isDefaultAddress())
		{
			userFacade.setDefaultAddress(addressData);
		}
		return addressData;
	}

	protected CartData setCartDeliveryAddressInternal(final String addressId) throws NoCheckoutCartException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setCartDeliveryAddressInternal: " + logParam("addressId", addressId));
		}
		final AddressData address = new AddressData();
		address.setId(addressId);
		final Errors errors = new BeanPropertyBindingResult(address, "addressData");
		deliveryAddressValidator.validate(address, errors);
		if (errors.hasErrors())
		{
			throw new CartAddressException("Address given by id " + addressId + " is not valid", CartAddressException.NOT_VALID,
					addressId);
		}
		if (checkoutFacade.setDeliveryAddress(address))
		{
			return getSessionCart();
		}
		throw new CartAddressException("Address given by id " + addressId + " cannot be set as delivery address in this cart",
				CartAddressException.CANNOT_SET, addressId);
	}

	protected CartData setCartDeliveryModeInternal(final String deliveryModeId) throws UnsupportedDeliveryModeException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setCartDeliveryModeInternal: " + logParam("deliveryModeId", deliveryModeId));
		}
		if (checkoutFacade.setDeliveryMode(deliveryModeId))
		{
			return getSessionCart();
		}
		throw new UnsupportedDeliveryModeException(deliveryModeId);
	}

	protected CartData applyVoucherForCartInternal(final String voucherId) throws NoCheckoutCartException,
			VoucherOperationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("apply voucher: " + logParam("voucherId", voucherId));
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot apply voucher. There was no checkout cart created yet!");
		}

		voucherFacade.applyVoucher(voucherId);
		return getSessionCart();
	}

	protected CartData addPaymentDetailsInternal(final HttpServletRequest request) throws WebserviceValidationException,
			InvalidPaymentInfoException, NoCheckoutCartException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("addPaymentInfo");
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot add PaymentInfo. There was no checkout cart created yet!");
		}

		final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
		final Errors errors = new BeanPropertyBindingResult(paymentInfoData, "paymentInfoData");

		final Collection<PaymentInfoOption> options = new ArrayList<PaymentInfoOption>();
		options.add(PaymentInfoOption.BASIC);
		options.add(PaymentInfoOption.BILLING_ADDRESS);

		httpRequestPaymentInfoPopulator.populate(request, paymentInfoData, options);
		ccPaymentInfoValidator.validate(paymentInfoData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		return addPaymentDetailsInternal(paymentInfoData);
	}

	protected CartData addPaymentDetailsInternal(final CCPaymentInfoData paymentInfoData) throws InvalidPaymentInfoException
	{
		final boolean emptySavedPaymentInfos = userFacade.getCCPaymentInfos(true).isEmpty();
		final CCPaymentInfoData createdPaymentInfoData = checkoutFacade.createPaymentSubscription(paymentInfoData);

		if (createdPaymentInfoData.isSaved() && (paymentInfoData.isDefaultPaymentInfo() || emptySavedPaymentInfos))
		{
			userFacade.setDefaultPaymentInfo(createdPaymentInfoData);
		}

		if (checkoutFacade.setPaymentDetails(createdPaymentInfoData.getId()))
		{
			return getSessionCart();
		}
		throw new InvalidPaymentInfoException(createdPaymentInfoData.getId());
	}

	protected CartData setPaymentDetailsInternal(final String paymentDetailsId) throws InvalidPaymentInfoException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setPaymentDetailsInternal: " + logParam(paymentDetailsId, "paymentDetailsId"));
		}
		if (checkoutFacade.setPaymentDetails(paymentDetailsId))
		{
			return getSessionCart();
		}
		throw new InvalidPaymentInfoException(paymentDetailsId);
	}

	protected void validateCartForPlaceOrder() throws NoCheckoutCartException, InvalidCartException, WebserviceValidationException
	{
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot place order. There was no checkout cart created yet!");
		}

		final CartData cartData = getSessionCart();

		final Errors errors = new BeanPropertyBindingResult(cartData, "sessionCart");
		placeOrderCartValidator.validate(cartData, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		try
		{
			final List<CartModificationData> modificationList = cartFacade.validateCartData();
			if (modificationList != null && !modificationList.isEmpty())
			{
				final CartModificationDataList cartModificationDataList = new CartModificationDataList();
				cartModificationDataList.setCartModificationList(modificationList);
				throw new WebserviceValidationException(cartModificationDataList);
			}
		}
		catch (final CommerceCartModificationException e)
		{
			throw new InvalidCartException(e);
		}
	}

	protected CartData getSessionCart()
	{
		return cartFacade.getSessionCart();
	}
}
