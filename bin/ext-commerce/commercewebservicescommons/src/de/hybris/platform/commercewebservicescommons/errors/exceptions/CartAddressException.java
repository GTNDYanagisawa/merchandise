package de.hybris.platform.commercewebservicescommons.errors.exceptions;

/**
 * 
 */
public class CartAddressException extends WebserviceException
{
	public static final String NOT_VALID = "notValid";
	public static final String CANNOT_SET = "cannotSet";
	public static final String CANNOT_RESET = "cannotReset";
	private static final String TYPE = "CartAddressError";
	private static final String SUBJECT_TYPE = "address";

	public CartAddressException(final String message)
	{
		super(message);
	}

	public CartAddressException(final String message, final String reason)
	{
		super(message, reason);
	}

	public CartAddressException(final String message, final String reason, final Throwable cause)
	{
		super(message, reason, cause);
	}

	public CartAddressException(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);
	}

	public CartAddressException(final String message, final String reason, final String subject, final Throwable cause)
	{
		super(message, reason, subject, cause);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}

	@Override
	public String getSubjectType()
	{
		return SUBJECT_TYPE;
	}
}
