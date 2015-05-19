package de.hybris.platform.cockpit.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.zkoss.zk.ui.HtmlBasedComponent;


public class UIToolsTest {

    @Mock
    private HtmlBasedComponent component;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testModifySClass()
    {
        Mockito.when(this.component.getSclass()).thenReturn("");
        UITools.modifySClass(this.component, "someClass", true);

        Mockito.when(this.component.getSclass()).thenReturn("someClass");
        UITools.modifySClass(this.component, "someClass", true);

        Mockito.verify(this.component, Mockito.times(2)).setSclass("someClass");

        UITools.modifySClass(this.component, "someClass", false);
        Mockito.verify(this.component).setSclass(null);
    }

    @Test
    public void testModifySClassEmptyInput()
    {
        UITools.modifySClass(this.component, "   ", false);
        UITools.modifySClass(this.component, "", false);
        UITools.modifySClass(this.component, null, false);
        UITools.modifySClass(this.component, "   ", true);
        UITools.modifySClass(this.component, "", true);
        UITools.modifySClass(this.component, null, true);
        Mockito.verifyNoMoreInteractions(this.component);
    }

    @Test
    public void testModifySClassMultipleOccurences()
    {
        Mockito.when(this.component.getSclass()).thenReturn("someClass someClass");
        UITools.modifySClass(this.component, "someClass", true);
        Mockito.verify(this.component).setSclass("someClass");

        UITools.modifySClass(this.component, "someClass", false);
        Mockito.verify(this.component).setSclass(null);
    }

    @Test
    public void testModifySClassSamePrefix()
    {
        Mockito.when(this.component.getSclass()).thenReturn("someClass2");
        UITools.modifySClass(this.component, "someClass", true);
        Mockito.verify(this.component).setSclass("someClass2 someClass");

        Mockito.when(this.component.getSclass()).thenReturn("someClass2 someClass");
        UITools.modifySClass(this.component, "someClass", false);
        Mockito.verify(this.component).setSclass("someClass2");
    }

}
