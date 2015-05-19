import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.constants.Europe1Constants.PARAMS;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.FormatFactory;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;
import de.hybris.platform.util.Utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import groovy.json.JsonSlurper;

response.contentType = "application/json"

"${request.method.toLowerCase()}"()
return

// ---------------------------------------------------------------
// --- GET, DELETE, POST, PUT, ...
// ---------------------------------------------------------------

def get()
{
	lookupAndDoWithOrder{ order -> sendOrder( order ) }
}

def delete()
{
	lookupAndDoWithOrder{ order -> 
		modelService.remove(order) 
	}
}

def post()
{
	def content = request.reader.text
	def parser = new JsonSlurper()
	def json = parser.parseText(content)
	try
	{
		def OrderModel order = null
		runAs( userService.adminUser, {
				order = jsonToOrder(json)
				modelService.save( order )
			}
		)
		
		response.status = HttpServletResponse.SC_CREATED
		response.contentType = 'application/json'
		response.setHeader('Location', "${request.requestURL}/${order?.code}")
		
	}
	catch( Exception e)
	{
		throw e
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters: ${e.message}")
		return
	}
}

def OrderModel jsonToOrder( json )
{
	CommonI18NService ci18n = commonI18NService
	UserService us = userService
	ModelService ms = modelService
	PaymentModeService pms = paymentModeService
	DeliveryModeService dms = deliveryModeService

	OrderModel order = ms.create(json.type ?: 'Order')
	order.code = json.code ?: UUID.randomUUID()
	order.currency = ci18n.getCurrency(json.currency)
	order.user = us.getUserForUID(json.user)
	order.date = json.date ?: new Date()
	order.net = json.net ?: false
	order.paymentMode = json.paymentMode ? pms.getPaymentModeForCode(json.paymentMode) : null
	order.deliveryMode = json.deliveryMode ? dms.getDeliveryModeForCode(json.deliveryMode) : null

				// simplified: 'address' defines both payment and delivery address
	if( json.address )
	{
		def sameAddress = jsonToAddress(json.address)
		sameAddress.owner = order

		order.paymentAddress = sameAddress
		order.deliveryAddress = sameAddress
	}
	else
	{
		def paymentAddress = json.paymentAddress ? jsonToAddress(json.paymentAddress) : null
		def deliveryAddress = json.deliveryAddress ? jsonToAddress(json.deliveryAddress) : paymentAddress

		if( paymentAddress ) paymentAddress.owner = order
		if( deliveryAddress ) deliveryAddress.owner = order

		order.paymentAddress = paymentAddress
		order.deliveryAddress = deliveryAddress
	}
	
	order.entries = json.entries ? json.entries.collect{ jsonToOrderEntry(it) } : []
	
	return order
}

def OrderEntryModel jsonToOrderEntry( entryJson )
{
	ProductService ps = productService
	CatalogVersionService cvs = catalogVersionService
	UnitService units = unitService
	ModelService ms = modelService
	
	OrderEntryModel entry = ms.create(entryJson.type ?: "OrderEntry")
	entry.order = order
	entry.entryNumber = entryJson.entryNumber
	def pTokens = entryJson.product.split(':')
	entry.product = ps.getProductForCode(cvs.getCatalogVersion(pTokens[0], pTokens[1]), pTokens[2])
	entry.quantity = entryJson.amount
	entry.unit = units.getUnitForCode(entryJson.unit)

	return entry
}

def AddressModel jsonToAddress( jsonAddr )
{
	ModelService ms = modelService
	AddressModel addr = ms.create(AddressModel.class)
	CommonI18NService ci18n = commonI18NService
	
	addr.title = jsonAddr.title ? us.getTitleForCode(jsonAddr.title) : null;
	addr.firstname = jsonAddr.firstName;
	addr.middlename = jsonAddr.middleName;
	addr.lastname = jsonAddr.lastName;

	addr.phone1 = jsonAddr.phone;
	addr.email = jsonAddr.email;
	
	addr.postalcode = jsonAddr.postalCode
	addr.town = jsonAddr.town
	addr.country = ci18n.getCountry(jsonAddr.country)
	addr.streetname = jsonAddr.streetName
	addr.streetnumber = jsonAddr.streetNumber
	
	return addr
}

def runAs( UserModel user, op )
{
	UserService us = userService
	def previousUser = us.currentUser
	try
	{
		us.currentUser = user
		op()
	}
	finally
	{
		us.currentUser = user
	}

}

//def put()
//{
//	lookupAndDoWithUser(
//		{ user ->
//			setUserData(user);
//			modelService.save( user );
//			sendUser( user )
//		},
//		{ createAndDoWithUser
//			{ user ->
//				sendUser( user )
//			}
//		}
//	)
//}

def options()
{
	notImplemented501()
}

// ---------------------------------------------------------------
// --- UserModel related stuff
// ---------------------------------------------------------------


