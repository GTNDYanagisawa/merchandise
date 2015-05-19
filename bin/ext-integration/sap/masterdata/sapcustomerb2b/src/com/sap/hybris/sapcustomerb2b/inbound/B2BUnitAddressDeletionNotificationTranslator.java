package com.sap.hybris.sapcustomerb2b.inbound;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.security.JaloSecurityException;


/**
 * This class includes the translator for B2BUnit address deletion notification
 */
public class B2BUnitAddressDeletionNotificationTranslator extends AbstractSpecialValueTranslator
{

	private static final String MESSAGEFUNCTION_DELETE = "003";
	private static final String MESSAGEFUNCTION_UPDATE = "004";

	private static final String PUBLIC_KEY = "publicKey";
	private static final String SAP_CUSTOMER_ID = "sapCustomerID";
	private static final String SAP_ADDRESS_USAGE = "sapAddressUsage";
	private static final String SAP_ADDRESS_USAGE_COUNTER = "sapAddressUsageCounter";

	private B2BUnitAddressDeletionService b2bUnitAddressDeletionService;

	/**
	 * @return B2BUnitAddressDeletionService
	 */
	public B2BUnitAddressDeletionService getB2BUnitAddressDeletionService()
	{
		return b2bUnitAddressDeletionService;
	}

	/**
	 * @param b2bUnitAddressDeletionService
	 */
	public void setB2BUnitAddressDeletionService(final B2BUnitAddressDeletionService b2bUnitAddressDeletionService)
	{
		this.b2bUnitAddressDeletionService = b2bUnitAddressDeletionService;
	}

	@Override
	public void init(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException
	{
		if (b2bUnitAddressDeletionService == null)
		{
			// retrieve B2BUnit-address deletion service
			b2bUnitAddressDeletionService = (B2BUnitAddressDeletionService) Registry.getApplicationContext().getBean(
					"b2bUnitAddressDeletionService");
		}
	}

	@Override
	public void performImport(final String transformationExpression, final Item processedItem) throws ImpExException
	{
		final String messageFunction = transformationExpression;
		if (MESSAGEFUNCTION_DELETE.equalsIgnoreCase(messageFunction) || MESSAGEFUNCTION_UPDATE.equalsIgnoreCase(messageFunction))
		{
			final String publicKey = getAttributeValue(processedItem, PUBLIC_KEY);
			if (publicKey != null)
			{
				final String b2bUnitId = publicKey.substring(0, publicKey.indexOf("|"));
				final String sapAddressUsage = getAttributeValue(processedItem, SAP_ADDRESS_USAGE);
				final String sapAddressUsageCounter = getAttributeValue(processedItem, SAP_ADDRESS_USAGE_COUNTER);
				if (!b2bUnitId.isEmpty() && sapAddressUsage != null && !sapAddressUsage.isEmpty() && sapAddressUsageCounter != null
						&& !sapAddressUsageCounter.isEmpty())
				{
					final String sapCustomerId = getAttributeValue(processedItem, SAP_CUSTOMER_ID);
					b2bUnitAddressDeletionService.processB2BUnitAddressDeletion(messageFunction, b2bUnitId, sapCustomerId,
							sapAddressUsage, sapAddressUsageCounter);
				}
			}
		}

	}

	private String getAttributeValue(final Item processedItem, final String attributeName) throws ImpExException
	{
		String attributeValue = null;
		try
		{
			final Object attributeObject = processedItem.getAttribute(attributeName);
			if (attributeObject != null)
			{
				attributeValue = attributeObject.toString();
			}
		}
		catch (final JaloSecurityException e)
		{
			throw new ImpExException(e);
		}
		return attributeValue;
	}

	@Override
	public void validate(final String paramString) throws HeaderValidationException
	{

	}

	@Override
	public String performExport(final Item paramItem) throws ImpExException
	{
		return null;
	}

	@Override
	public boolean isEmpty(final String paramString)
	{
		return false;
	}

}
