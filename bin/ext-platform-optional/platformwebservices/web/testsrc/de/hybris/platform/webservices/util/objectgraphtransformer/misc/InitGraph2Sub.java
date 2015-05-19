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



@GraphNode(target = InitGraph2Sub.class)
public class InitGraph2Sub extends InitGraph2Base
{
	// number1: override return type, add setter
	@Override
	public Integer getNumber1()
	{
		return null;
	}

	@Override
	public void setNumber1(final Number nmb)
	{
		//nop
	}

	// number2: override return type, does not touch setter
	@Override
	public Integer getNumber2()
	{
		return null;
	}

	//number3: add setter, property type: NUMBER
	public void setNumber3(@SuppressWarnings("unused") final Number nmb)
	{
		//nop
	}

	//number4: add getter
	public Number getNumber4()
	{
		return null;
	}

	//number5: add getter; new property type: Integer
	public Integer getNumber5()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.misc.InitGraph1Base#getNumber6()
	 */
	@Override
	public Integer getNumber6()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.util.objectgraphtransformer.misc.InitGraph1Base#setNumber6(java.lang.Number)
	 */
	@Override
	public void setNumber6(final Number nmb)
	{
		//nop
	}


}
