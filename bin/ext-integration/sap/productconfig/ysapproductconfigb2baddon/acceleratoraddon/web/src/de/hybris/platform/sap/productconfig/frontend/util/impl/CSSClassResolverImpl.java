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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.frontend.util.CSSClassResolver;

import org.springframework.validation.BindingResult;


/**

 * 
 */
public class CSSClassResolverImpl implements CSSClassResolver
{

	public static final String STYLE_CLASS_SEPERATOR = " ";
	public static final String STYLE_CLASS_CSTIC_LABEL = "product-config-csticlabel";
	public static final String STYLE_CLASS_CSTIC_LABEL_ERROR = "product-config-csticlabel-error";
	public static final String STYLE_CLASS_CSTIC_LABEL_REQUIRED = "product-config-csticlabel-required";
	public static final String STYLE_CLASS_CSTIC_LABEL_SUCCESS = "product-config-csticlabel-success";
	public static final String STYLE_CLASS_CSTIC_LABEL_WARNING = "product-config-csticlabel-warning";

	public static final String STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE = "product-config-csticValue";
	public static final String STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_ERROR = "product-config-csticValue-error";
	public static final String STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_MULTI_LIST = "product-config-csticValue-multi";

	public static final String STYLE_CLASS_GROUP = "product-config-group-header";
	public static final String STYLE_CLASS_GROUP_ERROR = "product-config-group-error";
	public static final String STYLE_CLASS_GROUP_WARNING = "product-config-group-warning";
	public static final String STYLE_CLASS_GROUP_PLUS = "product-config-group-title-plus";
	public static final String STYLE_CLASS_GROUP_MINUS = "product-config-group-title-minus";

	public static final String STYLE_CLASS_NODE = "product-config-specification-node";
	public static final String STYLE_CLASS_NODE_ERROR = "product-config-specification-node-error";
	public static final String STYLE_CLASS_NODE_WARNING = "product-config-specification-node-warning";
	public static final String STYLE_CLASS_NODE_PLUS = "product-config-specification-node-plus";
	public static final String STYLE_CLASS_NODE_MINUS = "product-config-specification-node-minus";
	public static final String STYLE_CLASS_NODE_EXPANDABLE = "product-config-specification-node-expandable";

	public static final String STYLE_CLASS_GROUP_TAB = "product-config-tab-title";
	public static final String STYLE_CLASS_GROUP_TAB_ERROR = "product-config-group-tab-error";
	public static final String STYLE_CLASS_GROUP_TAB_WARNING = "product-config-group-tab-warning";

	public static final String STYLE_CLASS_SIDEBAR_COMP_MINUS = "product-config-side-comp-header-minus";
	public static final String STYLE_CLASS_SIDEBAR_COMP_PLUS = "product-config-side-comp-header-plus";
	public static final String STYLE_CLASS_SPECIFICATION_COMP_WARNING = "product-config-specification-comp-warning";
	public static final String STYLE_CLASS_SPECIFICATION_COMP_ERROR = "product-config-specification-comp-error";



