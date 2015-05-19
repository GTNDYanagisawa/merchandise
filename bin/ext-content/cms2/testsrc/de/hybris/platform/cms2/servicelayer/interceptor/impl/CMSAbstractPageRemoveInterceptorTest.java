package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

public class CMSAbstractPageRemoveInterceptorTest extends ServicelayerTest
{

    @Resource
    private ModelService modelService;

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Before
    public void setUp() throws Exception
    {
        createCoreData();
        createDefaultCatalog();
    }

    @Test
    public void testRemoveInterceptorMultiPage()
    {
        final CatalogVersionModel cv = createCatalogVersion();

        final PageTemplateModel pageTemplateModel = modelService.create(PageTemplateModel.class);
        pageTemplateModel.setCatalogVersion(cv);
        pageTemplateModel.setUid("testTemplate");

        final ContentSlotModel contentSlot = modelService.create(ContentSlotModel.class);
        contentSlot.setCatalogVersion(cv);
        contentSlot.setUid("sampleContentSlot");

        final ProductPageModel pageOne = createPage("testUidForPPM_PageOne", cv, pageTemplateModel);
        final ProductPageModel pageTwo = createPage("testUidForPPM_PageTwo", cv, pageTemplateModel);

        final ContentSlotForPageModel csForPageOne = modelService.create(ContentSlotForPageModel.class);

        csForPageOne.setPage(pageOne);
        csForPageOne.setContentSlot(contentSlot);
        csForPageOne.setCatalogVersion(cv);
        csForPageOne.setPosition("testPosition");

        final ContentSlotForPageModel csForPageTwo = modelService.create(ContentSlotForPageModel.class);

        csForPageTwo.setPage(pageTwo);
        csForPageTwo.setContentSlot(contentSlot);
        csForPageTwo.setCatalogVersion(cv);
        csForPageTwo.setPosition("testPosition");

        modelService.saveAll();

        modelService.remove(pageOne);
        Assertions.assertThat(modelService.isRemoved(contentSlot)).isFalse();

        modelService.remove(pageTwo);
        Assertions.assertThat(modelService.isRemoved(contentSlot)).isTrue();

    }

    private ProductPageModel createPage(String pageUid, CatalogVersionModel cv, final PageTemplateModel pageTemplateModel)
    {
        final ProductPageModel page = modelService.create(ProductPageModel.class);
        page.setCatalogVersion(cv);
        page.setUid(pageUid);
        page.setMasterTemplate(pageTemplateModel);
        return page;
    }

    private CatalogModel getCatalog()
    {
        final CatalogModel example = new CatalogModel();
        example.setId("testCatalog");
        return flexibleSearchService.getModelByExample(example);
    }

    private CatalogVersionModel createCatalogVersion()
    {
        final CatalogVersionModel catalogVersion = modelService.create(CatalogVersionModel.class);
        catalogVersion.setActive(Boolean.TRUE);
        catalogVersion.setVersion("Staged");
        catalogVersion.setCatalog(getCatalog());
        return catalogVersion;
    }
}
