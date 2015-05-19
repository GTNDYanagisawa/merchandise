var ysapproductconfigb2baddonConstants = {};
var scrollListenerSet = false;
var sideBarHeight = 0;

ysapproductconfigb2baddonConstants.init = function(baseUrl, addToCartText,
		updateCartText) {
	ysapproductconfigb2baddonConstants.baseUrl = baseUrl;
	ysapproductconfigb2baddonConstants.addToCartText = addToCartText;
	ysapproductconfigb2baddonConstants.updateCartText = updateCartText;
	ysapproductconfigb2baddonConstants.ajaxRunning = false;
	ysapproductconfigb2baddonConstants.ajaxRunCounter = 0;
	ysapproductconfigb2baddonConstants.resizeing = false;
};

function getConfigureUrl() {
	return ysapproductconfigb2baddonConstants.baseUrl + "/config";
}
function getConfigureRedirectUrl() {
	return ysapproductconfigb2baddonConstants.baseUrl + "/redirectconfig";
}
function getAddToCartUrl() {
	return ysapproductconfigb2baddonConstants.baseUrl + "/addToCart";
}
function getResetUrl() {
	return ysapproductconfigb2baddonConstants.baseUrl + "/reset";
}
function getCopyUrl() {
	return ysapproductconfigb2baddonConstants.baseUrl + "/copy";
}

function getAddToCartText() {
	return ysapproductconfigb2baddonConstants.addToCartText;
}
function getUpdateCartText() {
	return ysapproductconfigb2baddonConstants.updateCartText;
}

function showSecondAddToCart() {
	var formHeight = $('#configurationForm').height();
	var browserHeight = $(window).height();
	if ((formHeight / 2) < browserHeight) {
		$('.product-config-addtocart-showonly').hide();
	} else {
		$('.product-config-addtocart-showonly').show();
	}
}

function registerGroupToggle() {
	$('.product-config-groups .product-config-group-header').on(
			'click keypress',
			function(e) {
				$(this).next().toggle("blind", 100);
				var groupId = $(this).attr('id');
				// Id has format GROUPID_title, replace suffix '_title' with
				// suffix '_collapsed'
				var targetId = jq(groupId.substring(0, groupId.length - 6)
						+ "_collapsed");
				toggleElement(e, $(this), targetId,
						"product-config-group-title");

				return false;
			});

	$('.product-config-specification-node-expandable').on(
			'click',
			function(e) {
				$(this).next(".product-config-specification-node-sub").toggle(
						"blind", 100);
				var groupId = $(this).parent().attr('id');
				// Id has format GROUPID_title, replace suffix '_title' with
				// suffix '_collapsed'
				var targetId = jq(groupId.substring(0, groupId.length - 6)
						+ "_collapsedInSpec");
				toggleElement(e, $(this), targetId,
						"product-config-specification-node");

				return false;
			});

	$('#specificationTitle').on('click', function(e) {
		$("#specificationContent").toggle("blind", 100);

		var targetId = "#specificationTreeCollapsed";
		toggleElement(e, $(this), targetId, "product-config-side-comp-header");

		return false;
	});

	$('#priceSummaryTitle').on('click', function(e) {
		$("#priceSummarySubContent").toggle("blind", 100);

		var targetId = "#priceSummaryCollapsed";
		toggleElement(e, $(this), targetId, "product-config-side-comp-header");

		return false;
	});

	$('.product-config-specification-node-title-link')
			.on(
					'click',
					function(e) {

						var groupId = $(this).parent().attr('id');
						// Id has format node_GROUPID_title,remove prefix
						// 'node_'
						groupId = jq(groupId.substring(5, groupId.length));
						var groupElem = $(groupId);
						// does target group exist?
						if (groupElem[0]) {
							// Yes - is it a Component or a TAB
							if (groupElem.hasClass("product-config-tab-title")) {
								// A TAB is it already selected?
								if (groupElem
										.hasClass("product-config-tab-selected")) {
									// yes - just set focus
									restoreFocus(groupElem.attr('id'), true);
								} else {
									// no, navigate to tab
									$("#focusId").val(groupElem.attr('id'));
									var tabToExpand = groupElem.prev().attr(
											'value');
									firePost(toggleTab,
											[ e, tabToExpand, false ]);
								}
							} else {
								// A component, expand it
								expandTreeFromLeave(e, groupElem);
							}
						} else {
							// NO - it must be a component on another TAB
							// expand the corresponding tree and switch tab
							$("#focusId").val(
									groupId.substring(1, groupId.length));
							var tabName = expandHiddenTreeFromLeave(e, groupId)
							var tabName = jq(tabName.substring(0, tabName
									.indexOf('.'))
									+ ".id");
							var tabName = tabName.substring(1, tabName.length);
							var tabToExpand = $('[name=' + tabName + ']').attr(
									'value');
							firePost(toggleTab, [ e, tabToExpand, false ]);
						}

						return false;
					});

}

