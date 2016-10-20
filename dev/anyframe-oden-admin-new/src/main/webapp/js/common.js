var app = angular.module('oden', ['ngTable']);

app.factory('gridCell', function() {
	return {
		imageLink : function(text){
			return "BBBB";
		},
		textLink : function(text){
			return "BBbb";
		},
		imageTextLink : function(image, text, link) {
			console.log("ffffffffffffffffffffffffffffffffffffffffffffffff");
			
			return "<span>CCCCC</span>";
		},
		image : function(text){
			return "DDDDD";
		}
	};
});


