package de.hybris.platform.sap.sappricingbol.backend.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;
import de.hybris.platform.sap.sappricingbol.constants.PaymentModeEnum;
import de.hybris.platform.sap.sappricingbol.constants.SappricingbolConstants;
import de.hybris.platform.sap.sappricingbol.converter.ConversionTools;

import java.util.Collection;

import org.springframework.util.Assert;

import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;


public class SapPricingHeaderMapper extends SapPricingBaseMapper
{
	static final private Log4JWrapper sapLogger = Log4JWrapper.getInstance(SapPricingHeaderMapper.class.getName());

	public void fillImportParameters(final JCoParameterList importParameters, final SapPricingPartnerFunction partnerFunction)
	{

		fillImportParameters(importParameters, partnerFunction, null);

	}

	protected void fillDeliveryMode(final JCoTable callerData, final AbstractOrderModel order)
	{

		if (order.getDeliveryMode() != null)
		{

			final Collection<ConfigurationPropertyAccess> deliveryModeAccesses = uncheckedCast(getModuleConfigurationAccess()
					.getPropertyAccessCollection(SappricingbolConstants.CONF_PROP_DELIVERY_MODES));

			Assert.notEmpty(deliveryModeAccesses, "Missing sap delivery modes configuration.");

			for (final ConfigurationPropertyAccess deliveryModeAccess : deliveryModeAccesses)
			{

				if (order.getDeliveryMode().getCode()
						.contentEquals(deliveryModeAccess.getPropertyAccess("deliveryMode").getProperty("code").toString()))
				{
					callerData.appendRow();
					callerData.setValue("NAME", "VSBED");
					callerData.setValue("VALUE", deliveryModeAccess.getProperty("deliveryValue").toString());
					break;
				}
			}

		}
	}

	protected void fillPaymentMode(final JCoTable callerData, final AbstractOrderModel order)
	{

		if (order.getPaymentInfo() != null)
		{
			final String paymemtMode = PaymentModeEnum.getPaymentTypeCode(order.getPaymentInfo().getItemtype());

			final Collection<ConfigurationPropertyAccess> paymentModeAccesses = uncheckedCast(getModuleConfigurationAccess()
					.getPropertyAccessCollection(SappricingbolConstants.CONF_PROP_PAYMENT_MODES));

			Assert.notEmpty(paymentModeAccesses, "Missing sap payment modes configuration.");

			for (final ConfigurationPropertyAccess paymentModeAccess : paymentModeAccesses)
			{
				if (paymemtMode.contentEquals(paymentModeAccess.getPropertyAccess("paymentMode").getProperty("code").toString()))
				{
					callerData.appendRow();
					callerData.setValue("NAME", paymentModeAccess.getProperty("paymentName").toString());
					callerData.setValue("VALUE", paymentModeAccess.getProperty("paymentValue").toString());
					break;
				}
			}

		}
	}



	/**
	 * Helps to avoid using {@code @SuppressWarnings( "unchecked"})} when casting to a generic type.
	 */
	@SuppressWarnings(
	{ "unchecked" })
	protected static <T> T uncheckedCast(final Object obj)
	{
		return (T) obj;
	}

	public void fillImportParameters(final JCoParameterList importParameters, final SapPricingPartnerFunction partnerFunction,
			final AbstractOrderModel order)
	{

		final JCoTable itHead = importParameters.getTable("IT_HEAD");
		itHead.appendRow();
		itHead.setValue("VBELN", SappricingbolConstants.VBELN);
		itHead.setValue("KUNNR", partnerFunction.getSoldTo());
		itHead.setValue("SPRAS", ConversionTools.getR3LanguageCode(partnerFunction.getLanguage()));
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("sappricingbol - Header Data: ");
			sapLogger.debug(itHead.toString());
		}

		final JCoTable callerData = itHead.getTable("CALLER_DATA");
		callerData.appendRow();
		callerData.setValue("NAME", "WAERK");
		callerData.setValue("VALUE", partnerFunction.getCurrency());

		if (order != null)
		{
			fillDeliveryMode(callerData, order);
			fillPaymentMode(callerData, order);

		}
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug("sappricingbol - Caller Data: ");
			sapLogger.debug(callerData.toString());
		}

	}


}
