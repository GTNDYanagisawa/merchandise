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
package de.hybris.platform.print.comet.converter.comment.impl;

import de.hybris.platform.cockpit.model.CommentMetadataModel;
import de.hybris.platform.comments.model.CommentGroupModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.ReplyModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.print.model.PageModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.junit.Ignore;


@Ignore
public abstract class AbstractConverterTest
{

	public CommentModel createCommentModelForPage(final PageModel page) throws ParseException
	{
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

		final CommentModel commentModel = new CommentModel();
		commentModel.setCode("commentCode ß#ä");
		commentModel.setSubject("Test Subject öäü");
		commentModel.setText("<p>Text is required for comment model. HTML tags should be removed in plain node.</p>");
		commentModel.setCreationtime(format.parse("20140216210023"));

		final CommentGroupModel commentGroupModel = new CommentGroupModel();
		commentGroupModel.setCode("Test Comment Group");
		commentGroupModel.setItem(page);

		final CommentMetadataModel commentMetadataModel = new CommentMetadataModel();
		commentMetadataModel.setX(Integer.valueOf(550));
		commentMetadataModel.setY(Integer.valueOf(300));
		commentMetadataModel.setPageIndex(Integer.valueOf(0));

		final ArrayList<CommentMetadataModel> metaDataModels = new ArrayList<CommentMetadataModel>();
		metaDataModels.add(commentMetadataModel);


		final UserModel userModel = new UserModel();
		userModel.setUid("Don product manager");





		commentModel.setCommentMetadata(metaDataModels);
		commentModel.setAuthor(userModel);
		commentModel.setCommentGroup(commentGroupModel);
		commentModel.setReplies(new ArrayList<ReplyModel>());


		return commentModel;
	}

	public PageModel createPage()
	{

		final PageModel pageModel = new PageModel();
		pageModel.setCode("my Test Page");
		pageModel.setId("pageStringUniqueID");

		return pageModel;

	}
}
