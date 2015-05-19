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
package de.hybris.platform.webservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Just a standard implementation for {@link CommandHandler}
 */
public class DefaultCommandHandler implements CommandHandler
{
	private Map<String, Command> cmdMap = null;

	public DefaultCommandHandler()
	{
		cmdMap = new HashMap<String, Command>();
	}

	@Override
	public void addCommand(final Command command)
	{
		cmdMap.put(command.getName(), command);
	}

	@Override
	public Command getCommand(final String name)
	{
		return cmdMap.get(name);
	}


	@Override
	public void setAllCommands(final List<Command> commands)
	{
		for (final Command cmd : commands)
		{
			this.addCommand(cmd);
		}
	}


}
