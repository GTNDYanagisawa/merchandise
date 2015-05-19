package de.hybris.platform.catalog.jalo.synchronization;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.synchronization.ServiceLayerSynchronizationPersistenceAdapter;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.user.Title;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

/**
 * Created by t0mus on 24.06.14.
 */

@IntegrationTest
public class ServiceLayerSynchronizationPersistenceAdapterTest extends ServicelayerBaseTest
{
    public static final String TEST_TITLE = "testTitle";
    public static final String TITLE = "Title";

    private ServiceLayerSynchronizationPersistenceAdapter persistenceAdapter;

    @Resource
    private TypeService typeService;

    @Resource
    private ModelService modelService;

    @Before
    public void setUp()
    {
        persistenceAdapter = new ServiceLayerSynchronizationPersistenceAdapter(null);
    }

    @Test
    public void shouldNotFailOnAnEmptyMapPassedToLocalizedAttribute()
    {
        Map<String,Object> attributes = new HashMap<String, Object>();
        attributes.put(Title.CODE,TEST_TITLE);
        attributes.put(Title.NAME, Collections.emptyMap());
        try{
            Title title = (Title) persistenceAdapter.create((ComposedType)modelService.getSource(typeService.getComposedType(TitleModel._TYPECODE)), attributes);
            assertEquals(TEST_TITLE,title.getCode());
            assertEquals(Collections.emptyMap(), title.getAllName());
        }catch(Exception e){
            e.printStackTrace();
            fail("Should not fail on an Collections.EmptyMap passed to localized attribute.");
        }
    }

    @Test
    public void shouldNotFailOnNullPassedToLocalizedAttribute()
    {
        Map<String,Object> attributes = new HashMap<String, Object>();
        attributes.put(Title.CODE,TEST_TITLE);
        attributes.put(Title.NAME, null);
        try{
            Title title = (Title) persistenceAdapter.create((ComposedType)modelService.getSource(typeService.getComposedType(TitleModel._TYPECODE)), attributes);
            assertEquals(TEST_TITLE,title.getCode());
            assertEquals(Collections.emptyMap(), title.getAllName());
        }catch(Exception e){
            e.printStackTrace();
            fail("Should not fail on null passed to localized attribute.");
        }
    }


}
