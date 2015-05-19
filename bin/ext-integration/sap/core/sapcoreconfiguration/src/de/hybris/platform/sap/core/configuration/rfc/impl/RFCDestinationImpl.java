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
package de.hybris.platform.sap.core.configuration.rfc.impl;

import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.sap.core.configuration.model.RFCDestinationAttributeModel;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestinationConstants;
import de.hybris.platform.sap.core.constants.SapcoreConstants;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Collection;


/**
 * Implementation of RFC Destination.
 */
public class RFCDestinationImpl implements RFCDestination
{

	private final SAPRFCDestinationModel model;

	/**
	 * Constructor using SAP RFC Destination model.
	 * 
	 * @param model
	 *           SAP RFC Destination model
	 */
	public RFCDestinationImpl(final SAPRFCDestinationModel model)
	{
		super();
		this.model = model;
	}

	@Override
	public String getPassword()
	{
		return model.getPassword();
	}



	@Override
	public String getSid()
	{
		return model.getSid();
	}

	@Override
	public String getGroup()
	{
		return model.getGroup();
	}

	@Override
	public String getPoolSize()
	{
		return model.getPoolSize();
	}

	@Override
	public String getClient()
	{
		return model.getClient();
	}

	@Override
	public String getMaxWaitTime()
	{
		return model.getMaxWaitTime();
	}

	@Override
	public String getTargetHost()
	{
		return model.getTargetHost();
	}

	@Override
	public String getUserid()
	{
		return model.getUserid();
	}

	@Override
	public Boolean getPooledConnectionMode()
	{
		return model.getPooledConnectionMode();
	}

	@Override
	public String getMessageServer()
	{
		return model.getMessageServer();
	}

	@Override
	public String getInstance()
	{
		return model.getInstance();
	}

	@Override
	public String getMaxConnections()
	{
		return model.getMaxConnections();
	}

	@Override
	public String getRfcDestinationName()
	{
		return model.getRfcDestinationName();
	}

	@Override
	public Boolean getConnectionType()
	{
		return model.getConnectionType();
	}

	@Override
	public String getJcoTraceLevel()
	{
		switch (model.getJcoTraceLevel().toString())
		{
			case RFCDestinationConstants.JCO_TRACE_LEVEL_NO_TRACE:
				return "0";
			case RFCDestinationConstants.JCO_TRACE_LEVEL_ERRORS:
				return "1";
			case RFCDestinationConstants.JCO_TRACE_LEVEL_ERRORS_WARNINGS:
				return "2";
			case RFCDestinationConstants.JCO_TRACE_LEVEL_INFOS_ERRORS_WARNINGS:
				return "3";
			case RFCDestinationConstants.JCO_TRACE_LEVEL_EXPATH_INFOS_ERRORS_WARNINGS:
				return "4";
			case RFCDestinationConstants.JCO_TRACE_LEVEL_VERBEXPATH_INFOS_ERRORS_WARNINGS:
				return "5";
			case RFCDestinationConstants.JCO_TRACE_LEVEL_VERBEXPATH_LIMDATADUMPS_INFOS_ERRORS_WARNINGS:
				return "6";
			case RFCDestinationConstants.JCO_TRACE_LEVEL_FULLEXPATH_DATADUMPS_VERBINFOS_ERRORS_WARNINGS:
				return "7";
			case RFCDestinationConstants.JCO_TRACE_LEVEL_FULLEXPATH_FULLDATADUMPS_VERBINFOS_ERRORS_WARNINGS:
				return "8";
			default:
				throw new UnknownIdentifierException(String.format("JCo Trace Level with value {%s} not allowed", model
						.getJcoTraceLevel().toString()));
		}
	}

	@Override
	public String getJcoTracePath()
	{
		return model.getJcoTracePath();
	}

	@Override
	public Boolean isJcoRFCTraceEnabled()
	{
		return model.getJcoRFCTrace();
	}

	@Override
	public String getJcoCPICTrace()
	{
		switch (model.getJcoCPICTrace().toString())
		{
			case RFCDestinationConstants.CPIP_TRACE_LEVEL_GLOBAL:
				return "-1";
			case RFCDestinationConstants.CPIP_TRACE_LEVEL_NO_TRACE:
				return "0";
			case RFCDestinationConstants.CPIP_TRACE_LEVEL_MINIMAL:
				return "1";
			case RFCDestinationConstants.CPIP_TRACE_LEVEL_FULL:
				return "2";
			case RFCDestinationConstants.CPIP_TRACE_LEVEL_FULL_DATA:
				return "3";
			default:
				throw new UnknownIdentifierException(String.format("CPIP trace level with value {%s} not allowed", model
						.getJcoCPICTrace().toString()));
		}
	}

	@Override
	public String getJcoMsServ()
	{
		return model.getJcoMsServ();
	}

	@Override
	public String getJcoSAPRouter()
	{
		return model.getJcoSAPRouter();
	}

	@Override
	public Boolean getJcoClientDelta()
	{
		return model.getJcoClientDelta();
	}

	@Override
	public String getBackendType()
	{
		return model.getBackendType().toString();
	}

	@Override
	public Boolean isSncEnabled()
	{
		return model.getSncMode();
	}

	@Override
	public String getSncQOP()
	{
		if (model.getSncQoP() != null)
		{
			switch (model.getSncQoP().toString())
			{
				case "AUTHENTICATION_ONLY":
					return "1";
				case "INTEGRITY_PROTECTION":
					return "2";
				case "PRIVACY_PROTECTION":
					return "3";
				default:
					throw new UnknownIdentifierException(String.format("SNC quality of service with value {%s} not allowed", model
							.getSncQoP().toString()));
			}
		}
		return null;


	}

	@Override
	public String getSncPartnerName()
	{
		return model.getSncPartnerName();
	}

	@Override
	public boolean isOffline()
	{
		return model.getOfflineMode();
	}

	@Override
	public Collection<RFCDestinationAttributeModel> getRFCDestinationAttributes()
	{
		return model.getAdditionalAttributes();
	}

	@Override
	public String toString()
	{
		return super.toString() + SapcoreconfigurationConstants.CRLF + "- RFC Destination Name: " + getRfcDestinationName()
				+ SapcoreconfigurationConstants.CRLF + "- RFC Backend Type: " + getBackendType() + SapcoreconfigurationConstants.CRLF
				+ "- RFC SID: " + getSid() + SapcoreconfigurationConstants.CRLF + "- RFC Message Server: " + getMessageServer()
				+ SapcoreconfigurationConstants.CRLF + "- RFC Target Host: " + getTargetHost() + SapcoreConstants.CRLF
				+ "-SNC enabled: " + isSncEnabled() + SapcoreConstants.CRLF + "-SNC Quality of Protection: " + getSncQOP()
				+ SapcoreConstants.CRLF + "-SNC Partner Name: " + getSncPartnerName() + SapcoreConstants.CRLF
				+ "-JCo RFC Trace Enabled: " + isJcoRFCTraceEnabled() + SapcoreConstants.CRLF + "-is offline: " + isOffline()
				+ SapcoreConstants.CRLF + "-Additional Attributes: " + getRFCDestinationAttributes().toString();
	}


}
