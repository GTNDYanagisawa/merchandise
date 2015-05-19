package de.hybris.platform.saporderexchange;

import de.hybris.platform.hmc.AbstractEditorMenuChip;
import de.hybris.platform.hmc.AbstractExplorerMenuTreeNodeChip;
import de.hybris.platform.hmc.EditorTabChip;
import de.hybris.platform.hmc.extension.HMCExtension;
import de.hybris.platform.hmc.extension.MenuEntrySlotEntry;
import de.hybris.platform.hmc.generic.ClipChip;
import de.hybris.platform.hmc.generic.ToolbarActionChip;
import de.hybris.platform.hmc.webchips.Chip;
import de.hybris.platform.hmc.webchips.DisplayState;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


/**
 * HMCExtension for SAP Order Exchange
 */
public class SaporderexchangeHMCExtension extends HMCExtension
{

	/**
	 * Edit the local|project.properties to change logging behavior (properties log4j.*).
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SaporderexchangeHMCExtension.class.getName());

	/** Path to the resource bundles. */
	public final static String RESOURCE_PATH = "de.hybris.platform.saporderexchange.hmc.locales";


	@Override
	public ResourceBundle getLocalizeResourceBundle(final Locale locale)
	{
		return null;
	}

	@Override
	public String getResourcePath()
	{
		return RESOURCE_PATH;
	}

	@Override
	public List<EditorTabChip> getEditorTabChips(final DisplayState arg0, final AbstractEditorMenuChip arg1)
	{
		return Collections.emptyList();
	}


	@Override
	public List<MenuEntrySlotEntry> getMenuEntrySlotEntries(final DisplayState arg0, final Chip arg1)
	{
		return Collections.emptyList();
	}

	@Override
	public List<ClipChip> getSectionChips(final DisplayState arg0, final ClipChip arg1)
	{
		return Collections.emptyList();
	}

	@Override
	public List<ToolbarActionChip> getToolbarActionChips(final DisplayState arg0, final Chip arg1)
	{
		return Collections.emptyList();
	}

	@Override
	public List<AbstractExplorerMenuTreeNodeChip> getTreeNodeChips(final DisplayState arg0, final Chip arg1)
	{
		return Collections.emptyList();
	}

}