function toggleElement(e, element, targetId, prefix) {
	if (element.hasClass(prefix + "-plus")) {
		element.removeClass(prefix + "-plus");
		element.addClass(prefix + "-minus");
		$(targetId).val("false");
	} else {
		element.removeClass(prefix + "-minus");
		element.addClass(prefix + "-plus");
		$(targetId).val("true");
	}

	var data = $('#configform').serialize();
	firePost(doUpdatePost, [ e, data ]);

	return false;
}

function expandHiddenTreeFromLeave(e, groupId) {
	var targetId = groupId.substring(0, groupId.length - 6) + "_collapsed";
	$(targetId).val("false");
	var parentsToExpand = $(targetId).parents('.product-config-group-hidden')
			.children("input[id$='collapsed']")
	for (var i = 0; i < parentsToExpand.length; i++) {
		$(parentsToExpand[i]).val("false");
	}
	return $(targetId).attr('name');
}

function expandTreeFromLeave(e, groupElem) {
	var somethingExpanded = false
	var parentsToExpand = groupElem.parents().prev(
			".product-config-group-title-plus");
	for (var i = parentsToExpand.length - 1; i >= 0; i--) {
		var expandExecuted = expandGroupIfRequired($(parentsToExpand[i]));
		somethingExpanded = somethingExpanded || expandExecuted;
	}
	var expandExecuted = expandGroupIfRequired(groupElem);
	somethingExpanded = somethingExpanded || expandExecuted;

	focusId = groupElem.attr('id');
	if (somethingExpanded) {
		var data = $('#configform').serialize();
		firePost(doUpdatePost, [ e, data, false, focusId ]);
	} else {
		restoreFocus(focusId, true);
	}
}

function expandGroupIfRequired(groupElem) {

	if (groupElem.hasClass("product-config-group-title-plus")) {
		groupElem.next().toggle("blind", 100);

		groupElem.removeClass("product-config-group-title-plus");
		groupElem.addClass("product-config-group-title-minus");
		var groupId = groupElem.attr('id');
		var targetId = jq(groupId.substring(0, groupId.length - 6)
				+ "_collapsed");
		$(targetId).val("false");
		return true;
	}
	return false;

}

function toggleTab(e, tabName, expandFirstGroupWithError) {
	$("#selectedGroup").val(tabName);

	var form = $('#configform')[0];

	var url = getConfigureRedirectUrl();
	if (expandFirstGroupWithError) {
		$("#autoExpand").val(true);
	}
	form.setAttribute("action", url);
	form.submit();

}

function setGroupVisible(element, visible) {
	if (visible) {
		if (element.hasClass("product-config-group-title-plus")) {
			element.removeClass("product-config-group-title-plus");
			element.addClass("product-config-group-title-minus");
		}
		element.next().show();
	} else {
		if (element.hasClass("product-config-group-title-minus")) {
			element.removeClass("product-config-group-title-minus");
			element.addClass("product-config-group-title-plus");

		}
		element.next().hide();
	}
}

function initGroups() {
	var groups = $('.product-config-group-title-plus')
	if (groups.length <= 1)
		return;

	for (var i = 0; i < groups.length; i++) {
		$(groups[i]).next().hide();
	}

	var resizeTimer;
	clearTimeout(resizeTimer);
	resizeTimer = setTimeout(showSecondAddToCart, 99);
}

function saveGroups(className) {
	var groupArray = [];
	var groups = $(className);

	for (var i = 0; i < groups.length; i++) {
		var group = groups[i];
		groupArray.push(group.id);
	}

	return groupArray;
}

