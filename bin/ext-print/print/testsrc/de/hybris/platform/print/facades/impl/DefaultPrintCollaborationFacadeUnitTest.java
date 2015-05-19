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
package de.hybris.platform.print.facades.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.print.collaboration.facades.PrintCollaborationFacade;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import org.junit.Test;


/**
 * tests for the PrintCollaborationFacade
 */
@UnitTest
public class DefaultPrintCollaborationFacadeUnitTest
{
	/**
	 * service to test
	 */
	private final PrintCollaborationFacade printCollaborationFacade = (PrintCollaborationFacade) Registry.getApplicationContext()
			.getBean("printCollaborationFacade");

	/**
	 * Comment may not be deleted, if it was received by DTP at least once
	 */
	@Test(expected = BusinessException.class)
	public void testDeleteCommentReceivedByDtp() throws BusinessException
	{

		//set up comment
		final CommentModel comment = new CommentModel();
		comment.setCode("My Test Comment");
		comment.setReceivedByDtp(Boolean.TRUE);

		//try to delete comment
		printCollaborationFacade.deleteComment(comment);

	}

	/**
	 * Test, if a comment may be deleted
	 */
	@Test
	public void testIsDeleteCommentAllowed()
	{

		//set up comment
		final CommentModel comment = new CommentModel();
		comment.setCode("My Test Comment");

		comment.setReceivedByDtp(Boolean.FALSE);

		//try to delete comment which was already received by DTP
		assertThat(printCollaborationFacade.isDeleteAllowed(comment)).isTrue();


		comment.setReceivedByDtp(Boolean.TRUE);
		//try to delete comment which was already received by DTP
		assertThat(printCollaborationFacade.isDeleteAllowed(comment)).isFalse();

	}

}
