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
package de.hybris.platform.webservices.util.objectgraphtransformer.misc;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


@GraphNode(target = TxCollectionModel.class)
public class TxCollectionDTO
{
	// List->Set  
	private List<String> users1 = null;

	// ArrayList->LinkedList 
	private ArrayList<String> users2 = null;

	// HashSet->ArrayList
	private HashSet<String> users3 = null;

	// Collection->List
	private Collection<String> users4 = null;

	// Collection->Collection
	private Collection<String> users5 = null;

	// Collection [no constructor]->Collection
	private Collection<String> users6 = null;

	private ArrayList<String> users7 = null;


	/**
	 * @return the users7
	 */
	public ArrayList<String> getUsers7()
	{
		return users7;
	}

	/**
	 * @param users7
	 *           the users7 to set
	 */
	public void setUsers7(final ArrayList<String> users7)
	{
		this.users7 = users7;
	}

	public Collection<String> getUsers6()
	{
		return users6;
	}

	public void setUsers6(final Collection<String> users6)
	{
		this.users6 = users6;
	}

	public void setUsers1(final List<String> users1)
	{
		this.users1 = users1;
	}

	public void setUsers2(final ArrayList<String> users2)
	{
		this.users2 = users2;
	}

	public void setUsers3(final HashSet<String> users3)
	{
		this.users3 = users3;
	}

	public void setUsers4(final Collection<String> users4)
	{
		this.users4 = users4;
	}

	public void setUsers5(final Collection<String> users5)
	{
		this.users5 = users5;
	}

	public List<String> getUsers1()
	{
		return users1;
	}

	public ArrayList<String> getUsers2()
	{
		return users2;
	}

	public HashSet<String> getUsers3()
	{
		return users3;
	}

	public Collection<String> getUsers4()
	{
		return users4;
	}

	public Collection<String> getUsers5()
	{
		return users5;
	}


}
