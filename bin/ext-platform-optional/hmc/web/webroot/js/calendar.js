var calendars = [];
var RE_NUM = /^\-?\d+$/;

function calendar(obj_target, str_title, str_header, date_pattern, month_array, weekday_array)
{
	this.prs_tsmp = cal_prs_tsmp1;
	this.popup    = cal_popup1;

	this.title    = str_title;
	this.header   = str_header;
	this.pattern = date_pattern;

	// months as they appear in the calendar's title
	this.ARR_MONTHS = month_array; // ["Jax", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
			
	// week day titles as they appear on the calendar
	this.ARR_WEEKDAYS = weekday_array; //["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"];
	
	// day week starts from (normally 0-Su or 1-Mo)
	this.NUM_WEEKSTART = 1;

	// validate input parameters
	if (!obj_target)
		return cal_error("Error calling the calendar: no target control specified");
	if (obj_target.value == null)
		return cal_error("Error calling the calendar: parameter specified is not valid tardet control");
	this.target = obj_target;
	
	// register in global collections
	this.id = calendars.length;
	calendars[this.id] = this;
}

function cal_popup1(str_datetime) 
{
	this.dt_current = this.prs_tsmp(str_datetime ? str_datetime : this.target.value);
	if (!this.dt_current) 
	{
		return;
	}

	var obj_calwindow = window.open(
		'calendar.jsp?datetime=' + this.dt_current.getTime()+ '&id=' + this.id,
		'Calendar', 'width=225,height=245'+
		',status=no,resizable=no,top=200,left=200,dependent=yes,alwaysRaised=yes'
	);
	obj_calwindow.opener = window;
	obj_calwindow.focus();
}

// timestamp parsing function
function cal_prs_tsmp1(date_string) 
{
	if( !date_string )
	{
		// return current timestamp
		return (new Date());
	}

	// if positive integer treat as milliseconds from epoch
	if (RE_NUM.exec(date_string))
	{
		return new Date(date_string);
	}

	// parse date string
	if( isDate(date_string, this.pattern) )
	{
		return new Date(getDateFromFormat(date_string, this.pattern));
	}
	else
	{
		return cal_error ("Invalid date format: '" + date_string + "'.\nFormat accepted is " + this.pattern + ".");
	}
}

function cal_error(str_message) 
{
	alert(str_message);
	return null;
}
