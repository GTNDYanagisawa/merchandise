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
package de.hybris.platform.b2b.punchout.actions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.core.model.order.CartModel;

import java.util.ArrayList;
import java.util.List;

import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.Header;
import org.cxml.SharedSecret;
import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutOrderMessageHeaderProcessingActionTest
{

	private static final String SHARED_SECRET = "SharedSecret";
	private static final String NETWORK_ID = "NetworkId";
	private static final String CREDENTIAL_ID = "Id123";
    private static final String USER_AGENT = "UserAgent-123";

    @InjectMocks
	private final PunchOutOrderMessageHeaderProcessingAction action = new PunchOutOrderMessageHeaderProcessingAction();

	@Mock
	private PunchOutSessionService punchOutSessionService;

	private final PunchOutSession punchOutSession = new PunchOutSession();
	private final CartModel cartModel = new CartModel();


	@Before
	public void setUp()
	{
		when(punchOutSessionService.getCurrentPunchOutSession()).thenReturn(punchOutSession);

		final List<Organization> organizationList = new ArrayList<Organization>();
		Organization organization = new Organization();
		organization.setDomain("InitiatedBy.Domain");
		organization.setIdentity("InitiatedBy.Identity");
		organizationList.add(organization);

		punchOutSession.setInitiatedBy(organizationList);
		organizationList.clear();

		organization = new Organization();
		organization.setDomain("TargetedTo.Domain");
		organization.setIdentity("TargetedTo.Identity");

		organizationList.add(organization);
		punchOutSession.setTargetedTo(organizationList);
		organizationList.clear();

		organization = new Organization();
		organization.setDomain(NETWORK_ID);
		organization.setIdentity(CREDENTIAL_ID);
		organization.setSharedsecret(SHARED_SECRET);

		organizationList.add(organization);
		punchOutSession.setSentBy(organizationList);
        punchOutSession.setSentByUserAgent(USER_AGENT);
	}

	@Test
	public void shouldCreateThePunchOutTransaction()
	{
		final CXML transaction = CXMLBuilder.newInstance().create();

		action.process(cartModel, transaction);

		final Header header = (Header) transaction.getHeaderOrMessageOrRequestOrResponse().get(0);
		final Credential fromCredential = header.getFrom().getCredential().get(0);
		List<Organization> organizationList = punchOutSession.getTargetedTo();


		assertEquals(organizationList.get(0).getDomain(), fromCredential.getDomain());
		assertEquals(organizationList.get(0).getIdentity(), fromCredential.getIdentity().getContent().get(0));

		final Credential toCredential = header.getTo().getCredential().get(0);
		organizationList = punchOutSession.getInitiatedBy();

		assertEquals(organizationList.get(0).getDomain(), toCredential.getDomain());
		assertEquals(organizationList.get(0).getIdentity(), toCredential.getIdentity().getContent().get(0));

		final Credential senderCredential = header.getSender().getCredential().get(0);
        assertThat(senderCredential.getDomain(), is(NETWORK_ID));
        assertThat((String) senderCredential.getIdentity().getContent().get(0), is(CREDENTIAL_ID));
        assertThat(header.getSender().getUserAgent(), is(USER_AGENT));
		assertTrue(Collections.isEmpty(senderCredential.getSharedSecretOrDigitalSignatureOrCredentialMac()));

	}
}
