<%@ taglib prefix="hac" uri="/WEB-INF/custom.tld" %> 

<nav class="topnavigation">
	<ul>
		<hac:topmenu-item label="platform" />
		<hac:topmenu-item label="monitoring" />
		<hac:topmenu-item label="maintenance" />
		<hac:topmenu-item label="console" />
	</ul>
</nav>

<nav class="subnavigation span-24 last" id="nav_platform">
	<ul>
		<hac:submenu-item href="/tenants/" label="tenants" />
		<hac:submenu-item href="/platform/config" label="configuration" />
		<hac:submenu-item href="/platform/system" label="system" />
		<hac:submenu-item href="/platform/log4j" label="logging" />
		<hac:submenu-item href="/platform/extensions" label="extensions" />
		<hac:submenu-item href="/platform/init" label="initialization" />
		<hac:submenu-item href="/platform/update" label="update" />
		<hac:submenu-item href="/platform/dryrun" label="SQL scripts" />
		<hac:submenu-item href="/platform/license" label="license" />
		<hac:submenu-item href="/platform/support" label="support" />
		<hac:submenu-item href="/platform/pkanalyzer" label="PK analyzer" />
		<hac:submenu-item href="/platform/jars" label="classpath analyzer" />
	</ul>
</nav>

<nav class="subnavigation span-24 last" id="nav_monitoring">
	<ul>
		<hac:submenu-item href="/monitoring/cache" label="cache" />
		<hac:submenu-item href="/monitoring/cluster" label="cluster" />
		<hac:submenu-item href="/monitoring/database" label="database" />
		<hac:submenu-item href="/monitoring/sessions" label="sessions" />
		<hac:submenu-item href="/monitoring/cronjobs" label="cron jobs" />
		<hac:submenu-item href="/monitoring/jmx" label="JMX MBeans" />
		<hac:submenu-item href="/monitoring/memory" label="memory" />
		<hac:submenu-item href="/monitoring/threaddump" label="thread dump" />
		<hac:submenu-item href="/monitoring/performance" label="performance" />
		<hac:submenu-item href="/monitoring/dynatrace" label="transaction tracing" />
	</ul>
</nav>

<nav class="subnavigation span-24 last" id="nav_maintenance">
	<ul>
		<hac:submenu-item href="/maintain/cleanup" label="cleanup" />
		<hac:submenu-item href="/maintain/keys" label="encryption keys" />
		<hac:submenu-item href="/maintain/deployments" label="deployment" />
	</ul>
</nav>

<nav class="subnavigation span-24 last" id="nav_console">
	<ul>
        <hac:submenu-item href="/console/scripting/" label="scripting languages" />
		<hac:submenu-item href="/console/flexsearch/" label="flexibleSearch" />
		<hac:submenu-item href="/console/impex/import" label="ImpEx import" />
		<hac:submenu-item href="/console/impex/export" label="ImpEx export" />
		<hac:submenu-item href="/console/ldap" label="LDAP" />
	</ul>
</nav>
