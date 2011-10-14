$(document).ready(function(){
	$("ul.children").parent().append("<span></span>"); //Only shows drop down trigger when js is enabled - Adds empty span tag after ul.children
	$("ul.topnav li span").click(function() { //When trigger is clicked...
		//Following events are applied to the children itself (moving children up and down)
		$(this).parent().find("ul.children").slideDown('fast').show(); //Drop down the children on click
		$(this).parent().hover(function() {
		}, function(){	
			$(this).parent().find("ul.children").slideUp('slow'); //When the mouse hovers out of the children, move it back up
		});
		//Following events are applied to the trigger (Hover events for the trigger)
		}).hover(function() { 
			$(this).addClass("subhover"); //On hover over, add class "subhover"
		}, function(){	//On Hover Out
			$(this).removeClass("subhover"); //On hover out, remove class "subhover"
	});
});