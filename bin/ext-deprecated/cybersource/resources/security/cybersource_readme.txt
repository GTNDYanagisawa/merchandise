When using cybersource extension, this folder should contain a file named: <merchantID>.p12
The <merchantID> is an unique id that is connected with a Cybersource account.
The file <merchantID>.p12 contains private security key that is used for secure communication with Cybersource servers 
when processing payment transactions.
More than one security key can be stored here, the one that is used is choosen by the entry "merchantID=..."
in /resource/cybs.properties file in cybersource extension.
It is also possible to install security key in other directory, see cybs.properties (or cybs.properties.template) 
in /resources folder of the cybersource extension for more details.


