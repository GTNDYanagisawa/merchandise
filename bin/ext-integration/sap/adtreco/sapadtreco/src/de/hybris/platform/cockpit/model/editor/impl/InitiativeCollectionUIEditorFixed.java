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
package de.hybris.platform.cockpit.model.editor.impl;

import de.hybris.platform.cockpit.constants.ImageUrls;
import de.hybris.platform.cockpit.model.editor.EditorListener;
import de.hybris.platform.cockpit.model.editor.ListUIEditor;
import de.hybris.platform.cockpit.model.editor.UIEditor;
import de.hybris.platform.cockpit.model.meta.DefaultPropertyEditorDescriptor;
import de.hybris.platform.cockpit.model.meta.EditorFactory;
import de.hybris.platform.cockpit.model.meta.PropertyEditorDescriptor;
import de.hybris.platform.cockpit.model.referenceeditor.collection.CollectionUIEditor;
import de.hybris.platform.core.Registry;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.*;


/**
 * Removed when https://jira.hybris.com/browse/PLA-13857 will be fixed !!!
 */
    public class InitiativeCollectionUIEditorFixed extends GenericCollectionUIEditor
{

    private PropertyEditorDescriptor singleValueEditorDescriptor;
    private transient Listbox collectionItems;
    private transient List<Object> collectionValues;
    protected static final String WIDTH = "width";
    protected static final String HEIGHT = "height";
    protected static final String SINGLE_VALUE_EDITOR_CODE="singleValueEditorCode";

    protected static final String _100PERCENT = "100%";
    private List<? extends Object> availableValues;

    public void setSingleValueEditorDescriptor(final PropertyEditorDescriptor singleValueEditorDescriptor)
    {
        this.singleValueEditorDescriptor = singleValueEditorDescriptor;
    }

    public PropertyEditorDescriptor getSingleValueEditorDescriptor()
    {
        return this.singleValueEditorDescriptor;
    }

    @Override
    public HtmlBasedComponent createViewComponent(final Object initialValue, final Map<String, ? extends Object> parameters,
                                                  final EditorListener listener)
    {
        collectionValues = createNewCollectionValuesList(initialValue);

        final Div listContainer = new Div();
        collectionItems = new Listbox();
        collectionItems.setSclass("collectionUIEditorItems");
        collectionItems.setOddRowSclass("oddRowRowSclass");
        collectionItems.setDisabled(!isEditable());
        collectionItems.setModel(getCollectionSimpleListModel(collectionValues));
        collectionItems.setFixedLayout(false);
        collectionItems.setItemRenderer(createCollectionItemListRenderer(parameters, listener));
        listContainer.appendChild(collectionItems);

        return listContainer;
    }

    public void updateCollectionItems()
    {
        collectionValues = new ArrayList<Object>(collectionValues);
        collectionItems.setModel(getCollectionSimpleListModel(collectionValues));
    }

    @Override
    public String getEditorType()
    {
        final PropertyEditorDescriptor desc = getSingleValueEditorDescriptor();
        return desc == null ? null : desc.getEditorType();
    }

    @Override
    public boolean isInline()
    {
        return false;
    }

    protected UIEditor createSingleValueEditor(final Map<String, ? extends Object> parameters){

        String editorCode  = PropertyEditorDescriptor.SINGLE;
        if(parameters.containsKey(SINGLE_VALUE_EDITOR_CODE)){
            editorCode = ObjectUtils.toString(parameters.get(SINGLE_VALUE_EDITOR_CODE));
        }
        return getSingleValueEditorDescriptor().createUIEditor(editorCode);
    }

