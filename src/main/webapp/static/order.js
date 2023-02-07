// API Calls

function getBaseUrl() {
  return $('meta[name=baseUrl]').attr('content');
}

function getOrderUrl() {
  return getBaseUrl() + '/api/orders/';
}

function getProductUrl() {
  return getBaseUrl() + '/api/products';
}

function getInventoryUrl() {
  return getBaseUrl() + '/api/inventory';
}
function getPdfUrl() {
  return getBaseUrl() + '/api/orders';
}
function getOrderList() {
  var url = getOrderUrl();
  $.ajax({
    url: url,
    type: 'GET',
    success: function (data) {
      console.log(data);
      displayOrderList(data);
    },
    error: handleAjaxError,
  });
}

function getProductById(productId, onSuccess) {
  const url = getProductUrl() + '/' + productId;
  console.log(url);
  $.ajax({
    url: url,
    type: 'GET',
    success: function (data) {
    console.log('getting product details',data);
      onSuccess(data);
    },
    error: handleAjaxError,
  });
}

//UI DISPLAY METHODS
let orderItems = [];
let deleteItems = [];
let updateItems = [];

function getCureentOrderItem() {
  return {
    barcode: $('#inputBarcode').val(),
    quantity: Number.parseInt($('#inputQuantity').val()),
    sellingPrice: Number.parseInt($('#inputSellingPrice').val()),
  };
}
function getCureentEditOrderItem() {
  return {
    barcode: $('#inputNewBarcode').val(),
    quantity: Number.parseInt($('#inputNewQuantity').val()),
    sellingPrice: Number.parseInt($('#inputNewSellingPrice').val()),
  };
}

function addItem(item) {
  const index = orderItems.findIndex((it) => it.barcode === item.barcode);
  if (index == -1) {
    orderItems.push(item);
  } else {
    orderItems[index].quantity += item.quantity;
  }
}

function isInvalidInput(item) {
  if (!item.barcode) {
    throwError('Please input a valid barcode!');
    return true;
  }
//  if(Number.isNaN(item.quantity)){
//    $.notify('Quantity must be a number!', 'error');
//    return true;
//  };
if (item.quantity <= 0  ) {
    throwError('Quantity must be positve!');
    return true;
  }
  if (item.sellingPrice <= 0  ) {
      throwError('Selling Price must be positve!');
      return true;
    }
if (!item.quantity ) {
      throwError('Quantity cannot be empty!');
    return true;
  }
  if (!item.sellingPrice) {
        throwError('Selling Price cannot be empty!');
      return true;
    }


  return false;
}

function addOrderItem() {
  const item = getCureentOrderItem();
  if(isInvalidInput(item))return;
  checkInventoryByBarcode(item, displayCreateOrderItems,resetAddItemForm);

}

function onQuantityChanged(barcode) {
  const index = orderItems.findIndex((it) => it.barcode === barcode);
  console.log('on change ', index)
  if (index == -1) return;

  const newQuantity = $(`#order-item-${barcode}`).val();
  orderItems[index].quantity = Number.parseInt(newQuantity);
}

function onEditQuantityChanged(barcode) {
  const index = orderItems.findIndex((it) => it.barcode === barcode);
  console.log('on change ', index)
  if (index == -1) return;

  const newQuantity = $(`#edit-order-item-${barcode}`).val();
  orderItems[index].quantity = Number.parseInt(newQuantity);
}
function onEditPriceChanged(barcode) {
  const index = orderItems.findIndex((it) => it.barcode === barcode);
  console.log('on change ', index)
  if (index == -1) return;

  const newPrice = $(`#edit-order-item-price-${barcode}`).val();
  orderItems[index].sellingPrice = Number.parseInt(newPrice);
}
function onPriceChanged(barcode) {
  const index = orderItems.findIndex((it) => it.barcode === barcode);
  console.log('on change ', index)
  if (index == -1) return;

  const newPrice = $(`#add-order-item-price-${barcode}`).val();
  orderItems[index].sellingPrice = Number.parseInt(newPrice);
}

