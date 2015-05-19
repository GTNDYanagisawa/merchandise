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
package de.hybris.platform.acceleratorservices.dataexport.generic.event;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.store.BaseStoreModel;


/**
 * ExportData event.
 */
public class ExportDataEvent extends AbstractEvent
{
	private String code;
	private BaseStoreModel baseStore;
	private CMSSiteModel site;
	private LanguageModel language;
	private CurrencyModel currency;
	private UserModel user;
	private String thirdPartyHost;
	private String thirdPartyUsername;
	private String thirdPartyPassword;
	private String dataGenerationPipeline;

	public String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	public BaseStoreModel getBaseStore()
	{
		return baseStore;
	}

	public void setBaseStore(final BaseStoreModel baseStore)
	{
		this.baseStore = baseStore;
	}

	public CMSSiteModel getSite()
	{
		return site;
	}

	public void setSite(final CMSSiteModel site)
	{
		this.site = site;
	}

	public LanguageModel getLanguage()
	{
		return language;
	}

	public void setLanguage(final LanguageModel language)
	{
		this.language = language;
	}

	public CurrencyModel getCurrency()
	{
		return currency;
	}

	public void setCurrency(final CurrencyModel currency)
	{
		this.currency = currency;
	}

	public UserModel getUser()
	{
		return user;
	}

	public void setUser(final UserModel user)
	{
		this.user = user;
	}

	public String getThirdPartyUsername()
	{
		return thirdPartyUsername;
	}

	public void setThirdPartyUsername(final String thirdPartyUsername)
	{
		this.thirdPartyUsername = thirdPartyUsername;
	}

	public String getThirdPartyPassword()
	{
		return thirdPartyPassword;
	}

	public void setThirdPartyPassword(final String thirdPartyPassword)
	{
		this.thirdPartyPassword = thirdPartyPassword;
	}

	public String getDataGenerationPipeline()
	{
		return dataGenerationPipeline;
	}

	public void setDataGenerationPipeline(final String dataGenerationPipeline)
	{
		this.dataGenerationPipeline = dataGenerationPipeline;
	}

	public String getThirdPartyHost()
	{
		return thirdPartyHost;
	}

	public void setThirdPartyHost(final String thirdPartyHost)
	{
		this.thirdPartyHost = thirdPartyHost;
	}
}
