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
package de.hybris.platform.core.customdto.order;

import de.hybris.platform.core.dto.c2l.CurrencyDTO;
import de.hybris.platform.core.dto.order.OrderEntryDTO;
import de.hybris.platform.core.dto.order.delivery.DeliveryModeDTO;
import de.hybris.platform.core.dto.order.payment.PaymentInfoDTO;
import de.hybris.platform.core.dto.order.payment.PaymentModeDTO;
import de.hybris.platform.core.dto.order.price.DiscountDTO;
import de.hybris.platform.core.dto.user.AddressDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.webservices.model.nodefactory.GenericNodeFactory;
import de.hybris.platform.webservices.objectgraphtransformer.HybrisEnumValueToStringConverter;
import de.hybris.platform.webservices.objectgraphtransformer.ModifiedProperties;
import de.hybris.platform.webservices.objectgraphtransformer.PkToLongConverter;
import de.hybris.platform.webservices.objectgraphtransformer.StringToHybrisEnumValueConverter;
import de.hybris.platform.webservices.objectgraphtransformer.UriPropertyInterceptor;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;
import de.hybris.platform.webservices.util.objectgraphtransformer.GraphProperty;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


//@GraphNode(target = OrderModel.class, factory = OrderModelFactory.class, addNodes =
//{ DebitPaymentInfoDTO.class, CreditCardPaymentInfoDTO.class, InvoicePaymentInfoDTO.class, AdvancePaymentInfoDTO.class })
@GraphNode(target = OrderModel.class, factory = GenericNodeFactory.class, uidProperties = "code")
@XmlRootElement(name = "order")
public class OrderDTO implements ModifiedProperties
{
	protected final Set<String> modifiedPropsSet = new HashSet();

	// unique attributes
	private String uri;
	private Long pk;
	private String code;

	// primitive attributes
	private Boolean calculated;
	private Date date;
	private Double deliveryCost;
	private Boolean net;
	private Double paymentCost;
	private Double subTotal;
	private Double totalDiscounts;
	private Double totalPrice;
	private Double totalTax;
	private String statusInfo;

	// references
	private AddressDTO deliveryAddress;
	private AddressDTO paymentAddress;
	private UserDTO user;
	private PaymentInfoDTO paymentInfo;
	private PaymentModeDTO paymentMode;
	private DeliveryModeDTO deliveryMode;

	// reference list
	private List<OrderEntryDTO> entries;
	private List<DiscountDTO> discounts;

	// references as string
	private CurrencyDTO currency;
	private String deliveryStatus;
	private String exportStatus;
	private String paymentStatus;
	private String status;

	/* Still missing */
	//globaldiscountvalues
	//totaltaxvalues

	public OrderDTO()
	{
		super();
	}

	public OrderDTO(final Long pk)
	{
		super();
		this.pk = pk;
	}

	@Override
	public Set<String> getModifiedProperties()
	{
		return this.modifiedPropsSet;
	}

	@XmlAttribute
	public Long getPk()
	{
		return pk;
	}

	@GraphProperty(interceptor = PkToLongConverter.class)
	public void setPk(final Long pk)
	{
		this.modifiedPropsSet.add("pk");
		this.pk = pk;
	}

	/**
	 * @return the calculated
	 */
	public Boolean getCalculated()
	{
		return calculated;
	}

	/**
	 * @param calculated
	 *           the calculated to set
	 */
	public void setCalculated(final Boolean calculated)
	{
		this.calculated = calculated;
		this.modifiedPropsSet.add("calculated");
	}