def lookupAndDoWithOrder( orderFoundOp , missingOrderOp = null )
{
	def code = params.code ?: ( pathTokens.length > 0 ? pathTokens[0] : null )
	if( !code )
	{
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "/<code> or ?code=<code> required")
		return
	}	
	
	
	FlexibleSearchService fs = flexibleSearchService
	
	SearchResult<OrderModel> sr = fs.search('SELECT {PK} FROM {Order} WHERE {code}=?code', ['code':code] )
	
	if( sr.count == 1 )
	{
		orderFoundOp( sr.result[0] )
		response.status = HttpServletResponse.SC_OK
	}
	else
	{
		if( missingOrderOp )
			missingOrderOp()
		else
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found")
	}
}

//def createAndDoWithUser( c )
//{
//	def uid = params.uid ?: pathTokens[0]
//	assert uid : "/<uid> or ?uid=<uid> required"
//		
//	UserModel newUser = modelService.create( params.itemtype ?: 'Customer' )
//	newUser.uid = uid
//	setUserData(newUser)
//	modelService.save( newUser )
//	
//	response.status = HttpServletResponse.SC_CREATED
//	response.contentType = 'application/json'
//	response.setHeader('Location', "${request.requestURL}/${newUser.uid}")
//	
//	c( newUser )
//}
//
//def setUserData( UserModel user)
//{
//	UserService us = userService
//
//	if( params.name ) user.name = params.name
//	if( params.loginDisabled != null )
//	{
//		System.out.println("user.logingDisabled = ${params.loginDisabled} / ${params.loginDisabled.toBoolean()}")
//		user.loginDisabled = params.loginDisabled.toBoolean()
//	}
//	if( params.groups ) user.groups = params.groups.split('[,; ]').collect{ uid -> us.getUserGroupForUID(uid)} as Set
//}

def sendOrder( OrderModel order )
{
	send(
		[
			code:order.code,
			customer:order.user.uid,
			date:order.date,
			currency:order.currency.isocode,
			net:order.net,
			subTotal:formatPrice(order.subtotal,order.currency),
			total:formatPrice(order.totalPrice,order.currency),
			totalTaxes:formatPrice(order.totalTax,order.currency),
			status:order.status,
			entries:order.entries.collect { OrderEntryModel entry -> toJson(entry) },
			deliveryAddress: toJson( order.deliveryAddress ),
			paymentAddress: toJson( order.paymentAddress),
			pk:order.pk.toString(),
			type:order.itemtype,
			created:order.creationtime,
			modified:order.modifiedtime
		]
	)
}

// ---------------------------------------------------------------
// --- Helpers
// ---------------------------------------------------------------

def String formatPrice( amount , CurrencyModel curr )
{
	FormatFactory ff = formatFactory
	
	NumberFormat f = ff.createNumberFormat()
	f.minimumFractionDigits = curr.digits
	f.groupingUsed = false
	
	return f.format(amount)
}

def Map toJson( OrderEntryModel entry )
{
	[
		line:entry.entryNumber,
		sku:entry.product.code,
		productName:entry.product.name,
		amount:entry.quantity,
		unit:entry.unit.code,
		basePrice:formatPrice(entry.basePrice,entry.order.currency),
		currency:entry.order.currency.symbol,
		total:formatPrice(entry.totalPrice,entry.order.currency),
		discounts:entry.discountValues.collect{
			DiscountValue dv ->
				dv.absolute ?
   				[
   					code:dv.code,
   					amount:dv.value,
   					currency:dv.currencyIsoCode
   				]
   			:
   				[
   					code:dv.code,
   					percentage:dv.getValue()
   				]
		},
		taxes:entry.taxValues.collect{
			TaxValue tv ->
				tv.absolute ?
   				[
   					code:tv.code,
   					amount:tv.value,
   					currency:tv.currencyIsoCode
   				]
   			:
   				[
   					code:tv.code,
   					rate:tv.getValue()
   				]
		}
	]
}

def Map toJson( AddressModel address )
{
	if( address )
	{
   	def Map ret = [ 
   		country:address.country?.isocode,
			pk:address.pk.toString()
		]
		if( address.title) ret.title = address.title.code;
		if( address.middlename) ret.middleName=address.middlename
		if( address.firstname) ret.firstName=address.firstname
		if( address.lastname) ret.lastName=address.lastname
		if( address.email) ret.email=address.email
		if( address.streetname) ret.streetName=address.streetname
		if( address.streetnumber) ret.streetNumber=address.streetnumber
		if( address.postalcode) ret.postalcode=address.postalcode
		if( address.town) ret.town=address.town
		
		return ret
	}
}

def send( Map payload  )
{
	payload._context_ = [
		sessionUser:userService.currentUser.uid,
		sessionLocale:i18NService.currentLocale as String,
		sessionTimeZone: i18nService.currentTimeZone.ID,
		params: params
	]

	json( payload )
}

def notAllowed403()
{
	response.sendError(403, "Forbidden")
}

def notImplemented501()
{
	response.sendError(501, "Not implemented.")
}
