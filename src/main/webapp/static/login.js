

function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/session/login";
}

function login(){
	var $form = $("#login-form");
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
	   $.notify('Logged In successfully!', 'success');
	   	$('#add-brand-modal').modal('toggle');
	   },
	   error: handleAjaxError,
	});
}

function init(){
	$('#login').click(login);
}

$(document).ready(init);