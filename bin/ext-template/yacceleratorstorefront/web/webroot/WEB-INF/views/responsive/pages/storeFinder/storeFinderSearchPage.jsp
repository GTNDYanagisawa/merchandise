<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<template:page pageTitle="${pageTitle}">

	<div class="global-alerts">
		<div class="alert alert-info" role="alert">
			<spring:theme code="text.page.message.underconstruction" text="Information: Page Under Construction - Not Completely Functional"/>
		</div>
	</div>

	
		<div class="storefinder">
			<div class="headline">Find a Store</div>

			<div class="form-group">
				<label for="address3">City, State or Zip Code</label>
				<div class="input-group">
					<input type="text" class="form-control"> <span
						class="input-group-btn">
						<button type="button" class="btn btn-primary">
							<span class="glyphicon glyphicon-search"></span>
						</button>
					</span>
				</div>
			</div>

			<button class="btn btn-info btn-block stores-nearby">
				<span class="glyphicon glyphicon-map-marker"></span> Stores Nearby
			</button>

			<iframe
				src="https://www.google.com/maps/embed?pb=!1m14!1m12!1m3!1d2662.5881570145257!2d11.576178754226701!3d48.13746517811247!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!5e0!3m2!1sde!2sde!4v1400083306211"
				width="100%" height="200" frameborder="0" style="border: 0"></iframe>

			<ul class="store-list">

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">1</span>
							</div>
							<div class="store-detail">
								<strong>Kawasaki Mets Mizonokuchi Hotel</strong> <br>
								<br> Takatsu-Ku<br>01.01.2005<br>Kawasaki<br>213-0001
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 141,3 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">2</span>
							</div>
							<div class="store-detail">
								<strong>Misato</strong> <br>
								<br> Tokyo-Gaikan Expy<br>Tokio
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 141,8 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">3</span>
							</div>
							<div class="store-detail">
								<strong>Tokio Cerulean Tower Tokyu Hotel</strong> <br>
								<br> Sakuragaokacho Shibuya<br>6-01<br>Tokio<br>150-8512
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 144 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">4</span>
							</div>
							<div class="store-detail">
								<strong>Yokohama Shin Yokohama Prince Hotel</strong> <br>
								<br> Shin Yokohama<br>03-04<br>Yokohama<br>222-8533
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 145,6 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">5</span>
							</div>
							<div class="store-detail">
								<strong>Tokio Flexstay Nippori Inn</strong> <br>
								<br> Higashi-Nippori Arakawa<br>5-43-7<br>Tokio<br>116-0014
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 147,4 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">6</span>
							</div>
							<div class="store-detail">
								<strong>Shinbashi</strong> <br>
								<br> Hibiya Dori<br>0<br>Tokio
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 148 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">7</span>
							</div>
							<div class="store-detail">
								<strong>Tokio Park Hotel Tokyo</strong> <br>
								<br> Higashi Shimbashi<br>01.07.2001<br>Tokio<br>105-7227
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 148,7 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">8</span>
							</div>
							<div class="store-detail">
								<strong>Tokio Dormy Inn Tokyo Hatchobori</strong> <br>
								<br> Shinkawa Chuo<br>2-20-4<br>Tokio<br>104-0033
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 149,9 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">9</span>
							</div>
							<div class="store-detail">
								<strong>Tokio Hotel Metropolitan Tokyo</strong> <br>
								<br> Shinkawa Chuo<br>2-20-4<br>Tokio<br>104-0033
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 149,9 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">10</span>
							</div>
							<div class="store-detail">
								<strong>Yokohama Sakuragicho Washington Hotel</strong> <br>
								<br> Sakuragicho<br>1-101-1<br>Yokohama<br>231-0062
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 150,1 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">11</span>
							</div>
							<div class="store-detail">
								<strong>Kawasaki Mets Mizonokuchi Hotel</strong> <br>
								<br> Takatsu-Ku<br>01.01.2005<br>Kawasaki<br>213-0001
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 141,3 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">12</span>
							</div>
							<div class="store-detail">
								<strong>Misato</strong> <br>
								<br> Tokyo-Gaikan Expy<br>Tokio
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 141,8 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">13</span>
							</div>
							<div class="store-detail">
								<strong>Tokio Cerulean Tower Tokyu Hotel</strong> <br>
								<br> Sakuragaokacho Shibuya<br>6-01<br>Tokio<br>150-8512
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 144 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">14</span>
							</div>
							<div class="store-detail">
								<strong>Yokohama Shin Yokohama Prince Hotel</strong> <br>
								<br> Shin Yokohama<br>03-04<br>Yokohama<br>222-8533
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 145,6 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">15</span>
							</div>
							<div class="store-detail">
								<strong>Tokio Flexstay Nippori Inn</strong> <br>
								<br> Higashi-Nippori Arakawa<br>5-43-7<br>Tokio<br>116-0014
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 147,4 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">16</span>
							</div>
							<div class="store-detail">
								<strong>Shinbashi</strong> <br>
								<br> Hibiya Dori<br>0<br>Tokio
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 148 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">17</span>
							</div>
							<div class="store-detail">
								<strong>Tokio Park Hotel Tokyo</strong> <br>
								<br> Higashi Shimbashi<br>01.07.2001<br>Tokio<br>105-7227
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 148,7 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">18</span>
							</div>
							<div class="store-detail">
								<strong>Tokio Dormy Inn Tokyo Hatchobori</strong> <br>
								<br> Shinkawa Chuo<br>2-20-4<br>Tokio<br>104-0033
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 149,9 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">19</span>
							</div>
							<div class="store-detail">
								<strong>Tokio Hotel Metropolitan Tokyo</strong> <br>
								<br> Shinkawa Chuo<br>2-20-4<br>Tokio<br>104-0033
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 149,9 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

				<li class="pickup-store-list-entry">
					<div class="row">
						<div class="col-xs-6">
							<div class="marker">
								<span class="glyphicon glyphicon-map-marker"></span> <span
									class="nummer">20</span>
							</div>
							<div class="store-detail">
								<strong>Yokohama Sakuragicho Washington Hotel</strong> <br>
								<br> Sakuragicho<br>1-101-1<br>Yokohama<br>231-0062
							</div>
						</div>
						<div class="col-xs-6">
							<div class="store-info">
								<div class="distance">
									<strong>Distance</strong> 150,1 km
								</div>
								<dl class="dl-horizontal">
									<dt>Mon</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Tue</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Wed</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Thu</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Fri</dt>
									<dd>09:00 - 20:00</dd>
									<dt>Sat</dt>
									<dd>10:00 - 20:00</dd>
									<dt>Sun</dt>
									<dd>10:00 - 16:00</dd>
								</dl>
							</div>
						</div>
					</div>
				</li>

			</ul>
		</div>


</template:page>