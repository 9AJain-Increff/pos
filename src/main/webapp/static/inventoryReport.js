function getSalesReportUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/reports/inventory";
}

function getBrandUrl() {
  return $('meta[name=baseUrl]').attr('content') + "/api/brands";
}

function getInventoryReport(onSuccess) {
    var url = getSalesReportUrl();
    console.log(url);
	var $form = $("#inventory-report-form");
	var json = toJson($form);

    var url = getSalesReportUrl();
    console.log(url);

    $.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
//    	   success: function(response) {
                 success: onSuccess,
//    	   }
    	   error: handleAjaxError,
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

function getBrandListInFilter(onSuccess) {
    const url = getBrandUrl();
    $.ajax({
	   url: url,
	   type: 'GET',
	   success: onSuccess,
	   error:
	   handleAjaxError
	});
};

function initDropdown() {
    getBrandListInFilter((brands) => {
        const brandCategory = brands.map((brandItem) => {
            return { brand: brandItem.name, category: brandItem.category };
        });
        setupBrandCategoryDropdown(brandCategory, '#brand-name-selection', '#brand-category-selection');
    });
}


//INITIALIZATION CODE
function init(){
    initDropdown();
    $('#display-filter-btn').click(() => {
    	$('#filter-modal').modal('toggle');
    });

    $("#filter-sales-report").click(() => {
        getInventoryReport((data) => {
           displayInventoryReport(data);
           $('#filter-modal').modal('toggle');
        });
    });

   var element = document.getElementById("report-icon");
   element.classList.add("thick");
   getInventoryReport(displayInventoryReport);
}

$(document).ready(init);