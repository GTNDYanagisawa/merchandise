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
 */

package com.hybris.backoffice.jalo;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.Employee;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.hybris.backoffice.cockpitng.dataaccess.facades.object.savedvalues.ItemModificationHistoryService;
import com.hybris.backoffice.cockpitng.editorarea.BackofficeEditorAreaRenderer;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.EditorArea;
import com.hybris.cockpitng.core.impl.DefaultWidgetModel;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.core.model.impl.DefaultModelValueHandler;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.editorarea.renderer.EditorAreaRendererUtils;


public class JaloPropertyNotVisibleInModelAccessorReadWriteTest
{
	@Mock
	private ModelService modelService;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private ItemModificationHistoryService itemModificationHistoryService;

	@InjectMocks
	final private JaloPropertyNotVisibleInModelAccessor jaloPropertyNotVisibleInModelAccessor = new JaloPropertyNotVisibleInModelAccessor();
	@InjectMocks
	final private JaloPersistanceHandler jaloPersistanceHandler = new JaloPersistanceHandler();
	@Spy
	final private BackofficeEditorAreaRenderer backofficeEditorAreaRenderer = new BackofficeEditorAreaRenderer();

	private ProductModel productModel1;
	private ProductModel productModel2;
	private EmployeeModel employee1;
	private Employee jaloEmployee1;

	protected static final String ATTRIBUTE_REFERENCE_TO_EMPLOYEE = "referenceToEmployee";
	protected static final String ATTRIBUTE_REFERENCE_TO_STRING = "referenceToString";
	protected static final String ATTRIBUTE_REFERENCE_TO_COLLECTION_STRING = "referenceToCollectionString";
	protected static final String ATTRIBUTE_REFERENCE_TO_LIST_EMPLOYEE = "referenceToListEmployee";
	protected static final String ATTRIBUTE_REFERENCE_TO_SET_EMPLOYEE = "referenceToSetEmployee";

	protected Map<String, Object> values = new HashMap<>();

	protected Map<String, Set<Class>> getDefaultSupportedJaloAttributes()
	{
		final Map<String, Set<Class>> supportedJaloAttributes = new HashMap<>();
		final Set<Class> set = new HashSet<>();
		set.add(ProductModel.class);
		supportedJaloAttributes.put(ATTRIBUTE_REFERENCE_TO_EMPLOYEE, set);
		supportedJaloAttributes.put(ATTRIBUTE_REFERENCE_TO_STRING, set);
		supportedJaloAttributes.put(ATTRIBUTE_REFERENCE_TO_COLLECTION_STRING, set);
		supportedJaloAttributes.put(ATTRIBUTE_REFERENCE_TO_LIST_EMPLOYEE, set);
		supportedJaloAttributes.put(ATTRIBUTE_REFERENCE_TO_SET_EMPLOYEE, set);
		return supportedJaloAttributes;
	}

	protected WidgetModel getWidgetModel()
	{
		final WidgetModel widgetModel = new DefaultWidgetModel(new HashMap<String, Object>(), new DefaultModelValueHandler());
		return widgetModel;
	}

	protected WidgetInstanceManager createWidgetInstanceManager(final WidgetModel widgetModel)
	{
		final WidgetInstanceManager wim = Mockito.mock(WidgetInstanceManager.class);
		Mockito.when(wim.getModel()).thenReturn(widgetModel);
		return wim;
	}

	protected EvaluationContext getDefaultEvaluationContext(final WidgetModel widgetModel)
	{
		return new StandardEvaluationContext(widgetModel);
	}

