//
//function getInventoryUrl(){
//	var baseUrl = $("meta[name=baseUrl]").attr("content")
//	return baseUrl + "/api/inventory";
//}
//
////BUTTON ACTIONS
//function addInventory(event){
//	//Set the values to update
//	var $form = $("#inventory-form");
//	var json = toJson($form);
//	var url = getInventoryUrl();
//    console.log(url);
//    console.log("ankur jain");
//    console.log(json);
//	$.ajax({
//	   url: url,
//	   type: 'POST',
//	   data: json,
//	   headers: {
//       	'Content-Type': 'application/json'
//       },
//	   success: function(response) {
//	   		getInventoryList();
//	   },
//	   error: handleAjaxError
//	});
//
//	return false;
//}
//
//function getInventoryList(){
//	var url = getInventoryUrl();
//	$.ajax({
//	   url: url,
//	   type: 'GET',
//	   success: function(data) {
//	   		displayInventoryList(data);
//	   },
//	   error: handleAjaxError
//	});
//}
//
//function deleteInventory(id){
//	var url = getInventoryUrl() + "/" + id;
//	console.log('pppppppppppppppppppppppp')
//    console.log(url);
//	$.ajax({
//	   url: url,
//	   type: 'DELETE',
//	   success: function(data) {
//	   		getInventoryList();
//	   },
//	   error: handleAjaxError
//	});
//}
//
////UI DISPLAY METHODS
//
//function displayEditInventory(id){
//console.log('jjjjjjjjjjjjjjjjjjjjjjjjjj')
//	var url = getInventoryUrl() + "/" + id;
//	console.log(url);
//	console.log('ttttttttttttttttttt')
//	$.ajax({
//	   url: url,
//	   type: 'PUT',
//	   success: function(data) {
//	   		displayInventoryList(data);
//	   },
//	   error: handleAjaxError
//	});
//}
//
//
//
//
//function displayInventoryList(data){
//	console.log('Printing Inventory data');
//	var $tbody = $('#inventory-table').find('tbody');
//	$tbody.empty();
//	for(var i in data){
//		var e = data[i];
//		console.log("ppppppppppppppppp")
//		console.log(e);
//		var buttonHtml = '<button onclick="deleteInventory(' + e.id + ')">delete</button>'
//		buttonHtml += ' <button onclick="displayEditInventory(' + e.id + ')">edit</button>'
//		var row = '<tr>'
//		+ '<td>' + e.id + '</td>'
//		+ '<td>' + e.name + '</td>'
//        + '<td>' + e.category + '</td>'
//		+ '<td>' + buttonHtml + '</td>'
//		+ '</tr>';
//        $tbody.append(row);
//	}
//}
//
//
////INITIALIZATION CODE
//function init(){
//	$('#add-inventory').click(addInventory);
//	$('#refresh-data').click(getInventoryList);
//
//}
//
//$(document).ready(init);
//$(document).ready(getInventoryList);
//














function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

//BUTTON ACTIONS
function addInventory(event){
	//Set the values to update
	var $form = $("#inventory-form");
	var json = toJson($form);
	var url = getInventoryUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getInventoryList();
	   },
	   error: handleAjaxError,
	});

	return false;
}

function updateInventory(event){
	$('#edit-inventory-modal').modal('toggle');
	//Get the ID
	var barcode = $("#inventory-edit-form input[name=barcode]").val();
	var url = getInventoryUrl() + "/" + barcode;

	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);
    console.log(url,barcode)
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   console.log("successsssssssssss")
	   		getInventoryList();
	   },
	   error:
	   handleAjaxError

	});

	return false;
}


function getInventoryList(){
console.log('get inven list')
	var url = getInventoryUrl();
	console.log(url)
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   console.log('success')
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}

function deleteInventory(barcode){
	var url = getInventoryUrl() + "/" + barcode;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getInventoryList();
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#inventoryFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
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

	var json = JSON.stringify(row);
	var url = getInventoryUrl();

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

function displayInventoryList(data){
    console.log('display inven list')
	var $tbody = $('#inventory-table').find('tbody');
	console.log('pppppppppppppp')
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		console.log(e);
		var buttonHtml = '<button onclick="deleteInventory(' + "'" + e.barcode + "'" + ')">delete</button>'
		buttonHtml += ' <button onclick="displayEditInventory(' + "'" + e.barcode + "'" + ')">edit</button>'
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.productName + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditInventory(barcode){
console.log('ankur jainnnnnnnnnnnnnn')
	var url = getInventoryUrl() + "/" + barcode;
	console.log(url)
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);
	   },
	   error: handleAjaxError
	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
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
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#inventoryFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');
}

function displayInventory(data){
    console.log('displayInventory')
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
//	$("#inventory-edit-form input[name=id]").val(data.id);
	$('#edit-inventory-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){
	$('#add-inventory').click(addInventory);
	$('#update-inventory').click(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName)
    var element = document.getElementById("inventory-icon");
    element.classList.add("thick");
}

$(document).ready(init);
$(document).ready(getInventoryList);

