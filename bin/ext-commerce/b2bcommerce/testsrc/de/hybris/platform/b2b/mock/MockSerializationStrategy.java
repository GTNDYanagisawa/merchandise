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
package de.hybris.platform.b2b.mock;

import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.strategies.SerializationStrategy;

import java.io.ObjectStreamException;

import org.junit.Ignore;

@Ignore
public class MockSerializationStrategy implements SerializationStrategy {
	@Override
	public Object writeReplace(final AbstractItemModel aim) throws ObjectStreamException {
		return aim;
	}

	@Override
	public Object readResolve(final AbstractItemModel aim) throws ObjectStreamException {
		return aim;
	}
}
