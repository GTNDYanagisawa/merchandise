<html>

<head>
	<script language="JavaScript" src="js/date.js"></script>

	<link rel="stylesheet" type="text/css" media="all" href="css/hmc.css">

	<script language="JavaScript">

		function swapImage(id, img)
		{
			document.getElementById( id ).src = img;
		}
		
		var re_url = new RegExp('datetime=(\\-?\\d+)');
		var dt_current = (re_url.exec(String(window.location)) ? new Date(new Number(RegExp.$1)) : new Date());
		
		var re_id = new RegExp('id=(\\d+)');
		var num_id = (re_id.exec(String(window.location)) ? new Number(RegExp.$1) : 0);
		
		var obj_caller = (window.opener ? window.opener.calendars[num_id] : null);
		
		if( obj_caller ) 
		{
			// get same date in the previous year
			var dt_prev_year = new Date(dt_current);
			dt_prev_year.setFullYear(dt_prev_year.getFullYear() - 1);
			if (dt_prev_year.getDate() != dt_current.getDate())
			{
				dt_prev_year.setDate(0);
			}
			
			// get same date in the next year
			var dt_next_year = new Date(dt_current);
			dt_next_year.setFullYear(dt_next_year.getFullYear() + 1);
			if (dt_next_year.getDate() != dt_current.getDate())
			{
				dt_next_year.setDate(0);
			}
		}
		
		// get same date in the previous month
		var dt_prev_month = new Date(dt_current);
		dt_prev_month.setMonth(dt_prev_month.getMonth() - 1);
		if (dt_prev_month.getDate() != dt_current.getDate())
		{
			dt_prev_month.setDate(0);
		}
		
		// get same date in the next month
		var dt_next_month = new Date(dt_current);
		dt_next_month.setMonth(dt_next_month.getMonth() + 1);
		if (dt_next_month.getDate() != dt_current.getDate())
		{
			dt_next_month.setDate(0);
		}
		
		// get first day to display in the grid for current month
		var dt_firstday = new Date(dt_current);
		dt_firstday.setDate(1);
		dt_firstday.setDate(1 - (7 + dt_firstday.getDay() - obj_caller.NUM_WEEKSTART) % 7);
				
		// function passing selected date to calling window
		function set_datetime(n_datetime, b_close) 
		{
			if (!obj_caller) 
			{
				return;
			}
		
			var dt_datetime = new Date(n_datetime);
		
			if (!dt_datetime) 
			{
				return;
			}
			
			if( b_close )
			{
				window.close();
				obj_caller.target.value = formatDate(dt_datetime, obj_caller.pattern);
			}
			else 
			{
				obj_caller.popup(dt_datetime.getTime());
			}
		}	
	</script>
</head>

