<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="spfooter" tagdir="/WEB-INF/tags/addons/secureportaladdon/desktop/common/spfooter"%> 

<spring:theme code="secureportal.footer.notice" var="notice"/>

<div id="footer">
	<div id="copyright" class="span-24">
		<span class="links">
			<a href="#"> Terms & Conditions </a> | 
			<a href="#"> F.A.Q </a> | 
			<a href="#"> Help </a>
		</span>
		<span class="right">
			<p class="no_footer_bar right">${notice}</p>
		</span>		
		<span class="lang_sel right">
			<ul class="language">
				<cms:pageSlot position="HeaderLinks" var="link">
					<cms:component component="${link}" element="li" />
				</cms:pageSlot>
				<li class="language-select"><spfooter:languageSelector languages="${languages}" currentLanguage="${currentLanguage}" /></li>
			</ul>
		</span>
	</div>
</div>