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
package de.hybris.platform.core.customdto.order.payment;

import de.hybris.platform.core.dto.order.delivery.DeliveryModeDTO;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.webservices.model.nodefactory.GenericNodeFactory;
import de.hybris.platform.webservices.objectgraphtransformer.CodeToComposedTypeConverter;
import de.hybris.platform.webservices.objectgraphtransformer.ComposedTypeToCodeConverter;
import de.hybris.platform.webservices.objectgraphtransformer.ModifiedProperties;
import de.hybris.platform.webservices.objectgraphtransformer.UriPropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@GraphNode(target = PaymentModeModel.class, factory = GenericNodeFactory.class, uidProperties = "code")
@XmlRootElement(name = "paymentmode")
public class PaymentModeDTO implements ModifiedProperties
{
	protected final Set<String> modifiedPropsSet = new HashSet();

	private String code;
	private String uri;

	private String name;
	private String description;
	private Boolean active;
	private List<DeliveryModeDTO> supportedDeliveryModes;
	private String paymentInfoType;

	public PaymentModeDTO()
	{
		super();
	}

	public PaymentModeDTO(final String code)
	{
		this.code = code;
	}

	@XmlAttribute
	public String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.modifiedPropsSet.add("code");
		this.code = code;
	}

	@XmlAttribute
	public String getUri()
	{
		return uri;
	}

	@GraphProperty(virtual = true, interceptor = UriPropertyInterceptor.class)
	public void setUri(final String uri)
	{
		this.uri = uri;
	}

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.modifiedPropsSet.add("name");
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(final String description)
	{
		this.modifiedPropsSet.add("description");
		this.description = description;
	}

	public Boolean getActive()
	{
		return active;
	}

	public void setActive(final Boolean active)
	{
		this.modifiedPropsSet.add("active");
		this.active = active;
	}

	@XmlElementWrapper(name = "deliverymodes")
	@XmlElement(name = "deliverymode")
	public List<DeliveryModeDTO> getSupportedDeliveryModes()
	{
		return supportedDeliveryModes;
	}

	public void setSupportedDeliveryModes(final List<DeliveryModeDTO> supportedDeliveryModes)
	{
		this.modifiedPropsSet.add("supportedDeliveryModes");
		this.supportedDeliveryModes = supportedDeliveryModes;
	}

	@GraphProperty(interceptor = ComposedTypeToCodeConverter.class)
	public void setPaymentInfoType(final String paymentInfoType)
	{
		this.modifiedPropsSet.add("paymentInfoType");
		this.paymentInfoType = paymentInfoType;
	}

	@GraphProperty(interceptor = CodeToComposedTypeConverter.class)
	public String getPaymentInfoType()
	{
		return paymentInfoType;
	}

	@Override
	public Set<String> getModifiedProperties()
	{
		return modifiedPropsSet;
	}

}
