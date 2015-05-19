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
package de.hybris.platform.mediaconversion.os.process;

import java.util.LinkedList;
import java.util.List;


/**
 * @author pohl
 */
final class EchoCommandFactory
{

	private EchoCommandFactory()
	{
		// do not instantiate
	}

	static String[] buildCommand(final String... message)
	{
		final List<String> ret = new LinkedList<String>();
		if (System.getProperty("os.name").toLowerCase().startsWith("win"))
		{
			// OS-2 ( <- that's the bug id not the operating system):
			// on windows 'echo' is not an executable, but a command build in to
			// the command prompt. So we have to actually start a command prompt...
			ret.add("cmd");
			ret.add("/C");
		}
		ret.add("echo");

		if (message != null)
		{
			for (final String msg : message)
			{
				ret.add(msg);
			}
		}
		return ret.toArray(new String[ret.size()]);
	}

}
