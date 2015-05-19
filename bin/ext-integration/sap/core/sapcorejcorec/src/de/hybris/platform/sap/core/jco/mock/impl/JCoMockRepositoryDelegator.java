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
/**
 * 
 */
package de.hybris.platform.sap.core.jco.mock.impl;

import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;

import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


/**
 * Delegator for JCoMockRepository to JCoRecRepository.
 */
class JCoMockRepositoryDelegator implements JCoMockRepository
{

	/**
	 * Delegator instance.
	 */
	private final RepositoryPlayback delegate;

	//	private final JCoRecRepository delegate;


	//	/**
	//	 * Standard delegator constructor.
	//	 * 
	//	 * @param delegate
	//	 *           delegator instance.
	//	 */
	//	protected JCoMockRepositoryDelegator(final JCoRecRepository delegate)
	//	{
	//		super();
	//		this.delegate = delegate;
	//	}

	/**
	 * Standard delegator constructor.
	 * 
	 * @param delegate
	 *           delegator instance.
	 */
	protected JCoMockRepositoryDelegator(final RepositoryPlayback delegate)
	{
		super();
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.jco.rec.JCoMockRepository#getRecord(java.lang.String)
	 */
	@Override
	public JCoRecord getRecord(final String key) throws JCoRecException
	{
		return delegate.getRecord(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.jco.rec.JCoMockRepository#getStructure(java.lang.String)
	 */
	@Override
	public JCoStructure getStructure(final String key) throws JCoRecException
	{
		return (JCoStructure) getRecord(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.jco.rec.JCoMockRepository#getTable(java.lang.String)
	 */
	@Override
	public JCoTable getTable(final String key) throws JCoRecException
	{
		return (JCoTable) getRecord(key);
	}

}
