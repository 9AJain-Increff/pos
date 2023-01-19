function getSalesReportUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/report/inventory";
}

function getInventoryReport() {
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
    var $tbody = $('#inventory-table').find('tbody');
    console.log('my data',data)
    $tbody.empty();
    var count =0;
    for(var i in data){
        var b = data[i];
        count++;
        var row = '<tr>'
        + '<td>' + count + '</td>'
        + '<td>' + b.brandName + '</td>'
        + '<td>' + b.brandCategory + '</td>'
        + '<td>' + b.quantity + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
}

//INITIALIZATION CODE
function init(){

//       var element = document.getElementById("inventory-report-icon");
//       element.classList.add("thick");
       getInventoryReport();
}

$(document).ready(init);