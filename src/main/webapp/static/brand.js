


function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}

//BUTTON ACTIONS
function addBrand(event){
	//Set the values to update
	var $form = $("#brand-add-form");
	var json = toJson($form);
	var url = getBrandUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   throwSuccess("Brand Added Sucessfully")
	   	$('#add-brand-modal').modal('toggle');
	   	getBrandList();
	   },
	   error: handleAjaxError,
	});

	return false;
}

function updateBrand(event){
	$('#edit-brand-modal').modal('toggle');
	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();
	var url = getBrandUrl() + "/" + id;

	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);
    console.log(url,id)
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        throwSuccess("Brand Updated Successfully");
	   		getBrandList();
	   },
	   error: handleAjaxError
	});

	return false;
}


function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrandList(data);
	   },
	   error: handleAjaxError
	});
}

function deleteBrand(id){
	var url = getBrandUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getBrandList();
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#brandFile')[0].files[0];

	if(!file)
	{
	    throwError("File is Empty");
	    return;
	}

	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
    console.log(results);
	fileData = results.data;
	var fields = results.meta.fields;
	if(fields.length!=2)
	{
	    var row = {};
	    row.error = "Incorrect number of headers";
	    errorData.push(row);
	    return;
	}

	if(fields[0]!='name' || fields[1]!='category')
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

	console.log('ankur jain', fileData)
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
	var url = getBrandUrl();
    console.log(url, json);
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

function displayBrandList(data){
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
    let count =1;
	for(var i in data){

		var e = data[i];
		var buttonHtml = ' <button onclick="displayEditBrand(' + e.id + ')" class="btn btn-outline-dark">edit</button>'
		var row = '<tr>'
		+ '<td>' + count+ '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>'  + e.category + '</td>'
        + `<td  ${isSupervisor() ? '' : 'hidden'}>` + buttonHtml + '</td>'

		+ '</tr>';
        $tbody.append(row);
        count++;
	}
}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	console.log(url)
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);
	   },
	   error: handleAjaxError
	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
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
	var $file = $('#brandFile');
	var fileName = $file.val();
	$('#brandFileName').html(fileName.split("\\").pop());
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-brand-modal').modal('toggle');
}

function displayBrand(data){
	$("#brand-edit-form input[name=name]").val(data.name);
	$("#brand-edit-form input[name=category]").val(data.category);
	$("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
}

function createNewBrand(){
    	$('#add-brand-modal').modal('toggle');

}



function displayAddBrandModal(data){
	$("#brand-add-form input[name=name]").val('');
	$("#brand-add-form input[name=category]").val('');
	$("#brand-add-form input[name=id]").val('');
	$('#add-brand-modal').modal('toggle');
}
//INITIALIZATION CODE
function init(){
	$('#add-brand').click(addBrand);
	$('#update-brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#brandFile').on('change', updateFileName)
   	$('#create-new-brand').click(displayAddBrandModal);

    var element = document.getElementById("brand-icon");
    element.classList.add("thick");
}

$(document).ready(init);
$(document).ready(getBrandList);

