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
package de.hybris.platform.b2bacceleratorfacades.exception;
import de.hybris.platform.b2bacceleratorfacades.exception.HybrisSystemException;
import java.lang.String;
import java.lang.Throwable;
/**
 * The generic exception class for hybris domain objects.
 */
public class DomainException extends HybrisSystemException
{

    /**
     * Creates a new DomainException object with the given message.
     *
     * @param message the reason for this DomainException
     */
    public DomainException(final String message)
    {
        super(message);
    }
    /**
     * Creates a new DomainException object using the given message and cause
     * exception.
     *
     * @param message The reason for this DomainException.
     * @param cause the Throwable that caused this DomainException.
     */
    public DomainException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