function displayCreateOrderItems(data) {
  const $tbody = $('#create-order-table').find('tbody');
  $tbody.empty();

  for (let i in data) {
    const item = data[i];
    const row = `
      <tr>
        <td>${Number.parseInt(i) + 1}</td>
        <td class="barcodeData">${item.barcode}</td>
        <td>${item.name}</td>
        <td>
          <input
            id="add-order-item-price-${item.barcode}"
            type="number"
            class="form-controll
            quantityData"
            value="${item.sellingPrice}"
            onchange="onPriceChanged('${item.barcode}')"
            style="width:4.5rem" min="1">
        </td>
        <td>
          <input
            id="order-item-${item.barcode}"
            type="number"
            class="form-controll
            quantityData"
            value="${item.quantity}"
            onchange="onQuantityChanged('${item.barcode}')"
            style="width:70%" min="1">
        </td>
        <td>
          <button onclick="deleteOrderItem('${item.barcode}')" class="btn btn-outline-danger">Delete</button>
        </tb>
      </tr>
    `;

    $tbody.append(row);
  }
}


function deleteOrderItem(barcode) {
  const index = orderItems.findIndex((it) => it.barcode === barcode);
  console.log(index)
  if (index == -1) return;
  orderItems.splice(index, 1);
  displayCreateOrderItems(orderItems);
}

function resetAddItemForm() {
  $('#inputBarcode').val('');
  $('#inputQuantity').val('');
  $('#inputSellingPrice').val('');
}


function resetModal() {
  resetAddItemForm();
  orderItems = [];
  displayCreateOrderItems(orderItems);
}
function getFormattedDate(timeUTC) {
while(timeUTC.length <6 ){
timeUTC.push(0);
}
  const year = timeUTC[0];
  const month = timeUTC[1];
  const day = timeUTC[2];
   const hour = timeUTC[3];
    const min = timeUTC[4];
const sec = timeUTC[5];

  const ist = new Date(`${month}/${day}/${year} ${hour}:${min}:${sec} UTC`);

  const doubleDigit = (digit) => {
    if (digit.toString().length < 2) {
      return `0${digit}`;
    }
    return digit;
  };

  const dformat =
    [doubleDigit(ist.getDate()), doubleDigit(ist.getMonth() + 1), ist.getFullYear()].join('/') +
    ' ' +
    [doubleDigit(ist.getHours()), doubleDigit(ist.getMinutes()), doubleDigit(ist.getSeconds())].join(':');

  return dformat;
}

function callPdfGenerator(id){
const url = getOrderUrl() +id+'/invoice';
  $.ajax({
    url: url,
    type: 'GET',
    xhrFields: {
      responseType: 'blob',
    },
    headers: {
      'Content-Type': 'application/json',
    },
    success: (pdfBlob) => {
      const link = document.createElement('a');
      link.href = window.URL.createObjectURL(pdfBlob);
      link.download = 'invoice_' + id + '_' + new Date().getTime() + '.pdf';
      link.click();
    },
    error: handleAjaxError,
  });
}

function getPdf(orderItems){
const pdfUrl = getPdfUrl();

       getData = orderItems.map((it) => {
        return {
          barcode: it.barcode,
          quantity: it.quantity,
          sellingPrice: it.sellingPrice,
          name: it.name,
          id: it.id
        };
      });
      var data= {id: 1, datetime : "2021-08-20 14:17:43",items:getData };
//
//    data[id]=1;
//    data[sateTime]="29/09/2015";

      const json = JSON.stringify(data);
      console.log(json);
      $.ajax({
          url: pdfUrl,
          type: 'POST',
          data: json,
          headers: {
            'Content-Type': 'application/json',
          },
          success: function(base64){
          console.log(base64)
          },
          error: handleAjaxError,
        });}

function displayOrderList(orders) {
  var $tbody = $('#order-table').find('tbody');
  $tbody.empty();

  orders.forEach((order) => {

    date = getFormattedDate(order.createdOn);
    console.log('time', date);
    var row = `
        <tr>
            <td>${order.id}</td>
            <td>${date}</td>
            <td>
                <button class="btn btn-outline-dark" onclick="fetchOrderDetails(${order.id})">
                  Details
                </button>
                <button class="btn btn-outline-dark" onclick="displayEditModal(${order.id})">
                  Edit
                </button>
                <button class="btn btn-outline-dark" onclick="callPdfGenerator(${order.id})">
                                  Download pdf
                                </button>
            </td>
        </tr>
    `;
    $tbody.append(row);
  });
}


