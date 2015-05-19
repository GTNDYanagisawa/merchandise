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
package de.hybris.platform.acceleratorstorefrontcommons.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used in controller methods for which the following conditions will be evaluated to be true in
 * order for the request to proceeded:
 * 
 * # The request is secure. # The current user is not anonymous. # The GUID cookie token matches the value in the
 * session .
 * 
 * If any of this conditions are not met the request is redirected to the login page.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{ ElementType.METHOD, ElementType.TYPE })
public @interface RequireHardLogIn
{
	// empty 
}
