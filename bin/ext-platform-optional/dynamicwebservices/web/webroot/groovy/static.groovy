println """
<html>
    <head>
        <title>Static Groovy File Example</title>
    </head>
    <body>
		Hello, ${userService.currentUser.name ?: userService.currentUser.uid}!
		<p/>
		You're locale is ${i18NService.currentLocale} ... 
    </body>
</html>
"""