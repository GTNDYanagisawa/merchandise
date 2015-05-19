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
package de.hybris.platform.core.customdto.media;

import de.hybris.platform.catalog.dto.CatalogVersionDTO;
import de.hybris.platform.category.dto.CategoryDTO;
import de.hybris.platform.core.dto.ItemDTO;
import de.hybris.platform.core.dto.media.MediaContainerDTO;
import de.hybris.platform.core.dto.media.MediaFolderDTO;
import de.hybris.platform.core.dto.media.MediaFormatDTO;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.webservices.model.nodefactory.GenericNodeFactory;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * NOT GENERATED DTO.
 * <p>
 * - XmlAttribute annotation for downloadURL
 */
@GraphNode(target = MediaModel.class, factory = GenericNodeFactory.class, uidProperties = "code")
@XmlRootElement(name = "media")
@SuppressWarnings("PMD")
public class MediaDTO extends ItemDTO
{
	/** <i>Generated variable</i> - Variable of <code>Media.mime</code> attribute defined at extension <code>core</code>. */
	private String mime;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.downloadURL</code> attribute defined at extension
	 * <code>core</code>.
	 */
	private String downloadURL;

	/** <i>Generated variable</i> - Variable of <code>Media.URL2</code> attribute defined at extension <code>core</code>. */
	private String url2;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.folder</code> attribute defined at extension <code>core</code>
	 * .
	 */
	private MediaFolderDTO folder;

	/** <i>Generated variable</i> - Variable of <code>Media.URL</code> attribute defined at extension <code>core</code>. */
	private String url;

	/** <i>Generated variable</i> - Variable of <code>Media.code</code> attribute defined at extension <code>core</code>. */
	private String code;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.description</code> attribute defined at extension
	 * <code>core</code>.
	 */
	private String description;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.realfilename</code> attribute defined at extension
	 * <code>core</code>.
	 */
	private String realfilename;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.altText</code> attribute defined at extension
	 * <code>core</code>.
	 */
	private String altText;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.catalogVersion</code> attribute defined at extension
	 * <code>catalog</code>.
	 */
	private CatalogVersionDTO catalogVersion;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.dataPK</code> attribute defined at extension <code>core</code>
	 * .
	 */
	private Long dataPK;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.foreignDataOwners</code> attribute defined at extension
	 * <code>core</code>.
	 */
	private List<MediaDTO> foreignDataOwners;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.supercategories</code> attribute defined at extension
	 * <code>category</code>.
	 */
	private List<CategoryDTO> supercategories;

	/** <i>Generated variable</i> - Variable of <code>Media.size</code> attribute defined at extension <code>core</code>. */
	private Long size;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.mediaContainer</code> attribute defined at extension
	 * <code>core</code>.
	 */
	private MediaContainerDTO mediaContainer;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.removable</code> attribute defined at extension
	 * <code>core</code>.
	 */
	private Boolean removable;

	/**
	 * <i>Generated variable</i> - Variable of <code>Media.mediaFormat</code> attribute defined at extension
	 * <code>core</code>.
	 */
	private MediaFormatDTO mediaFormat;


	/**
	 * <i>Generated constructor</i> - for generic creation.
	 */
	public MediaDTO()
	{
		super();

	}


	public String getAltText()
	{
		return this.altText;
	}

	public CatalogVersionDTO getCatalogVersion()
	{
		return this.catalogVersion;
	}

	@XmlAttribute
	public String getCode()
	{
		return this.code;
	}

	public Long getDataPK()
	{
		return this.dataPK;
	}

	public String getDescription()
	{
		return this.description;
	}

	@XmlAttribute
	public String getDownloadURL()
	{
		return this.downloadURL;
	}

	public MediaFolderDTO getFolder()
	{
		return this.folder;
	}

	@XmlElementWrapper(name = "foreigndataowners")
	@XmlElement(name = "media")
	public List<MediaDTO> getForeignDataOwners()
	{
		return this.foreignDataOwners;
	}

	public MediaContainerDTO getMediaContainer()
	{
		return this.mediaContainer;
	}

	public MediaFormatDTO getMediaFormat()
	{
		return this.mediaFormat;
	}

	public String getMime()
	{
		return this.mime;
	}

	public String getRealfilename()
	{
		return this.realfilename;
	}

	public Boolean getRemovable()
	{
		return this.removable;
	}

	public Long getSize()
	{
		return this.size;
	}

	@XmlElementWrapper(name = "supercategories")
	@XmlElement(name = "category")
	public List<CategoryDTO> getSupercategories()
	{
		return this.supercategories;
	}

	public String getURL()
	{
		return this.url;
	}

	public String getURL2()
	{
		return this.url2;
	}

	public void setAltText(final String value)
	{
		this.modifiedPropsSet.add("altText");
		this.altText = value;
	}

	public void setCatalogVersion(final CatalogVersionDTO value)
	{
		this.modifiedPropsSet.add("catalogVersion");
		this.catalogVersion = value;
	}

	public void setCode(final String value)
	{
		this.modifiedPropsSet.add("code");
		this.code = value;
	}

	public void setDataPK(final Long value)
	{
		this.modifiedPropsSet.add("dataPK");
		this.dataPK = value;
	}

	public void setDescription(final String value)
	{
		this.modifiedPropsSet.add("description");
		this.description = value;
	}

	public void setDownloadURL(final String value)
	{
		this.modifiedPropsSet.add("downloadURL");
		this.downloadURL = value;
	}

	public void setFolder(final MediaFolderDTO value)
	{
		this.modifiedPropsSet.add("folder");
		this.folder = value;
	}

	public void setForeignDataOwners(final List<MediaDTO> value)
	{
		this.modifiedPropsSet.add("foreignDataOwners");
		this.foreignDataOwners = value;
	}

	public void setMediaContainer(final MediaContainerDTO value)
	{
		this.modifiedPropsSet.add("mediaContainer");
		this.mediaContainer = value;
	}

	public void setMediaFormat(final MediaFormatDTO value)
	{
		this.modifiedPropsSet.add("mediaFormat");
		this.mediaFormat = value;
	}

	public void setMime(final String value)
	{
		this.modifiedPropsSet.add("mime");
		this.mime = value;
	}

	public void setRealfilename(final String value)
	{
		this.modifiedPropsSet.add("realfilename");
		this.realfilename = value;
	}

	public void setRemovable(final Boolean value)
	{
		this.modifiedPropsSet.add("removable");
		this.removable = value;
	}

	public void setSize(final Long value)
	{
		this.modifiedPropsSet.add("size");
		this.size = value;
	}

	public void setSupercategories(final List<CategoryDTO> value)
	{
		this.modifiedPropsSet.add("supercategories");
		this.supercategories = value;
	}

	public void setURL(final String value)
	{
		this.modifiedPropsSet.add("URL");
		this.url = value;
	}

	public void setURL2(final String value)
	{
		this.modifiedPropsSet.add("URL2");
		this.url2 = value;
	}

}
