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
package de.hybris.platform.acceleratorservices.dataexport.googlelocal.model;

import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFile;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFileMethod;


@DelimitedFile(delimiter = "\t")
public class Product
{
	//required
	private String itemid; //unique across all stores
	private String title;

	//recommended
	private String webItemId; //This should be the 'id' of the corresponding variant in the online product listings, if one exists. If this is not possible, please see GTIN, MPN or Brand below.
	private String gtin; //Global Trade Item Number (GTIN) of the item (e.g., UPC, EAN, ISBN, JAN)
	private String mpn; //Manufacturer Part Number (MPN) of the item
	private String brand; //Brand of the item
	private String price; //National default price of the item. Google assumes the currency based on the country of the store location.
	private String condition; //There are only three accepted values: 'new', 'used' and 'refurbished'.

	private String description;

	//googleProductCategory is only applicable to a small subset of categories. should not replace the 'product type' attribute. accepts only one value.
	//Required for all items that belong to the 'Apparel and Accessories', 'Media', and 'Software' categories, and are in feeds which target the US, UK, Germany, France, or Japan.
	//If your products do not fall into one of the categories listed below, or if your feed doesn't target one of the required countries, this attribute is recommended but not required. If you choose not to provide a Google Product Category value for other product categories, leave the attribute blank.
	private String googleProductCategory;
	private String productType; //Your category of the item
	private String link; //URL directly linking to your item's page on your website
	private String imageLink; //URL of an image of the item - recommended instead of required.
	private String additionalImageLink; //You can include up to 10 additional images per item by including the attribute multiple times. For tab-delimited, separate each URL by a comma. For XML, include each URL as a separate <additional_image_link> attribute
	private String itemGroupId; //Shared identifier for all variants of the same product
	private String color;
	private String material;
	private String pattern;
	private String size;
	private String gender; //The only three accepted values are: Male, Female, Unisex
	private String ageGroup; //The only two accepted values of this attribute are: Adult, Kids

	public String getItemid()
	{
		return itemid;
	}

	@DelimitedFileMethod(position = 1)
	public void setItemid(final String itemid)
	{
		this.itemid = itemid;
	}

	public String getTitle()
	{
		return title;
	}

	@DelimitedFileMethod(position = 2)
	public void setTitle(final String title)
	{
		this.title = title;
	}

	public String getWebItemId()
	{
		return webItemId;
	}

	@DelimitedFileMethod(position = 3, name = "webitemid")
	public void setWebItemId(final String webItemId)
	{
		this.webItemId = webItemId;
	}

	public String getGtin()
	{
		return gtin;
	}

	@DelimitedFileMethod(position = 4)
	public void setGtin(final String gtin)
	{
		this.gtin = gtin;
	}

	public String getMpn()
	{
		return mpn;
	}

	@DelimitedFileMethod(position = 5)
	public void setMpn(final String mpn)
	{
		this.mpn = mpn;
	}

	public String getBrand()
	{
		return brand;
	}

	@DelimitedFileMethod(position = 6)
	public void setBrand(final String brand)
	{
		this.brand = brand;
	}

	public String getPrice()
	{
		return price;
	}

	@DelimitedFileMethod(position = 7)
	public void setPrice(final String price)
	{
		this.price = price;
	}

	public String getCondition()
	{
		return condition;
	}

	@DelimitedFileMethod(position = 8)
	public void setCondition(final String condition)
	{
		this.condition = condition;
	}

	public String getDescription()
	{
		return description;
	}

	@DelimitedFileMethod(position = 9)
	public void setDescription(final String description)
	{
		this.description = description;
	}

	public String getGoogleProductCategory()
	{
		return googleProductCategory;
	}

	@DelimitedFileMethod(position = 10, name = "google_product_category")
	public void setGoogleProductCategory(final String googleProductCategory)
	{
		this.googleProductCategory = googleProductCategory;
	}

	public String getProductType()
	{
		return productType;
	}

	@DelimitedFileMethod(position = 11, name = "product_type")
	public void setProductType(final String productType)
	{
		this.productType = productType;
	}

	public String getLink()
	{
		return link;
	}

	@DelimitedFileMethod(position = 12)
	public void setLink(final String link)
	{
		this.link = link;
	}

	public String getImageLink()
	{
		return imageLink;
	}

	@DelimitedFileMethod(position = 13, name = "image_link")
	public void setImageLink(final String imageLink)
	{
		this.imageLink = imageLink;
	}

	public String getAdditionalImageLink()
	{
		return additionalImageLink;
	}

	@DelimitedFileMethod(position = 14, name = "additional_image_link")
	public void setAdditionalImageLink(final String additionalImageLink)
	{
		this.additionalImageLink = additionalImageLink;
	}

	public String getItemGroupId()
	{
		return itemGroupId;
	}

	@DelimitedFileMethod(position = 15, name = "item_group_id")
	public void setItemGroupId(final String itemGroupId)
	{
		this.itemGroupId = itemGroupId;
	}

	public String getColor()
	{
		return color;
	}

	@DelimitedFileMethod(position = 16)
	public void setColor(final String color)
	{
		this.color = color;
	}

	public String getMaterial()
	{
		return material;
	}

	@DelimitedFileMethod(position = 17)
	public void setMaterial(final String material)
	{
		this.material = material;
	}

	public String getPattern()
	{
		return pattern;
	}

	@DelimitedFileMethod(position = 18)
	public void setPattern(final String pattern)
	{
		this.pattern = pattern;
	}

	public String getSize()
	{
		return size;
	}

	@DelimitedFileMethod(position = 19)
	public void setSize(final String size)
	{
		this.size = size;
	}

	public String getGender()
	{
		return gender;
	}

	@DelimitedFileMethod(position = 20)
	public void setGender(final String gender)
	{
		this.gender = gender;
	}

	public String getAgeGroup()
	{
		return ageGroup;
	}

	@DelimitedFileMethod(position = 21, name = "age_group")
	public void setAgeGroup(final String ageGroup)
	{
		this.ageGroup = ageGroup;
	}
}
