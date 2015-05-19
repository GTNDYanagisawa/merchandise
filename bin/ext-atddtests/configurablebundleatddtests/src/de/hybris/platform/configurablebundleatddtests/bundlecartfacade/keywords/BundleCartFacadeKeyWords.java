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

package de.hybris.platform.configurablebundleatddtests.bundlecartfacade.keywords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.atddengine.keywords.AbstractKeywordLibrary;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Robotframework library class for BundleCartFacade
 *
 */
public class BundleCartFacadeKeyWords extends AbstractKeywordLibrary
{

	private static final Logger LOG = Logger.getLogger(BundleCartFacadeKeyWords.class);	
	
	/**This constant value is used to represents default quantity of product added to the bundle. */
	private static final long DEFAULT_QUANTITY =1;
	
	/**This constant value is used to represents default bundle number (-1=create new bundle) */
	private static final int DEFAULT_BUNDLE_NUMBER =-1;
	
	/**This constant value is used to signal <code>BundleCartFacade</code> to remove the given product from the bundle*/
	//private static final boolean REMOVE_CURRENT_PRODUCT= true;
	
	/**This constant value is used to signal <code>BundleCartFacade</code> to add the given product to the bundle*/
	private static final boolean ADD_CURRENT_PRODUCT=false;
	
	@Autowired
    @Qualifier("defaultBundleCartFacade")        
	BundleCartFacade bundleCartFacade;
	
	/**
	 * Java implementation of the robot keyword <br>
	 * 
	 * <p>
	 * <i> add product "${productCode1}" for component "${bundleTemplateId1}" </i>
	 * <p>
	 * 
	 * @param productCode Product to add as a code
	 * @param bundleTemplateId Component as bundleTemplateId
	 */
	public void addProductToNewBundle(final String productCode, final String bundleTemplateId)
	{
		try
		{
			bundleCartFacade.addToCart(productCode, DEFAULT_QUANTITY, DEFAULT_BUNDLE_NUMBER, bundleTemplateId, ADD_CURRENT_PRODUCT);
							
		}
		catch(CommerceCartModificationException ex)
		{
			LOG.error("An exception occured while adding a product to a bundle", ex);
			fail(ex.getMessage());
		}
	}
	
	/**
	 * Java implementation of the robot keyword <br>
	 * 
	 * <p>
	 * <i> add product "${productCode1}" for component "${bundleTemplateId1}" to bundle "${bundleNo}" </i>
	 * <p>
	 * 
	 * @param productCode Product to add as a code
	 * @param bundleTemplateId Component as bundleTemplateId
	 * @param bundleNo Bundle number (-1=create new bundle; 0=standalone product/no	bundle; >0=number of existing bundle)
	 */
	public void addProductToExistingBundle(final String productCode, final String bundleTemplateId,final int bundleNo)
	{
		try
		{
			bundleCartFacade.addToCart(productCode, DEFAULT_QUANTITY, bundleNo, bundleTemplateId, ADD_CURRENT_PRODUCT);
							
		}
		catch(CommerceCartModificationException ex)
		{
			LOG.error("An exception occured while adding a product to a existing bundle : "+bundleNo, ex);
			fail(ex.getMessage());
		}
	}
	
	/**
	 * Java implementation of the robot keyword <br>
	 * 
	 * <p>
	 * <i> add product "${productCode1}" for component "${bundleTemplateId1}" and product "${productCode2}" for component "${bundleTemplateId2}" </i>
	 * <p>
	 * 
	 * @param productCode1 Product one to add as a code
	 * @param bundleTemplateId1 Component one  as bundleTemplateId
	 * @param productCode2 Product two to add as a code
	 * @param bundleTemplateId2 Component two as bundleTemplateId
	 */
	public void addTwoProductsToNewBundle(final String productCode1, final String bundleTemplateId1,final String productCode2, final String bundleTemplateId2)
	{
		try
		{
			bundleCartFacade.addToCart(productCode1, DEFAULT_BUNDLE_NUMBER, bundleTemplateId1, productCode2, bundleTemplateId2);			
			
		}
		catch(CommerceCartModificationException ex)
		{
			LOG.error("An exception occured while adding two products to a bundle", ex);
			fail(ex.getMessage());
		}
	}
	
	
	/**
	 * Java implementation of the robot keyword <br>
	 * 
	 * <p>
	 * <i> add product "${productCode1}" for component "${bundleTemplateId1}" and product "${productCode2}" for component "${bundleTemplateId2}" to bundle "${bundleNo}" </i>
	 * <p>
	 * 
	 * @param productCode1 Product one to add as a code	 * 
	 * @param bundleTemplateId1 Component one  as bundleTemplateId
	 * @param productCode2 Product one to add as a code
	 * @param bundleTemplateId2 Component two  as bundleTemplateId
	 * @param bundleNo Bundle number(-1=create new bundle; >0=number of existing	bundle; 0=standalone product/no bundle is not allowed here) 
	 */
	public void addTwoProductsToExistingBundle(final String productCode1, final String bundleTemplateId1,final String productCode2, final String bundleTemplateId2, final int bundleNo)
	{
		try
		{
			bundleCartFacade.addToCart(productCode1, bundleNo, bundleTemplateId1, productCode2, bundleTemplateId2);			
			
		}
		catch(CommerceCartModificationException ex)
		{
			LOG.error("An exception occured while adding two products to a existing bundle : "+bundleNo, ex);
			fail(ex.getMessage());
		}
	}
	
	/**
	 * Java implementation of the robot keyword <br>
	 * 
	 * <p>
	 * <i >delete bundle "${bundleNo}" from cart </i>
	 * <p>
	 * 
	 * @param bundleNo Bundle number to be deleted from the cart
	 */
	public void deleteCartBundle(final int bundleNo) 
	{
		try
		{
			bundleCartFacade.deleteCartBundle(bundleNo);			
		}
		catch(CommerceCartModificationException ex)
		{
			LOG.error("An exception occured while deleting a bundle from the cart", ex);
			fail(ex.getMessage());
		}
	}
	
	/**
	 * Java implementation of the robot keyword <br>
	 * 
	 * <p>
	 * <i> check the session cart with added bundle is valid </i>
	 * <p>
	 */
	public void verifySessionCartIsValid()
	{
		boolean value = bundleCartFacade.isCartValid();
		
		assertEquals("Current session cart status is not as expected: ",false, value);
	}
	
}
