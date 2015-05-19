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
package de.hybris.platform.yb2bacceleratorstorefront.controllers.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.product.data.CartEntryData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.util.Config;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.AbstractController;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.ControllerConstants;
import de.hybris.platform.yb2bacceleratorstorefront.controllers.util.GlobalMessages;
import de.hybris.platform.yb2bacceleratorstorefront.forms.AddToCartForm;
import de.hybris.platform.yb2bacceleratorstorefront.forms.AddToCartOrderForm;


/**
 * Controller for Add to Cart functionality which is not specific to a certain page.
 */
@Controller
@Scope("tenant")
public class AddToCartController extends AbstractController {
    private static final String TYPE_MISMATCH_ERROR_CODE = "typeMismatch";
    private static final String ERROR_MSG_TYPE = "errorMsg";
    private static final String QUANTITY_INVALID_BINDING_MESSAGE_KEY = "basket.error.quantity.invalid.binding";

    protected static final Logger LOG = Logger.getLogger(AddToCartController.class);
    private static final Long MINIMUM_SINGLE_SKU_ADD_CART = 0L;
    private static final String SHOWN_PRODUCT_COUNT = "storefront.minicart.shownProductCount";
    public static final String SUCCESSFUL_MODIFICATION_CODE = "success";

    @Resource(name = "cartFacade")
    private CartFacade cartFacade;

    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
    }

    @RequestMapping(value = "/cart/add", method = RequestMethod.POST, produces = "application/json")
    public String addToCart(@RequestParam("productCodePost") final String code, final Model model,
                            @Valid final AddToCartForm form, final BindingResult bindingErrors) {
        if (bindingErrors.hasErrors()) {
            return getViewWithBindingErrorMessages(model, bindingErrors);
        }

        final OrderEntryData orderEntryData = getOrderEntryData(form.getQty(), code, null);
        final CartModificationData modification = cartFacade.addOrderEntry(orderEntryData);

        model.addAttribute("numberShowing", Config.getInt(SHOWN_PRODUCT_COUNT, 3));
        model.addAttribute("modifications", (modification != null ? Lists.newArrayList(modification) : Collections.emptyList()));

        addStatusMessages(model, modification);

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    protected void addStatusMessages(final Model model, final CartModificationData modification) {
        boolean hasMessage = StringUtils.isNotEmpty(modification.getStatusMessage());
        if (hasMessage){
            if (SUCCESSFUL_MODIFICATION_CODE.equals(modification.getStatusCode())) {
                GlobalMessages.addMessage(model, GlobalMessages.CONF_MESSAGES_HOLDER, modification.getStatusMessage(), null);
            } else if (!model.containsAttribute(ERROR_MSG_TYPE)) {
                GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, modification.getStatusMessage(), null);
            }
        }
    }


    @RequestMapping(value = "/cart/addGrid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public final String addGridToCart(@RequestBody final AddToCartOrderForm form, final Model model) {
        List<OrderEntryData> orderEntries = getOrderEntryData(form.getCartEntries());
        final List<CartModificationData> modifications = cartFacade.addOrderEntryList(orderEntries);

        model.addAttribute("modifications", modifications);
        model.addAttribute("numberShowing", Config.getInt(SHOWN_PRODUCT_COUNT, 3));

        for(CartModificationData modification : modifications)
        {
            addStatusMessages(model, modification);
        }

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    protected String getViewWithBindingErrorMessages(final Model model, final BindingResult bindingErrors) {
        for (final ObjectError error : bindingErrors.getAllErrors()) {
            if (error.getCode().equals(TYPE_MISMATCH_ERROR_CODE)) {
                model.addAttribute(ERROR_MSG_TYPE, QUANTITY_INVALID_BINDING_MESSAGE_KEY);
            } else {
                model.addAttribute(ERROR_MSG_TYPE, error.getDefaultMessage());
            }
        }
        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    protected OrderEntryData getOrderEntryData(final long quantity, final String productCode, final Integer entryNumber) {

        OrderEntryData orderEntry = new OrderEntryData();
        orderEntry.setQuantity(quantity);
        orderEntry.setProduct(new ProductData());
        orderEntry.getProduct().setCode(productCode);
        orderEntry.setEntryNumber(entryNumber);

        return orderEntry;
    }


    protected List<OrderEntryData> getOrderEntryData(final List<CartEntryData> cartEntries) {
        List<OrderEntryData> orderEntries = new ArrayList<>();

        for (CartEntryData entry : cartEntries) {
            final Integer entryNumber = entry.getEntryNumber() != null ? entry.getEntryNumber().intValue() : null;
            orderEntries.add(getOrderEntryData(entry.getQuantity(), entry.getSku(), entryNumber));
        }
        return orderEntries;
    }

}
