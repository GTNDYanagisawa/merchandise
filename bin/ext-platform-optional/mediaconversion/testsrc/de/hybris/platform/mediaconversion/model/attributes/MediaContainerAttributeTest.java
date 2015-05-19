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
package de.hybris.platform.mediaconversion.model.attributes;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.enums.ConversionStatus;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author pohl
 */
@IntegrationTest
public class MediaContainerAttributeTest extends HybrisJUnit4TransactionalTest
{

	private final ModelService modelService = Registry.getApplicationContext().getBean("modelService", ModelService.class);

	// MECO-126 accessing master of an unsaved container
	@Test
	public void testGetMasterUnsaved()
	{
		this.runTest(false);
	}

	@Test
	public void testGetMasterAttribute()
	{
		this.runTest(true);
	}

	private void runTest(final boolean save)
	{
		final CatalogVersionModel catVers = this.someCatalogVersion();
		final MediaContainerModel container = this.modelService.create(MediaContainerModel.class);
		//        Assert.assertNull("Not saved.", container.getMaster()); --> throws IllegalStateException

		container.setQualifier("test_" + System.currentTimeMillis());
		container.setCatalogVersion(catVers);
		if (save)
		{
			this.modelService.save(container);
		}
		Assert.assertNull("Saved but empty.", container.getMaster());
		Assert.assertEquals("Container has conversion status empty.", ConversionStatus.EMPTY, container.getConversionStatus());


		final MediaModel master;
		{
			final MediaModel media = this.modelService.create(MediaModel.class);
			media.setCode(container.getQualifier() + "other");
			media.setCatalogVersion(catVers);
			media.setMediaFormat(this.someFormat());
			media.setMediaContainer(container);
			if (save)
			{
				Assert.assertNull("(not saved yet).", container.getMaster());
				this.modelService.save(media);
			}
			else
			{
				container.setMedias(new LinkedList<MediaModel>(Collections.singletonList(media)));
				Assert.assertEquals("Container set on unsaved media.", container, media.getMediaContainer());
			}
			Assert.assertEquals("Now we got a master.", media, container.getMaster());
			master = media;
		}

		Assert.assertEquals("Converted as there are no formats available.", ConversionStatus.CONVERTED,
				container.getConversionStatus());
		final ConversionMediaFormatModel format = this.someConversionFormat();
		Assert.assertEquals("Unconverted as we created a format.", ConversionStatus.UNCONVERTED, container.getConversionStatus());

		{
			final MediaModel media = this.modelService.create(MediaModel.class);
			media.setCode(container.getQualifier() + "second");
			media.setCatalogVersion(catVers);
			media.setMediaFormat(format);
			media.setMediaContainer(container);
			if (save)
			{
				this.modelService.save(media);
			}
			else
			{
				container.getMedias().add(media);
				Assert.assertEquals("Container set on unsaved media.", container, media.getMediaContainer());
			}
			Assert.assertNull("Master is gone, as there are two 'originals' now.", container.getMaster());
			media.setOriginal(master);
			if (save)
			{
				this.modelService.save(media);
			}
			Assert.assertEquals("Master is back again.", master, container.getMaster());
		}

		if (save)
		{
			this.modelService.remove(master);
		}
		else
		{
			container.getMedias().remove(master);
		}
		Assert.assertNull("removed master from container.", container.getMaster());
	}

	private CatalogVersionModel someCatalogVersion()
	{
		final CatalogModel cat = this.modelService.create(CatalogModel.class);
		cat.setId("test" + System.currentTimeMillis());
		this.modelService.save(cat);
		final CatalogVersionModel ret = this.modelService.create(CatalogVersionModel.class);
		ret.setCatalog(cat);
		ret.setVersion("1");
		this.modelService.save(ret);
		return ret;
	}

	private MediaFormatModel someFormat()
	{
		final MediaFormatModel ret = this.modelService.create(MediaFormatModel.class);
		ret.setName("test format");
		ret.setQualifier("test" + System.currentTimeMillis());
		this.modelService.save(ret);
		return ret;
	}

	private ConversionMediaFormatModel someConversionFormat()
	{
		final ConversionMediaFormatModel ret = this.modelService.create(ConversionMediaFormatModel.class);
		ret.setName("conversion format");
		ret.setQualifier("conversion" + System.currentTimeMillis());
		ret.setConversion("-monochrome");
		this.modelService.save(ret);
		return ret;
	}
}
