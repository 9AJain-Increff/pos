function getSalesReportUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/reports/sales";
}

function getBaseUrl() {
  return $('meta[name=baseUrl]').attr('content');
}

function getBrandUrl() {
  return getBaseUrl() + '/api/brands';
}


function filterSalesReport() {
    var $form = $("#sales-form");

    let jsonString = toJson($form);

      const json = JSON.parse(jsonString);

      setupDate(json);
      jsonString = JSON.stringify(json);

      const url = getSalesReportUrl();

    $.ajax({
       url: url,
       type: 'POST',
       data: jsonString,
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(response) {
            console.log(response);
            $('#filter-modal').modal('toggle');
            displaySalesReport(response);
       },
       error: handleAjaxError
    });
}


function filterSalesReportOnLoad() {
    var $form = $("#sales-form");

    let jsonString = toJson($form);

      const json = JSON.parse(jsonString);

      setupDate(json);
      jsonString = JSON.stringify(json);

      const url = getSalesReportUrl();

    $.ajax({
       url: url,
       type: 'POST',
       data: jsonString,
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(response) {
            console.log(response);
            displaySalesReport(response);
       },
       error: handleAjaxError
    });
}
function displaySalesReport(data) {
    var $tbody = $('#sales-table').find('tbody');
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
        + '<td>' + b.revenue + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
}
//  function prependOptions(selectElementId, displayName) {
//    const $selectElement = $(selectElementId);
//    const optionHtml = `<option value="" selected >${displayName}</option>`;
//    $selectElement.prepend(optionHtml);
//  }

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
   $('#filter-sales-report').click(filterSalesReport);
   $('#display-filter-btn').click(() => {
	$('#filter-modal').modal('toggle');
   });
   var element = document.getElementById("report-icon");
   element.classList.add("thick");
   filterSalesReportOnLoad();
}

$(document).ready(init);