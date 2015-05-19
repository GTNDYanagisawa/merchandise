package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.config;


public class TestConfigFactory {
	private static final String DEFAULT_WEBROOT = 'rest'
	private static final String webroot = ModuleConstants.ORIGINAL_MODULE_NAME.equals(ModuleConstants.CURRENT_MODULE_NAME) ? DEFAULT_WEBROOT : ModuleConstants.CURRENT_MODULE_NAME;
	private static Map<String, ConfigObject> configsCache = new HashMap<>();

	public static synchronized ConfigObject createConfig(String version, String propertyFileClassPath) {
		String key = version + propertyFileClassPath;
		if (configsCache.containsKey(key)) {
			return configsCache.get(key);
		}
		else {
			ConfigObject config = createConfigInternal(version, propertyFileClassPath);
			configsCache.put(key, config);
			return config;
		}
	}

	public static ConfigObject createConfigInternal(String version, String propertyFileClassPath) {
		Map<String, Object> initialValues = new HashMap<>();
		initialValues.put("WEBROOT", webroot);
		initialValues.put("VERSION", version);
		return createConfigFromConfigProperties(propertyFileClassPath, initialValues);
	}

	private static ConfigObject createConfigFromConfigProperties(String propertyFile, Map<String, Object> initialValues) {
		String configScript = this.getClass().getResource(propertyFile).text
		if (configScript == null) return null;

		ConfigSlurper configSlurper = new ConfigSlurper()
		configSlurper.setBinding(initialValues);

		ConfigObject configObject = configSlurper.parse(configScript)
		return configObject;
	}
}
