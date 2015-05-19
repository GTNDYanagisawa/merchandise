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

import static org.mockito.BDDMockito.given;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import de.hybris.platform.accountsummaryaddon.facade.B2BAccountSummaryFacade;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.media.MediaPermissionService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.user.UserService;

@UnitTest
public class B2BDocumentsSecureMediaFilterTest {

	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;
	@Mock
	private UserModel userModel;
	@Mock
	private MediaModel mediaModel;
	@Mock
	private MediaPermissionService mediaPermissionService;
	@Mock
	private Collection<PrincipalModel> principalModel;
	@Mock
	private CustomerModel customer;
	@Mock
	private B2BUnitModel loginUser;
	@Mock
	private B2BUnitModel mediaUser;
	@Mock
	private B2BUserGroupModel loginB2BUserGroupModel;
	@Mock
	private B2BUserGroupModel mediaB2BUserGroupModel;
	@Mock
	private B2BAccountSummaryFacade b2bAccountSummaryFacade;
	@Mock
	private B2BDocumentModel document;
	@Mock
	private MediaService mediaService;

	private B2BDocumentsSecureMediaFilter b2bDocumentsSecureMediaFilter;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private MockFilterChain filterChain;

	@Before
	public void testSetup() throws Exception {
		MockitoAnnotations.initMocks(this);
		b2bDocumentsSecureMediaFilter = new B2BDocumentsSecureMediaFilter();

		given(modelService.get(PK.parse("8796093066078"))).willReturn(mediaModel);
		b2bDocumentsSecureMediaFilter.setModelService(modelService);

		given(mediaPermissionService.getPermittedPrincipals(mediaModel)).willReturn(principalModel);
		b2bDocumentsSecureMediaFilter.setMediaPermissionService(mediaPermissionService);

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = new MockFilterChain();
	}

	// No error when user try to get another resources not a documentMedia
	@Test
	public void shouldReturnNoErrorWithGetDocumentMedia() throws IOException, ServletException {
		b2bDocumentsSecureMediaFilter.doFilter(request, response, filterChain);

		TestCase.assertEquals(200, response.getStatus());
	}

	// Get an error the mediaModel is null, file not found error 404
	@Test
	public void shouldGetErrorFileNotFound() throws IOException, ServletException {

		setURI("234123412414");

		b2bDocumentsSecureMediaFilter.doFilter(request, response, filterChain);

		TestCase.assertEquals(404, response.getStatus());
	}

	// Get the mediaModel response 200
	@Test
	public void hasAccessToTheMedia() throws IOException, ServletException {

		setURI("8796093066078");
		setLoginUserService("8796126773252", false);
		setMediaService("8796126773252", false);

		b2bDocumentsSecureMediaFilter.doFilter(request, response, filterChain);

		TestCase.assertEquals(200, response.getStatus());
		TestCase.assertEquals("Hello", response.getContentAsString());
	}

	// Get an error user don't have access to the mediaModel error 403
	@Test
	public void dontHaveAccessToTheMedia() throws IOException, ServletException {

		setURI("8796093066078");
		setLoginUserService("8796126773253", false);
		setMediaService("8796126773252", false);

		b2bDocumentsSecureMediaFilter.doFilter(request, response, filterChain);

		TestCase.assertEquals(403, response.getStatus());
	}

	// Get the mediaModel response 200
	@Test
	public void hasParentAccessToTheMedia() throws IOException, ServletException {

		setURI("8796093066078");
		setLoginUserService("8796126773252", true);
		setMediaService("8796126773252", true);

		b2bDocumentsSecureMediaFilter.doFilter(request, response, filterChain);

		TestCase.assertEquals(200, response.getStatus());
		TestCase.assertEquals("Hello", response.getContentAsString());
	}

	private void setURI(final String mediaPK) {
		request.setRequestURI("http://localhost:9001/hmc_junit/securemedias");
		request.setParameter("mediaPK", mediaPK);
	}

	private void setLoginUserService(final String loginUserPK, final boolean withParent) {

		given(loginUser.getPk()).willReturn(PK.parse(loginUserPK));
		final Set<PrincipalGroupModel> loginPrincipalGroupModel = new HashSet<PrincipalGroupModel>();
		loginPrincipalGroupModel.add(loginUser);
		if (withParent) {
			loginPrincipalGroupModel.add(loginB2BUserGroupModel);
		}

		given(customer.getGroups()).willReturn(loginPrincipalGroupModel);
		given(userService.getCurrentUser()).willReturn(customer);

		b2bDocumentsSecureMediaFilter.setUserService(userService);
	}

	private void setMediaService(final String mediaUserPK, final boolean withParent) {

		given(mediaUser.getPk()).willReturn(PK.parse(mediaUserPK));
		final Set<PrincipalGroupModel> mediaPrincipalGroupModel = new HashSet<PrincipalGroupModel>();
		mediaPrincipalGroupModel.add(mediaUser);
		if (withParent) {
			mediaPrincipalGroupModel.add(mediaB2BUserGroupModel);
		}

		given(document.getUnit()).willReturn(mediaUser);
		given(document.getUnit().getPk()).willReturn(PK.parse(mediaUserPK));
		given(document.getUnit().getAllGroups()).willReturn(mediaPrincipalGroupModel);

		final SearchResult<B2BDocumentModel> b2bDocumentModelResult = new SearchResultImpl<>(Arrays.asList(document), 1, 1, 1);

		given(b2bAccountSummaryFacade.getOpenDocuments(mediaModel)).willReturn(b2bDocumentModelResult);
		given(mediaModel.getSize()).willReturn(new Long(1));

		final InputStream is = new ByteArrayInputStream("Hello".getBytes());
		given(mediaService.getStreamFromMedia(mediaModel)).willReturn(is);

		b2bDocumentsSecureMediaFilter.setB2bAccountSummaryFacade(b2bAccountSummaryFacade);
		b2bDocumentsSecureMediaFilter.setMediaService(mediaService);
	}
}
