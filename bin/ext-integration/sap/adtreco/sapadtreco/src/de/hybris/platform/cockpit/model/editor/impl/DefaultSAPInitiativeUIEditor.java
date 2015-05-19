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
package de.hybris.platform.cockpit.model.editor.impl;

import de.hybris.platform.cockpit.model.editor.EditorListener;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.core.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import com.sap.wec.adtreco.bo.impl.SAPInitiative;
import com.sap.wec.adtreco.bo.intf.SAPInitiativeReader;


/**
 * Simple text editor.
 */
public class DefaultSAPInitiativeUIEditor extends AbstractTextBasedUIEditor
{
	private static final Logger LOG = Logger.getLogger(DefaultSAPInitiativeUIEditor.class); // NOPMD
	@SuppressWarnings("unused")
	private static final String EDITOR_TOOLTIP_PARAM = "editorTooltip";
	private static final String ENUM_EDITOR_SCLASS = "enumEditor";

	private List<? extends Object> availableValues;
	private List<? extends Object> originalAvailableValues;
	private final Combobox editorView = new Combobox();
	private String searchString = "";
	private SAPInitiativeReader initiativeReader;
	private final boolean inUpdate = false;

	private final List<String> eventValues = new ArrayList<String>();
	private String lastSearch;

	protected Comboitem addInitiativeToCombo(final SAPInitiative initiative, final Combobox box)
	{
		String label = initiative.getId() + " " + initiative.getName() + " (" + initiative.getMemberCount() + ")";
		final String value = initiative.getId();
		final Comboitem comboitem = new Comboitem();

		if (label == null || label.isEmpty())
		{
			label = "unknown";
		}
		comboitem.setLabel(label);
		comboitem.setValue(value);
		comboitem.setTooltiptext(label);
		box.appendChild(comboitem);
		return comboitem;
	}


	protected int getPosition(final Object item)
	{
		int index = -1;
		if (availableValues != null)
		{
			index = getAvailableValues().indexOf(item);
		}
		return index;
	}

	protected void setEnumValue(final Combobox combo, final Object value)
	{
		final int index = getPosition(value);
		if (index >= 0)
		{
			combo.setSelectedIndex(index);
		}
	}

	@Override
	public HtmlBasedComponent createViewComponent(final Object initialValue, final Map<String, ? extends Object> parameters,
			final EditorListener listener)
	{
		parseInitialInputString(parameters);
		SAPInitiative initiative = null;

		editorView.setConstraint("strict");
		editorView.setSclass("initiative-combo");
		editorView.setAutodrop(true);

		final String intialValueString = (String) initialValue;
		if (intialValueString != null && !intialValueString.isEmpty())
		{
			initiative = getInitiativeReader().getSelectedInitiative(intialValueString);
		}

		if (isEditable())
		{
			if (initiative != null)
			{
				final Comboitem item = addInitiativeToCombo(initiative, editorView);
				editorView.setSelectedItem(item);
			}

			final CancelButtonContainer ret = new CancelButtonContainer(listener, new CancelListener()
			{
				@Override
				public void cancelPressed()
				{
					setEnumValue(editorView, initialEditValue);
					setValue(initialEditValue);
					fireValueChanged(listener);
					listener.actionPerformed(EditorListener.ESCAPE_PRESSED);
				}
			});

			ret.setSclass(ENUM_EDITOR_SCLASS);
			ret.setContent(editorView);


			editorView.addEventListener(Events.ON_FOCUS, new EventListener()
			{

				@Override
				public void onEvent(final Event event) throws Exception
				{
					if (editorView.getSelectedItem() != null)
					{
						initialEditValue = editorView.getSelectedItem().getValue();
					}
					ret.showButton(Boolean.TRUE.booleanValue());
				}
			});

			editorView.addEventListener(Events.ON_CHANGE, new EventListener()
			{
				@Override
				public void onEvent(final Event arg0) throws Exception // NOPMD zk specific
				{
					validateAndFireEvent(listener);
				}
			});
			editorView.addEventListener(Events.ON_BLUR, new EventListener()
			{
				@Override
				public void onEvent(final Event arg0) throws Exception // NOPMD zk specific
				{
					ret.showButton(Boolean.FALSE.booleanValue());
				}
			});
			editorView.addEventListener(Events.ON_OK, new EventListener()
			{
				@Override
				public void onEvent(final Event arg0) throws Exception // NOPMD zk specific
				{
					validateAndFireEvent(listener);
					listener.actionPerformed(EditorListener.ENTER_PRESSED);
				}
			});
			editorView.addEventListener(Events.ON_CANCEL, new EventListener()
			{
				@Override
				public void onEvent(final Event arg0) throws Exception // NOPMD zk specific
				{
					ret.showButton(Boolean.FALSE.booleanValue());
					DefaultSAPInitiativeUIEditor.this.setEnumValue(editorView, initialEditValue);
					setValue(initialEditValue);
					fireValueChanged(listener);
					listener.actionPerformed(EditorListener.ESCAPE_PRESSED);
				}
			});

			editorView.addEventListener(Events.ON_CHANGING, new EventListener()
			{
				@Override
				public void onEvent(final Event event) throws Exception // NOPMD zk specific
				{
					ret.showButton(true);
					handleChangingEvents(listener, event);
				}
			});


			if (UISessionUtils.getCurrentSession().isUsingTestIDs())
			{
				String id = "Enum_";
				String attributeQualifier = (String) parameters.get(AbstractUIEditor.ATTRIBUTE_QUALIFIER_PARAM);
				if (attributeQualifier != null)
				{
					attributeQualifier = attributeQualifier.replaceAll("\\W", "");
					id = id + attributeQualifier;
				}
				UITools.applyTestID(editorView, id);
			}

			return ret;
		}
		else
		{
			editorView.setDisabled(true);

			final Label ret;
			if (initiative != null)
			{
				ret = new Label(initiative.getId() + " " + initiative.getName());
			}
			else
			{
				ret = new Label(" ");
			}
			return ret;
		}
	}

