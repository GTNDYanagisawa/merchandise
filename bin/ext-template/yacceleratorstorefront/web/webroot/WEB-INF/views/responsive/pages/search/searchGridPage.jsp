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



		<div class="simpleimagecomponent">
			<a href="#"><theme:image code="img.SearchPage.banner" /></a>
		</div>

		<div class="row">


			<div id="product-facet"
				class="col-md-3 col-lg-2 hidden-sm hidden-xs  product-facet js-product-facet">

				<div class="facet js-facet">
					<div class="facet-name js-facet-name">Brand</div>
					<ul class="facet-list js-facet-list">

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Burton
										(74)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Volcom
										(15)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">WLD
										(10)</span></label> </span></li>

					</ul>
				</div>

				<div class="facet js-facet">
					<div class="facet-name js-facet-name">Price</div>
					<ul class="facet-list js-facet-list">

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">£50-£99.99
										(46)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">£100-£199.99
										(53)</span></label> </span></li>

					</ul>
				</div>

				<div class="facet js-facet">
					<div class="facet-name js-facet-name">Colour</div>
					<ul class="facet-list js-facet-list">

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Black
										(21)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Blue
										(25)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Brown
										(4)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Green
										(12)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Grey
										(11)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Orange
										(4)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Pink
										(2)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Purple
										(14)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">Red
										(15)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">White
										(7)</span></label> </span></li>

					</ul>
				</div>

				<div class="facet js-facet">
					<div class="facet-name js-facet-name">Size</div>
					<ul class="facet-list js-facet-list">

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">XXS
										(4)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">XS
										(17)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">S
										(23)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">M
										(24)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">L
										(20)</span></label> </span></li>

						<li><label> <input class="facet-checkbox"
								type="checkbox"> <span class="facet-label"> <span
									class="facet-mark"></span> <span class="facet-text">XL
										(11)</span></label> </span></li>

					</ul>
				</div>



				<div class="row hidden-md hidden-lg">
					<div class="col-xs-6">
						<button class="btn btn-default btn-block">Cancel</button>
					</div>
					<div class="col-xs-6">
						<button class="btn btn-primary btn-block">Save</button>
					</div>
				</div>

			</div>

			<div class="col-md-9 col-lg-10">
				<div class="pagination-bar top">
					<div class="row">
						<div class="col-xs-6 col-md-4">
							Show Products <strong>1-10</strong> of <strong>99</strong>
						</div>
						<div class="col-xs-6 col-md-4 col-md-push-4">
							<div class="hidden-xs hidden-sm">
								<ul class="pagination">
									<li class="disabled"><span>&laquo;</span></li>
									<li class="active"><span>1 <span class="sr-only">(current)</span><span></li>
									<li><a href="#">2</a></li>
									<li><a href="#">5</a></li>
									<li><a href="#">&raquo;</a></li>
								</ul>

							</div>

							<div class="hidden-md hidden-lg">
								<ul class="pager">
									<li><a href="#">Previous</a></li>
									<li><a href="#">Next</a></li>
								</ul>
							</div>


						</div>
						<div class="helper clearfix hidden-md hidden-lg"></div>
						<div class="sort-refine-bar">
							<div class="col-xs-6 col-md-4 col-md-pull-4">
								<div class="form-group">
									<select class="form-control">
										<option>Sort By</option>
										<option>2</option>
										<option>3</option>
										<option>4</option>
										<option>5</option>
									</select>
								</div>
							</div>
							<div class="col-xs-6 col-md-4 hidden-md hidden-lg">
								<button class="btn btn-default pull-right js-show-facets">Refine</button>
								<a href="" class="btn btn-link pull-right"><span
									class="glyphicon glyphicon-th-list"></span></a> <a href=""
									class="btn btn-link pull-right"><span
									class="glyphicon glyphicon-th"></span></a>
							</div>
						</div>
					</div>
				</div>

				<ul class="product-listing product-grid">

					<li class="product-item">
						<div class="thumb">
							<theme:image code="img.missingProductImage.product" />
						</div>
						<div class="details">
							<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
								18-55mm IS STM lens - Black</div>

							<div class="price msrp">Retail: $988.88</div>
							<div class="price">$988.88</div>
						</div>
						<div class="addtocart">
							<button type="button" class="btn btn-primary btn-block">Add
								to Cart</button>
							<button type="button"
								class="btn btn-default btn-block js-pickup-in-store-button">Pick
								Up In Store</button>
						</div>
					</li>



					<li class="product-item">
						<div class="thumb">
							<theme:image code="img.missingProductImage.product" />
						</div>
						<div class="details">
							<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
								18-55mm IS STM lens - Black</div>

							<div class="price msrp">Retail: $988.88</div>
							<div class="price">$988.88</div>
						</div>
						<div class="addtocart">
							<button type="button" class="btn btn-primary btn-block">Add
								to Cart</button>
							<button type="button"
								class="btn btn-default btn-block js-pickup-in-store-button">Pick
								Up In Store</button>
						</div>
					</li>



					<li class="product-item">
						<div class="thumb">
							<theme:image code="img.missingProductImage.product" />
						</div>
						<div class="details">
							<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
								18-55mm IS STM lens - Black</div>

							<div class="price msrp">Retail: $988.88</div>
							<div class="price">$988.88</div>
						</div>
						<div class="addtocart">
							<button type="button" class="btn btn-primary btn-block">Add
								to Cart</button>
							<button type="button"
								class="btn btn-default btn-block js-pickup-in-store-button">Pick
								Up In Store</button>
						</div>
					</li>



					<li class="product-item">
						<div class="thumb">
							<theme:image code="img.missingProductImage.product" />
						</div>
						<div class="details">
							<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
								18-55mm IS STM lens - Black</div>

							<div class="price msrp">Retail: $988.88</div>
							<div class="price">$988.88</div>
						</div>
						<div class="addtocart">
							<button type="button" class="btn btn-primary btn-block">Add
								to Cart</button>
							<button type="button"
								class="btn btn-default btn-block js-pickup-in-store-button">Pick
								Up In Store</button>
						</div>
					</li>



					<li class="product-item">
						<div class="thumb">
							<theme:image code="img.missingProductImage.product" />
						</div>
						<div class="details">
							<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
								18-55mm IS STM lens - Black</div>

							<div class="price msrp">Retail: $988.88</div>
							<div class="price">$988.88</div>
						</div>
						<div class="addtocart">
							<button type="button" class="btn btn-primary btn-block">Add
								to Cart</button>
							<button type="button"
								class="btn btn-default btn-block js-pickup-in-store-button">Pick
								Up In Store</button>
						</div>
					</li>



					<li class="product-item">
						<div class="thumb">
							<theme:image code="img.missingProductImage.product" />
						</div>
						<div class="details">
							<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
								18-55mm IS STM lens - Black</div>

							<div class="price msrp">Retail: $988.88</div>
							<div class="price">$988.88</div>
						</div>
						<div class="addtocart">
							<button type="button" class="btn btn-primary btn-block">Add
								to Cart</button>
							<button type="button"
								class="btn btn-default btn-block js-pickup-in-store-button">Pick
								Up In Store</button>
						</div>
					</li>



					<li class="product-item">
						<div class="thumb">
							<theme:image code="img.missingProductImage.product" />
						</div>
						<div class="details">
							<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
								18-55mm IS STM lens - Black</div>

							<div class="price msrp">Retail: $988.88</div>
							<div class="price">$988.88</div>
						</div>
						<div class="addtocart">
							<button type="button" class="btn btn-primary btn-block">Add
								to Cart</button>
							<button type="button"
								class="btn btn-default btn-block js-pickup-in-store-button">Pick
								Up In Store</button>
						</div>
					</li>



					<li class="product-item">
						<div class="thumb">
							<theme:image code="img.missingProductImage.product" />
						</div>
						<div class="details">
							<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
								18-55mm IS STM lens - Black</div>

							<div class="price msrp">Retail: $988.88</div>
							<div class="price">$988.88</div>
						</div>
						<div class="addtocart">
							<button type="button" class="btn btn-primary btn-block">Add
								to Cart</button>
							<button type="button"
								class="btn btn-default btn-block js-pickup-in-store-button">Pick
								Up In Store</button>
						</div>
					</li>



					<li class="product-item">
						<div class="thumb">
							<theme:image code="img.missingProductImage.product" />
						</div>
						<div class="details">
							<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
								18-55mm IS STM lens - Black</div>

							<div class="price msrp">Retail: $988.88</div>
							<div class="price">$988.88</div>
						</div>
						<div class="addtocart">
							<button type="button" class="btn btn-primary btn-block">Add
								to Cart</button>
							<button type="button"
								class="btn btn-default btn-block js-pickup-in-store-button">Pick
								Up In Store</button>
						</div>
					</li>



					<li class="product-item">
						<div class="thumb">
							<theme:image code="img.missingProductImage.product" />
						</div>
						<div class="details">
							<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
								18-55mm IS STM lens - Black</div>

							<div class="price msrp">Retail: $988.88</div>
							<div class="price">$988.88</div>
						</div>
						<div class="addtocart">
							<button type="button" class="btn btn-primary btn-block">Add
								to Cart</button>
							<button type="button"
								class="btn btn-default btn-block js-pickup-in-store-button">Pick
								Up In Store</button>
						</div>
					</li>



				</ul>


				<div class="pagination-bar bottom">
					<div class="row">
						<div class="col-xs-6 col-md-4">
							Show Products <strong>1-10</strong> of <strong>99</strong>
						</div>
						<div class="col-xs-6 col-md-4 col-md-push-4">
							<div class="hidden-xs hidden-sm">
								<ul class="pagination">
									<li class="disabled"><span>&laquo;</span></li>
									<li class="active"><span>1 <span class="sr-only">(current)</span><span></li>
									<li><a href="#">2</a></li>
									<li><a href="#">5</a></li>
									<li><a href="#">&raquo;</a></li>
								</ul>

							</div>

							<div class="hidden-md hidden-lg">
								<ul class="pager">
									<li><a href="#">Previous</a></li>
									<li><a href="#">Next</a></li>
								</ul>
							</div>


						</div>
						<div class="helper clearfix hidden-md hidden-lg"></div>
						<div class="sort-refine-bar">
							<div class="col-xs-6 col-md-4 col-md-pull-4">
								<div class="form-group">
									<select class="form-control">
										<option>Sort By</option>
										<option>2</option>
										<option>3</option>
										<option>4</option>
										<option>5</option>
									</select>
								</div>
							</div>
							<div class="col-xs-6 col-md-4 hidden-md hidden-lg">
								<button class="btn btn-default pull-right js-show-facets">Refine</button>
								<a href="" class="btn btn-link pull-right"><span
									class="glyphicon glyphicon-th-list"></span></a> <a href=""
									class="btn btn-link pull-right"><span
									class="glyphicon glyphicon-th"></span></a>
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>

</template:page>