function expandFirstGroupWithError() {
	var errorGroups = $('.product-config-group-warning,.product-config-group-error');
	var lastGroup = undefined;
	for (var i = 0; i < errorGroups.length; i++) {
		var group = $(errorGroups[i]);
		if (lastGroup && lastGroup.next().has(group).length == 0) {
			break;
		}
		expandGroupIfRequired(group);
		lastGroup = group;
	}

	if (!lastGroup) {
		lastGroup = $('.product-config-groups');
	} else {
		lastGroup = lastGroup.next();
	}
	var errorCstic = lastGroup
			.find(
					'.product-config-csticlabel-warning,.product-config-csticlabel-error')
			.first().next().find("input,select").first();

	focusOnInput(errorCstic.attr('name'));

}

function expandFirstTabWithError(e) {
	var tabToExpand = undefined;
	var currentTab = $('.product-config-tab-selected');
	if (!hasTabErrors(currentTab)) {
		var allErrorTabs = $('.product-config-group-tab-warning,.product-config-group-tab-error');
		if (allErrorTabs.length > 0) {
			tabToExpand = getGroupNameFromTab(allErrorTabs[0]);
		}
	}
	if (tabToExpand) {
		toggleTab(tabToExpand);
		firePost(toggleTab, [ e, tabToExpand, true ]);
	} else {
		data = $('#configform').serialize();
		firePost(doUpdatePost, [ e, data, true ]);
	}
}

function getGroupNameFromTab(tab) {
	return $(tab).prev().attr('value');
}

function hasTabErrors(tab) {
	var jQueryElem = $(tab);
	var hasErrors = jQueryElem.hasClass('product-config-group-tab-warning')
			|| jQueryElem.hasClass('product-config-group-tab-error');
	return hasErrors;
}

function restoreGroupStatus(groupArray, visible) {
	for (var i = 0; i < groupArray.length; i = i + 1) {
		try {
			var element = $('#' + groupArray[i]);
			if (element) {
				setGroupVisible(element, visible);
			}
		} catch (e) {
		}
	}
}

function firePost(fx, args) {
	ysapproductconfigb2baddonConstants.ajaxRunCounter++;
	waitToFirePost(fx, args);	
}

function waitToFirePost(fx, args) {
	if (ysapproductconfigb2baddonConstants.ajaxRunning == true) {
		setTimeout(function() {
			waitToFirePost.call(this, fx, args)
		}, 100);
	} else {
		ysapproductconfigb2baddonConstants.ajaxRunning = true
		// setTimeout(function() {
		fx.apply(this, args);
		// }, 50);
	}
}


function saveFocus() {
	return document.activeElement.id;
}

function restoreFocus(focusElementId, srollTo) {
	var focusElement = $(jq(focusElementId));
	if (focusElement[0]) {
		// use focus methos of DOM Object
		focusElement[0].focus();
		if (srollTo) {
			var offset = focusElement.offset();
			window.scrollTo(0, offset.top);
		}
	}

}

function registerAjax() {

	$('#configform').submit(function(e) {
		e.preventDefault();
	});

	$('#configform :input').change(function(e) {
		data = $('#configform').serialize();
		setTimeout(function() {
			firePost(doUpdatePost, [ e, data ])
		}, 50);
	});

	$(document).ajaxError(function(event, xhr, settings, exception) {
		document.write(xhr.responseText);
	});

}

function focusOnInput(elementName) {
	// foucs on Input if existent
	var nodeList = document.getElementsByName(elementName);
	var length = nodeList.length;

	var focusElem;
	if (length > 0) {
		// length == 1: a simple input field / DDLB ==> focus on it;
		// length > 1: radio buttons ==> focus on first element
		focusElem = $(nodeList[0]);
	} else { // length = 0
		// maybe a check box List?
		var firstCheckboxName = elementName.slice(0, elementName.length - 5);
		firstCheckboxName += "domainvalues[0].selected";
		nodeList = document.getElementsByName(firstCheckboxName);
		length = nodeList.length;
		// focus on first checkbox
		if (length > 0) {
			focusElem = $(nodeList[0]);
		} else {
			// no input at all, could be a read only field,
			// focus on Error Message instead
			var id = elementName + ".errors";
			// escape special chars in id
			id = id.replace(/\./g, "\\.");
			id = id.replace(/\]/g, "\\]");
			id = id.replace(/\[/g, "\\[");
			focusElem = $("#" + id);
		}
	}
	// call focus of DOM element, not jQuery element (works better)
	focusElem[0].focus();
	var offset = focusElem.offset();
	var safetyBuffer = 100;
	var delay = 100;
	$('html, body').animate(
			$.scrollTo(focusElem.closest("div.product-config-cstic")), delay);
}