    protected ListitemRenderer createCollectionItemListRenderer(final Map<String, ? extends Object> parameters,
                                                                final EditorListener listener)
    {
        return new ListitemRenderer()
        {
            @Override
            public void render(final Listitem itemRow, final Object value) throws Exception //NOPMD: ZK specific
            {
                if ((value instanceof Collection && ((Collection) value).isEmpty()))
                {
                    return;
                }
                else if (value == null)
                {
                    // add new value editor
                    final UIEditor editor = createSingleValueEditor(parameters);
                    editor.setEditable(isEditable());

                    if (editor instanceof ListUIEditor && !getAvailableValues().isEmpty())
                    {
                        ((ListUIEditor) editor).setAvailableValues(getAvailableValues());
                    }

                    Image addImage = null;
                    if (isEditable())
                    {
                        addImage = new Image(ImageUrls.GREEN_ADD_PLUS_IMAGE);
                        addImage.setTooltiptext(Labels.getLabel("collectionEditor.button.add.tooltip"));
                        addImage.addEventListener(Events.ON_CLICK, new EventListener()
                        {
                            @Override
                            public void onEvent(final Event arg0) throws Exception //NOPMD: ZK specific
                            {
                                // fake button, see valueChanged() below
                            }
                        });
                    }

                    final Component editorView = editor.createViewComponent(value, parameters, new EditorListener()
                    {
                        @Override
                        public void actionPerformed(final String actionCode)
                        {
                            if (EditorListener.ENTER_PRESSED.equals(actionCode) && editor.getValue() != null)
                            {
                                addCollectionElement(editor, listener);
                            }
                        }

                        @Override
                        public void valueChanged(final Object value)
                        {
                            if (editor.getValue() != null)
                            {
                                addCollectionElement(editor, listener);
                            }
                        }
                    });

                    itemRow.appendChild(createListCell(editorView, addImage));
                }
                else
                {
                    final UIEditor editor = createSingleValueEditor(parameters);
                    editor.setEditable(isEditable());

                    if (editor instanceof ListUIEditor && !getAvailableValues().isEmpty())
                    {
                        ((ListUIEditor) editor).setAvailableValues(getAvailableValues());
                    }

                    final Component editorView = editor.createViewComponent(value, parameters, new EditorListener()
                    {
                        @Override
                        public void actionPerformed(final String actionCode)
                        {
                            // YTODO Auto-generated method stub
                        }

                        @Override
                        public void valueChanged(final Object value)
                        {
                            if (collectionValues != null && !collectionValues.isEmpty() && itemRow.getIndex() >= 0
                                    && collectionValues.size() > itemRow.getIndex())
                            {
                                if (!collectionValues.get(itemRow.getIndex()).equals(value))
                                {
                                    collectionValues = createNewCollectionValuesList(collectionValues);
                                }
                                if (value == null)
                                {
                                    collectionValues.remove(itemRow.getIndex());
                                    collectionValues = createNewCollectionValuesList(collectionValues);
                                    setValue(collectionValues);
                                    updateCollectionItems();
                                }
                                else
                                {
                                    collectionValues.set(itemRow.getIndex(), value);
                                    setValue(collectionValues);
                                }
                            }
                            listener.valueChanged(getValue());
                        }
                    });

                    Image removeImage = null;
                    if (isEditable())
                    {
                        removeImage = new Image(ImageUrls.REMOVE_BUTTON_IMAGE);
                        removeImage.setTooltiptext(Labels.getLabel("collectionEditor.button.remove.tooltip"));
                        removeImage.addEventListener(Events.ON_CLICK, new EventListener()
                        {
                            @Override
                            public void onEvent(final Event arg0) throws Exception //NOPMD: ZK specific
                            {
                                collectionValues.remove(itemRow.getIndex());
                                collectionValues = createNewCollectionValuesList(collectionValues);
                                setValue(collectionValues);
                                updateCollectionItems();
                                listener.valueChanged(getValue());
                            }
                        });
                    }
                    itemRow.appendChild(createListCell(editorView, removeImage));
                }
            }

            private Listcell createListCell(final Component editorView, final Image image)
            {
                final Listcell cellItem = new Listcell();

                final Div cellContainerDiv = new Div();
                cellContainerDiv.setSclass("collectionUIEditorItem");

                final Div firstCellDiv = new Div();
                firstCellDiv.appendChild(editorView);
                cellContainerDiv.appendChild(firstCellDiv);

                if (image != null)
                {
                    firstCellDiv.setSclass("editor");
                    final Div secondCellDiv = new Div();
                    secondCellDiv.appendChild(image);
                    secondCellDiv.setSclass("image");
                    cellContainerDiv.appendChild(secondCellDiv);
                }
                cellItem.appendChild(cellContainerDiv);
                return cellItem;
            }
        };
    }

    private SimpleListModel getCollectionSimpleListModel(final List<Object> collectionValues)
    {
        final List<Object> newCollectionValues = new ArrayList<Object>(((collectionValues == null) ? Collections.EMPTY_LIST
                : collectionValues));

        // add empty element to the list for the 'add element' editor
        if ((!newCollectionValues.isEmpty() && newCollectionValues.get(newCollectionValues.size() - 1) != null)
                || newCollectionValues.isEmpty())
        {
            newCollectionValues.add(null);
        }

        return new SimpleListModel(newCollectionValues);
    }

    private void addCollectionElement(final UIEditor editor, final EditorListener listener)
    {
        collectionValues.add(editor.getValue());
        collectionValues = createNewCollectionValuesList(collectionValues);
        setValue(collectionValues);
        updateCollectionItems();
        listener.valueChanged(getValue());
    }

    public List<? extends Object> getAvailableValues()
    {
        return (this.availableValues == null) ? Collections.EMPTY_LIST : this.availableValues;
    }


    public void setAvailableValues(final List<? extends Object> availableValues)
    {
        this.availableValues = availableValues;
    }

    protected List<Object> createNewCollectionValuesList(final Object values)
    {
        return new ArrayList<Object>(values == null ? Collections.EMPTY_LIST
                : ((values instanceof String) && StringUtils.isEmpty((String) values)) ? Collections.EMPTY_LIST
                : (values instanceof Collection ? (Collection) values : Collections.EMPTY_LIST));
    }
}
