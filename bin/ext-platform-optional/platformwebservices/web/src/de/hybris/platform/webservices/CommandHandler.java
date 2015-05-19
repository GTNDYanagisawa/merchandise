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

import java.util.List;


/**
 * Manages a collection of resource {@link Command} instances.
 */
public interface CommandHandler
{

	/**
	 * Returns a {@link Command} by it's name or 'null' when nothing is found.
	 * @param name
	 *           name of {@link Command}
	 * @return {@link Command}
	 */
	Command getCommand(String name);

	/**
	 * Sets a list of commands.
	 * @param commands
	 *           List of commands
	 */
	void setAllCommands(List<Command> commands);

	/**
	 * Adds a single command.
	 * @param command
	 *           command to add.
	 */
	void addCommand(Command command);


}