function resetUploadDialog() {
  //Reset file name
  var $file = $('#orderFile');
  $file.val('');
  $('#orderFileName').html('Choose File');
  //Reset various counts
  processCount = 0;
  fileData = [];
  errorData = [];
  //Update counts
  updateUploadDialog();
}

function updateUploadDialog() {
  $('#rowCount').html('' + fileData.length);
  $('#processCount').html('' + processCount);
  $('#errorCount').html('' + errorData.length);
}

function updateFileName() {
  var $file = $('#orderFile');
  var fileName = $file.val();
  $('#orderFileName').html(fileName);
}

function displayUploadData() {
  resetUploadDialog();
  $('#upload-order-modal').modal('toggle');
}

function displayOrderDetails(data) {
const $tbody = $('#show-order-table').find('tbody');
  $tbody.empty();
  var count =1;
  for (let i in data) {
    const item = data[i];
//     var count = i+1;
    		var row = '<tr>'
    		+ '<td>' + count + '</td>'
    		+ '<td>' + item.barcode + '</td>'
    		+ '<td>'  + item.sellingPrice + '</td>'
    		+ '<td>' + item.quantity + '</td>'
//    		+ '<td>' + e.price + '</td>'
//    		+ '<td>' + e.barcode + '</td>'
//    		+ '<td>' + buttonHtml + '</td>'
    		+ '</tr>';
//            $tbody.append(row);

    $tbody.append(row);
    count++;

  }
}


function getOrderData(id) {
$('#order-id-edit').text(id)
     var url = getOrderUrl()+id;
      $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
        console.log('data from backend')
          console.log(data);
          orderItems = data;
           displayEditOrderItems(data)
//          return data;
        },
        error: handleAjaxError,
      });
}


function resetEditItemForm() {
  $('#inputNewBarcode').val('');
  $('#inputNewQuantity').val('');
  $('#inputNewSellingPrice').val('');
}


function deleteItem(barcode) {
console.log('deleteing in edit')
console.log(orderItems)
  const index = orderItems.findIndex((it) => it.barcode === barcode);
  console.log(index, barcode);
  if (index == -1) return;
  orderItems.splice(index, 1);
  console.log(orderItems)
  displayEditOrderItems(orderItems);

}

function updateItem(barcode, quantity) {
console.log('deleteing in edit')
console.log(orderItems)
  const index = orderItems.findIndex((it) => it.barcode === barcode);
  console.log(index, barcode);
  if (index == -1) return;
  orderItems.splice(index, 1);
  console.log(orderItems)
  displayEditOrderItems(orderItems);

}


function displayEditOrderItems( data) {
  const $tbody = $('#edit-order-table').find('tbody');
  $tbody.empty();

//  $('#edit-order-btn').unbind().click(() => {
//    // todo: call update qirh is
//  })

    console.log(data);
    console.log('ppppppppppppp')
  for (let i in data) {

    const item = data[i];
    console.log('ankur jainnnnnnnnnnn');
    console.log(item.sellingPrice);
    const row = `
      <tr>
        <td>${Number.parseInt(i) + 1}</td>
        <td class="barcodeData">${item.barcode}</td>
        <td>${item.name}</td>
        <td>
          <input
            id="edit-order-item-price-${item.barcode}"
            type="number"
            class="form-controll
            quantityData"
            value="${item.sellingPrice}"
            onchange="onEditPriceChanged('${item.barcode}')"
            style="width:4.5rem" min="1">
        </td>
        <td>
          <input
            id="edit-order-item-${item.barcode}"
            type="number"
            class="form-controll
            quantityData"
            value="${item.quantity}"
            onchange="onEditQuantityChanged('${item.barcode}')"
            style="width:70%" min="1">
        </td>
        <td>
          <button onclick="deleteItem('${item.barcode}')" class="btn btn-outline-danger ">Delete</button>
        </tb>
      </tr>
    `;

    $tbody.append(row);
  }
}
function resetEditModal(id) {
  $("#edit-item-form input[name=id]").val(id);
  resetEditItemForm();
  orderItems = [];
  getOrderData(id);
}

function displayEditModal(id) {
    resetEditModal(id);
    $('#edit-order-modal').modal({ backdrop: 'static', keyboard: false }, 'show');
}

function displayCreationModal() {
  resetModal();
  $('#create-order-modal').modal({ backdrop: 'static', keyboard: false }, 'show');
}

