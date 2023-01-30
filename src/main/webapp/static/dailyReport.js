function getSalesReportUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/reports/daily";
}

function getDailyReport() {
    var url = getSalesReportUrl();
    console.log(url);
    $.ajax({
       url: url,
       type: 'GET',
       success: function(response) {
            console.log(response);
            displayInventoryReport(response);
       },
       error: handleAjaxError
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

//INITIALIZATION CODE
function init(){

//       var element = document.getElementById("inventory-report-icon");
//       element.classList.add("thick");
       getDailyReport();
}

$(document).ready(init);