	@Override
	public String getLabelStyleClass(final CsticData cstic)
	{
		String styleClassString = STYLE_CLASS_CSTIC_LABEL;
		if (cstic.isRequired())
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_CSTIC_LABEL_REQUIRED);
		}
		switch (cstic.getCsticStatus())
		{
			case ERROR:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_CSTIC_LABEL_ERROR);
				break;
			case WARNING:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_CSTIC_LABEL_WARNING);
				break;
			case FINISHED:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_CSTIC_LABEL_SUCCESS);
				break;
		}

		return styleClassString;
	}

	@Override
	public String getValueStyleClass(final CsticData cstic)
	{
		String styleClassString = STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE;
		final CsticStatusType csticStatus = cstic.getCsticStatus();
		if (CsticStatusType.ERROR.equals(csticStatus) || CsticStatusType.WARNING.equals(csticStatus))
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_ERROR);
		}
		if (isMulitUiElementsType(cstic))
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_PRODUCT_CONFIG_CSTIC_VALUE_MULTI_LIST);
		}

		return styleClassString;
	}

	@Override
	public String getGroupStyleClass(final UiGroupData group)
	{
		String styleClassString = STYLE_CLASS_GROUP;
		switch (group.getGroupStatus())
		{
			case ERROR:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_ERROR);
				break;
			case WARNING:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_WARNING);
				break;
		}

		if (group.isCollapsed())
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_PLUS);
		}
		else
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_MINUS);
		}

		return styleClassString;
	}

	private boolean isMulitUiElementsType(final CsticData cstic)
	{
		return cstic.getType() == UiType.CHECK_BOX_LIST || cstic.getType() == UiType.RADIO_BUTTON;
	}

	protected String appendStyleClass(String styleClassString, final String styleClass)
	{
		if (!styleClassString.isEmpty())
		{
			styleClassString += STYLE_CLASS_SEPERATOR;
		}
		styleClassString += styleClass;
		return styleClassString;
	}

	@Override
	public String getGroupTabStyleClass(final UiGroupData group)
	{
		String styleClassString = STYLE_CLASS_GROUP_TAB;
		switch (group.getGroupStatus())
		{
			case ERROR:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_TAB_ERROR);
				break;
			case WARNING:
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_GROUP_TAB_WARNING);
				break;
		}
		return styleClassString;
	}

	@Override
	public String getNodeStyleClass(final UiGroupData group, final boolean isFirstLevelNode, final boolean showNodeTitle)
	{
		String styleClassString = "";
		if (isFirstLevelNode && showNodeTitle && thereIsContentToExpand(group))
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_NODE_EXPANDABLE);
			if (group.isCollapsedInSpecificationTree())
			{
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_NODE_PLUS);
			}
			else
			{
				styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_NODE_MINUS);
			}
		}
		if (showNodeTitle)
		{
			switch (group.getGroupStatus())
			{
				case ERROR:
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_NODE_ERROR);
					break;
				case WARNING:
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_NODE_WARNING);
					break;
			}
		}
		return styleClassString;
	}


	protected boolean thereIsContentToExpand(final UiGroupData group)
	{
		boolean thereIsContentToExpand = false;
		// we only show first level of non-configurable components
		if (group.isConfigurable())
		{
			final boolean hasSubGroups = group.getSubGroups() != null && group.getSubGroups().size() > 0;
			// if there is only exactly one configurable  sub instance, it is inlined into the parent instance
			// so there is not instance to show
			thereIsContentToExpand = hasSubGroups && !group.isOneConfigurableSubGroup();
			if (!thereIsContentToExpand && hasSubGroups)
			{
				// however even if teis sub instance is inlined into the parent, there might be content on a depper level
				for (final UiGroupData subGroup : group.getSubGroups())
				{
					if (!subGroup.isConfigurable())
					{
						// first level of non-configurable components always shown
						thereIsContentToExpand = true;
						break;
					}
					else
					{
						// check recursively
						thereIsContentToExpand = thereIsContentToExpand(subGroup);
						if (thereIsContentToExpand)
						{
							// if one branch has content, it is sufficient
							break;
						}
					}
				}
			}
		}

		return thereIsContentToExpand;
	}

	@Override
	public String getStyleClassForSideBarComponent(final boolean collapsed, final BindingResult bindResult)
	{
		String styleClassString = "";

		if (collapsed)
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_SIDEBAR_COMP_PLUS);

			if (bindResult != null && bindResult.hasFieldErrors())
			{
				if (ErrorResolver.hasErrorMessages(bindResult))
				{
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_SPECIFICATION_COMP_ERROR);
				}
				else
				{
					styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_SPECIFICATION_COMP_WARNING);
				}
			}
		}
		else
		{
			styleClassString = appendStyleClass(styleClassString, STYLE_CLASS_SIDEBAR_COMP_MINUS);
		}

		return styleClassString.toString();
	}

}
