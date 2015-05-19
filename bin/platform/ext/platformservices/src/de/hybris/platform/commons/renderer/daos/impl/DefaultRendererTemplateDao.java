/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.commons.renderer.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.daos.RendererTemplateDao;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

import java.util.Collections;
import java.util.List;


/**
 * Default implementation of {@link RendererTemplateDao}
 */
public class DefaultRendererTemplateDao extends DefaultGenericDao<RendererTemplateModel> implements RendererTemplateDao
{

	public DefaultRendererTemplateDao()
	{
		this(RendererTemplateModel._TYPECODE);
	}

	public DefaultRendererTemplateDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<RendererTemplateModel> findRendererTemplatesByCode(final String code)
	{
		validateParameterNotNull(code, "Code must not be null!");

		return find(Collections.singletonMap(RendererTemplateModel.CODE, (Object) code));
	}
}
