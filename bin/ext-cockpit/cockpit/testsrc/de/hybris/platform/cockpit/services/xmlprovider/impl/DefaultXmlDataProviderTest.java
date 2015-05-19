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
 */
package de.hybris.platform.cockpit.services.xmlprovider.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.cockpit.model.listview.impl.DefaultColumnDescriptor;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.model.referenceeditor.impl.ContextAreaReferenceCollectionUIEditor;
import de.hybris.platform.cockpit.services.config.ColumnConfiguration;
import de.hybris.platform.cockpit.services.config.EditorRowConfiguration;
import de.hybris.platform.cockpit.services.config.EditorSectionConfiguration;
import de.hybris.platform.cockpit.services.config.ListViewConfiguration;
import de.hybris.platform.cockpit.services.config.UIConfigurationService;
import de.hybris.platform.cockpit.services.config.impl.DefaultColumnGroupConfiguration;
import de.hybris.platform.cockpit.services.config.jaxb.editor.preview.AttributeType;
import de.hybris.platform.cockpit.services.config.jaxb.editor.preview.RowType;
import de.hybris.platform.cockpit.services.config.jaxb.editor.preview.SectionType;
import de.hybris.platform.cockpit.services.label.LabelService;
import de.hybris.platform.cockpit.services.media.MediaInfo;
import de.hybris.platform.cockpit.services.media.MediaInfoService;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.cockpit.services.xmlprovider.XmlDataProvider;
import de.hybris.platform.cockpit.services.xmlprovider.XmlDataProvider.ROW_TYPE;
import de.hybris.platform.cockpit.session.impl.AbstractAdvancedBrowserModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.easymock.classextension.EasyMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


/**
 * The Class DefaultXmlDataProviderTest.
 */
@IntegrationTest
public class DefaultXmlDataProviderTest extends HybrisJUnit4TransactionalTest
{
	private static final String COL_DESCR_NAME2 = "column2";
	private static final String COL_DESCR_NAME1 = "column1";
	private static final String COLL_MODEL_LABEL_2 = "col model 2";
	private static final String COLL_MODEL_LABEL_1 = "col model 1";
	private static final String CLS_ATTR_VALUE_MODEL_NAME = "clsAttrValueModelName";
	private static final String PROP_VALUE_TYPED_OBJECT_TEXT_LABEL = "typedObjectTxtLabel";
	private static final String MEDIA_INFO_ICON = "non/web/media/icon.jpg";
	private static final String MEDIA_MODEL_MIME = "application/ms-word";
	private static final String MEDIA_MODEL_REAL_FILENAME = "Foobar.jpg";
	private static final String MEDIA_MODEL_CODE = "My Media Code";
	private static final String MEDIA_IMG_URL = "/foo/bar/baz";
	private static final String PROP_DESCR_NAME_2 = "Prop descr name 2";
	private static final String PROP_DESCR_NAME_1 = "Prop descr name 1";
	private static final String PROPERTY_VALUE_STRING = "Lorem ipsum";
	private static final String EDITOR_SECTION_CONF_LABEL = "FooBar";

	private class MyRowComparator implements Comparator<Object>
	{

		@Override
		public int compare(final Object first, final Object other)
		{
			if (first instanceof RowType && other instanceof RowType)
			{
				final AttributeType firstAttr = ((RowType) first).getAttribute();
				final AttributeType otherAttr = ((RowType) other).getAttribute();
				if (firstAttr != null && otherAttr != null)
				{
					return firstAttr.getName().compareTo(otherAttr.getName());
				}
				return ((RowType) first).getType().compareTo(((RowType) other).getType());
			}
			return 0;
		}

	}

	private XmlDataProvider xmlDataProvider;
	private EditorSectionConfiguration editorSectionConfMock;
	private TypedObject typedObjectMock;
	private ArrayList<EditorRowConfiguration> rows;
	private EditorRowConfiguration editRowConfMock1;
	private EditorRowConfiguration editRowConfMock2;
	private PropertyDescriptor propDescrMock1;
	private PropertyDescriptor propDescrMock2;
	private ModelService modelServiceMock;
	private TypedObject propValueTypedObjectMock;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *            the exception
	 */
	@Before
	public void setUp() throws Exception
	{
		modelServiceMock = createMock(ModelService.class);
		propValueTypedObjectMock = createMock(TypedObject.class);

		rows = new ArrayList<EditorRowConfiguration>();
		editRowConfMock1 = createMock(EditorRowConfiguration.class);
		editRowConfMock2 = createMock(EditorRowConfiguration.class);

		rows.add(editRowConfMock1);
		rows.add(editRowConfMock2);

		propDescrMock1 = createMock(PropertyDescriptor.class);
		propDescrMock2 = createMock(PropertyDescriptor.class);

		editorSectionConfMock = createMock(EditorSectionConfiguration.class);
		typedObjectMock = createMock(TypedObject.class);
	}

