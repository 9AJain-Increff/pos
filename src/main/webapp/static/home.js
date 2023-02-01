

const baseUrl = $('meta[name=baseUrl]').attr('content');

const UI_URLS = {
  brandUrl: baseUrl + '/ui/brand',
  productUrl: baseUrl + '/ui/product',
  inventoryUrl: baseUrl + '/ui/inventory',
  ordersUrl: baseUrl + '/ui/order',
  reportUrl: baseUrl + '/ui/report',
  adminUrl: baseUrl + '/ui/admin',
};

function redirectTo(url) {
  location.href = url;
}

function init() {
  $('#brands-card').click(() => redirectTo(UI_URLS.brandUrl));
  $('#products-card').click(() => redirectTo(UI_URLS.productUrl));
  $('#inventory-card').click(() => redirectTo(UI_URLS.inventoryUrl));
  $('#orders-card').click(() => redirectTo(UI_URLS.ordersUrl));
  $('#reports-card').click(() => redirectTo(UI_URLS.reportUrl));
  $('#admin-card').click(() => redirectTo(UI_URLS.adminUrl));
  $('#ui-nav').empty();
  var element = document.getElementById("home-icon");
  element.classList.add("thick");
}

$(document).ready(init);