function hideCreationModal() {
  $('#create-order-modal').modal('toggle');
      throwSuccess("order placed");
  getOrderList();
}
function fetchOrderDetails(id) {
  $('#show-order-modal').modal({ backdrop: 'static', keyboard: false }, 'show');
  $('#order-id').text(id)
//document.getElementById("order-id").innerHTML = "I have changed!";

  var url = getOrderUrl() + id;
  $.ajax({
    url: url,
    type: 'GET',
    success: function (data) {
//    console.log(data);
      displayOrderDetails(data);
    },
    error: handleAjaxError,
  });
}


//function displayOrderModal() {
//  resetModal();
//  $('#create-order-modal').modal({ backdrop: 'static', keyboard: false }, 'show');
//}

function updateOrder(json, onSuccess, id) {
  //Set the values to update
  const url = getOrderUrl()+id;
    console.log('sending data..........',json)
  $.ajax({
    url: url,
    type: 'PUT',
    data: json,
    headers: {
      'Content-Type': 'application/json',
    },
    success: onSuccess,
    error: handleAjaxError,
  });

  return false;
}


function editOrder() {
	var orderId = $("#edit-item-form input[name=id]").val();
     if (orderItems.length == 0) {
     throwError('Order cannot be empty!');
     return ;
     }
  const data = orderItems.map((it) => {

    return {
      barcode: it.barcode,
      quantity: it.quantity,
      id: it.id,
      orderId: orderId,
      sellingPrice: it.sellingPrice,
      name: it.name,
    };
  });

  const json = JSON.stringify(data);
  updateOrder(json, hideEditModal, orderId);
}


function editOrderItem() {
  const item = getCureentEditOrderItem();
  if(isInvalidInput(item))return;
  checkInventoryByBarcode(item, displayEditOrderItems,resetEditItemForm);
}

function checkInventoryByBarcode(item, displayCallBack, resetCallBack){
const url = getInventoryUrl() + "/barcode" ;
  console.log(url);
  data ={barcode: item.barcode};
  const json = JSON.stringify(data);
  $.ajax({
    url: url,
    type: 'POST',
    data: json,
    headers: {'Content-Type': 'application/json'},
    success: function (data) {

        if(data.quantity>=item.quantity){
                  getProductById(data.id, (product) => {
                  if(product.price<item.sellingPrice){
                      throwError(`Selling Price cannot be more than the MRP = ${product.price}`);
                      return;
                  }
                    addItem({
                      barcode: product.barcode,
                      name: product.name,
                      sellingPrice: item.sellingPrice,
                      quantity: item.quantity,
                    });

                    displayCallBack(orderItems);
                    resetCallBack();
        })
        }
        else{
               throwError('only '+ data.quantity+ ' pieces available in inventory');
               return;
        }
    },
    error: handleAjaxError,
  });
}
//INITIALIZATION CODE
function init() {
  $('#add-order-item').click(addOrderItem);
  $('#create-order').click(displayCreationModal);
  $('#refresh-data').click(getOrderList);
  $('#upload-data').click(displayUploadData);
  $('#place-order-btn').click(placeNewOrder);
  $('#edit-order-btn').click(editOrder);
  $('#edit-order-item').click(editOrderItem);

//  $('#process-data').click(processData);
//  $('#download-errors').click(downloadErrors);
     var element = document.getElementById("order-icon");
     element.classList.add("thick");
}

$(document).ready(init);
$(document).ready(getOrderList);

// Place Order
function placeNewOrder() {
     if (orderItems.length == 0) {
     throwError('Order cannot be empty!');
       return ;
     }
  const data = orderItems.map((it) => {
    return {
      barcode: it.barcode,
      quantity: it.quantity,
      sellingPrice: it.sellingPrice,
      name:it.name
    };
  });

  const json = JSON.stringify(data);
  placeOrder(json, hideCreationModal);
}


function hideEditModal() {
  $('#edit-order-modal').modal('toggle');
  getOrderList();
}
//BUTTON ACTIONS
function placeOrder(json, onSuccess) {
  //Set the values to update
  const url = getOrderUrl();
   console.log('posting data',json);
  $.ajax({
    url: url,
    type: 'POST',
    data: json,
    headers: {
      'Content-Type': 'application/json',
    },
    success:onSuccess,
    error: handleAjaxError,
  });

  return false;
}
