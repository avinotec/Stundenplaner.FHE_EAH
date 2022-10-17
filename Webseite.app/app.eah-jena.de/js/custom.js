$(document).ready(function() {
	
if (!/ipod|iphone|ipad|android|blackberry|iemobile/i.test(navigator.userAgent)) {
};
			
//$('.fancybox').fancybox();			
$("a, #submit").each(function(){this.onmouseup = this.blur();});
 $.tools.validator.localize("ger", {
  ':email'  		: 'Bitte e-Mail überprüfen',
  '[required]' 	: 'Pflichtfeld!'
  });
$("#form").validator({lang: 'ger', position:'bottom center '}).submit(function(e) {
	
	if (!e.isDefaultPrevented()) {
	
	$.ajax({
type: "POST",
url: "../contactme_.php",
  data: $("#form").serialize(), 
  success: function( msg ) {
 $('#form').html(msg);
  }
});

	
	e.preventDefault();
	
	};
	});

});