var grr = {};

/* XMLHttpRequest Wrapper */
grr.XRequest = function(){
	this._xreq = this._get();
}

grr.XRequest.prototype._get = function(){
	if (window.ActiveXObject) {		/* IE */
		try {
			return new ActiveXObject('MSXML2.XMLHTTP');
		} catch (e) {
			try {
				return new ActiveXObject('Microsoft.XMLHTTP');
			} catch (e1) {
				alert(e1);
			}
		}
	} else if (window.XMLHttpRequest) {		/* FF */
		return new XMLHttpRequest;
	}
	return null;
}

//grr.XRequest.prototype.send = function(url, params, callback, isPost){
//	try{
//		this._xreq = this._xreq || this._get();
//		if(this._xreq == null) {
//			throw new Error('fail to get XMLHttpRequest object');
//		}
//		isPost = isPost || false;
//		var _params = '';
//		if(params && params != ''){
//			for(var key in params){
//				if(_params != '') _params+='&';
//				_params+=key+ '=' + encodeURIComponent(params[key]);
//			}
//		}
//		var _url = url;
//		if(!isPost && _params != ""){
//			_url+='?'+_params;
//		}
//		
//		this._xreq.open(isPost ? 'POST' : 'GET', _url, true);
//		
//		if(isPost){
//			this._xreq.setRequestHeader('Content-Type', 
//				'application/x-www-form-urlencoded');
//		}
//		
//		var _this = this;
//		this._xreq.onreadystatechange = function(){
//			callback(_this._xreq);
//		}
//
//		this._xreq.send( isPost ? _params : null );
//	}catch(e){
//		alert(e.name+': '+e.message);
//	}
//}

grr.XRequest.prototype.send = function(url, content, callback, isAsync){
	try{
		this._xreq = this._xreq || this._get();
		if(this._xreq == null) {
			throw new Error('Fail to get XMLHttpRequest object');
		}

		var _this = this;
		this._xreq.onreadystatechange = function(){
			callback(_this._xreq);
		}
		this._xreq.open('POST', url, isAsync || true);
		this._xreq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		
		this._xreq.send(content);
	}catch(e){
		alert(e.name+': '+e.message);
	}
}
