<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<template:page pageTitle="${pageTitle}">

	<div class="global-alerts">
		<div class="alert alert-info" role="alert">
			<spring:theme code="text.page.message.underconstruction" text="Information: Page Under Construction - Not Completely Functional"/>
		</div>
	</div>

	<div class="search-empty">
		<div class="headline">
			0 items found for keyword <strong>"keyword"</strong> 
		</div>
		
		<div class="search-suggestions">
			Related Searches: <br> <strong>stem word1</strong>  |  <strong>stem word2</strong>  | <strong> stem word3</strong>
		</div>
		
		<button class="btn btn-link">Click here to go back</button>
	</div>

	
		<div class="carousel-component">

		<div class="headline">Suggestions</div>

		<div class="carousel js-owl-carousel js-owl-default">

			<div class="item">
				<a href="#">
					<div class="thumb">
						<theme:image code="img.missingProductImage.thumbnail" />
					</div>
					<div class="item-name">Canon - EOS Rebel T3 DSLR Camera w
						18-55mm EF-S IS</div>
				</a>
				<button class="btn btn-primary btn-block">Add to Cart</button>
				<button class="btn btn-default btn-block js-pickup-in-store-button">Pick
					Up In Store</button>
			</div>

			<div class="item">
				<a href="#">
					<div class="thumb">
						<theme:image code="img.missingProductImage.thumbnail" />
					</div>
					<div class="item-name">Canon - EOS Rebel T3 DSLR Camera w
						18-55mm EF-S IS</div>
				</a>
				<button class="btn btn-primary btn-block">Add to Cart</button>
				<button class="btn btn-default btn-block js-pickup-in-store-button">Pick
					Up In Store</button>
			</div>

			<div class="item">
				<a href="#">
					<div class="thumb">
						<theme:image code="img.missingProductImage.thumbnail" />
					</div>
					<div class="item-name">Canon - EOS Rebel T3 DSLR Camera w
						18-55mm EF-S IS</div>
				</a>
				<button class="btn btn-primary btn-block">Add to Cart</button>
				<button class="btn btn-default btn-block js-pickup-in-store-button">Pick
					Up In Store</button>
			</div>

			<div class="item">
				<a href="#">
					<div class="thumb">
						<theme:image code="img.missingProductImage.thumbnail" />
					</div>
					<div class="item-name">Canon - EOS Rebel T3 DSLR Camera w
						18-55mm EF-S IS</div>
				</a>
				<button class="btn btn-primary btn-block">Add to Cart</button>
				<button class="btn btn-default btn-block js-pickup-in-store-button">Pick
					Up In Store</button>
			</div>

			<div class="item">
				<a href="#">
					<div class="thumb">
						<theme:image code="img.missingProductImage.thumbnail" />
					</div>
					<div class="item-name">Canon - EOS Rebel T3 DSLR Camera w
						18-55mm EF-S IS</div>
				</a>
				<button class="btn btn-primary btn-block">Add to Cart</button>
				<button class="btn btn-default btn-block js-pickup-in-store-button">Pick
					Up In Store</button>
			</div>

			<div class="item">
				<a href="#">
					<div class="thumb">
						<theme:image code="img.missingProductImage.thumbnail" />
					</div>
					<div class="item-name">Canon - EOS Rebel T3 DSLR Camera w
						18-55mm EF-S IS</div>
				</a>
				<button class="btn btn-primary btn-block">Add to Cart</button>
				<button class="btn btn-default btn-block js-pickup-in-store-button">Pick
					Up In Store</button>
			</div>

			<div class="item">
				<a href="#">
					<div class="thumb">
						<theme:image code="img.missingProductImage.thumbnail" />
					</div>
					<div class="item-name">Canon - EOS Rebel T3 DSLR Camera w
						18-55mm EF-S IS</div>
				</a>
				<button class="btn btn-primary btn-block">Add to Cart</button>
				<button class="btn btn-default btn-block js-pickup-in-store-button">Pick
					Up In Store</button>
			</div>

			<div class="item">
				<a href="#">
					<div class="thumb">
						<theme:image code="img.missingProductImage.thumbnail" />
					</div>
					<div class="item-name">Canon - EOS Rebel T3 DSLR Camera w
						18-55mm EF-S IS</div>
				</a>
				<button class="btn btn-primary btn-block">Add to Cart</button>
				<button class="btn btn-default btn-block js-pickup-in-store-button">Pick
					Up In Store</button>
			</div>

			<div class="item">
				<a href="#">
					<div class="thumb">
						<theme:image code="img.missingProductImage.thumbnail" />
					</div>
					<div class="item-name">Canon - EOS Rebel T3 DSLR Camera w
						18-55mm EF-S IS</div>
				</a>
				<button class="btn btn-primary btn-block">Add to Cart</button>
				<button class="btn btn-default btn-block js-pickup-in-store-button">Pick
					Up In Store</button>
			</div>

			<div class="item">
				<a href="#">
					<div class="thumb">
						<theme:image code="img.missingProductImage.thumbnail" />
					</div>
					<div class="item-name">Canon - EOS Rebel T3 DSLR Camera w
						18-55mm EF-S IS</div>
				</a>
				<button class="btn btn-primary btn-block">Add to Cart</button>
				<button class="btn btn-default btn-block js-pickup-in-store-button">Pick
					Up In Store</button>
			</div>

		</div>
	</div>
	

</template:page>