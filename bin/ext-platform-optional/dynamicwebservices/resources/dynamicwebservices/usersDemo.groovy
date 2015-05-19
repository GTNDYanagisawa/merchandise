import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel
import de.hybris.platform.europe1.enums.UserTaxGroup;
import de.hybris.platform.hmc.model.UserProfileModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.PasswordEncoderService;
import de.hybris.platform.servicelayer.user.UserService;
import groovy.json.JsonSlurper;


import javax.servlet.http.HttpServletResponse

response.contentType = "application/json"

"${request.method.toLowerCase()}"()
return

// ---------------------------------------------------------------
// --- GET, DELETE, POST, PUT, ...
// ---------------------------------------------------------------

def get()
{
	lookupAndDoWithUser{ user -> sendUser( user ) }
}

def delete()
{
	lookupAndDoWithUser{ user -> 
      modelService.remove(user) 
  }
}

def post()
{
	createAndDoWithUser{ user -> sendUser( user )  }
}

def put()
{
	lookupAndDoWithUser(
			{ existingUser ->
				try
				{
   				setUserData(existingUser);
   				modelService.save( existingUser );
				}
				catch( Exception e)
				{
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error updating user ${uid}: ${e.message}")
					return
				}
   			sendUser( existingUser )
			},
			{
				createAndDoWithUser{ newUser -> sendUser( newUser ) } }
			)
}

def options()
{
	notImplemented501()
}

// ---------------------------------------------------------------
// --- UserModel related stuff
// ---------------------------------------------------------------


def lookupAndDoWithUser( userFoundOp , missingUserOp = null )
{
	def uid = params.uid ?: ( pathTokens.length > 0 ? pathTokens[0] : null )
	if( !uid )
	{
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "/<uid> or ?uid=<uid> required")
		return
	}
   UserService us = userService
	try
	{
		userFoundOp( us.getUserForUID( uid ) )
		response.status = HttpServletResponse.SC_OK
	}
	catch( UnknownIdentifierException e)
	{
		if( missingUserOp ) missingUserOp()
		else response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found")
	}
}

def createAndDoWithUser( c )
{
   ModelService ms = modelService

	def uid = params.uid ?: pathTokens[0]
	assert uid : "/<uid> or ?uid=<uid> required"

	UserModel newUser = ms.create( params.itemtype ?: 'Customer' )
	newUser.uid = uid
	try 
	{
		setUserData(newUser)
		ms.save( newUser )
	} 
	catch (Exception e)
	{
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error creating user ${uid}: ${e.message}")
		return
	}

	response.status = HttpServletResponse.SC_CREATED
	response.contentType = 'application/json'
	response.setHeader('Location', "${request.requestURL}/${newUser.uid}")

	c( newUser )
}

def setUserData( UserModel user)
{
	UserService us = userService
	CommonI18NService ci18n = commonI18NService
	PasswordEncoderService pwenc =passwordEncoderService 

	if( params.name ) user.name = params.name
	if( params.customerID ) user.customerID = params.customerID
	if( params.loginDisabled != null )
	{
		System.out.println("user.logingDisabled = ${params.loginDisabled} / ${params.loginDisabled.toBoolean()}")
		user.loginDisabled = params.loginDisabled.toBoolean()
	}
	if( params.groups ) user.groups = params.groups.split('[,; ]').collect{ uid -> us.getUserGroupForUID(uid)} as Set
	
	if( params.sessionLanguage ) user.sessionLanguage = ci18n.getLanguage(params.sessionLanguage)
	if( params.sessionCurrency ) user.sessionCurrency = ci18n.getCurrency(params.sessionCurrency)
	if( params.password )
	{
		user.passwordEncoding = params.passwordEncoding ?: "md5"
		user.encodedPassword = pwenc.encode(user, params.password, user.passwordEncoding)
	}
	
	if( params.taxArea ) user.europe1PriceFactory_UTG = UserTaxGroup.valueOf(params.taxArea)
	
	if( params.addresses )
	{
		def parser = new JsonSlurper()
		def jsonAddresses = parser.parseText(params.addresses)
		
		if( jsonAddresses )
		{
			user.addresses = jsonAddresses.collect{ jsonToAddress(user, it) }
		}
	}

	
}

def sendUser( UserModel user )
{
	send(
			[
				uid:user.uid,
				customerID:user.customerID,
				name:user.name,
				password:"${user.passwordEncoding}::${user.encodedPassword}",
				loginDisabled:user.loginDisabled,
				groups:user.groups.collect{ it.uid }.toListString(),
				taxArea:user.europe1PriceFactory_UTG?.code,
				userProfile:user.userprofile.collect{ UserProfileModel up ->
					[
   					readable:up.allReadableLanguages.collect{it.isoCode},
   					writable:up.allWritableLanguages.collect{it.isoCode},
   					expand:up.expandInitial
   				]
				},
				orders:user.orders.collect{
					[
						code:it.code,
						date:it.date,
						pk:it.pk as String
					]
				},
				addresses:user.addresses.collect{
					[
						street:it.streetname,
						streetNr:it.streetnumber,
						town:it.town,
						country:it.country?.isocode,
						pk:it.pk as String
					]
				},
				sessionLanguage:user.sessionLanguage?.isocode,
				sessionCurrency:user.sessionCurrency?.isocode,
   			pk:user.pk.toString(),
   			type:user.itemtype,
   			created:user.creationtime,
   			modified:user.modifiedtime
			]
			)
}

def AddressModel jsonToAddress( UserModel user, jsonAddr )
{
	ModelService ms = modelService
	AddressModel addr = ms.create(AddressModel.class)
	CommonI18NService ci18n = commonI18NService
	UserService us = userService
	
	addr.owner = user
	
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


// ---------------------------------------------------------------
// --- Helpers
// ---------------------------------------------------------------


def send( Map payload  )
{
	UserService userService = userService
	I18NService i18NService = i18NService
	
	if( params.verbose )
   	payload.context = [
   		sessionUser:userService.currentUser.uid,
   		sessionLocale:i18NService.currentLocale as String,
   		sessionTimeZone: i18NService.currentTimeZone.ID,
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