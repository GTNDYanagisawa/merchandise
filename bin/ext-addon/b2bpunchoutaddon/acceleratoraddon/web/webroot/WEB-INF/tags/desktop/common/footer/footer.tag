<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:theme code="punchout.footer.notice" var="notice"/>

<div id="footer">
	<div id="copyright">
		<p class="no_footer_bar">${notice}</p>
	</div>
</div>