function doUpdatePost(e, data, openErrorGroups, focusId) {
	$
			.post(
					getConfigureUrl(),
					data,
					function(response) {
						if (ysapproductconfigb2baddonConstants.ajaxRunCounter == 1) {
							var focusElementId;
							var scrollTo;
							if (focusId) {
								focusElementId = focusId;
								scrollTo = true;
							} else {
								focusElementId = saveFocus();
								scrollTo = false;
							}
							var expandedGroupArray = saveGroups('.product-config-group-title-minus');
							var collapsedGroupArray = saveGroups('.product-config-group-title-plus');
							updateContent(response);
							restoreGroupStatus(expandedGroupArray, true);
							restoreGroupStatus(collapsedGroupArray, false);
							restoreFocus(focusElementId, scrollTo);
							doAfterPost();
							if (openErrorGroups) {
								expandFirstGroupWithError();
							}}
						ysapproductconfigb2baddonConstants.ajaxRunning = false;
						ysapproductconfigb2baddonConstants.ajaxRunCounter--;
					});

	e.preventDefault();
}

function updateContent(response) {
	updateSlotContent(response, 'configNavBarSlot');
	updateSlotContent(response, 'configContentSlot');
	updateSlotContent(response, 'configSidebarSlot');
}

function updateSlotContent(response, slotName) {
	var newSlotContent = getNewSlotContent(response, slotName);
	$('#' + slotName).replaceWith(newSlotContent);
}

function getNewSlotContent(response, slotName) {
	var startTag = '<div id="start:' + slotName + '"/>';
	var endTag = '<div id="end:' + slotName + '"/>';
	var newContent = "";

	var startIndex = response.indexOf(startTag);
	if (startIndex != -1) {
		startIndex = startIndex + startTag.length;
		var endIndex = response.indexOf(endTag)
		if (endIndex != -1) {
			newContent = response.substring(startIndex, endIndex);
		}
	}

	return newContent;
}

function doAddToCartPost(e) {
	var headButton = $('.product-config-addtocart .add_to_cart_button');
	if (headButton.length == 0) {
		ysapproductconfigb2baddonConstants.ajaxRunning = false;
		ysapproductconfigb2baddonConstants.ajaxRunCounter--;
		return;
	}
	if ($(headButton[0]).hasClass('out-of-stock')
			|| $(headButton[0]).hasClass('product-config-addtocart-inactive')) {
		ysapproductconfigb2baddonConstants.ajaxRunning = false;
		ysapproductconfigb2baddonConstants.ajaxRunCounter--;
		return;
	}

	var form = $('#configform')[0];

	form.setAttribute("action", getAddToCartUrl());
	form.submit();
}

function doAfterPost() {
	scrollListenerSet = false;
	registerGroupToggle();
	showSecondAddToCart();
	checkAddToCartStatus();
	addToCartPopupSetup();
	checkUpdateMode()
	registerAjax();
	checkSidebarHeight();
	registerLongTextMore()
}

function registerLongTextMore() {
	$(".product-config-cstic-morelink").on(
			'click',
			function(e) {
				var csticKey = $(this)
						.parent(".product-config-cstic-long-text").attr('id');
				var targetId = jq(csticKey.substring(0, csticKey.length - 9)
						+ ".showFullLongText");

				$(targetId).val("true");

				$(this).next().show();
				$(this).hide();

				var data = $('#configform').serialize();
				firePost(doUpdatePost, [ e, data ]);
			});

	$(".product-config-cstic-lesslink").on(
			'click',
			function(e) {
				var csticKey = $(this).parent(".product-config-cstic-moretext")
						.parent(".product-config-cstic-long-text").attr('id');
				var targetId = jq(csticKey.substring(0, csticKey.length - 9)
						+ ".showFullLongText");

				$(targetId).val("false");

				$(this).parent('.product-config-cstic-moretext').hide();
				$(this).parent('.product-config-cstic-moretext').prev().show();

				var data = $('#configform').serialize();
				firePost(doUpdatePost, [ e, data ]);
			});
}

