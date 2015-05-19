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
package de.hybris.platform.sap.sapcommonbol.transaction.util.impl;


/**
 * Helper class in order to have a nice output of fields, e.g. for toString()
 *
 * @version 1.0
 */

public class PrettyPrinter {
    StringBuilder output;

	/**
	 * Constructor
	 * 
	 * @param start
	 */
	public PrettyPrinter(final String start)
	{
        output = new StringBuilder(start);
    }

    PrettyPrinter(final StringBuilder output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return output.toString();
    }

	/**
	 * Appends the fieldName and the value to the string builder
	 * 
	 * @param o
	 * @param fieldName
	 */
	public void add(final Object o, final String fieldName)
	{
        if (o != null)
            doAppend(o, fieldName);
    }

    private void doAppend(final Object o, final String fieldName) {
        output.append("\n" + fieldName + "=[" + o + "]");
    }
}
