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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class TxCollectionModel
{
	// List->Set  
	private Set<String> users1 = null;

	// ArrayList->LinkedList 
	private LinkedList<String> users2 = null;

	// HashSet->ArrayList
	private ArrayList<String> users3 = null;

	// Collection->List
	private List<String> users4 = null;

	// Collection->Collection
	private Collection<String> users5 = null;

	// Collection [no constructor]->Collection
	private Collection<String> users6 = null;

	// ArrayList -> ArrayList
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

	public void setUsers1(final Set<String> users1)
	{
		this.users1 = users1;
	}

	public void setUsers2(final LinkedList<String> users2)
	{
		this.users2 = users2;
	}

	public void setUsers3(final ArrayList<String> users3)
	{
		this.users3 = users3;
	}

	public void setUsers4(final List<String> users4)
	{
		this.users4 = users4;
	}

	public void setUsers5(final Collection<String> users5)
	{
		this.users5 = users5;
	}

	public Set<String> getUsers1()
	{
		return users1;
	}

	public LinkedList<String> getUsers2()
	{
		return users2;
	}

	public ArrayList<String> getUsers3()
	{
		return users3;
	}

	public List<String> getUsers4()
	{
		return users4;
	}

	public Collection<String> getUsers5()
	{
		return users5;
	}


}