function initConfigPage() {
	$(window).resize(function() {
		var resizeTimer;
		clearTimeout(resizeTimer);
		resizeTimer = setTimeout(showSecondAddToCart, 100);

		if (ysapproductconfigb2baddonConstants.resizeing) {
			return;
		}

		ysapproductconfigb2baddonConstants.resizeing = true;
		checkSidebarHeight();
	});

	resizeTimer = setTimeout(function() {
		var elements = $('.span-24-pc');
		for (var i = 0; i < elements.length; i++) {
			$(elements[i]).removeClass('span-24-pc');
			$(elements[i]).addClass('span-24');
		}
	}, 100);

	doAfterPost();
	ysapproductconfigb2baddonConstants.ajaxRunning = false;
	ysapproductconfigb2baddonConstants.ajaxRunCounter = 0;
	registerAddToCartButton();
	initGroups();

	if ($("#autoExpand").attr('value') == 'true') {
		expandFirstGroupWithError();
		$("#autoExpand").val(false);
	}

	var tabNav = $('#configTabNavigation');
	var param = new RegExp('[\\?&amp;]' + 'tab' + '=([^&amp;#]*)')
			.exec(window.location.href);
	if ($("#focusId").attr('value').length > 0) {
		restoreFocus($("#focusId").attr('value'), true);
		$("#focusId").val("");
	}
}

function checkSidebarHeight() {
	var windowHeight = $(window).height();
	var specContent = $(".product-config-specification-content");
	var currentSideBarHeight = $("#configSidebarSlot").height();

	if (sideBarHeight == 0) {
		sideBarHeight = currentSideBarHeight;
		var content = $("#configContent");

		if (content.height() < sideBarHeight) {
			content.css({
				height : sideBarHeight
			});
		}
	}

	if (sideBarHeight + 10 < windowHeight) {
		specContent.css({
			height : "auto"
		});
		specificationFollowsScrolling();
		ysapproductconfigb2baddonConstants.resizeing = false;
		return 0;
	}

	var heightOther = $("#specificationTitle").height()
			+ $("#priceSummary").height() + 60;
	var maxHeightSpecContent = windowHeight - heightOther;

	specContent.css({
		height : maxHeightSpecContent
	});

	$('.scrollbar-inner').scrollbar();

	$(".product-config-specification-content-scroll-wrapper").css({
		paddingRight : 10,
		height : maxHeightSpecContent
	});

	specificationFollowsScrolling();
	ysapproductconfigb2baddonConstants.resizeing = false;
}

function specificationFollowsScrolling() {
	var specTree = $("#configSidebarSlot");
	if (specTree != null) {
		try {
			var top = specTree.offset().top
					- parseFloat(specTree.css("marginTop").replace(/auto/, 0));

			var parentDiv = $("#configSidebar").parent();
			var bottom = parentDiv.offset().top + parentDiv.height();
			var specHeight = specTree.height();

			var yT = $(window).scrollTop();
			if (yT >= top) {
				specTree.addClass('fixed');
				if (yT + specHeight < bottom) {
					specTree.css({
						top : 0
					});
				} else {
					var specTop = yT + specHeight
					specTree.css({
						top : bottom - specTop
					});
				}
			} else {
				specTree.removeClass('fixed');
			}

			if (!scrollListenerSet) {
				$(window).scroll(
						function(event) {
							var yT = $(this).scrollTop();

							if (yT >= top) {
								var parentDiv = $("#configSidebar").parent();
								var bottom = parentDiv.offset().top
										+ parentDiv.height();

								specTree.addClass('fixed');
								if (yT + specHeight < bottom) {
									specTree.css({
										top : 0
									});
								} else {
									var specTop = yT + specHeight
									specTree.css({
										top : bottom - specTop
									});
								}
							} else {
								specTree.removeClass('fixed');
							}

							var content = $("#configContent");

							if (content.height() < specHeight) {
								content.css({
									height : specHeight
								});
							}
						});
				scrollListenerSet = true;
			}

		} catch (e) {
		}
	}
}

function registerAddToCartButton() {
	var postFunction = function(e) {
		firePost(doAddToCartPost, [ e ]);
	}
	// use delegated events (selector evaluated on click), as the button itself
	// is dynamically reloaded on POST,
	$(document).on('click', '.product-config-addtocart .add_to_cart_button',
			postFunction);
}