<body bgcolor="#f2f2f5" topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0" marginheight="0" marginwidth="0" style="height:100%" onLoad="document.title=obj_caller.title;">
	<table cellspacing="0" cellpadding="0" style="width:100%; height:100%;">

		<!-- top blue bar -->
		<tr style="height:29px" >
			<td style="width:7px;background-image:url(images/window_head_back.jpg);"> &nbsp; </td>
			<td align="left" style="background-image:url(images/window_head_back.jpg);vertical-align:middle; text-align:middle; white-space:nowrap; color:#ffffff;">
				<script language="JavaScript">
					document.write(obj_caller.header);
				</script>
			</td>
			<td style="background-image:url(images/window_head_back.jpg);width:7px"> &nbsp; </td>
		</tr>

		<!-- blue round corners -->
		<tr style="vertical-align:top;">
			<td style="width:7px; font-size:1pt;"><img src="images/logo-corner-ul.gif"></td>
			<td style="width:100%; font-size:1pt;"> &nbsp; </td>
			<td style="width:7px; font-size:1pt;"><img src="images/logo-corner-ur.gif"></td>
		</tr>			
			
		<tr style="height:100%;">
			<td></td>
			<td>	
				<table style="width:100%; height:100%; border:10px solid #f2f2f5;" cellspacing="0" cellpadding="0" >
					
					<!-- blue header bar corners -->
					<tr>
						<td><img src="images/bluebar_corner_small_ul.gif"></td>
						<td style="width:100%; background-color:#3566F0;"></td>
						<td><img src="images/bluebar_corner_small_ur.gif"></td>
					</tr>
					
					<tr>
						<td colspan="3">
							<!-- table containing the whole calendar with month picker - start -->
							<table class="listtable" style="width:100%; border:0px;" cellspacing="0" cellpadding="0" border="0">
							
								<tr style="height:21px;">
									<script language="JavaScript">
									
									// print weekdays titles
									for (var n=0; n<7; n++)
									{
										document.write('<th bgcolor="#e1e1e1" align="center">'+obj_caller.ARR_WEEKDAYS[(obj_caller.NUM_WEEKSTART+n)%7]+'</th>');
									}
									document.write('</tr>');
									
									// print calendar table
									var dt_current_day = new Date(dt_firstday);
									while (dt_current_day.getMonth() == dt_current.getMonth() ||
										dt_current_day.getMonth() == dt_firstday.getMonth()) 
									{
										// print row heder
										document.write('<tr style="height:21px;">');
										for (var n_current_wday=0; n_current_wday<7; n_current_wday++) 
										{
											if (dt_current_day.getDate() == dt_current.getDate() && dt_current_day.getMonth() == dt_current.getMonth())
											{
												// print current date
												document.write('<td style="padding-top:0px; padding-left:0px;" bgcolor="#ffb6c1" align="center" width="14%">');
											}
											else if (dt_current_day.getDay() == 0 || dt_current_day.getDay() == 6)
											{
												// weekend days
												document.write('<td style="padding-top:0px; padding-left:0px;" bgcolor="#e6eef9" align="center" width="14%">');
											}
											else
											{
												// print working days of current month
												document.write('<td style="padding-top:0px; padding-left:0px;" bgcolor="#ffffff" align="center" width="14%">');
											}
									
											document.write('<a href="javascript:set_datetime('+dt_current_day.getTime() +', true);">');
									
											if (dt_current_day.getMonth() == this.dt_current.getMonth())
											{
												// print days of current month
												document.write('<font color="#000000">');
											}
											else 
											{
												// print days of other months
												document.write('<font color="#606060">');
											}
												
											document.write(dt_current_day.getDate()+'</font></a></td>');
											dt_current_day.setDate(dt_current_day.getDate()+1);
										}
										// print row footer
										document.write('</tr>');
									}
									</script>
			
								<!-- footer containing month and arrows -->
								<tr>
									<td style="border:0px; padding:0px; background-color:#e1e1e1;" colspan="7">
										<table cellspacing="0" cellpadding="0" style="width:100%; padding:0px; border:0px;" border="0">
											<tr>
												<!-- lower left corner -->
												<td style="width:7px; padding:0px; vertical-align:bottom; height:23px;"><img src="images/editortab_corner_bl.gif"></td>

												<!-- previous year icon -->
												<td style="padding:0px;">
													<a href="#" hidefocus="true" style="text-decoration:none;"
														onMouseover="document.getElementById('id_btn_first_bg_left').style.backgroundImage = 'url(images/icons/footer_background_hover_l.gif)'; document.getElementById('id_btn_first_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_hover_m.gif)'; document.getElementById('id_btn_first_bg_right').style.backgroundImage = 'url(images/icons/footer_background_hover_r.gif)'; return true;"
														onMouseout="document.getElementById('id_btn_first_bg_left').style.backgroundImage = 'url(images/icons/footer_background_l.gif)'; document.getElementById('id_btn_first_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_m.gif)'; document.getElementById('id_btn_first_bg_right').style.backgroundImage = 'url(images/icons/footer_background_r.gif)'; return true;"
														onFocus="document.getElementById('id_btn_first_bg_left').style.backgroundImage = 'url(images/icons/footer_background_hover_l.gif)'; document.getElementById('id_btn_first_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_hover_m.gif)'; document.getElementById('id_btn_first_bg_right').style.backgroundImage = 'url(images/icons/footer_background_hover_r.gif)'; return true;"
														onBlur="document.getElementById('id_btn_first_bg_left').style.backgroundImage = 'url(images/icons/footer_background_l.gif)'; document.getElementById('id_btn_first_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_m.gif)'; document.getElementById('id_btn_first_bg_right').style.backgroundImage = 'url(images/icons/footer_background_r.gif)'; return true;"
														onClick="set_datetime(dt_prev_year.getTime());">
															<table cellpadding="0" cellspacing="0" border="0">
																<tr>
																	<td id="id_btn_first_bg_left" style="width:3px; padding:0px;" background="images/icons/footer_background_l.gif"><div style="width:3px;"></div></td>
																	<td id="id_btn_first_bg_middle" style="height:23px; padding:0px;" background="images/icons/footer_background_m.gif"><img src="images/icons/footer_first.gif" alt="previous year"></td>
																	<td id="id_btn_first_bg_right" style="width:3px; padding:0px;" background="images/icons/footer_background_r.gif"><div style="width:3px;"></div></td>
																</tr>
															</table>
													</a>
												</td>

												<!-- previous month icon -->
												<td style="padding:0px;">
													<a href="#" hidefocus="true" style="text-decoration:none;"
														onMouseover="document.getElementById('id_btn_previous_bg_left').style.backgroundImage = 'url(images/icons/footer_background_hover_l.gif)'; document.getElementById('id_btn_previous_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_hover_m.gif)'; document.getElementById('id_btn_previous_bg_right').style.backgroundImage = 'url(images/icons/footer_background_hover_r.gif)'; return true;"
														onMouseout="document.getElementById('id_btn_previous_bg_left').style.backgroundImage = 'url(images/icons/footer_background_l.gif)'; document.getElementById('id_btn_previous_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_m.gif)'; document.getElementById('id_btn_previous_bg_right').style.backgroundImage = 'url(images/icons/footer_background_r.gif)'; return true;"
														onFocus="document.getElementById('id_btn_previous_bg_left').style.backgroundImage = 'url(images/icons/footer_background_hover_l.gif)'; document.getElementById('id_btn_previous_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_hover_m.gif)'; document.getElementById('id_btn_previous_bg_right').style.backgroundImage = 'url(images/icons/footer_background_hover_r.gif)'; return true;"
														onBlur="document.getElementById('id_btn_previous_bg_left').style.backgroundImage = 'url(images/icons/footer_background_l.gif)'; document.getElementById('id_btn_previous_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_m.gif)'; document.getElementById('id_btn_previous_bg_right').style.backgroundImage = 'url(images/icons/footer_background_r.gif)'; return true;"
														onClick="set_datetime(dt_prev_month.getTime())">
															<table cellpadding="0" cellspacing="0" border="0">
																<tr>
																	<td id="id_btn_previous_bg_left" style="width:3px; padding:0px;" background="images/icons/footer_background_l.gif"><div style="width:3px;"></div></td>
																	<td id="id_btn_previous_bg_middle" style="height:23px; padding:0px;" background="images/icons/footer_background_m.gif"><img src="images/icons/footer_previous.gif" alt="previous month"></td>
																	<td id="id_btn_previous_bg_right" style="width:3px; padding:0px;" background="images/icons/footer_background_r.gif"><div style="width:3px;"></div></td>
																</tr>
															</table>
													</a>
												</td>

													<!-- current month and year -->
												<td style="padding:0px; text-align:center; width:100%; white-space:nowrap;"><font style="color:#0021c7; font-weight:bold;"><script language="JavaScript">document.write(obj_caller.ARR_MONTHS[dt_current.getMonth()] + ' ' + dt_current.getFullYear());</script></font></td>

												<!-- next month icon -->
												<td style="padding:0px;">
													<a href="#" hidefocus="true" style="text-decoration:none;"
														onMouseover="document.getElementById('id_btn_next_bg_left').style.backgroundImage = 'url(images/icons/footer_background_hover_l.gif)'; document.getElementById('id_btn_next_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_hover_m.gif)'; document.getElementById('id_btn_next_bg_right').style.backgroundImage = 'url(images/icons/footer_background_hover_r.gif)'; return true;"
														onMouseout="document.getElementById('id_btn_next_bg_left').style.backgroundImage = 'url(images/icons/footer_background_l.gif)'; document.getElementById('id_btn_next_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_m.gif)'; document.getElementById('id_btn_next_bg_right').style.backgroundImage = 'url(images/icons/footer_background_r.gif)'; return true;"
														onFocus="document.getElementById('id_btn_next_bg_left').style.backgroundImage = 'url(images/icons/footer_background_hover_l.gif)'; document.getElementById('id_btn_next_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_hover_m.gif)'; document.getElementById('id_btn_next_bg_right').style.backgroundImage = 'url(images/icons/footer_background_hover_r.gif)'; return true;"
														onBlur="document.getElementById('id_btn_next_bg_left').style.backgroundImage = 'url(images/icons/footer_background_l.gif)'; document.getElementById('id_btn_next_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_m.gif)'; document.getElementById('id_btn_next_bg_right').style.backgroundImage = 'url(images/icons/footer_background_r.gif)'; return true;"
														onClick="set_datetime(dt_next_month.getTime())"> 
															<table cellpadding="0" cellspacing="0" border="0">
																<tr>
																	<td id="id_btn_next_bg_left" style="width:3px; padding:0px;" background="images/icons/footer_background_l.gif"><div style="width:3px;"></div></td>
																	<td id="id_btn_next_bg_middle" style="height:23px; padding:0px;" background="images/icons/footer_background_m.gif"><img src="images/icons/footer_next.gif" alt="next month"></td>
																	<td id="id_btn_next_bg_right" style="width:3px; padding:0px;" background="images/icons/footer_background_r.gif"><div style="width:3px;"></div></td>
																</tr>
															</table>
													</a>
												</td>

												<!-- next year icon -->
												<td style="padding:0px;">
													<a href="#" hidefocus="true" style="text-decoration:none;"
														onMouseover="document.getElementById('id_btn_last_bg_left').style.backgroundImage = 'url(images/icons/footer_background_hover_l.gif)'; document.getElementById('id_btn_last_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_hover_m.gif)'; document.getElementById('id_btn_last_bg_right').style.backgroundImage = 'url(images/icons/footer_background_hover_r.gif)'; return true;"
														onMouseout="document.getElementById('id_btn_last_bg_left').style.backgroundImage = 'url(images/icons/footer_background_l.gif)'; document.getElementById('id_btn_last_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_m.gif)'; document.getElementById('id_btn_last_bg_right').style.backgroundImage = 'url(images/icons/footer_background_r.gif)'; return true;"
														onFocus="document.getElementById('id_btn_last_bg_left').style.backgroundImage = 'url(images/icons/footer_background_hover_l.gif)'; document.getElementById('id_btn_last_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_hover_m.gif)'; document.getElementById('id_btn_last_bg_right').style.backgroundImage = 'url(images/icons/footer_background_hover_r.gif)'; return true;"
														onBlur="document.getElementById('id_btn_last_bg_left').style.backgroundImage = 'url(images/icons/footer_background_l.gif)'; document.getElementById('id_btn_last_bg_middle').style.backgroundImage = 'url(images/icons/footer_background_m.gif)'; document.getElementById('id_btn_last_bg_right').style.backgroundImage = 'url(images/icons/footer_background_r.gif)'; return true;"
														onClick="set_datetime(dt_next_year.getTime())"> 
															<table cellpadding="0" cellspacing="0" border="0">
																<tr>
																	<td id="id_btn_last_bg_left" style="width:3px; padding:0px;" background="images/icons/footer_background_l.gif"><div style="width:3px;"></div></td>
																	<td id="id_btn_last_bg_middle" style="height:23px; padding:0px;" background="images/icons/footer_background_m.gif"><img src="images/icons/footer_last.gif" alt="next year"></td>
																	<td id="id_btn_last_bg_right" style="width:3px; padding:0px;" background="images/icons/footer_background_r.gif"><div style="width:3px;"></div></td>
																</tr>
															</table>
													</a>
												</td>

											<!-- lower right corner -->
												<td style="width:20px; padding:0px; vertical-align:bottom;"><img src="images/editortab_corner_br.gif"></td>
											</tr>
										</table>
									</td>		
								</tr>
							</table>
						<td>
					</tr>
										
					<!-- filler -->
					<tr>
						<td colspan="3" height="100%"></td>
					</tr>

				</table>
				
			</td>
			<td></td>
		</tr>

		<!-- empty grey space -->
		<tr style="vertical-align:bottom; height:7px;">
			<td colspan="3" style="width:100%; font-size:1pt;"> &nbsp; </td>
		</tr>

	</table>

</body>

</html>

