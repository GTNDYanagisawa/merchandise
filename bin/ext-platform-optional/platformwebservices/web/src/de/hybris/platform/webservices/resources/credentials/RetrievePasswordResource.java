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
package de.hybris.platform.webservices.resources.credentials;

import java.security.SecureRandom;

import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.persistence.security.SaltedMD5PasswordEncoder;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservices.AbstractResource;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Retrieve a lost password.
 */
@Path("/retrievepassword")
@Produces(MediaType.APPLICATION_XML)
public class RetrievePasswordResource extends AbstractResource<UserModel>
{

	private SaltedMD5PasswordEncoder encoder;

	/**
	 * Getting passwordQuestion for {userid}.
	 * 
	 * @param userId
	 * @return {@link Response}
	 */
	@GET
	@Path("{userid}")
	public Response getPasswordQuestion(@PathParam("userid") final String userId)
	{
		try
		{
			setResourceId(userId);
			final UserModel userModel = getResourceValue();// readResource();
			final String question = userModel.getPasswordQuestion();
			final String answer = userModel.getPasswordAnswer();

			if (answer == null && question == null)
			{
				getResponse().entity("Your passwordQestion and passwordAnswer fields are empty.\nYou cannot retrieve your password.");
			}
			else if (question == null)
			{
				getResponse().entity("You have not created question.\nTry to put correct answer.");
			}
			else
			{
				getResponse()
						.entity(
								"Password question is: "
										+ question
										+ "\nTry to put correct answer. Use PUT method:\n<user uid=\"the same id as the one in the URL\">\n	<passwordAnswer>YOUR ANSWER</passwordAnswer>\n</user>");
			}
		}
		catch (final UnknownIdentifierException e)
		{
			getResponse().status(Response.Status.NOT_FOUND);
			getResponse().entity("User not found.");
		}

		return getResponse().build();

	}

	/**
	 * Setting passwordAnswer and getting new password for {userid}.
	 * 
	 * @param userId
	 *           - each user
	 * @param dto
	 *           - UserDTO
	 * @return {@link Response}
	 */
	@PUT
	@Path("{userid}")
	public Response putPasswordAnswer(@PathParam("userid") final String userId, final UserDTO dto)
	{
		setResourceId(userId);
		final UserModel userModel = getResourceValue(); //userService.getUser(userId);
		try
		{
			// set users uid when not provided
			if (dto.getUid() == null)
			{
				dto.setUid(userId);
			}
			// assure users uid equals this resource id
			if (dto.getUid().equals(userId))
			{
				final User user = serviceLocator.getModelService().getSource(userModel);
				final String requestAnswer = dto.getPasswordAnswer();
				final String answer = userModel.getPasswordAnswer();
				final String question = userModel.getPasswordQuestion();
				final String newRawPassword = getNewPassword();
				final String newEncPassword = encodePassword(userId, newRawPassword);

				if (requestAnswer == null)
				{
					getResponse().status(Response.Status.BAD_REQUEST);
					getResponse().entity("Use <passwordAnswer>***</passwordAnswer> tags!");
				}
				else
				{
					if (answer == null && question == null)
					{
						getResponse().entity(
								"Your passwordQestion and passwordAnswer fields are empty.\nYou cannot retrieve your password.");
					}
					else if ((answer == null && requestAnswer.compareTo("") == 0)
							|| (answer != null && answer.compareTo(requestAnswer) == 0))
					{
						//set new password
						user.setEncodedPassword(newEncPassword, "MD5");
						getResponse().entity(
								"Correct answer.\nNew password is: " + newRawPassword
										+ "\nChange this temporary password as soon as possible.");
					}
					else
					{
						getResponse().entity("Incorrect answer.\nTry again.");
					}
				}
			}
			else
			{
				getResponse().status(Response.Status.BAD_REQUEST);
				getResponse().entity("Resource identifier '" + userId + "' doesn't match uid '" + dto.getUid() + "'");
			}
		}
		catch (final UnknownIdentifierException e)
		{
			getResponse().status(Response.Status.NOT_FOUND);
			getResponse().entity("User not found.");
		}

		return getResponse().build();

	}

	/**
	 * Randomize password for user.
	 */
	public String getNewPassword()
	{
		final SecureRandom rand = new SecureRandom();
		String newPass = "";
		for (int ii = 0; ii < 8; ii++)
		{
			newPass += Integer.toString(rand.nextInt(9));
		}
		return newPass;

	}

	/**
	 * Encode(MD5) new password for current user.
	 * 
	 * @param userID
	 *           - userID
	 * @param newRawPassword
	 *           - new password
	 * @return encoded password
	 */
	public String encodePassword(final String userID, final String newRawPassword)
	{
		return encoder.encode(userID, newRawPassword);
	}

	/**
	 * Returns the user
	 * 
	 */
	@Override
	protected UserModel readResource(final String resourceId)
	{
		return serviceLocator.getUserService().getUserForUID(resourceId);
	}

	public void setEncoder(final SaltedMD5PasswordEncoder encoder)
	{
		this.encoder = encoder;
	}
}
