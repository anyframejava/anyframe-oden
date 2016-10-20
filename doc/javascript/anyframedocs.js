/*
	anyframedocs.js
    JavaScript library for use by Anyframe Documentation.

	Base version was created by David Keith
	Radar Component: RefLib Navigation
	
    Copyright (c) 2009 SAMSUNG SDS Co., Ltd. All Rights Reserved.
	Copyright (c) 2009 Apple Inc. All Rights Reserved.

*/


// Set default cookie expire date.
var COOKIE_EXPIRE_DATE = new Date();
COOKIE_EXPIRE_DATE.setFullYear(COOKIE_EXPIRE_DATE.getFullYear() + 1);

var Links = {
    reasignRelative: function (linkList, force, target) {
		var linkHREF, i;
        if (Prototype.Browser.IE) {
            return Links.reasignRelativeIE(linkList, force, target);
        } else {
            for (i = 0; i < linkList.length; i++) {
                linkHREF = linkList[i].getAttribute('href');
				if (linkHREF.indexOf('://') === -1 && !linkList[i].hasClassName("browserLink")) {
                    linkList[i].setAttribute('href', (Book.root + linkHREF));
                    if (linkList[i].descendantOf('toc')) {
                        linkList[i].setAttribute('id', 'tocEntry_' + i);
                        TOC.processLink(linkList[i]);
                    }
                    if (typeof target !== 'undefined') {
						linkList[i].target = target;
					}
                }
            }
        }
        return linkList; 
    },
    reasignRelativeIE: function (linkList, force, target) {
        var pageDir, i;
		pageDir = window.location.href.slice(0, window.location.href.replace(window.location.hash, '').lastIndexOf('/'));
        for (i = 0; i < linkList.length; i++) {
            if (force || (!linkList[i].hasClassName('urlLink') && !linkList[i].hasClassName("browserLink"))) {
                linkList[i].setAttribute('href', linkList[i].getAttribute('href').replace(pageDir, Book.root));
                if (linkList[i].descendantOf('toc')) {
                    linkList[i].setAttribute('id', 'tocEntry_' + i);
                    TOC.processLink(linkList[i]);
                }
                if (target) { 
					linkList[i].target = target;
				}
            }
        }
        return linkList;
    }
};

var AnyframeDocs = {
    init: function () {
        Book.root = $('INDEX') ? $('INDEX').href.slice(0, $('INDEX').href.lastIndexOf('/')) + '/': "";
    },
    Window: function (id) {
		var box, header, contents, footer;
        box = document.createElement('div');
        header = document.createElement('div');
        contents = document.createElement('div');
        footer = document.createElement('div');
        box.setAttribute('id', id + 'Window');
        header.setAttribute('id', id + 'Header');
        contents.setAttribute('id', id + 'Body');
        footer.setAttribute('id', id + 'Footer');
        box.appendChild(header);
        box.appendChild(contents);
        box.appendChild(footer);
        box.style.display = 'none';
        document.body.appendChild(box);
        this.box = $(id + 'Window');
        this.header = $(id + 'Header');
        this.contents = $(id + 'Body');
        this.footer = $(id + 'Footer');
    }
};

var Cookie = { //Generic cookie management
    set: function (parameters) { 
        /*	Input: Object of cookie {cName: String, cValue: Object, cExpiry: Date, cDomain: String, cPath: String}
         *	value must have toString() method.
         *	Returns: Cookie String 
		 */
        if (typeof localStorage !== 'undefined') {
			localStorage.setItem(parameters.cName, parameters.cValue);
			return parameters.cValue;
		} else {
			// Set the domain, file:// has no domain so we don't do this if the page is loaded from there
			if (parameters.cDomain === "" && window.location.domain) {
				parameters.cDomain = window.location.domain; //Set to current domain by default
			}

			// build and set the cookie string
			return (document.cookie = parameters.cName +
                '=' + escape(parameters.cValue) +
                (parameters.cExpiry ? '; expires=' + parameters.cExpiry.toGMTString() : '') +
                (parameters.cDomain ? '; domain=' + parameters.cDomain : '') +
                (parameters.cPath ? '; path=' + parameters.cPath : '; path=' +  window.location.pathname));
		}
    },
    get: function (cookie_name) {
        // Input: Sting of Cookie name to retrive
        // Returns: String of Cookie value
        if ((typeof localStorage !== 'undefined') && localStorage.getItem(cookie_name) !== 'null') {
			// Check localStorage first
			return localStorage.getItem(cookie_name);
		} else { // Otherwise data might be in a cookie
			var cookie_array, i, aCookie;
			
			// the browser returns all inscope cookies as 'myCookie1=myValue;myCookie2=anotherValue'
			// Split the string by the divider
			cookie_array = document.cookie.split(';'); 
			
			for (i = 0; i < cookie_array.length; i++) { 
				aCookie = cookie_array[i];
				while (aCookie.charAt(0) === ' ') {
					aCookie = aCookie.substring(1, aCookie.length);
				}
				if (aCookie.indexOf(cookie_name + '=') === 0) {
					if (typeof localStorage !== 'undefined') { // if localStorage is supported, move data there.
						localStorage.setItem(cookie_name, aCookie.substring(cookie_name.length + 1, aCookie.length));
						Cookie.forceExpire(cookie_name);
						return localStorage.getItem(cookie_name);
					} else {
						return aCookie.substring(cookie_name.length + 1, aCookie.length);
					}
				}
			}
			return null;
		}
	},
    expire: function (cookie_name) {
        // Input: Sting of Cookie name to expire
        // Returns: Expired Cookie String
		if (typeof localStorage !== 'undefined') {
			return localStorage.removeItem(cookie_name);
		} else {
			return Cookie.forceExpire(cookie_name);
		}
    },
	forceExpire: function (cookie_name) {
        // Input: Sting of Cookie name to expire
        // Returns: Expired Cookie String
		
		// to expire a cookie we just reset it with a date in the past.
		var past = new Date();
		past.setUTCFullYear(2000);
		return this.set({cName: cookie_name, cValue: null, cExpiry: past, cPath: "/" });
    }
};

/* Client-side access to querystring name=value pairs
    Version 1.3
    28 May 2008
    
    License (Simplified BSD):
    http://adamv.com/dev/javascript/qslicense.txt
*/
function Querystring(qs) { // optionally pass a querystring to parse
	var args, i, pair, name, value;
    this.params = {};
    
    if (!qs) {
		qs = location.search.substring(1, location.search.length);
	}
    if (qs.length === 0) {
		return null;
	}

// Turn <plus> back to <space>
// See: http://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4.1
    qs = qs.replace(/\+/g, ' ');
    args = qs.split('&'); // parse out name/value pairs separated via &
    
// split out each name=value pair
    for (i = 0; i < args.length; i++) {
        pair = args[i].split('=');
        name = decodeURIComponent(pair[0]);
        
        value = (pair.length === 2) ? decodeURIComponent(pair[1]) : name;
        
        this.params[name] = value;
    }
}

Querystring.prototype.get = function (key, default_) {
    var value = this.params[key];
    return (value !== null) ? value : default_;
};

Querystring.prototype.contains = function (key) {
    var value = this.params[key];
    return (value !== null);
};

//End http://adamv.com/dev/javascript/qslicense.txt

document.observe("dom:loaded", function () {
    Prototype.Query = new Querystring();
    AnyframeDocs.init();
});