function showAccessKeys() {
    if (jQuery(".accessKeyMenu").size()>0) {
        jQuery(".accessKeyMenu").remove();
        return;
    }
    var container = jQuery("<div></div>");
    container.css("display","none");
    var div = jQuery("<div></div>");
    div.attr("id","accessKeyMenuPopup");
    div.addClass("accessKeyMenu");
    div.css({"padding":"8px", "margin":"4px", "font-size" : "12px"});
    container.append(div);

    var table = jQuery("<table></table>");
    div.append(table);

    jQuery("[accesskey]").each(function() {
        var item = jQuery(this);
        var key = item.attr("accesskey");


        if (key !=null && key !="") {
              var row = jQuery("<tr></tr>");
              var keySpan = jQuery("<span>" + key + "</span>");
              keySpan.css({"background-color":"#CCCCCC", "color":"black","padding":"6px", "margin":"2px","border-radius" : "2px", "font-size" : "12px", "font-weight" : "bold", "font-family": "monospace"});
              var keyText = this.innerHTML;
              if (this.tagName=="INPUT" && (item.attr("type")=="button" || item.attr("type")=="submit")) {
                 keyText = item.attr("value");
              }
              if (keyText && keyText!="" && keyText.indexOf("<!--")!=0 ) {
                var td = jQuery("<td></td>");
                td.css({"padding":"6px"});
                td.append(keySpan);
                row.append(td);

                var descSpan = jQuery("<span></span>");
                descSpan.css({"white-space":"nowrap"});
                descSpan.append(keyText);

                td = jQuery("<td></td>");
                td.append(descSpan);
                row.append(td);

                table.append(row);
              }
         }
    });
    jQuery("body").append(container);
    showFancyBox("#accessKeyMenuPopup");
}

function bindShortCuts() {
    // bind access keys to Ctrl+Shift+
    jQuery("[accesskey]").each(function() {
        var item = jQuery(this);
        var key = item.attr("accesskey");
        if (key !=null && key !="") {
              var newKeyCode = "Ctrl+Shift+" + key;
              var clickHandler = function(event) {event.preventDefault();item[0].click();return false;};
              // Document wide binding
              jQuery(document).bind('keydown', newKeyCode, clickHandler);
              // add bindings on all inputs
              jQuery("INPUT,TEXTAREA,SELECT").bind('keydown', newKeyCode, clickHandler);
              var mceFrames = jQuery(".mceIframeContainer > IFRAME").contents().find("body");
              mceFrames.bind('keydown', newKeyCode, clickHandler);
         }
    });
    // bind help screen
    jQuery(document).bind('keydown', 'Shift+h', showAccessKeys);
}

// run binding on document ready
jQuery(document).ready(function() {
     // wait for all other onready event to do their work before we tweak the binding
     // this is needed for TinyMce
     window.setTimeout(bindShortCuts, 1000);
     var fireFox=navigator.userAgent.search("Firefox") >= 0;
     jQuery("#document_create\\:nxl_heading\\:nxw_title").keydown(function (evt) {
         var e = evt || window.event;
    var key = e.keyCode || e.which;

    if (!e.shiftKey && !e.altKey && !e.ctrlKey &&
    // numbers   
    key >= 48 && key <= 57 ||
    // Numeric keypad
    key >= 96 && key <= 105 || (key > 64 && key < 91)||
    // Backspace and Tab and Enter
    key == 8 || key == 9 || key == 13 ||
    // Home and End
    key == 35 || key == 36 ||key == 32 ||
    // left and right arrows
    key == 37 || key == 39 ||
    // Del and Ins
    key == 46 || key == 45 ||  key == 189 || (fireFox && e.which == 173)) {
        // input is VALID
    }else if(e.shiftKey && e.which == 189 ||(fireFox && e.shiftKey && e.which == 173)){
 	  return true;
    }else if( e.which === 86 && e.ctrlKey ){
    	e.returnValue = false;
        if (e.preventDefault) e.preventDefault();
    }else if( e.which === 90 && e.ctrlKey ){
     //alert('control + z'); 
    } else {
        // input is INVALID
        e.returnValue = false;
        if (e.preventDefault) e.preventDefault();
    }
      
    });
     
     jQuery("#document_create\\:nxl_heading\\:nxw_title").blur(function(){
       	 var regx = /^[a-zA-Z0-9\s\-_]*$/;///^[a-zA-z0-9\x|]|[|-|'|.]*$/;
       	 jQuery("#document_create\\:nxl_heading\\:nxw_title_message").html("");
            if(this.value!=null){
              var str =this.value;
              if(str.match(regx)){
               console.log("valid");
              }else{
           	   jQuery(this).val(null);
           	   jQuery(this).focus();//errorMessage
           	   jQuery("#document_create\\:nxl_heading\\:nxw_title_message").addClass("errorMessage");
           	   jQuery("#document_create\\:nxl_heading\\:nxw_title_message").html("You can enter only alpha-numeric or _ or - character");
                    console.log("invalid");
              }  
            }
        });
});

