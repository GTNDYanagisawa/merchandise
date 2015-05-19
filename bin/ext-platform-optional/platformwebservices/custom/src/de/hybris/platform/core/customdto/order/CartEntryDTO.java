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
package de.hybris.platform.core.customdto.order;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.core.dto.product.UnitDTO;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.webservices.model.nodefactory.GenericNodeFactory;
import de.hybris.platform.webservices.objectgraphtransformer.ModifiedProperties;
import de.hybris.platform.webservices.objectgraphtransformer.PkToLongConverter;
import de.hybris.platform.webservices.objectgraphtransformer.UriPropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;


@GraphNode(target = CartEntryModel.class, factory = GenericNodeFactory.class, uidProperties = "pk")
@XmlRootElement(name = "cartentry")
public class CartEntryDTO implements ModifiedProperties
{
	protected final Set<String> modifiedPropsSet = new HashSet();

	// unique attributes
	private Long pk;
	private String uri;

	// primitive attributes
	private Double basePrice;
	private Integer entryNumber;
	private String info;
	private Long quantity;
	private Double totalPrice;
	private Boolean calculated;
	private Boolean giveAway;

	// references
	private ProductDTO product;
	private CartDTO order;

	/**
	 * @return the order
	 */
	public CartDTO getOrder()
	{
		return order;
	}

	/**
	 * @param order
	 *           the order to set
	 */
	public void setOrder(final CartDTO order)
	{
		this.modifiedPropsSet.add("order");
		this.order = order;
	}

	private UnitDTO unit;

	/* Still missing */
	//discountvalues
	//taxvalues

	@Override
	public Set<String> getModifiedProperties()
	{
		return this.modifiedPropsSet;
	}

	/**
	 * @return the basePrice
	 */
	public Double getBasePrice()
	{
		return basePrice;
	}

	/**
	 * @param basePrice
	 *           the basePrice to set
	 */
	public void setBasePrice(final Double basePrice)
	{
		this.modifiedPropsSet.add("basePrice");
		this.basePrice = basePrice;
	}

	/**
	 * @return the entryNumber
	 */
	public Integer getEntryNumber()
	{
		return entryNumber;
	}

	/**
	 * @param entryNumber
	 *           the entryNumber to set
	 */
	public void setEntryNumber(final Integer entryNumber)
	{
		this.modifiedPropsSet.add("entryNumber");
		this.entryNumber = entryNumber;
	}

	/**
	 * @return the info
	 */
	public String getInfo()
	{
		return info;
	}

	/**
	 * @param info
	 *           the info to set
	 */
	public void setInfo(final String info)
	{
		this.modifiedPropsSet.add("info");
		this.info = info;
	}

	/**
	 * @return the quantity
	 */
	public Long getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	public void setQuantity(final Long quantity)
	{
		this.modifiedPropsSet.add("quantity");
		this.quantity = quantity;
	}

	/**
	 * @return the totalPrice
	 */
	public Double getTotalPrice()
	{
		return totalPrice;
	}

	/**
	 * @param totalPrice
	 *           the totalPrice to set
	 */
	public void setTotalPrice(final Double totalPrice)
	{
		this.modifiedPropsSet.add("totalPrice");
		this.totalPrice = totalPrice;
	}

	/**
	 * @return the calculated
	 */
	public Boolean getCalculated()
	{
		return calculated;
	}

	/**
	 * @param calculated
	 *           the calculated to set
	 */
	public void setCalculated(final Boolean calculated)
	{
		this.modifiedPropsSet.add("calculated");
		this.calculated = calculated;
	}

	/**
	 * @return the giveAway
	 */
	public Boolean getGiveAway()
	{
		return giveAway;
	}

	/**
	 * @param giveAway
	 *           the giveAway to set
	 */
	public void setGiveAway(final Boolean giveAway)
	{
		this.modifiedPropsSet.add("giveAway");
		this.giveAway = giveAway;
	}

	@XmlAttribute
	public Long getPk()
	{
		return pk;
	}

	@GraphProperty(interceptor = PkToLongConverter.class)
	public void setPk(final Long pk)
	{
		this.modifiedPropsSet.add("pk");
		this.pk = pk;
	}

	/**
	 * @return the uri
	 */
	@XmlAttribute
	public String getUri()
	{
		return uri;
	}

	/**
	 * @param uri
	 *           the uri to set
	 */
	@GraphProperty(virtual = true, interceptor = UriPropertyInterceptor.class)
	public void setUri(final String uri)
	{
		this.modifiedPropsSet.add("uri");
		this.uri = uri;
	}

	/**
	 * @return the product
	 */
	public ProductDTO getProduct()
	{
		return product;
	}

	/**
	 * @param product
	 *           the product to set
	 */
	public void setProduct(final ProductDTO product)
	{
		this.modifiedPropsSet.add("product");
		this.product = product;
	}

	/**
	 * @return the unit
	 */
	public UnitDTO getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	public void setUnit(final UnitDTO unit)
	{
		this.modifiedPropsSet.add("unit");
		this.unit = unit;
	}
}
