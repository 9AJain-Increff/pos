function getSalesReportUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/reports/daily";
}

function getDailyReport() {
    var $form = $("#sales-form");
    	var json = toJson($form);
    	var url = getSalesReportUrl();

    	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	   	$('#filter-modal').modal('toggle');
    	   	displayInventoryReport(response);
    	   },
    	   error: handleAjaxError,
    	});
}

function getDailyReportOnLoad() {
    var $form = $("#sales-form");
    	var json = toJson($form);
    	var url = getSalesReportUrl();

    	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	   	displayInventoryReport(response);
    	   },
    	   error: handleAjaxError,
    	});
}

function displayInventoryReport(data) {
    var $tbody = $('#daily-report-table').find('tbody');
    console.log('my data',data)
    $tbody.empty();
    var count =0;
    for(var i in data){
        var b = data[i];
        d = getDate(b.date);
        count++;
        var row = '<tr>'
        + '<td>' + d + '</td>'
        + '<td>' + b.ordersQuantity + '</td>'
        + '<td>' + b.orderItemsQuantity + '</td>'
        + '<td>' + b.revenue + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
}

function getDate(date){

    var year = date.year.toString();
    const month = date.monthValue.toString();
    const day = date.dayOfMonth.toString();
    return day+"/"+month+"/"+year;

}
function getFormattedDate(timeUTC) {
  const year = timeUTC.year;
  const month = timeUTC.monthValue;
  const day = timeUTC.dayOfMonth;
   const hour = timeUTC.hour;
    const min = timeUTC.minute;
const sec = timeUTC.second;

  const ist = new Date(`${month}/${day}/${year} ${hour}:${min}:${sec} UTC`);

  const doubleDigit = (digit) => {
    if (digit.toString().length < 2) {
      return `0${digit}`;
    }
    return digit;
  };

  const dformat =
    [doubleDigit(ist.getDate()), doubleDigit(ist.getMonth() + 1), ist.getFullYear()].join('/') +
    ' ' +
    [doubleDigit(ist.getHours()), doubleDigit(ist.getMinutes()), doubleDigit(ist.getSeconds())].join(':');

  return dformat;
}

function openFilterModal(){
    	$('#filter-modal').modal('toggle');

}

//INITIALIZATION CODE
function init(){
        // todo: change to POST, pass date as JSON
   $('#filter-sales-report').click(getDailyReport);
   $('#display-filter-btn').click(openFilterModal);

    var element = document.getElementById("report-icon");
       element.classList.add("thick");
       getDailyReportOnLoad();
}

$(document).ready(init);