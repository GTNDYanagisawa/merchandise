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
package de.hybris.platform.cockpit.session;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cockpit.events.CockpitEvent;
import de.hybris.platform.cockpit.reports.jasperreports.JasperReportsRefresh;
import de.hybris.platform.cockpit.services.NewItemService;
import de.hybris.platform.cockpit.services.SystemService;
import de.hybris.platform.cockpit.services.config.UIConfigurationService;
import de.hybris.platform.cockpit.services.label.LabelService;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.cockpit.services.query.SavedQueryService;
import de.hybris.platform.cockpit.services.search.SearchService;
import de.hybris.platform.cockpit.services.security.UIAccessRightService;
import de.hybris.platform.cockpit.services.undo.UndoManager;
import de.hybris.platform.cockpit.services.values.ObjectValueHandlerRegistry;
import de.hybris.platform.cockpit.session.impl.PushCreationContainer;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DummyUISession implements UISession
{
	@Override
	public void addSessionListener(final UISessionListener listener)
	{
		// YTODO Auto-generated method stub
	}

	@Override
	public void removeSessionListener(final UISessionListener listener)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public String getLanguageIso()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGlobalDataLanguageIso()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getGlobalDataLocale()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGlobalDataLanguageIso(final String iso)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public UserModel getUser()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void login(final UserModel user)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void logout()
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void setUser(final UserModel currentUser)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void setUserByUID(final String uid)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public UICockpitPerspective getCurrentPerspective()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrentPerspective(final UICockpitPerspective perspective)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void setCurrentPerspective(final UICockpitPerspective perspective, final Map<String, ? extends Object> params)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public List<UICockpitPerspective> getAvailablePerspectives()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPerspectiveAvailable(final String uid)
	{
		// YTODO Auto-generated method stub
		return false;
	}

	@Override
	public UICockpitPerspective getPerspective(final String uid) throws IllegalArgumentException
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelectedCatalogVersions(final List<CatalogVersionModel> catalogVersions)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public List<CatalogVersionModel> getSelectedCatalogVersions()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public SystemService getSystemService()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeService getTypeService()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public NewItemService getNewItemService()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectValueHandlerRegistry getValueHandlerRegistry()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public LabelService getLabelService()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchService getSearchService()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public SavedQueryService getSavedQueryService()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public UIConfigurationService getUiConfigurationService()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public ModelService getModelService()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public UndoManager getUndoManager()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendGlobalEvent(final CockpitEvent event)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void sendGlobalEvent(final CockpitEvent event, final boolean immediate)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public boolean isUsingTestIDs()
	{
		// YTODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDragOverPerspectivesEnabled()
	{
		// YTODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCachePerspectivesEnabled()
	{
		// YTODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRequestHandler(final RequestHandler reqHandler)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public RequestHandler getRequestHandler()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPushContainers(final List<PushCreationContainer> pushContainers)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public List<PushCreationContainer> getPushContainers()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setSessionAttribute(final String key, final Object value)
	{
		// YTODO Auto-generated method stub
		return false;
	}

	@Override
	public UIAccessRightService getUiAccessRightService()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public JasperReportsRefresh getJasperReportsRefresh()
	{
		// YTODO Auto-generated method stub
		return null;
	}

}