	/**
	 * Test method for.
	 *
	 * {@link de.hybris.platform.cockpit.services.xmlprovider.impl.DefaultXmlDataProvider#generateAsXml(de.hybris.platform.cockpit.services.config.EditorSectionConfiguration, de.hybris.platform.cockpit.model.meta.TypedObject)}
	 * .
	 */
	@Test
	public void testGenerateAsXmlPropertyMediaTypedObject()
	{
		final MediaModel mediaModelMock = createMock(MediaModel.class);

		final MediaInfoService mediaInfoServiceMock = createMock(MediaInfoService.class);
		expect(mediaInfoServiceMock.isWebMedia(mediaModelMock)).andReturn(Boolean.TRUE).atLeastOnce();
		replay(mediaInfoServiceMock);

		expect(propValueTypedObjectMock.getObject()).andReturn(mediaModelMock).atLeastOnce();
		replay(propValueTypedObjectMock);

		final File fileMock = createMock(File.class);
		expect(fileMock.getPath()).andReturn(MEDIA_IMG_URL).atLeastOnce();
		replay(fileMock);

		final List<File> files = new ArrayList<File>();
		files.add(fileMock);

		final Media mediaItemMock = createMock(Media.class);
		expect(mediaItemMock.getFiles()).andReturn(files).atLeastOnce();
		replay(mediaItemMock);

		expect(modelServiceMock.getSource(mediaModelMock)).andReturn(mediaItemMock).atLeastOnce();
		replay(modelServiceMock);

		xmlDataProvider = new DefaultXmlDataProvider()
		{
			@Override
			public MediaInfoService getMediaInfoService()
			{
				return mediaInfoServiceMock;
			}

			@Override
			public ModelService getModelService()
			{
				return modelServiceMock;
			}

			@Override
			protected Object getPropertyValue(final TypedObject curObj, final PropertyDescriptor descriptor)
			{
				return propValueTypedObjectMock;
			}
		};

		prepareBaseMocks(true, true);

		SectionType result = (SectionType) xmlDataProvider.generateAsXml(editorSectionConfMock, typedObjectMock);

		assertThat(result.getName(), is(equalTo(EDITOR_SECTION_CONF_LABEL)));
		assertThat(result.getType(), is(equalTo(ROW_TYPE.simple.toString())));

		List<Object> rowTypes = getRows(result);

		assertThat(Integer.valueOf(rowTypes.size()), is(equalTo(Integer.valueOf(2))));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_1)));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_2)));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getImgUrl(), is(equalTo(MEDIA_IMG_URL)));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getImgUrl(), is(equalTo(MEDIA_IMG_URL)));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getValue(), is(equalTo("")));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getValue(), is(equalTo("")));
		assertThat(((RowType) rowTypes.get(0)).getType(), is(equalTo(ROW_TYPE.media.toString())));
		assertThat(((RowType) rowTypes.get(1)).getType(), is(equalTo(ROW_TYPE.media.toString())));

		verifyBaseMocks();

		// NonWeb media
		final MediaInfo mediaInfoMock = createMock(MediaInfo.class);
		expect(mediaInfoMock.getIcon()).andReturn(MEDIA_INFO_ICON).atLeastOnce();
		replay(mediaInfoMock);

		reset(mediaInfoServiceMock);
		expect(mediaInfoServiceMock.isWebMedia(mediaModelMock)).andReturn(Boolean.FALSE).atLeastOnce();
		expect(mediaInfoServiceMock.getNonWebMediaInfo(MEDIA_MODEL_MIME)).andReturn(mediaInfoMock).atLeastOnce();
		replay(mediaInfoServiceMock);

		reset(mediaModelMock);
		expect(mediaModelMock.getCode()).andReturn(MEDIA_MODEL_CODE).atLeastOnce();
		expect(mediaModelMock.getRealFileName()).andReturn(MEDIA_MODEL_REAL_FILENAME).atLeastOnce();
		expect(mediaModelMock.getMime()).andReturn(MEDIA_MODEL_MIME).atLeastOnce();
		replay(mediaModelMock);

		prepareBaseMocks(true, true);

		result = (SectionType) xmlDataProvider.generateAsXml(editorSectionConfMock, typedObjectMock);

		rowTypes = getRows(result);

		assertThat(((RowType) rowTypes.get(0)).getAttribute().getValue(), is(MEDIA_MODEL_CODE + " " + MEDIA_MODEL_REAL_FILENAME));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getValue(), is(MEDIA_MODEL_CODE + " " + MEDIA_MODEL_REAL_FILENAME));

		verifyBaseMocks();
		verify(mediaInfoMock);
		verify(mediaInfoServiceMock);
		verify(mediaModelMock);
	}

	@Test
	public void testGenerateAsXmlPropertyFeatureValue()
	{
		final FeatureValue featureValueMock = createMock(FeatureValue.class);

		xmlDataProvider = new DefaultXmlDataProvider()
		{
			@Override
			protected Object getPropertyValue(final TypedObject curObj, final PropertyDescriptor descriptor)
			{
				return featureValueMock;
			}
		};

		prepareBaseMocks(true, true);

		final ClassificationAttributeValueModel clsAttrValueModelMock = createMock(ClassificationAttributeValueModel.class);
		expect(clsAttrValueModelMock.getName()).andReturn(CLS_ATTR_VALUE_MODEL_NAME).atLeastOnce();
		replay(clsAttrValueModelMock);

		expect(featureValueMock.getValue()).andReturn(clsAttrValueModelMock).atLeastOnce();
		expect(featureValueMock.getUnit()).andReturn(null).atLeastOnce();
		replay(featureValueMock);

		final SectionType result = (SectionType) xmlDataProvider.generateAsXml(editorSectionConfMock, typedObjectMock);

		assertThat(result.getName(), is(equalTo(EDITOR_SECTION_CONF_LABEL)));
		assertThat(result.getType(), is(equalTo(ROW_TYPE.simple.toString())));

		final List<Object> rowTypes = getRows(result);

		assertThat(Integer.valueOf(rowTypes.size()), is(equalTo(Integer.valueOf(2))));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_1)));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_2)));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getImgUrl(), is(nullValue()));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getImgUrl(), is(nullValue()));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getValue(), is(CLS_ATTR_VALUE_MODEL_NAME));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getValue(), is(CLS_ATTR_VALUE_MODEL_NAME));
		assertThat(((RowType) rowTypes.get(0)).getType(), is(equalTo(ROW_TYPE.simple.toString())));
		assertThat(((RowType) rowTypes.get(1)).getType(), is(equalTo(ROW_TYPE.simple.toString())));

		verifyBaseMocks();
		verify(featureValueMock);
		verify(clsAttrValueModelMock);
	}

	@Test
	public void testGenerateAsXmlPropertyOtherTypedObject()
	{
		final ItemModel itemModelMock = createMock(ItemModel.class);

		expect(propValueTypedObjectMock.getObject()).andReturn(itemModelMock).atLeastOnce();
		replay(propValueTypedObjectMock);

		final LabelService labelServiceMock = createMock(LabelService.class);
		expect(labelServiceMock.getObjectTextLabelForTypedObject(propValueTypedObjectMock)).andReturn(
				PROP_VALUE_TYPED_OBJECT_TEXT_LABEL).atLeastOnce();
		replay(labelServiceMock);

		xmlDataProvider = new DefaultXmlDataProvider()
		{
			@Override
			public ModelService getModelService()
			{
				return modelServiceMock;
			}

			@Override
			protected Object getPropertyValue(final TypedObject curObj, final PropertyDescriptor descriptor)
			{
				return propValueTypedObjectMock;
			}

			@Override
			public LabelService getLabelService()
			{
				return labelServiceMock;
			}
		};

		prepareBaseMocks(true, true);

		final SectionType result = (SectionType) xmlDataProvider.generateAsXml(editorSectionConfMock, typedObjectMock);

		final List<Object> rowTypes = getRows(result);

		assertThat(result.getName(), is(equalTo(EDITOR_SECTION_CONF_LABEL)));
		assertThat(result.getType(), is(equalTo(ROW_TYPE.simple.toString())));

		assertThat(((RowType) rowTypes.get(0)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_1)));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_2)));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getImgUrl(), is(nullValue()));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getImgUrl(), is(nullValue()));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getValue(), is(PROP_VALUE_TYPED_OBJECT_TEXT_LABEL));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getValue(), is(PROP_VALUE_TYPED_OBJECT_TEXT_LABEL));
		assertThat(((RowType) rowTypes.get(0)).getType(), is(equalTo(ROW_TYPE.simple.toString())));
		assertThat(((RowType) rowTypes.get(1)).getType(), is(equalTo(ROW_TYPE.simple.toString())));

		verifyBaseMocks();
		verify(propValueTypedObjectMock);
		verify(labelServiceMock);
	}

	/**
	 * Test method for.
	 *
	 * {@link de.hybris.platform.cockpit.services.xmlprovider.impl.DefaultXmlDataProvider#generateAsXml(de.hybris.platform.cockpit.services.config.EditorSectionConfiguration, de.hybris.platform.cockpit.model.meta.TypedObject)}
	 * .
	 */
	@Test
	public void testGenerateAsXmlPropertyString()
	{
		xmlDataProvider = new DefaultXmlDataProvider()
		{
			private int testCounter = 1;

			@Override
			protected Object getPropertyValue(final TypedObject curObj, final PropertyDescriptor descriptor)
			{
				final String result = PROPERTY_VALUE_STRING + testCounter;
				testCounter++;
				return result;
			}
		};

		prepareBaseMocks(true, true);

		final SectionType result = (SectionType) xmlDataProvider.generateAsXml(editorSectionConfMock, typedObjectMock);

		assertThat(result.getName(), is(equalTo(EDITOR_SECTION_CONF_LABEL)));
		assertThat(result.getType(), is(equalTo(ROW_TYPE.simple.toString())));

		final List<Object> rowTypes = getRows(result);

		assertThat(Integer.valueOf(rowTypes.size()), is(equalTo(Integer.valueOf(2))));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_1)));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_2)));
		assertThat(((RowType) rowTypes.get(0)).getType(), is(equalTo(ROW_TYPE.simple.toString())));
		assertThat(((RowType) rowTypes.get(1)).getType(), is(equalTo(ROW_TYPE.simple.toString())));

		verifyBaseMocks();
	}

	@Test
	public void testGenerateAsXmlWithOneEditorRowNotVisible()
	{
		xmlDataProvider = new DefaultXmlDataProvider()
		{
			private int testCounter = 1;

			@Override
			protected Object getPropertyValue(final TypedObject curObj, final PropertyDescriptor descriptor)
			{
				final String result = PROPERTY_VALUE_STRING + testCounter;
				testCounter++;
				return result;
			}
		};

		prepareBaseMocks(false, true);

		final SectionType result = (SectionType) xmlDataProvider.generateAsXml(editorSectionConfMock, typedObjectMock);
		final List<Object> rowTypes = getRows(result);

		assertThat(Integer.valueOf(rowTypes.size()), is(equalTo(Integer.valueOf(1))));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_2)));
	}

	@Test
	public void testGenerateAsXmlPropertyMediaCollection()
	{
		final MediaModel mediaMockModel1 = createMock(MediaModel.class);
		expect(mediaMockModel1.getCode()).andReturn(PROP_DESCR_NAME_1).atLeastOnce();
		replay(mediaMockModel1);
		final TypedObject mediaMock1 = createMock(TypedObject.class);
		expect(mediaMock1.getObject()).andReturn(mediaMockModel1).atLeastOnce();
		replay(mediaMock1);

		final MediaModel mediaMockModel2 = createMock(MediaModel.class);
		expect(mediaMockModel2.getCode()).andReturn(PROP_DESCR_NAME_2).atLeastOnce();
		replay(mediaMockModel2);
		final TypedObject mediaMock2 = createMock(TypedObject.class);
		expect(mediaMock2.getObject()).andReturn(mediaMockModel2).atLeastOnce();
		replay(mediaMock2);

		xmlDataProvider = new DefaultXmlDataProvider()
		{
			@Override
			public ModelService getModelService()
			{
				return modelServiceMock;
			}

			@Override
			protected Object getPropertyValue(final TypedObject curObj, final PropertyDescriptor descriptor)
			{
				final List<TypedObject> col = new ArrayList<TypedObject>();
				col.add(mediaMock1);
				col.add(mediaMock2);
				return col;
			}

			@Override
			protected String getMediaImageUrl(final MediaModel mediaModel)
			{
				return StringUtils.EMPTY;
			}
		};

		expect(Boolean.valueOf(editRowConfMock1.isVisible())).andReturn(Boolean.TRUE);
		expect(Boolean.valueOf(editRowConfMock2.isVisible())).andReturn(Boolean.TRUE);
		expect(editRowConfMock1.getXmlDataProvider()).andReturn(xmlDataProvider).once();
		expect(editRowConfMock2.getXmlDataProvider()).andReturn(xmlDataProvider).once();
		expect(editRowConfMock1.getPropertyDescriptor()).andReturn(propDescrMock1).once();
		expect(editRowConfMock2.getPropertyDescriptor()).andReturn(propDescrMock2).once();
		replay(editRowConfMock1);
		replay(editRowConfMock2);

		expect(propDescrMock1.getName()).andReturn(PROP_DESCR_NAME_1).atLeastOnce();
		expect(propDescrMock2.getName()).andReturn(PROP_DESCR_NAME_2).atLeastOnce();
		expect(propDescrMock1.getEditorType()).andReturn(PropertyDescriptor.TEXT).once();
		expect(propDescrMock2.getEditorType()).andReturn(PropertyDescriptor.TEXT).once();
		replay(propDescrMock1);
		replay(propDescrMock2);

		expect(editorSectionConfMock.getLabel()).andReturn(EDITOR_SECTION_CONF_LABEL).once();
		expect(editorSectionConfMock.getSectionRows()).andReturn(rows).once();
		replay(editorSectionConfMock);


		final SectionType result = (SectionType) xmlDataProvider.generateAsXml(editorSectionConfMock, typedObjectMock);

		assertThat(result.getName(), is(equalTo(EDITOR_SECTION_CONF_LABEL)));
		assertThat(result.getType(), is(equalTo(ROW_TYPE.simple.toString())));

		final List<Object> rowTypes = getRows(result);

		assertThat(Integer.valueOf(rowTypes.size()), is(equalTo(Integer.valueOf(2))));
		assertThat(((RowType) rowTypes.get(0)).getAttributes().getAttribute().get(0).getName(), is(equalTo(PROP_DESCR_NAME_1)));
		assertThat(((RowType) rowTypes.get(1)).getAttributes().getAttribute().get(1).getName(), is(equalTo(PROP_DESCR_NAME_2)));

		assertThat(((RowType) rowTypes.get(0)).getAttributes().getAttribute().get(0).getImgUrl(), is(equalTo(StringUtils.EMPTY)));
		assertThat(((RowType) rowTypes.get(1)).getAttributes().getAttribute().get(0).getImgUrl(), is(equalTo(StringUtils.EMPTY)));
		assertThat(((RowType) rowTypes.get(0)).getAttributes().getAttribute().get(0).getValue(), is(equalTo(StringUtils.EMPTY)));
		assertThat(((RowType) rowTypes.get(1)).getAttributes().getAttribute().get(0).getValue(), is(equalTo(StringUtils.EMPTY)));

		assertThat(((RowType) rowTypes.get(0)).getType(), is(equalTo(ROW_TYPE.attributes.toString())));
		assertThat(((RowType) rowTypes.get(1)).getType(), is(equalTo(ROW_TYPE.attributes.toString())));

		verifyBaseMocks();
		verify(mediaMockModel1);
		verify(mediaMockModel2);
		verify(mediaMock1);
		verify(mediaMock2);
	}

	@Test
	public void testGenerateAsXmlPropertyItemCollection()
	{
		final ItemModel itemMockModel1 = createMock(ItemModel.class);
		replay(itemMockModel1);
		final TypedObject itemMock1 = createMock(TypedObject.class);
		expect(itemMock1.getObject()).andReturn(itemMockModel1).atLeastOnce();
		replay(itemMock1);

		final TypedObject itemMock2 = createMock(TypedObject.class);
		replay(itemMock2);

		final LabelService labelServiceMock = createMock(LabelService.class);
		expect(labelServiceMock.getObjectTextLabelForTypedObject(itemMock1)).andReturn(COLL_MODEL_LABEL_1).atLeastOnce();
		expect(labelServiceMock.getObjectTextLabelForTypedObject(itemMock2)).andReturn(COLL_MODEL_LABEL_2).atLeastOnce();
		replay(labelServiceMock);

		xmlDataProvider = new DefaultXmlDataProvider()
		{
			@Override
			public LabelService getLabelService()
			{
				return labelServiceMock;
			}

			@Override
			public ModelService getModelService()
			{
				return modelServiceMock;
			}

			@Override
			protected Object getPropertyValue(final TypedObject curObj, final PropertyDescriptor descriptor)
			{
				final List<TypedObject> col = new ArrayList<TypedObject>();
				col.add(itemMock1);
				col.add(itemMock2);
				return col;
			}
		};

		expect(Boolean.valueOf(editRowConfMock1.isVisible())).andReturn(Boolean.TRUE);
		expect(Boolean.valueOf(editRowConfMock2.isVisible())).andReturn(Boolean.TRUE);
		expect(editRowConfMock1.getXmlDataProvider()).andReturn(xmlDataProvider).once();
		expect(editRowConfMock2.getXmlDataProvider()).andReturn(xmlDataProvider).once();
		expect(editRowConfMock1.getPropertyDescriptor()).andReturn(propDescrMock1).once();
		expect(editRowConfMock2.getPropertyDescriptor()).andReturn(propDescrMock2).once();
		replay(editRowConfMock1);
		replay(editRowConfMock2);

		expect(propDescrMock1.getName()).andReturn(PROP_DESCR_NAME_1).once();
		expect(propDescrMock2.getName()).andReturn(PROP_DESCR_NAME_2).once();
		expect(propDescrMock1.getEditorType()).andReturn(PropertyDescriptor.TEXT).once();
		expect(propDescrMock2.getEditorType()).andReturn(PropertyDescriptor.TEXT).once();
		replay(propDescrMock1);
		replay(propDescrMock2);

		expect(editorSectionConfMock.getLabel()).andReturn(EDITOR_SECTION_CONF_LABEL).once();
		expect(editorSectionConfMock.getSectionRows()).andReturn(rows).once();
		replay(editorSectionConfMock);


		final SectionType result = (SectionType) xmlDataProvider.generateAsXml(editorSectionConfMock, typedObjectMock);

		assertThat(result.getName(), is(equalTo(EDITOR_SECTION_CONF_LABEL)));
		assertThat(result.getType(), is(equalTo(ROW_TYPE.simple.toString())));

		final List<Object> rowTypes = getRows(result);

		assertThat(Integer.valueOf(rowTypes.size()), is(equalTo(Integer.valueOf(2))));
		assertThat(((RowType) rowTypes.get(0)).getType(), is(equalTo(ROW_TYPE.simple.toString())));
		assertThat(((RowType) rowTypes.get(1)).getType(), is(equalTo(ROW_TYPE.simple.toString())));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_1)));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getName(), is(equalTo(PROP_DESCR_NAME_2)));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getImgUrl(), is(nullValue()));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getImgUrl(), is(nullValue()));
		assertThat(((RowType) rowTypes.get(0)).getAttribute().getValue(),
				is(equalTo(COLL_MODEL_LABEL_1 + ", " + COLL_MODEL_LABEL_2)));
		assertThat(((RowType) rowTypes.get(1)).getAttribute().getValue(),
				is(equalTo(COLL_MODEL_LABEL_1 + ", " + COLL_MODEL_LABEL_2)));

		verifyBaseMocks();
		verify(itemMockModel1);
		verify(itemMock1);
		verify(itemMock2);
		verify(labelServiceMock);
	}

	@Test
	public void testGenerateAsXmlPropertyReferenceCollection()
	{
		final ItemModel itemMockModel1 = createMock(ItemModel.class);
		replay(itemMockModel1);
		final TypedObject itemMock1 = createMock(TypedObject.class);
		replay(itemMock1);

		final TypedObject itemMock2 = createMock(TypedObject.class);
		replay(itemMock2);

		final ObjectTemplate objectTemplateMock = createMock(ObjectTemplate.class);

		final TypeService typeServiceMock = createMock(TypeService.class);
		replay(typeServiceMock);

		final LabelService labelServiceMock = createMock(LabelService.class);
		replay(labelServiceMock);


		final DefaultColumnDescriptor cDescr1 = createMock(DefaultColumnDescriptor.class);
		expect(Boolean.valueOf(cDescr1.isVisible())).andReturn(Boolean.TRUE).atLeastOnce();
		expect(cDescr1.getName()).andReturn(COL_DESCR_NAME1).atLeastOnce();
		replay(cDescr1);
		final DefaultColumnDescriptor cDescr2 = createMock(DefaultColumnDescriptor.class);
		expect(Boolean.valueOf(cDescr2.isVisible())).andReturn(Boolean.TRUE).atLeastOnce();
		expect(cDescr2.getName()).andReturn(COL_DESCR_NAME2).atLeastOnce();
		replay(cDescr2);

		final ColumnConfiguration cConfMock1 = createMock(ColumnConfiguration.class);
		expect(cConfMock1.getColumnDescriptor()).andReturn(cDescr1).atLeastOnce();
		expect(cConfMock1.getValueHandler()).andReturn(null).atLeastOnce();
		replay(cConfMock1);
		final ColumnConfiguration cConfMock2 = createMock(ColumnConfiguration.class);
		expect(cConfMock2.getColumnDescriptor()).andReturn(cDescr2).atLeastOnce();
		expect(cConfMock2.getValueHandler()).andReturn(null).atLeastOnce();
		replay(cConfMock2);

		final List<ColumnConfiguration> allColumnConfs = new ArrayList<ColumnConfiguration>();
		allColumnConfs.add(cConfMock1);
		allColumnConfs.add(cConfMock2);

		final DefaultColumnGroupConfiguration cGroupConfMock = createMock(DefaultColumnGroupConfiguration.class);
		expect(cGroupConfMock.getAllColumnConfigurations()).andReturn(allColumnConfs).atLeastOnce();
		replay(cGroupConfMock);

		final ListViewConfiguration listViewConfigMock = createMock(ListViewConfiguration.class);
		expect(listViewConfigMock.getRootColumnGroupConfiguration()).andReturn(cGroupConfMock).atLeastOnce();
		replay(listViewConfigMock);

		final UIConfigurationService uiConfigServiceMock = createMock(UIConfigurationService.class);
		expect(
				uiConfigServiceMock.getComponentConfiguration(objectTemplateMock, AbstractAdvancedBrowserModel.LIST_VIEW_CONFIG_CODE,
						ListViewConfiguration.class)).andReturn(listViewConfigMock).atLeastOnce();
		replay(uiConfigServiceMock);

		// final ObjectTemplate objectTemplateMock = createMock(ObjectTemplate.class);
		// replay(objectTemplateMock);

		xmlDataProvider = new DefaultXmlDataProvider()
		{
			@Override
			public LabelService getLabelService()
			{
				return labelServiceMock;
			}

			@Override
			public ModelService getModelService()
			{
				return modelServiceMock;
			}

			@Override
			public UIConfigurationService getUiConfigurationService()
			{
				return uiConfigServiceMock;
			}

			@Override
			public TypeService getTypeService()
			{
				return typeServiceMock;
			}

			@Override
			protected Object getPropertyValue(final TypedObject curObj, final PropertyDescriptor descriptor)
			{
				final List<TypedObject> col = new ArrayList<TypedObject>();
				col.add(itemMock1);
				col.add(itemMock2);
				return col;
			}

			@Override
			protected ObjectTemplate getObjectTemplate(final PropertyDescriptor propDesc)
			{
				return objectTemplateMock;
			}

			@Override
			protected ObjectTemplate processVariantTypeCheck(final TypedObject curObj, final PropertyDescriptor propDesc,
					final ObjectTemplate template)
			{
				return objectTemplateMock;
			}
		};

		expect(Boolean.valueOf(editRowConfMock1.isVisible())).andReturn(Boolean.TRUE).atLeastOnce();
		expect(Boolean.valueOf(editRowConfMock2.isVisible())).andReturn(Boolean.TRUE).atLeastOnce();
		expect(editRowConfMock1.getXmlDataProvider()).andReturn(xmlDataProvider).once();
		expect(editRowConfMock2.getXmlDataProvider()).andReturn(xmlDataProvider).once();
		expect(editRowConfMock1.getPropertyDescriptor()).andReturn(propDescrMock1).atLeastOnce();
		expect(editRowConfMock2.getPropertyDescriptor()).andReturn(propDescrMock2).atLeastOnce();
		expect(editRowConfMock1.getEditor()).andReturn(ContextAreaReferenceCollectionUIEditor.NAME).once();
		expect(editRowConfMock2.getEditor()).andReturn(ContextAreaReferenceCollectionUIEditor.NAME).once();
		expect(editRowConfMock1.getPrintoutAs()).andReturn(null).once();
		expect(editRowConfMock2.getPrintoutAs()).andReturn(null).once();
		replay(editRowConfMock1);
		replay(editRowConfMock2);

		expect(propDescrMock1.getEditorType()).andReturn(PropertyDescriptor.REFERENCE).atLeastOnce();
		expect(propDescrMock2.getEditorType()).andReturn(PropertyDescriptor.REFERENCE).atLeastOnce();
		expect(propDescrMock1.getName()).andReturn(PROP_DESCR_NAME_1).atLeastOnce();
		expect(propDescrMock2.getName()).andReturn(PROP_DESCR_NAME_2).atLeastOnce();
		replay(propDescrMock1);
		replay(propDescrMock2);

		expect(editorSectionConfMock.getLabel()).andReturn(EDITOR_SECTION_CONF_LABEL).once();
		expect(editorSectionConfMock.getSectionRows()).andReturn(rows).once();
		replay(editorSectionConfMock);


		final SectionType result = (SectionType) xmlDataProvider.generateAsXml(editorSectionConfMock, typedObjectMock);

		assertThat(result.getName(), is(equalTo(EDITOR_SECTION_CONF_LABEL)));
		assertThat(result.getType(), is(equalTo(ROW_TYPE.simple.toString())));

		final List<Object> rowTypes = getRows(result);

		assertThat(Integer.valueOf(rowTypes.size()), is(equalTo(Integer.valueOf(2))));
		assertThat(((RowType) rowTypes.get(0)).getAttribute(), is(nullValue()));
		assertThat(((RowType) rowTypes.get(0)).getType(), is(equalTo(ROW_TYPE.table.toString())));
		assertThat(((RowType) rowTypes.get(1)).getType(), is(equalTo(ROW_TYPE.table.toString())));
		assertThat(((RowType) rowTypes.get(0)).getColumnsTitles().getTitle().get(0).getValue(), is(equalTo(COL_DESCR_NAME1)));
		assertThat(((RowType) rowTypes.get(0)).getColumnsTitles().getTitle().get(1).getValue(), is(equalTo(COL_DESCR_NAME2)));

		verifyBaseMocks();
		verify(itemMockModel1);
		verify(itemMock1);
		verify(itemMock2);
		verify(labelServiceMock);
	}

	private void verifyBaseMocks()
	{
		verify(editRowConfMock1);
		verify(editRowConfMock2);
		verify(propDescrMock1);
		verify(propDescrMock2);
		verify(editorSectionConfMock);
	}

	private List<Object> getRows(final SectionType result)
	{
		final List<Object> row = result.getRow();
		Collections.sort(row, new MyRowComparator());
		return row;
	}

	private void prepareBaseMocks(final boolean visibleRow1, final boolean visibleRow2)
	{
		reset(editRowConfMock1);
		reset(editRowConfMock2);
		reset(editRowConfMock1);
		reset(editRowConfMock2);
		reset(propDescrMock1);
		reset(propDescrMock2);
		reset(editorSectionConfMock);

		expect(Boolean.valueOf(editRowConfMock1.isVisible())).andReturn(Boolean.valueOf(visibleRow1));
		expect(Boolean.valueOf(editRowConfMock2.isVisible())).andReturn(Boolean.valueOf(visibleRow2));
		expect(editRowConfMock1.getXmlDataProvider()).andReturn(xmlDataProvider).once();
		expect(editRowConfMock2.getXmlDataProvider()).andReturn(xmlDataProvider).once();
		expect(editRowConfMock1.getPropertyDescriptor()).andReturn(propDescrMock1).once();
		expect(editRowConfMock2.getPropertyDescriptor()).andReturn(propDescrMock2).once();
		replay(editRowConfMock1);
		replay(editRowConfMock2);

		expect(propDescrMock1.getName()).andReturn(PROP_DESCR_NAME_1).atLeastOnce();
		expect(propDescrMock2.getName()).andReturn(PROP_DESCR_NAME_2).atLeastOnce();
		replay(propDescrMock1);
		replay(propDescrMock2);

		expect(editorSectionConfMock.getLabel()).andReturn(EDITOR_SECTION_CONF_LABEL).once();
		expect(editorSectionConfMock.getSectionRows()).andReturn(rows).once();
		replay(editorSectionConfMock);
	}

}
