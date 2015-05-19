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
package de.hybris.platform.secureportaladdon.tests.util;

import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;


public class B2BSecurePortalTestsUtil
{
	/*
	 * returns a String containing an impex file content
	 */

	public static String impexFileToString(final String file) throws Exception
	{
		String impexContent = null;
		InputStream inputStream = null;

		try
		{
			inputStream = ServicelayerTest.class.getResourceAsStream(file);
			impexContent = IOUtils.toString(inputStream);
		}
		finally
		{
			inputStream.close();
		}

		return impexContent;
	}


	/*
	 * 
	 * returns a list of employee's uids belonging to userGroup and present in an impex file
	 */
	public static List<String> getEmployeesUidsFromImpex(final String impexContent, final String userGroup, final int uidIndex,
			final int userGroupIndex)
	{

		final List<String> list = new ArrayList<String>();

		final String[] lines = impexContent.split("\n");

		int index = 0;

		while (!lines[index].trim().startsWith("INSERT_UPDATE Employee"))
		{
			index++;
		}

		while (++index < lines.length && lines[index].trim().startsWith(";"))
		{
			final String[] lineTockens = lines[index].split(";");
			if (userGroup.equals(lineTockens[userGroupIndex])) //Employee should be from the right userGroup
			{
				list.add(lineTockens[uidIndex]);
			}
		}

		return list;
	}


	/*
	 * Returns a list of uids of employees received as a parameter
	 */
	public static List<String> employeesToUids(final List<EmployeeModel> employees)
	{
		final List<String> uids = new ArrayList<String>();

		for (final EmployeeModel employee : employees)
		{
			uids.add(employee.getUid());
		}

		return uids;
	}


	/*
	 * Return a list of userGroups in an impex file
	 */
	public static List<String> getUserGroupsFromImpex(final String impexContent, final int uidIndex)
	{

		final List<String> list = new ArrayList<String>();

		final String[] lines = impexContent.split("\n");

		int index = 0;

		while (!lines[index].trim().startsWith("INSERT_UPDATE UserGroup"))
		{
			index++;
		}

		while (++index < lines.length && lines[index].trim().startsWith(";"))
		{
			final String[] lineTockens = lines[index].split(";");
			list.add(lineTockens[uidIndex]);

		}

		return list;
	}

	/*
	 * Returns a list of active emails (isContactAddress = true) or inactive emails (isContactAddress = true) present in
	 * an impex file and belonging to an Employee from listUids. isContactAddress parameter makes reference to attribute
	 * _contactAddress in class AddressModel
	 */
	public static List<String> getEmailsFromImpex(final String impexContent, final List<String> listUids,
			final int isContactAddressIndex, final int uidIndex, final int emailIndex, final boolean isContactAddress)
	{

		final List<String> list = new ArrayList<String>();

		final String[] lines = impexContent.split("\n");

		final String contactAddressType = isContactAddress ? "true" : "false";

		int index = 0;

		while (!lines[index].trim().startsWith("INSERT_UPDATE Address"))
		{
			index++;

		}

		while (++index < lines.length && lines[index].trim().startsWith(";"))
		{
			final String[] lineTockens = lines[index].split(";");

			if (contactAddressType.equals(lineTockens[isContactAddressIndex].trim()) && listUids.contains(lineTockens[uidIndex]))
			{
				list.add(lineTockens[emailIndex]);
			}
		}

		return list;
	}

	/*
	 * Returns a list of emails extracted from a list of addresses received as a parameter
	 */
	public static List<String> emailAddressModelsToEmails(final List<EmailAddressModel> addresses)
	{
		final List<String> emails = new ArrayList<String>();

		for (final EmailAddressModel address : addresses)
		{
			emails.add(address.getEmailAddress());
		}

		return emails;
	}


	/*
	 * Compare 2 lists of string
	 */
	public static boolean compareLists(final List<String> listA, final List<String> listB)
	{
		return listA.size() == listB.size() && CollectionUtils.intersection(listA, listB).size() == listA.size();
	}

}
