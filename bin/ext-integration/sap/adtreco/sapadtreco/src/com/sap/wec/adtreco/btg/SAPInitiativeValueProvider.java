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
package com.sap.wec.adtreco.btg;

import de.hybris.platform.btg.condition.operand.types.StringSet;
import de.hybris.platform.btg.condition.operand.valueproviders.CollectionOperandValueProvider;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.cockpit.model.editor.impl.DefaultSAPInitiativeUIEditor;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.wec.adtreco.model.BTGSAPInitiativeOperandModel;
import com.sap.wec.adtreco.bo.ADTUserIdProvider;
import com.sap.wec.adtreco.bo.impl.SAPInitiative;
import com.sap.wec.adtreco.bo.intf.SAPInitiativeReader;


/**
 *
 */
public class SAPInitiativeValueProvider implements CollectionOperandValueProvider<BTGSAPInitiativeOperandModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultSAPInitiativeUIEditor.class); // NOPMD
	private static final String INITIATIVES_PREFIX = "Initiatives_";
	protected SAPInitiativeReader sapInitiativeReader;
	protected ADTUserIdProvider userIdProvider;
	protected SessionService sessionService;

	@Override
	public Object getValue(final BTGSAPInitiativeOperandModel operand, final UserModel user,
			final BTGConditionEvaluationScope scope)
	{
		List<String> result = new ArrayList<String>();
		if (user != null)
		{
			final String userId = userIdProvider.getADTUserId(user);

			if (userId != null)
			{
				if (sessionService.getAttribute(INITIATIVES_PREFIX + userId) != null)
				{
					result = sessionService.getAttribute(INITIATIVES_PREFIX + userId);
				}
				else
				{
					List<SAPInitiative> initiativesForBP = new ArrayList<SAPInitiative>();
					try
					{
						initiativesForBP = sapInitiativeReader.searchInitiativesForBP(userId);
						if (initiativesForBP.size() > 0)
						{
							sessionService.setAttribute(INITIATIVES_PREFIX + userId, result);
							for (final SAPInitiative initiative : initiativesForBP)
							{
								result.add(initiative.getId());
							}
						}
					}
					catch (Exception e)
					{
						LOG.error("", e);
					}
				}
			}
		}
		return new StringSet(result);
	}

	@Override
	public Class getValueType(final BTGSAPInitiativeOperandModel operand)
	{
		return SAPInitiativeSet.class;
	}

	@Override
	public Class getAtomicValueType(final BTGSAPInitiativeOperandModel operand)
	{
		return String.class;
	}

	/**
	 * @return the sapInitiativeReader
	 */
	public SAPInitiativeReader getSapInitiativeReader()
	{
		return sapInitiativeReader;
	}

	/**
	 * @param sapInitiativeReader
	 *           the sapInitiativeReader to set
	 */
	public void setSapInitiativeReader(final SAPInitiativeReader sapInitiativeReader)
	{
		this.sapInitiativeReader = sapInitiativeReader;
	}

	/**
	 * @return the userIdProvider
	 */
	public ADTUserIdProvider getUserIdProvider()
	{
		return userIdProvider;
	}

	/**
	 * @param userIdProvider
	 *           the userIdProvider to set
	 */
	public void setUserIdProvider(final ADTUserIdProvider userIdProvider)
	{
		this.userIdProvider = userIdProvider;
	}

	public SessionService getsessionService()
	{
		return sessionService;
	}

	public void setsessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
