

const reportsBaseUrl = $('meta[name=baseUrl]').attr('content') + '/ui/report';

const REPORT_URLS = {
  brandReportUrl: reportsBaseUrl + '/brand',
  salesReportUrl: reportsBaseUrl + '/sales',
  perDaySaleReportUrl: reportsBaseUrl + '/daily',
  inventoryReportUrl: reportsBaseUrl + '/inventory-report',
};

function redirctTo(url) {
  location.href = url;
}

function init() {
  $('#brand-report-card').click(() => {
    redirctTo(REPORT_URLS.brandReportUrl);
  });

  $('#sales-report-card').click(() => {
    redirctTo(REPORT_URLS.salesReportUrl);
  });

  $('#per-day-sale-report-card').click(() => {
    redirctTo(REPORT_URLS.perDaySaleReportUrl);
  });

  $('#inventory-report-card').click(() => {
    redirctTo(REPORT_URLS.inventoryReportUrl);
  });

  var element = document.getElementById("report-icon");
  element.classList.add("thick");
}

$(document).ready(init);
