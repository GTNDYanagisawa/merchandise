package de.hybris.platform.commercewebservicescommons.errors.exceptions;

/**
 * Thrown when there is a problem with session attribute.
 */
public class SessionAttributeException extends WebserviceException
{
	public static final String NOT_FOUND = "notFound";
	private static final String TYPE = "SessionAttributeError";
	private static final String SUBJECT_TYPE = "attribute";

	public SessionAttributeException(final String message)
	{
		super(message);
	}

	public SessionAttributeException(final String message, final String reason)
	{
		super(message, reason);
	}

	public SessionAttributeException(final String message, final String reason, final Throwable cause)
	{
		super(message, reason, cause);
	}

	public SessionAttributeException(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);
	}

	public SessionAttributeException(final String message, final String reason, final String subject, final Throwable cause)
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
