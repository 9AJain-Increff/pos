
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
    $.notify(response.message + " ❌", 'error');
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

function setupBrandCategoryDropdown(brands, brandSelectionId, categorySelectionId, defaults) {
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
  setupFilterBehavior(brandSelectionId, categorySelectionId, brands, defaults);
}
function setupFilterBehavior(brandSelectionId, categorySelectionId, brands, defaults) {
  if (!defaults) {
    defaults = { category: 'All Categories', brand: 'All Brands' };
  }
  const brandCategoryMapper = setupBrandCategoryFilters(brands);
  $(brandSelectionId).on('change', () => {
    const brandValue = $(brandSelectionId).val();
    const categoryValue = $(categorySelectionId).val();
    const categories = brandCategoryMapper.getCategories(brandValue);
    const isValidCategorySelected = contains(categories, categoryValue);
    const selectAll = isValidCategorySelected ? '' : 'selected';
    $(categorySelectionId).empty();
    $(categorySelectionId).append(`<option value="" ${selectAll}>${defaults.category}</option>`);
    appendOptions(categorySelectionId, categories);
    if (isValidCategorySelected) {
      $(categorySelectionId).val(categoryValue);
    }
  });
  $(categorySelectionId).on('change', () => {
    const categoryValue = $(categorySelectionId).val();
    const brandValue = $(brandSelectionId).val();
    const brands = brandCategoryMapper.getBrands(categoryValue);
    const validBrandSelected = contains(brands, brandValue);
    const selectAll = validBrandSelected ? '' : 'selected';
    $(brandSelectionId).empty();
    $(brandSelectionId).append(`<option value="" ${selectAll}>${defaults.brand}</option>`);
    appendOptions(brandSelectionId, brands);
    if (validBrandSelected) {
      $(brandSelectionId).val(brandValue);
    }
  });
}
function contains(list, item) {
  for (it in list) {
    if (list[it] === item) return true;
  }
  return false;
}
function notifyError(errorMessage) {
  $('.notifyjs-wrapper').trigger('notify-hide');
  $.notify.defaults({ clickToHide: true, autoHide: false });
  $.notify(errorMessage + ' :x:', 'error');
}
function appendOptions(selectElementId, options) {
  const $selectElement = $(selectElementId);
  options.forEach((option) => {
    const optionHtml = `<option value="${option}">${option}</option>`;
    $selectElement.append(optionHtml);
  });
}
function setupBrandCategoryFilters(brandCategories) {
  const brandToCategoryMap = new Map();
  const categoryToBrandMap = new Map();
  const brandSet = new Set();
  const categorySet = new Set();
  brandCategories.forEach((item) => {
    brandSet.add(item.brand);
    categorySet.add(item.category);
    if (!brandToCategoryMap.has(item.brand)) {
      brandToCategoryMap.set(item.brand, []);
    }
    brandToCategoryMap.get(item.brand).push(item.category);
    if (!categoryToBrandMap.has(item.category)) {
      categoryToBrandMap.set(item.category, []);
    }
    categoryToBrandMap.get(item.category).push(item.brand);
  });
  function getCategoriesForBrand(brandName) {
    if (!brandName) {
      const arr = Array.from(categorySet);
      arr.sort();
      return arr;
    }
    return brandToCategoryMap.get(brandName);
  }
  function getBrandsForCategory(categoryName) {
    if (!categoryName) {
      const arr = Array.from(brandSet);
      arr.sort();
      return arr;
    }
    return categoryToBrandMap.get(categoryName);
  }
  return {
    getBrands: getBrandsForCategory,
    getCategories: getCategoriesForBrand,
  };
}