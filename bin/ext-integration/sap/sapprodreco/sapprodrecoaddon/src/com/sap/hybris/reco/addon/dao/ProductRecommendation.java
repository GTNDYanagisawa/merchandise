/**
 * 
 */
package com.sap.hybris.reco.addon.dao;

import de.hybris.platform.core.model.product.ProductModel;


/**
 * @author Administrator
 * 
 */
public class ProductRecommendation
{


	private ProductModel product;


	public ProductModel getProduct()
	{
		return product;
	}

	/**
	 * @param product
	 *           the product to set
	 */
	public void setProduct(final ProductModel product)
	{
		this.product = product;
	}

}