	/**
	 * @return the code
	 */
	@XmlAttribute
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
		this.modifiedPropsSet.add("code");
	}

	/**
	 * @return the date
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 * @param date
	 *           the date to set
	 */
	public void setDate(final Date date)
	{
		this.date = date;
		this.modifiedPropsSet.add("date");
	}

	/**
	 * @return the deliveryCost
	 */
	public Double getDeliveryCost()
	{
		return deliveryCost;
	}

	/**
	 * @param deliveryCost
	 *           the deliveryCost to set
	 */
	public void setDeliveryCost(final Double deliveryCost)
	{
		this.deliveryCost = deliveryCost;
		this.modifiedPropsSet.add("deliveryCost");
	}

	/**
	 * @return the net
	 */
	public Boolean getNet()
	{
		return net;
	}

	/**
	 * @param net
	 *           the net to set
	 */
	public void setNet(final Boolean net)
	{
		this.net = net;
		this.modifiedPropsSet.add("net");
	}

	/**
	 * @return the paymentCost
	 */
	public Double getPaymentCost()
	{
		return paymentCost;
	}

	/**
	 * @param paymentCost
	 *           the paymentCost to set
	 */
	public void setPaymentCost(final Double paymentCost)
	{
		this.paymentCost = paymentCost;
		this.modifiedPropsSet.add("paymentCost");
	}

	/**
	 * @return the statusInfo
	 */
	public String getStatusInfo()
	{
		return statusInfo;
	}

	/**
	 * @param statusInfo
	 *           the statusInfo to set
	 */
	public void setStatusInfo(final String statusInfo)
	{
		this.statusInfo = statusInfo;
		this.modifiedPropsSet.add("statusInfo");
	}

	/**
	 * @return the subTotal
	 */
	public Double getSubTotal()
	{
		return subTotal;
	}

	/**
	 * @param subTotal
	 *           the subTotal to set
	 */
	public void setSubTotal(final Double subTotal)
	{
		this.subTotal = subTotal;
		this.modifiedPropsSet.add("subTotal");
	}

	/**
	 * @return the totalDiscounts
	 */
	public Double getTotalDiscounts()
	{
		return totalDiscounts;
	}

	/**
	 * @param totalDiscounts
	 *           the totalDiscounts to set
	 */
	public void setTotalDiscounts(final Double totalDiscounts)
	{
		this.totalDiscounts = totalDiscounts;
		this.modifiedPropsSet.add("totalDiscounts");
	}

	/**
	 * @return the totalPrice
	 */
	public Double getTotalPrice()
	{
		return totalPrice;
	}

	/**
	 * @param totalPrice
	 *           the totalPrice to set
	 */
	public void setTotalPrice(final Double totalPrice)
	{
		this.totalPrice = totalPrice;
		this.modifiedPropsSet.add("totalPrice");
	}

	/**
	 * @return the totalTax
	 */
	public Double getTotalTax()
	{
		return totalTax;
	}

	/**
	 * @param totalTax
	 *           the totalTax to set
	 */
	public void setTotalTax(final Double totalTax)
	{
		this.totalTax = totalTax;
		this.modifiedPropsSet.add("totalTax");
	}

	/**
	 * @return the currency
	 */
	public CurrencyDTO getCurrency()
	{
		return currency;
	}

	/**
	 * @param currency
	 *           the currency to set
	 */
	public void setCurrency(final CurrencyDTO currency)
	{
		this.currency = currency;
		this.modifiedPropsSet.add("currency");
	}

	/**
	 * @return the uri
	 */
	@XmlAttribute
	public String getUri()
	{
		return uri;
	}

	/**
	 * @param uri
	 *           the uri to set
	 */
	@GraphProperty(virtual = true, interceptor = UriPropertyInterceptor.class)
	public void setUri(final String uri)
	{
		this.uri = uri;
		this.modifiedPropsSet.add("uri");
	}

	/**
	 * @return the entries
	 */
	@XmlElementWrapper(name = "entries")
	@XmlElement(name = "entry")
	public List<OrderEntryDTO> getEntries()
	{
		return entries;
	}

	/**
	 * @param entries
	 *           the entries to set
	 */
	public void setEntries(final List<OrderEntryDTO> entries)
	{
		this.entries = entries;
		this.modifiedPropsSet.add("entries");
	}

	/**
	 * @return the deliveryAddress
	 */
	public AddressDTO getDeliveryAddress()
	{
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress
	 *           the deliveryAddress to set
	 */
	public void setDeliveryAddress(final AddressDTO deliveryAddress)
	{
		this.deliveryAddress = deliveryAddress;
		this.modifiedPropsSet.add("deliveryAddress");
	}

	/**
	 * @return the paymentAddress
	 */
	public AddressDTO getPaymentAddress()
	{
		return paymentAddress;
	}

	/**
	 * @param paymentAddress
	 *           the paymentAddress to set
	 */
	public void setPaymentAddress(final AddressDTO paymentAddress)
	{
		this.paymentAddress = paymentAddress;
		this.modifiedPropsSet.add("paymentAddress");
	}

	/**
	 * @return the user
	 */
	public UserDTO getUser()
	{
		return user;
	}

	/**
	 * @param user
	 *           the user to set
	 */
	public void setUser(final UserDTO user)
	{
		this.user = user;
		this.modifiedPropsSet.add("user");
	}

	/**
	 * @return the deliveryStatus
	 */
	@GraphProperty(interceptor = StringToHybrisEnumValueConverter.class)
	public String getDeliveryStatus()
	{
		return deliveryStatus;
	}

	/**
	 * @param deliveryStatus
	 *           the deliveryStatus to set
	 */
	@GraphProperty(interceptor = HybrisEnumValueToStringConverter.class)
	public void setDeliveryStatus(final String deliveryStatus)
	{
		this.deliveryStatus = deliveryStatus;
		this.modifiedPropsSet.add("deliveryStatus");
	}

	/**
	 * @return the paymentStatus
	 */
	@GraphProperty(interceptor = StringToHybrisEnumValueConverter.class)
	public String getPaymentStatus()
	{
		return paymentStatus;
	}

	/**
	 * @param paymentStatus
	 *           the paymentStatus to set
	 */
	@GraphProperty(interceptor = HybrisEnumValueToStringConverter.class)
	public void setPaymentStatus(final String paymentStatus)
	{
		this.paymentStatus = paymentStatus;
		this.modifiedPropsSet.add("paymentStatus");
	}

	/**
	 * @return the status
	 */
	@GraphProperty(interceptor = StringToHybrisEnumValueConverter.class)
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *           the status to set
	 */
	@GraphProperty(interceptor = HybrisEnumValueToStringConverter.class)
	public void setStatus(final String status)
	{
		this.status = status;
		this.modifiedPropsSet.add("status");
	}

	/**
	 * @return the paymentinfo
	 */
	public PaymentInfoDTO getPaymentInfo()
	{
		return paymentInfo;
	}

	/**
	 * @param paymentInfo
	 *           the paymentinfo to set
	 */
	public void setPaymentInfo(final PaymentInfoDTO paymentInfo)
	{
		this.paymentInfo = paymentInfo;
		this.modifiedPropsSet.add("paymentInfo");
	}

	/**
	 * @return the delivery mode
	 */
	public DeliveryModeDTO getDeliveryMode()
	{
		return deliveryMode;
	}

	/**
	 * @param deliveryMode
	 *           the deliveryMode to set
	 */
	public void setDeliveryMode(final DeliveryModeDTO deliveryMode)
	{
		this.deliveryMode = deliveryMode;
		this.modifiedPropsSet.add("deliveryMode");
	}

	/**
	 * @return the payment mode
	 */
	public PaymentModeDTO getPaymentMode()
	{
		return paymentMode;
	}

	/**
	 * @param paymentMode
	 *           the paymentMode to set
	 */
	public void setPaymentMode(final PaymentModeDTO paymentMode)
	{
		this.paymentMode = paymentMode;
		this.modifiedPropsSet.add("paymentMode");
	}

	/**
	 * @return the exportStatus
	 */
	@GraphProperty(interceptor = StringToHybrisEnumValueConverter.class)
	public String getExportStatus()
	{
		return exportStatus;
	}

	/**
	 * @param exportStatus
	 *           the exportStatus to set
	 */
	@GraphProperty(interceptor = HybrisEnumValueToStringConverter.class)
	public void setExportStatus(final String exportStatus)
	{
		this.exportStatus = exportStatus;
		this.modifiedPropsSet.add("exportStatus");
	}

	/**
	 * @return the discounts
	 */
	@XmlElementWrapper(name = "discounts")
	@XmlElement(name = "discount")
	public List<DiscountDTO> getDiscounts()
	{
		return discounts;
	}

	/**
	 * @param discounts
	 *           the discounts to set
	 */
	public void setDiscounts(final List<DiscountDTO> discounts)
	{
		this.discounts = discounts;
		this.modifiedPropsSet.add("discounts");
	}
}
