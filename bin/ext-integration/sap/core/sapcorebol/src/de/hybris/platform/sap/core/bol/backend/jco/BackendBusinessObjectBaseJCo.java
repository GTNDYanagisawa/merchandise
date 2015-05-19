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
package de.hybris.platform.sap.core.bol.backend.jco;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObjectBase;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.constants.SapcoreConstants;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionContainer;
import de.hybris.platform.sap.core.jco.connection.impl.DefaultJCoManagedConnectionContainer;

import org.springframework.beans.factory.annotation.Required;


/**
 * Base implementation of the BackendBusinessObjectJCo interface.
 */
public class BackendBusinessObjectBaseJCo extends BackendBusinessObjectBase implements BackendBusinessObjectJCo
{

	private static Log4JWrapper log = Log4JWrapper.getInstance(BackendBusinessObjectBaseJCo.class.getName());

	private String defaultConnectionName;

	private String defaultDestinationName;

	private JCoManagedConnectionContainer managedConnectionContainer;

	/**
	 * Injection setter for {@link DefaultJCoManagedConnectionContainer}.
	 * 
	 * @param managedConnectionContainer
	 *           {@link DefaultJCoManagedConnectionContainer}
	 */
	@Required
	public void setManagedConnectionContainer(final JCoManagedConnectionContainer managedConnectionContainer)
	{
		this.managedConnectionContainer = managedConnectionContainer;
	}

	@Override
	public JCoConnection getDefaultJCoConnection()
	{
		if (defaultDestinationName != null)
		{
			return managedConnectionContainer.getManagedConnection(getDefaultConnectionName(), defaultDestinationName);
		}
		return managedConnectionContainer.getManagedConnection(getDefaultConnectionName());
	}

	@Override
	public JCoConnection getJCoConnection(final String connectionName, final String destinationName)
	{
		return managedConnectionContainer.getManagedConnection(connectionName, destinationName);
	}

	@Override
	public JCoConnection getJCoConnection(final String connectionName, final String destinationName, final String scopeId)
	{
		return managedConnectionContainer.getManagedConnection(connectionName, destinationName, scopeId);
	}

	/**
	 * Get the current module id.
	 * 
	 * @return module id
	 */
	protected String getModuleId()
	{
		if (moduleConfigurationAccess != null)
		{
			return moduleConfigurationAccess.getModuleId();
		}
		log.debug("The module id could not be determined; no module configuration access is set.");
		return "";
	}

	@Override
	public void setDefaultConnectionName(final String defaultConnectionName)
	{
		this.defaultConnectionName = defaultConnectionName;

	}

	@Override
	public String getDefaultConnectionName()
	{
		return this.defaultConnectionName;
	}

	@Override
	public void setDefaultDestinationName(final String defaultDestinationName)
	{
		this.defaultDestinationName = defaultDestinationName;
	}

	@Override
	public String getDefaultDestinationName()
	{
		return this.defaultDestinationName;
	}

	@Override
	public String toString()
	{
		return super.toString() + SapcoreConstants.CRLF + "- Default Connection Name: " + defaultConnectionName
				+ SapcoreConstants.CRLF + "- Destination Name: " + defaultDestinationName;
	}

}
