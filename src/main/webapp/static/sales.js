function getSalesReportUrl(){
   var baseUrl = $("meta[name=baseUrl]").attr("content")
   return baseUrl + "/api/report/sales";
}

function getBaseUrl() {
  return $('meta[name=baseUrl]').attr('content');
}

function getBrandUrl() {
  return getBaseUrl() + '/api/brands';
}


function filterSalesReport() {
    var $form = $("#sales-form");
    var json = toJson($form);
    var url = getSalesReportUrl();
    console.log(url);
    console.log(json);
    $.ajax({
       url: url,
       type: 'POST',
       data: json,
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
  function prependOptions(selectElementId, displayName) {
    const $selectElement = $(selectElementId);
    const optionHtml = `<option value="" selected >${displayName}</option>`;
    $selectElement.prepend(optionHtml);
  }

function getBrandListInFilter() {
const url = getBrandUrl();
$.ajax({
	   url: url,
	   type: 'GET',
	   success:async function(brands) {
	       const brandCategory = brands.map((brandItem) => {
             return { brand: brandItem.name, category: brandItem.category };
           });
           brandCategory.push()
          await setupBrandCategoryDropdown(brandCategory, '#brand-name-selection', '#brand-category-selection');
          prependOptions('#brand-name-selection',"All Brands");
          prependOptions('#brand-category-selection',"All Category");
          displayProduct();
	   },
	   error:
	   handleAjaxError
	});

  };


function displayProduct(){

	$('#filter-modal').modal('toggle');
}
function showFilterModal(){
    getBrandListInFilter();
}
//INITIALIZATION CODE
function init(){

   $('#filter-sales-report').click(filterSalesReport);
   $('#display-filter-btn').click(showFilterModal);
//       var element = document.getElementById("sales-icon");
//       element.classList.add("thick");
}

$(document).ready(init);