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
package de.hybris.platform.b2b.punchout.services;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutSessionNotFoundException;


/**
 * This service handles the basic operations on {@link PunchOutService} instances.
 */
public interface PunchOutSessionService
{

	/**
	 * Activates a {@link PunchOutSession} for the current user session.
	 * 
	 * @param punchOutSession
	 *           the new punchOut session
	 */
	void activate(PunchOutSession punchOutSession);

	/**
	 * Loads and activates a {@link PunchOutSession} by its ID.
	 * 
	 * @param punchOutSessionId
	 *           the punchOut session ID
	 * @return the newly loaded session
	 * @throws PunchOutException
	 *            when the session is not found
	 */
	PunchOutSession load(String punchOutSessionId) throws PunchOutSessionNotFoundException;

	/**
	 * Set the cart for the current session using the cart saved in a given punch out session. This is necessary as the
	 * punchOut provider may use different sessions for sequential calls (e.g.: edit setup request and edit seamless
	 * login).<br>
	 * <i>Notice that this should only be called after punch out user is authenticated.</i>
	 * 
	 * @param punchOutSessionId
	 *           The punch out session ID.
	 */
	void setCurrentCartFromPunchOutSetup(final String punchOutSessionId);

	/**
	 * Retrieves the currently loaded {@link PunchOutSession}.
	 * 
	 * @return the punchOut session or <code>null</code> if none has been loaded yet
	 */
	PunchOutSession getCurrentPunchOutSession();

	/**
	 * Gets the currently active punchOut session.
	 * 
	 * @return the active punchOut session ID
	 */
	String getCurrentPunchOutSessionId();
}