	@Override
	public boolean isInline()
	{
		return true;
	}


	@Override
	public String getEditorType()
	{
		return PropertyDescriptor.TEXT;
	}

	public List<? extends Object> getAvailableValues()
	{
		return this.availableValues;
	}

	public void setAvailableValues(final List<? extends Object> availableValues)
	{
		if (availableValues == null || availableValues.isEmpty())
		{
			editorView.setValue(Labels.getLabel("general.nothingtodisplay"));
			editorView.setDisabled(true);
			this.availableValues = null;
			this.originalAvailableValues = null;
		}
		else
		{
			this.availableValues = new ArrayList<Object>(availableValues);
			if (isOptional())
			{
				this.availableValues.add(0, null);
			}
			this.originalAvailableValues = new ArrayList<Object>(availableValues);
		}
	}

	@Override
	public void setFocus(final HtmlBasedComponent rootEditorComponent, final boolean selectAll)
	{
		final Combobox element = (Combobox) ((CancelButtonContainer) rootEditorComponent).getContent();
		element.setFocus(true);

		if (initialInputString != null)
		{
			element.setText(initialInputString);
		}
	}

	@Override
	public void setOptional(final boolean optional)
	{
		if (!optional)
		{
			availableValues = originalAvailableValues;
		}
		super.setOptional(optional);
	}

	protected void validateAndFireEvent(final EditorListener listener)
	{
		if (editorView.getSelectedItem() == null)
		{
			setEnumValue(editorView, initialEditValue);
		}
		else
		{
			DefaultSAPInitiativeUIEditor.this.setValue(editorView.getSelectedItem().getValue());
			editorView.setTooltiptext(ObjectUtils.toString(editorView.getSelectedItem().getValue()));
			listener.valueChanged(getValue());
		}
	}

	protected void handleChangingEvents(final EditorListener listener, final Event event)
	{
		final String newSearchString = ((InputEvent) event).getValue();
		lastSearch = newSearchString;
		if (newSearchString.length() >= 3 && !searchString.equals(newSearchString))
		{
			final List<SAPInitiative> initiatives = searchValues(newSearchString);
			synchronized (this)
			{
				if (newSearchString.equals(lastSearch))
				{
					searchString = lastSearch;
					clearComboBox();
					fillComboBox(initiatives);
					listener.valueChanged(getValue());
				}
			}
		}
	}

	/**
	 * 
	 */
	protected List<SAPInitiative> searchValues(final String newSearchString)
	{
		final String exceptionText = de.hybris.platform.util.localization.Localization
				.getLocalizedString("connectionError.description");
		final String exceptionTitle = de.hybris.platform.util.localization.Localization.getLocalizedString("connectionError.title");

		final SAPInitiativeReader reader = getInitiativeReader();
		List<SAPInitiative> initiatives = new ArrayList<SAPInitiative>();

		try
		{
			initiatives = reader.searchInitiatives(newSearchString);
		}
		catch (Exception e)
		{
			LOG.error("", e);
			try
			{
				Messagebox.show(exceptionText, exceptionTitle, Messagebox.OK, Messagebox.ERROR);
			}
			catch (final Exception e1)
			{

			}
		}
		return initiatives;
	}

	/**
	 * 
	 */
	protected void fillComboBox(final List<SAPInitiative> initiatives)
	{
		for (final SAPInitiative initiative : initiatives)
		{
			addInitiativeToCombo(initiative, editorView);
		}

	}


	/**
	 * 
	 */
	protected SAPInitiativeReader getInitiativeReader()
	{
		if (initiativeReader != null)
		{
			return initiativeReader;
		}
		return initiativeReader = (SAPInitiativeReader) Registry.getApplicationContext().getBean("sapInitiativeReader");
	}

	/**
	 * 
	 */
	protected void clearComboBox()
	{
		final int size = editorView.getChildren().size();
		for (int i = 0; i < size; i++)
		{
			editorView.removeItemAt(0);
		}
	}

	protected Combobox getEditorView()
	{
		return editorView;
	}

}
