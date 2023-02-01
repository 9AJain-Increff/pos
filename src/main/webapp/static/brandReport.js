function getSalesReportUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/reports/brand";
}

function getBrandReport(onSuccess) {

	var $form = $("#brand-report-form");
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
                 success: onSuccess,
    	   error: handleAjaxError,
    	});

}

function displayInventoryReport(data) {
    var $tbody = $('#brand-table').find('tbody');
    console.log('my data',data)
    $tbody.empty();
    var count =0;
    for(var i in data){
        var b = data[i];
        count++;
        var row = '<tr>'
        + '<td>' + count + '</td>'
        + '<td>' + b.name + '</td>'
        + '<td>' + b.category + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
}

function getBrandUrl() {
  return $('meta[name=baseUrl]').attr('content') + "/api/brands";
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
        getBrandReport((data) => {
           displayInventoryReport(data);
           $('#filter-modal').modal('toggle');
        });
    });

   var element = document.getElementById("report-icon");
   element.classList.add("thick");
   getBrandReport(displayInventoryReport);
}

$(document).ready(init);