	@Before
	public void before() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		prepareProductModel();
		prepareEmployeeModel();
		prepareBackofficeEditorAreaRenderer();
		prepareTypeFacade(true);
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getDefaultSupportedJaloAttributes());
	}

	protected void prepareTypeFacade(final boolean isWritable)
	{
		try
		{
			final DataType dataType = Mockito.mock(DataType.class);
			final DataAttribute dataAttribute = Mockito.mock(DataAttribute.class);
			Mockito.when(dataAttribute.isWritable()).thenReturn(isWritable);
			Mockito.when(dataType.getAttribute(Mockito.anyString())).thenReturn(dataAttribute);

			Mockito.when(typeFacade.getType(productModel1)).thenReturn("Product");
			Mockito.when(typeFacade.getType(productModel2)).thenReturn("Product");
			Mockito.when(typeFacade.load("Product")).thenReturn(dataType);
		}
		catch (final TypeNotFoundException ex)
		{
			throw new RuntimeException(ex);
		}
	}


	protected void prepareBackofficeEditorAreaRenderer()
	{
		backofficeEditorAreaRenderer.setJaloPersistanceHandler(jaloPersistanceHandler);
		Mockito
				.doNothing()
				.when(backofficeEditorAreaRenderer)
				.delegateRendering(Mockito.any(Component.class), Mockito.any(EditorArea.class), Mockito.anyObject(),
						Mockito.any(DataType.class), Mockito.any(WidgetInstanceManager.class));
	}

	protected void prepareProductModel() throws JaloBusinessException
	{
		productModel1 = Mockito.mock(ProductModel.class);
		productModel2 = Mockito.mock(ProductModel.class);
		final long productPk1 = 123;
		final long productPk2 = 222;
		final PK pk1 = PK.fromLong(productPk1);
		final PK pk2 = PK.fromLong(productPk2);

		Mockito.when(productModel1.getPk()).thenReturn(pk1);
		Mockito.when(productModel1.getPk()).thenReturn(pk2);

		final Product product1 = new Product();
		final Product spy1 = Mockito.spy(product1);

		values.put(ATTRIBUTE_REFERENCE_TO_LIST_EMPLOYEE, null);
		values.put(ATTRIBUTE_REFERENCE_TO_SET_EMPLOYEE, null);
		values.put(ATTRIBUTE_REFERENCE_TO_COLLECTION_STRING, null);
		values.put(ATTRIBUTE_REFERENCE_TO_STRING, null);
		values.put(ATTRIBUTE_REFERENCE_TO_EMPLOYEE, null);


		Mockito.doAnswer(new Answer()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				return values.get(invocation.getArguments()[0]);
			}
		}).when(spy1).getAttribute(Mockito.anyString());


		Mockito.doAnswer(new Answer()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				values.put((String) invocation.getArguments()[0], invocation.getArguments()[1]);
				return null;
			}
		}).when(spy1).setAttribute(Mockito.anyString(), Mockito.any());

		Mockito.doAnswer(new Answer()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				values.putAll((Map) invocation.getArguments()[0]);
				return null;
			}
		}).when(spy1).setAllAttributes(Mockito.anyMap());

		Mockito.when(modelService.getSource(productModel1)).thenReturn(spy1);
	}

	protected void prepareEmployeeModel()
	{
		employee1 = Mockito.mock(EmployeeModel.class);
		jaloEmployee1 = Mockito.mock(Employee.class);
		Mockito.when(jaloEmployee1.getPK()).thenReturn(PK.BIG_PK);
		Mockito.when(modelService.getSource(employee1)).thenReturn(jaloEmployee1);
		Mockito.when(modelService.get(jaloEmployee1)).thenReturn(employee1);
	}


	@Test
	public void testListAttribute() throws AccessException
	{
		final List<EmployeeModel> employeeList = new ArrayList<>();
		employeeList.add(employee1);
		testSkeleton(ATTRIBUTE_REFERENCE_TO_LIST_EMPLOYEE, employeeList, new TypedValueAssertion()
		{
			@Override
			public void doAssertions(final TypedValue typedValue)
			{
				Assertions.assertThat(typedValue).isNotNull();
				Assertions.assertThat(typedValue.getValue()).isInstanceOf(List.class);
				final List list = (List) typedValue.getValue();
				Assertions.assertThat(list).containsOnly(employee1);
			}
		});
	}

	@Test
	public void testListAttributeNotWritable() throws AccessException
	{
		prepareTypeFacade(false);
		final List<EmployeeModel> employeeList = new ArrayList<>();
		employeeList.add(employee1);
		testSkeletonNotWritable(ATTRIBUTE_REFERENCE_TO_LIST_EMPLOYEE, employeeList);
	}

	@Test
	public void testSetAttribute() throws Exception
	{
		final Set<EmployeeModel> employeeSet = new HashSet<>();
		employeeSet.add(employee1);
		testSkeleton(ATTRIBUTE_REFERENCE_TO_SET_EMPLOYEE, employeeSet, new TypedValueAssertion()
		{
			@Override
			public void doAssertions(final TypedValue typedValue)
			{
				Assertions.assertThat(typedValue).isNotNull();
				Assertions.assertThat(typedValue.getValue()).isInstanceOf(Set.class);
				final Set set = (Set) typedValue.getValue();
				Assertions.assertThat(set).containsOnly(employee1);
			}
		});
	}

	@Test
	public void testReferenceAttribute() throws Exception
	{

		testSkeleton("ATTRIBUTE_REFERENCE_TO_EMPLOYEE", employee1, new TypedValueAssertion()
		{
			@Override
			public void doAssertions(final TypedValue typedValue)
			{
				Assertions.assertThat(typedValue).isNotNull();
				Assertions.assertThat(typedValue.getValue()).isInstanceOf(EmployeeModel.class);
				Assertions.assertThat(typedValue.getValue()).isEqualTo(employee1);
			}
		});
	}

	@Test
	public void testSimpleAttribute() throws Exception
	{
		testSkeleton(ATTRIBUTE_REFERENCE_TO_STRING, "123456", new TypedValueAssertion()
		{
			@Override
			public void doAssertions(final TypedValue typedValue)
			{
				Assertions.assertThat(typedValue).isNotNull();
				Assertions.assertThat(typedValue.getValue()).isInstanceOf(String.class);
				Assertions.assertThat(typedValue.getValue()).isEqualTo("123456");
			}
		});
	}

	@Test
	public void testSimpleCollectionAttribute() throws Exception
	{
		backofficeEditorAreaRenderer.render(null, null, null, null, null);

		final List<String> list = new ArrayList<>();
		final String someValue1 = "123456";
		final String someValue2 = "654321";
		list.add(someValue1);
		list.add(someValue2);
		testSkeleton(ATTRIBUTE_REFERENCE_TO_COLLECTION_STRING, list, new TypedValueAssertion()
		{
			@Override
			public void doAssertions(final TypedValue typedValue)
			{
				Assertions.assertThat(typedValue).isNotNull();
				Assertions.assertThat(typedValue.getValue()).isInstanceOf(Collection.class);
				final Collection readCollection = (Collection) typedValue.getValue();
				Assertions.assertThat(readCollection).hasSize(2).containsOnly(someValue1, someValue2);
			}
		});
	}

	protected void simulateSave(final WidgetModel widgetModel) throws Exception
	{
		final Map<String, EventListener<Event>> afterSaveListeners = EditorAreaRendererUtils.getAfterSaveListeners(widgetModel);

		for (final EventListener<Event> event : afterSaveListeners.values())
		{
			event.onEvent(new Event("afterSave"));
		}
	}

    protected void testSkeletonNotWritable(final String attributeName, final Object value)
    {
        try
        {
            final WidgetModel widgetModel = getWidgetModel();
            widgetModel.put("currentObject", productModel1);
            final WidgetInstanceManager widgetInstanceManager = createWidgetInstanceManager(widgetModel);
            final EvaluationContext evaluationContext = getDefaultEvaluationContext(widgetModel);
            backofficeEditorAreaRenderer.render(null, null, productModel1, null, widgetInstanceManager);

            final TypedValue readValue1 = jaloPropertyNotVisibleInModelAccessor.read(evaluationContext, productModel1, attributeName);
            Assertions.assertThat(readValue1.getValue()).isNull();
            jaloPropertyNotVisibleInModelAccessor.write(evaluationContext, productModel1, attributeName, value);
            simulateSave(widgetModel);
            backofficeEditorAreaRenderer.render(null, null, productModel1, null, widgetInstanceManager);
            final TypedValue readValue2 = jaloPropertyNotVisibleInModelAccessor.read(evaluationContext, productModel1, attributeName);
            Assertions.assertThat(readValue2.getValue()).isNull();
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

	protected void testSkeleton(final String attributeName, final Object value, final TypedValueAssertion typedValueAssertion)
	{
		try
		{
			final WidgetModel widgetModel = getWidgetModel();
			widgetModel.put("currentObject", productModel1);
			final WidgetInstanceManager widgetInstanceManager = createWidgetInstanceManager(widgetModel);
			final EvaluationContext evaluationContext = getDefaultEvaluationContext(widgetModel);
			backofficeEditorAreaRenderer.render(null, null, productModel1, null, widgetInstanceManager);

			final TypedValue readValue1 = jaloPropertyNotVisibleInModelAccessor.read(evaluationContext, productModel1, attributeName);
			Assertions.assertThat(readValue1.getValue()).isNull();
			jaloPropertyNotVisibleInModelAccessor.write(evaluationContext, productModel1, attributeName, value);
			final TypedValue readValue2 = jaloPropertyNotVisibleInModelAccessor.read(evaluationContext, productModel1, attributeName);

			typedValueAssertion.doAssertions(readValue2);
			simulateSave(widgetModel);

			final TypedValue readValue3 = jaloPropertyNotVisibleInModelAccessor.read(evaluationContext, productModel1, attributeName);
			typedValueAssertion.doAssertions(readValue3);
			//render one more time the same object (refresh)
			backofficeEditorAreaRenderer.render(null, null, productModel1, null, widgetInstanceManager);
			final TypedValue readValue4 = jaloPropertyNotVisibleInModelAccessor.read(evaluationContext, productModel1, attributeName);
			typedValueAssertion.doAssertions(readValue4);


		}
		catch (final Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

}

interface TypedValueAssertion
{
	void doAssertions(final TypedValue typedValue);
}
