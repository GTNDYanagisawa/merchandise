package com.hybris.backoffice.cockpitng.dataaccess.facades.permissions.custom.impl;


import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.model.ModelService;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class LanguageInstancePermissionAdvisorTest
{
    @InjectMocks
    private final LanguageInstancePermissionAdvisor advisor = new LanguageInstancePermissionAdvisor();

    @Mock
    private ItemModelContext context;

    @Mock
    private ModelService modelService;

    @Mock
    private LanguageModel language;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        Mockito.when(language.getItemModelContext()).thenReturn(context);
    }

    @Test
    public void testCanModify()
    {
        Assertions.assertThat(advisor.canModify(new LanguageModel())).isTrue();
        Assertions.assertThat(advisor.canModify(new LanguageModel()
        {
            // any type that inherits from Language shouldn't be rejected
        })).isTrue();
        Mockito.verifyZeroInteractions(language);
        Mockito.verifyZeroInteractions(context);
    }

    @Test
    public void canDeleteNewItem()
    {
        Mockito.when(modelService.isNew(language)).thenReturn(true);
        Mockito.when(language.getActive()).thenReturn(Boolean.FALSE);
        Assertions.assertThat(advisor.canDelete(language)).isTrue();
        Mockito.when(language.getActive()).thenReturn(Boolean.TRUE);
        Assertions.assertThat(advisor.canDelete(language)).isFalse();
        Mockito.verifyZeroInteractions(context);
    }

    @Test
    public void canDeletePersistedItem()
    {
        Mockito.when(modelService.isNew(language)).thenReturn(false);

        Mockito.when(context.isDirty(LanguageModel.ACTIVE)).thenReturn(false);
        Mockito.when(language.getActive()).thenReturn(Boolean.FALSE);
        Assertions.assertThat(advisor.canDelete(language)).isTrue();
        Mockito.when(language.getActive()).thenReturn(Boolean.TRUE);
        Assertions.assertThat(advisor.canDelete(language)).isFalse();
        Mockito.verify(context, Mockito.times(2)).isDirty(LanguageModel.ACTIVE);

        Mockito.when(context.isDirty(LanguageModel.ACTIVE)).thenReturn(true);

        Mockito.when(context.getOriginalValue(LanguageModel.ACTIVE)).thenReturn(true);
        Mockito.when(language.getActive()).thenReturn(Boolean.FALSE);
        Assertions.assertThat(advisor.canDelete(language)).isFalse();
        Mockito.when(context.getOriginalValue(LanguageModel.ACTIVE)).thenReturn(false);
        Mockito.when(language.getActive()).thenReturn(Boolean.TRUE);
        Assertions.assertThat(advisor.canDelete(language)).isTrue();
        Mockito.verify(context, Mockito.times(4)).isDirty(LanguageModel.ACTIVE);
        Mockito.verify(context, Mockito.times(2)).getOriginalValue(LanguageModel.ACTIVE);
        Mockito.verifyNoMoreInteractions(context);

    }

    @Test
    public void isApplicableTo()
    {
        Assertions.assertThat(advisor.isApplicableTo(new LanguageModel())).isTrue();
        Assertions.assertThat(advisor.isApplicableTo(new LanguageModel()
        {
            // any type that inherits from Language should be also accepted
        })).isTrue();
        Assertions.assertThat(advisor.isApplicableTo(new ProductModel())).isFalse();
        Mockito.verifyZeroInteractions(language);
        Mockito.verifyZeroInteractions(context);
    }

}
