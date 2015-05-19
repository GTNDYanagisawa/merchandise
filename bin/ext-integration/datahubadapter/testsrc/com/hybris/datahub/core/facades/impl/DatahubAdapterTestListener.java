package com.hybris.datahub.core.facades.impl;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import com.hybris.datahub.core.event.DatahubAdapterImportEvent;

import java.util.ArrayList;
import java.util.List;

public class DatahubAdapterTestListener extends AbstractEventListener<DatahubAdapterImportEvent>
{
	private List<DatahubAdapterImportEvent> events;

	public List<DatahubAdapterImportEvent> getEvents()
	{
		return events;
	}

	public void addEvent(final DatahubAdapterImportEvent event)
	{
		if (events == null)
		{
			events = new ArrayList<>();
		}
		events.add(event);
	}

	@Override
	protected void onEvent(final DatahubAdapterImportEvent event)
	{
		addEvent(event);
	}
}
