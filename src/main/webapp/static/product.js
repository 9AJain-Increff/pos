
function getBaseUrl() {
  return $('meta[name=baseUrl]').attr('content');
}

function getBrandUrl() {
  return getBaseUrl() + '/api/brands';
}




function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}

//BUTTON ACTIONS
function addProduct(event){
	//Set the values to update

	var $form = $("#product-add-form");
	var json = toJson($form);
	var url = getProductUrl();
    console.log(url,json)
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   throwSuccess("Product Added Successfully");
	   	$('#add-product-modal').modal('toggle');
	   		getProductList();

	   },
	   error: handleAjaxError,
	});

	return false;
}

function updateProduct(event){
	$('#edit-product-modal').modal('toggle');
	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductUrl() + "/" + id;

	//Set the values to update
	var $form = $("#product-edit-form");
	var json = toJson($form);
	json = JSON.parse(json);
	json.price = formatPrice(json.price);
	json = JSON.stringify(json);
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   throwSuccess("Product Updated");
	   		getProductList();
	   },
	   error: handleAjaxError
	});

	return false;
}


var productData = [];
function getProductList(){
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   productData = data;
	   displayProductList(data);
	   },
	   error: handleAjaxError
	});
}

function deleteProduct(barcode){
	var url = getProductUrl() + "/" + barcode;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getProductList();
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#productFile')[0].files[0];

	if(!file)
	{
	    throwError("File is Empty");
	    return;
	}

	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
		fileData = results.data;
    	var fields = results.meta.fields;
    	if(fields.length!=5)
    	{
    	    var row = {};
    	    row.error = "Incorrect number of headers";
    	    errorData.push(row);
    	    return;
    	}

    	if(fields[0]!='name' || fields[1]!='brandName' || fields[2]!='brandCategory' || fields[3]!='barcode'|| fields[4]!='price' )
    	{
    	    var row = {};
            row.error = "Incorrect headers";
            errorData.push(row);
            return;
    	}



        if(fileData.length===0)
        {
            throwError("Empty Tsv");
            return;
        }

        if(fileData.length>5000)
        {
            throwError("File length cannot exceed 5000");
            return;
        }
        console.log(processCount);

    	uploadRows();
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
		return;
	}

	//Process next row
	var row = fileData[processCount];
	processCount++;

	if(row.__parsed_extra)
	{
	    row.error = "Extra fields";
	    errorData.push(row);
	    uploadRows();
	    return;
	}

	var json = JSON.stringify(row);
	var url = getProductUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		uploadRows();
	   },
	   error: function(response){
	   		row.error=response.responseText
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS

function displayProductList(data){
console.log('ankur jinfo')

	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	let count =1;
	for(var i in data){
		const e = data[i];
		console.log(e.barcode);
		const editBtnId = 'edit-product-'+e.id
//		var buttonHtml = '<button onclick="deleteProduct(' + "'" + e.barcode + "'" +')">delete</button>'
		var buttonHtml = `<button class="btn btn-outline-dark" id=${editBtnId}>Edit</button>`
		var row = '<tr>'
        + '<td>' + count + '</td>'
        + '<td>' + e.barcode + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>'  + e.brandName + '</td>'
		+ '<td>' + e.brandCategory + '</td>'
		+ '<td>' + e.price + '</td>'
        + `<td  ${isSupervisor() ? '' : 'hidden'}>` + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
        count++;
        $('#'+editBtnId).click(() => displayEditProduct(e))
	}
}

function displayEditProduct(e){
	var url = getProductUrl() + "/" + e.id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success:async function(data) {
            $('#brand-category-edit').empty();
            $('#brand-name-edit').empty();
            getBrandListInEdit(data)
	   },
	   error:
	   handleAjaxError
	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#productFileName').html(fileName.split("\\").pop());
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-product-modal').modal('toggle');
}

function displayProduct(data){
    console.log(data, "ankur pppppppppppp")
//    var selectBrand = document.getElementById('selectBrand');

	$("#product-edit-form input[name=name]").val(data.name);
//	selectBrand.selectedIndex = data.brandName;
	$("#brand-name-edit").val(data.brandName);
//	$("#brand-category-edit").val(data.brandName);
  $('#brand-name-edit').val(data.brandName).change();
  $('#brand-category-edit').val(data.brandCategory).change();
//    $(`#brand-category-edit option[value=${data.brandCategory}]`).attr('selected', 'selected')
//    $(`#brand-name-edit option[value=${data.brandName}]`).attr('selected', 'selected')
//	$("#product-edit-form input[name=brandCategory]").val(data.brandCategory);
//	$("#product-edit-form input[name=brandName]").val(data.brandName);
	$("#product-edit-form input[name=price]").val(data.price);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	$("#product-edit-form input[name=id]").val(data.id);
	$('#edit-product-modal').modal('toggle');

}


function displayAddProductModal(){
    	$("#product-add-form input[name=name]").val('');
        $("#product-add-form input[name=barcode]").val('');
        $("#product-add-form input[name=price]").val('');
}
function openAddModel(){

	$('#add-product-modal').modal('toggle');
	displayAddProductModal();
	var url = getBrandUrl() ;
    	console.log(url)
    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success:function(data) {
    	   	getBrandList(data);
    	   },
    	   error:
    	   handleAjaxError
    	});
}



function getBrandList(brands) {
    const brandCategory = brands.map((brandItem) => {
      return { brand: brandItem.name, category: brandItem.category };
    });
    defaults = { category: '', brand: '' };
    setupBrandCategoryDropdown(brandCategory, '#brand-name-selection', '#brand-category-selection',
        defaults
    );
  };

function getBrandListInEdit(data) {
const url = getBrandUrl();
$.ajax({
	   url: url,
	   type: 'GET',
	   success:async function(brands) {
	       const brandCategory = brands.map((brandItem) => {
             return { brand: brandItem.name, category: brandItem.category };
           });
          await setupBrandCategoryDropdown(brandCategory, '#brand-name-edit', '#brand-category-edit',
           { category: '', brand: '' }
          );
          displayProduct(data);
	   },
	   error:
	   handleAjaxError
	});

  };



//INITIALIZATION CODE
function init(){
	$('#add-product').click(addProduct);
	$('#update-product').click(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName);
    $('#create-new-product').click(openAddModel);

    var element = document.getElementById("product-icon");
    element.classList.add("thick");
}

$(document).ready(init);
$(document).ready(getProductList);


