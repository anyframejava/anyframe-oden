/*
    heade_toc.js
    JavaScript library for use by Anyframe Documentation.
	
    Copyright (c) 2009 SAMSUNG SDS Co., Ltd. All Rights Reserved.
	Copyright (c) 2009 Apple Inc. All Rights Reserved.

*/


var TOC = {
    initExpandable: function (sections) {
        var i,
            anyframe_ref,
            chapter;
        
        for (i = 0; i < sections.length; i++) {
            sections[i].observe("click", TOC.toggleSection);
            anyframe_ref = sections[i].down("a").getAttribute("href");
            chapter = anyframe_ref.slice(anyframe_ref.lastIndexOf("/") + 1, anyframe_ref.length);
            if (Cookie.get(chapter) === "open") {
                TOC.toggleSection({element: function () { 
                    return sections[i];
                }});
            }
        }
        return i;
    },
    createIcon: function (attributes) {
        var icon = document.createElement('img');
        icon.setAttribute("src", Book.resourcesURI + attributes.src);
        icon.setAttribute("height", "16");
        icon.setAttribute("width", "16");
        icon.setAttribute("alt", attributes.alt);
        icon.setAttribute("id", attributes.id);
        return icon;
    },
    changeCurrentLocation: function (event) {
        if (Book.currentLocation) {
            Book.currentLocation.removeClassName("currentLocation");
        }
        Book.currentLocation = Event.element(event);
        return Book.currentLocation.addClassName("currentLocation");
    },
    processLink: function (link) {
        return link;
    },
    processLinks: function () {
        if ($("PDF_link")) { // If TOC has PDF link, move to header
            $("title").insert({top: $("PDF_link").remove()});
            $('PDF_link').down().insert({top: TOC.createIcon({
                src: "images/common/page_white_acrobat.png",
                id: "pdf_icon",
                alt: "PDF Icon"
            })});
            Links.reasignRelative([$('PDF_link').down()], true);
        }
        return Links.reasignRelative($$("#toc a"));
    },
    init: function (transport) {
        $("toc").innerHTML = transport.responseText;
        $("title").insert({top: $("book_title").remove()}); //Insert Book Title
        if ($("book_subtitle")) { // Insert subtitle
            $("book_title").insert({bottom: " &mdash; " + $("book_subtitle").remove().innerHTML});
        }
        TOC.processLinks();
        return TOC.initExpandable($$("#toc .children"));
    },
    load: function () {
        if (Book.tocURI !== "") {
            if (Cookie.get("toc-visible") === "true") {
                TOC.toggle();
            }
            $('toc_button').observe("click", TOC.toggle);
            $('toc_button').show();
            return (new Ajax.Request(Book.tocURI, { 
                method:     'get',
                onSuccess:  TOC.init,
                onFailure: function (transport) {
                    console.log(transport);
                }
            }));
        } else {
            var title = document.createElement('h1');
            if (Book.bookTitle !== undefined && Book.bookTitle !== "") {
                title.innerHTML = Book.bookTitle;
            } else {
                title.innerHTML = document.title.split(":")[0];
            }
            return $("title").insert({top: title});
        }
    },
    toggle: function () {
        if ($('tocContainer').toggle('appear').visible()) {
            $('contents').style.left = "230px";
            $('toc_button').down("button").addClassName("open");
            Cookie.set({cName: 'toc-visible', cValue: 'true', cExpiry: COOKIE_EXPIRE_DATE, cPath: "/"});
        } else {
            $('contents').style.left = "0px";
            $('toc_button').down("button").removeClassName("open");
            Cookie.expire('toc-visible');
        }
        return $('tocContainer');
    },
    toggleSection: function (event) {
        if (event.element().hasClassName('children')) {
            var toc_expandable = $(event.element()),
                anyframe_ref = toc_expandable.down('a').getAttribute("href"),
                chapter = anyframe_ref.slice(anyframe_ref.lastIndexOf("/") + 1, anyframe_ref.length);
            if (toc_expandable.down(".collapsible").toggle('slide').visible()) {
                toc_expandable.addClassName('open');
                return Cookie.set({cName: chapter, cValue: 'open', cExpiry: COOKIE_EXPIRE_DATE, cPath: "/"});
            } else {
                toc_expandable.removeClassName('open');
                return Cookie.expire(chapter);
            }
        } else {
            return false;
        }
    }
};

document.observe("dom:loaded", function () {
    TOC.load();
    $('contents').focus();
    if (!Prototype.Browser.WebKit) {
        document.body.style.overflow = "hidden"; 
        //Causes issues with keyboard navigation in WebKit, lack of css causes double scroll bars in other browsers
    }
});