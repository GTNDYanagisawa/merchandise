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
package de.hybris.platform.b2bdocumentsfilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import de.hybris.platform.accountsummaryaddon.facade.B2BAccountSummaryFacade;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.jalo.media.AbstractMedia;
import de.hybris.platform.media.MediaSource;
import de.hybris.platform.servicelayer.media.MediaPermissionService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.MediaUtil;


public class B2BDocumentsSecureMediaFilter extends GenericFilterBean
{

	private static final String MEDIA_PK = "mediaPK";
	private String secureMediaToken = "securemedias";

	@Resource(name = "b2bAccountSummaryFacade")
	protected B2BAccountSummaryFacade b2bAccountSummaryFacade;

	@Resource(name = "modelService")
	protected ModelService modelService;

	@Resource(name = "mediaService")
	protected MediaService mediaService;

	@Resource(name = "mediaPermissionService")
	protected MediaPermissionService mediaPermissionService;

	@Resource(name = "userService")
	protected UserService userService;

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{

		if ((!(request instanceof HttpServletRequest)) || (!(response instanceof HttpServletResponse)))
		{
			throw new ServletException("SecureMediaFilter just supports HTTP requests");
		}
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse = (HttpServletResponse) response;

		final String resourcePath = getResourcePath(httpRequest);
		try
		{
			setSecureURLRendererForThread(httpResponse);

			if (StringUtils.contains(resourcePath, this.secureMediaToken))
			{
				final String mediaPKStr = httpRequest.getParameter(MEDIA_PK);

				final MediaModel mediaModel = (MediaModel) this.modelService.get(PK.parse(mediaPKStr));

				if (mediaModel == null)
				{
					httpResponse.sendError(404);
					return;
				}
				else if (!(isAccessGranted(mediaModel)))
				{
					httpResponse.sendError(403);
					return;
				}
				final int mediaSize = (mediaModel.getSize() == null) ? 0 : mediaModel.getSize().intValue();
				sendData(httpResponse, this.mediaService.getStreamFromMedia(mediaModel), mediaSize);
			}

		}
		finally
		{

			chain.doFilter(request, response);
			clearSecureURLRendererForThread();
		}
	}

	private void clearSecureURLRendererForThread()
	{
		MediaUtil.unsetCurrentSecureMediaURLRenderer();
	}

	private void setSecureURLRendererForThread(final HttpServletResponse httpResponse)
	{
		final String urlEncoded = httpResponse.encodeURL(this.secureMediaToken);

		MediaUtil.setCurrentSecureMediaURLRenderer(new MediaUtil.SecureMediaURLRenderer()

		{
			public String renderSecureMediaURL(final AbstractMedia media)
			{
				return urlEncoded + "?" + MEDIA_PK + "=" + media.getPK().toString();
			}

			@Override
			public String renderSecureMediaURL(MediaSource mediaSource)
			{
				return null;
			}



		});
	}

	private void sendData(final HttpServletResponse httpResponse, final InputStream mediaStream, final int length)
			throws IOException
	{
		httpResponse.setContentLength(length);

		try
		{
			final OutputStream out = httpResponse.getOutputStream();
			IOUtils.copyLarge(mediaStream, out);
		}
		finally
		{
			if (mediaStream != null)
			{
				mediaStream.close();
			}
			if (mediaStream != null)
			{
				mediaStream.close();
			}
		}
	}

	private boolean isAccessGranted(final MediaModel mediaModel)
	{
		final PK loginUserB2BUnitPK = getLoginUserB2BUnitPK();
		return (loginUserB2BUnitPK != null && checkLoginUserB2BUnitHasAccessToMedia(mediaModel, loginUserB2BUnitPK));
	}

	private PK getLoginUserB2BUnitPK()
	{
		PK loginUserB2BUnitPK = null;
		if (this.userService.getCurrentUser() != null)
		{
			final Set<PrincipalGroupModel> listByLogin = this.userService.getCurrentUser().getGroups();
			for (final PrincipalGroupModel principalGroupModel : listByLogin)
			{
				if (principalGroupModel instanceof B2BUnitModel)
				{
					loginUserB2BUnitPK = ((B2BUnitModel) principalGroupModel).getPk();
				}
			}
		}
		return loginUserB2BUnitPK;
	}

	private boolean checkLoginUserB2BUnitHasAccessToMedia(final MediaModel mediaModel, final PK loginUserB2BUnitPK)
	{
		boolean hasAccess = false;
		PK mediaUserB2BUnitPK = null;
		final SearchResult<B2BDocumentModel> documentsResult = b2bAccountSummaryFacade.getOpenDocuments(mediaModel);

		final List<B2BDocumentModel> documents = documentsResult.getResult();
		if (documents != null && !documents.isEmpty())
		{

			mediaUserB2BUnitPK = documents.iterator().next().getUnit().getPk();
			if (loginUserB2BUnitPK.compareTo(mediaUserB2BUnitPK) == 0)
			{
				hasAccess = true;
				return hasAccess;
			}

			final Set<PrincipalGroupModel> listByMedia = documents.iterator().next().getUnit().getAllGroups();

			for (final PrincipalGroupModel principalGroupModel : listByMedia)
			{
				if (principalGroupModel instanceof B2BUnitModel)
				{
					mediaUserB2BUnitPK = ((B2BUnitModel) principalGroupModel).getPk();

					if (loginUserB2BUnitPK.compareTo(mediaUserB2BUnitPK) == 0)
					{
						hasAccess = true;
						return hasAccess;
					}
				}
			}
		}
		return hasAccess;
	}

	protected String getResourcePath(final HttpServletRequest httpRequest)
	{
		String resourcePath = httpRequest.getServletPath();
		if ((resourcePath == null) || (resourcePath.trim().isEmpty()))

		{
			final String reqURI = httpRequest.getRequestURI();
			final String ctxPath = httpRequest.getContextPath();
			resourcePath = reqURI.replace(ctxPath, "");
		}

		return resourcePath;
	}

	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public void setMediaPermissionService(final MediaPermissionService mediaPermissionService)
	{
		this.mediaPermissionService = mediaPermissionService;
	}

	public void setSecureMediaToken(final String secureMediaToken)
	{
		if (!(StringUtils.isNotBlank(secureMediaToken)))
		{
			return;
		}
		this.secureMediaToken = secureMediaToken;
	}

	public void setB2bAccountSummaryFacade(final B2BAccountSummaryFacade b2bAccountSummaryFacade)
	{
		this.b2bAccountSummaryFacade = b2bAccountSummaryFacade;
	}
}
