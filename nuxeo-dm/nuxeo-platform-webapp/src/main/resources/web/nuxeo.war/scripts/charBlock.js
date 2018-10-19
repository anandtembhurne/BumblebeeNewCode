jQuery(document).ready(function() { 
    // wait for all other onready event to do their work before we tweak the binding
    // this is needed for TinyMce
	 var fireFox=navigator.userAgent.search("Firefox") >= 0;   
    jQuery("#document_edit\\:nxl_heading\\:nxw_title").keydown(function (evt) {
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
   key == 35 || key == 36 ||
   // left and right arrows
   key == 37 || key == 39 || key == 32 ||
   // Del and Ins
   key == 46 || key == 45 ||  key == 189 || (fireFox && e.which == 173)) {
       // input is VALID
   }else if(e.shiftKey && e.which == 189 ||(fireFox && e.shiftKey && e.which == 173)){
   return true;
   } else if( e.which === 90 && e.ctrlKey ){
    //alert('control + z'); 
   } else {
       // input is INVALID
       e.returnValue = false;
       if (e.preventDefault) e.preventDefault();
   }
     
   });
    
   
    jQuery("#document_edit\\:nxl_heading\\:nxw_title").blur(function(){//document_edit:nxl_heading:nxw_title
   	 var regx = /^[a-zA-Z0-9\s\-_]*$/;///^[a-zA-z0-9\x|]|[|-|'|.]*$/;
   	 jQuery("#document_edit\\:nxl_heading\\:nxw_title_message").html("");//document_edit:nxl_heading:nxw_title_message
        if(this.value!=null){
          var str =this.value;
          if(str.match(regx)){
           console.log("valid");
          }else{
       	   jQuery(this).val(null);
       	   jQuery(this).focus();//errorMessage document_edit:nxl_heading:nxw_title_message
       	   jQuery("#document_edit\\:nxl_heading\\:nxw_title_message").addClass("errorMessage");
       	   jQuery("#document_edit\\:nxl_heading\\:nxw_title_message").html("You can enter only alpha-numeric or _ or - character");
              console.log("invalid");
          }  
        }
    });
});