function fadeOutPopup() {
	$("#product-config-addToCartPopupDialog").fadeOut("50");
}

function fadeInPopup() {
	$("#product-config-addToCartPopupDialog").fadeIn("50");
}

function addToCartPopupSetup() {

	$("#resolveLink").on('click', function(e) {
		fadeOutPopup();
		expandFirstTabWithError(e);
	});
	$(".product-config-addToCartPopup .close").on('click', function(e) {
		fadeOutPopup();
		data = $('#configform').serialize();
		firePost(doUpdatePost, [ e, data ]);
	});

	$("#sameConfigLink").on('click', function(e) {
		fadeOutPopup();
		firePost(copyAndRedirect, [ e, getConfigureUrl() ]);
	});

	$("#resetLink").on('click', function(e) {
		fadeOutPopup();
		firePost(resetAndRedirect, [ e, getConfigureUrl() ]);
	});
	$("#homeLink").on('click', function(e) {
		fadeOutPopup();
		firePost(resetAndRedirect, [ e, '/' ]);
	});
	$("#checkoutLink").on('click', function(e) {
		fadeOutPopup();
		firePost(resetAndRedirect, [ e, '/cart' ]);
	});

	$("#showMoreLink").on('click', function(e) {
		$(".product-config-addToCartPopup .more").show();
		$("#showMoreLink").hide();
	});
	$(".product-config-addToCartPopup .more").hide();

	fadeInPopup();
}

function checkUpdateMode() {
	var btnText;
	if ($("#cartItemPK").prop('value') === '') {
		btnText = getAddToCartText();
	} else {
		btnText = getUpdateCartText();
	}

	$(".product-config-addtocart .add_to_cart_button").prop('textContent',
			btnText);
}

function resetAndRedirect(e, url) {
	var input = $('input[name=CSRFToken]');
	var token = null;
	var form = null;

	if (input && input.length != 0) {
		token = input.attr("value");
	}
	if (token) {
		form = $('<form action="' + getResetUrl()
				+ '" method="post" style="display: none;">'
				+ '<input type="text" name="url" value="' + url + '" />'
				+ '<input type="hidden" name="CSRFToken" value="' + token
				+ '" />' + '</form>');
	} else {
		form = $('<form action="' + getResetUrl()
				+ '" method="post" style="display: none;">'
				+ '<input type="text" name="url" value="' + url + '" />'
				+ '" />' + '</form>');
	}

	$('body').append(form);
	$(form).submit();

}

function copyAndRedirect(e, url) {
	var input = $('input[name=CSRFToken]');
	var token = null;
	var form = null;
	if (input && input.length != 0) {
		token = input.attr("value");
	}
	if (token) {
		form = $('<form action="' + getCopyUrl()
				+ '" method="post" style="display: none;">'
				+ '<input type="text" name="url" value="' + url + '" />'
				+ '<input type="hidden" name="CSRFToken" value="' + token
				+ '" />' + '</form>');
	} else {
		form = $('<form action="' + getCopyUrl()
				+ '" method="post" style="display: none;">'
				+ '<input type="text" name="url" value="' + url + '" />'
				+ '</form>');
	}
	$('body').append(form);
	$(form).submit();
}

function checkAddToCartStatus() {
	var addToCartPopupPresent = $("#product-config-addToCartPopup");
	if (addToCartPopupPresent.length > 0)
		return;

	var addToCartButtons = $("div.product-config-addtocart .add_to_cart_button");
	var headErrors = $(".product-config-head-error");

	if (headErrors.length == 0) {
		for (var i = 0; i < addToCartButtons.length; i++) {
			if ($(addToCartButtons[i]).hasClass(
					"product-config-addtocart-inactive")) {
				$(addToCartButtons[i]).removeClass(
						"product-config-addtocart-inactive");
			}
		}
	} else {
		for (var i = 0; i < addToCartButtons.length; i++) {
			if (!$(addToCartButtons[i]).hasClass(
					"product-config-addtocart-inactive")) {
				$(addToCartButtons[i]).addClass(
						"product-config-addtocart-inactive");
			}
		}
	}
}

function jq(myid) {
	return "#" + myid.replace(/(:|\.|\[|\$|\])/g, "\\\$1");
}
