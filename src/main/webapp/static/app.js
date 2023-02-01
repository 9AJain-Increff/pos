
//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}


//function handleAjaxError(response){
//console.log('failllllllllllll')
//	var response = JSON.parse(response.responseText);
//	$.notify.defaults( {clickToHide:true,autoHide:false} );
//	$('.notifyjs-wrapper').trigger('notify-hide');
//	$.notify(response.message, 'error');
//}
function handleAjaxError(response){
    var response = JSON.parse(response.responseText);
    $('.notifyjs-wrapper').trigger('notify-hide');
    $.notify.defaults( {clickToHide:true,autoHide:false} );
    $.notify(response.message + " :x:", 'error');
}

function throwError(message){
    $('.notifyjs-wrapper').trigger('notify-hide');
    $.notify.defaults( {clickToHide:true,autoHide:false} );
    $.notify(message + " :x:", 'error');
}

//prevent 'e' press in number field
var invalidChars = [
    "-",
    "+",
    "e",
];
const verifyNumberInput = () =>  document.querySelectorAll('input[type="number"]').forEach( input => input.addEventListener("keydown", function(e) {
    if (invalidChars.includes(e.key)) {
      e.preventDefault();
    }
  }));
function readFileData(file, callback){
	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}	
	}
	Papa.parse(file, config);
}


function writeFileData(arr){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
	
	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tsv;charset=utf-8;'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'download.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'download.tsv');
    tempLink.click(); 
}

function setupBrandCategoryDropdown(brands, brandSelectionId, categorySelectionId) {
  const brandNameSet = new Set();
  const brandCategorySet = new Set();

  brands.forEach((item) => {
    brandNameSet.add(item.brand);
    brandCategorySet.add(item.category);
  });
   const sortAndAppend = (id, set) => {
      const list = Array.from(set);
      list.sort();
      appendOptions(id, list);
    };

    sortAndAppend(brandSelectionId, brandNameSet);
    sortAndAppend(categorySelectionId, brandCategorySet);
  }
  function appendOptions(selectElementId, options) {
    const $selectElement = $(selectElementId);
    $selectElement.empty();
    options.forEach((option) => {
      const optionHtml = `<option value="${option}">${option}</option>`;
      $selectElement.append(optionHtml);
    });
  }

