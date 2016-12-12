var pieType = '';
$(document).ready(function() {
    
    $('.box').hover(function() {});
    
    if((!navigator.userAgent.match(/iPhone/i)) || (!navigator.userAgent.match(/iPod/i)) || (!navigator.userAgent.match(/iPad/i))) {
        
        $('img:not(.ignore)').each(function() {
            $(this).css({'opacity' : 0}).animate({'top' : '+=30'});
            
        });
    
        $('img:not(.ignore)').appear(function() {
            if($(this).hasClass('chart-stat-2')) {
                appearDelay = 700;
            } else if($(this).hasClass('chart-stat-3')) {
                appearDelay = 900;
            } else {
                appearDelay = 500;
            }
            $(this).delay(appearDelay).animate({'opacity' : 1, 'top' : '-=30'}, 500);
        });
    
    }
    
    //$('.pie-stat').css('opacity',0);
    setBoxValues(2010);
    
    skyColour($(document).scrollTop());
    
    $(document).scroll(function() {
        skyColour($(document).scrollTop());
    });
    
    //Scroll down to data on click
    $('.cta').click(function() {
        var distance = $('.sky .primary').offset();
        $("html:not(:animated),body:not(:animated)").animate({ scrollTop : distance.top - 25}, 800, "swing" );
        return false;
    });
    
    $('.sky .key a').click(function() {return false;});
    
    
    $('.sky .key a').hover(function() {
        $('.interact-3').fadeOut(200);
        if(!$('.main-tree').hasClass('switched')) {
            var type = $(this).attr('class').substring(2);
            $('.active .'+type+'-stat').toggleClass('active');
        }
    });
    
    
    //Check if the nav arrows should be active on page load
    activeCheck($('.selected'));
    var bioHeight = $('.k-bio').parent().height();
    $('.time ul a').click(function() {
        $(this).closest('.time').find('li').each(function() {$(this).removeClass('selected');});
        $(this).parent().addClass('selected');
        
        if($(this).closest('.grass').length) {
            $('.interact-4').fadeOut(200);
            $('.boxes li').removeClass().addClass('y'+$(this).text());
            setBoxValues($(this).text());
        }
        if($(this).closest('.sky').length) {
            $('.interact-2').fadeOut(200);
            $('.blobs').removeClass('active');
            $('.b'+$(this).text()).addClass('active');
            if(!$('.b'+$(this).text()+' .bio-stat').length) {
                $('.k-bio').parent().animate({
                }, {
                    queue: false, duration: 400,
                    step: function() {
                        $(this).css("overflow","visible");
                    },                     
                    complete: function() {
                        $(this).css("overflow","visible");
                    }
                });
            } else {
                $('.k-bio').parent().animate({
                    'height' : bioHeight,
                    'opacity' : 1,
                    'margin-bottom' : 3
                }, {
                    queue: false, duration: 400,
                    step: function() {
                        $(this).css("overflow","visible");
                    },                     
                    complete: function() {
                        $(this).css("overflow","visible");
                    }
                });
            }
        }
        
        activeCheck($(this).parent());
        
        return false;
    });
    
    $('.time .prev, .time .next').click(function() {
        if(!$(this).hasClass('inactive')) {
            
            //Find current selected item
            var current = $(this).parent().find('.selected');
            
            //Determine next item and remove/set class
            if($(this).attr('class') == 'next') {nextYear = current.next('li');} else {nextYear = current.prev('li');}
            $(this).closest('.time').find('li').each(function() {$(this).removeClass('selected');});
            nextYear.addClass('selected');
            
            //Check if the nav arrows should be active
            activeCheck(nextYear);
            
            if($(this).closest('.grass').length) {
                $('.boxes li').removeClass().addClass('y'+nextYear.children().text());
                setBoxValues(nextYear.children().text());
            }
            if($(this).closest('.sky').length) {
                $('.blobs').removeClass('active');
                $('.b'+nextYear.children().text()).addClass('active');
                if(!$('.b'+nextYear.text()+' .bio-stat').length) {
                    $('.k-bio').parent().animate({
                        'height' : 0,
                        'opacity' : 0,
                        'margin-bottom' : 0
                    }, {queue: false, duration: 400});
                } else {
                    $('.k-bio').parent().animate({
                        'height' : bioHeight,
                        'opacity' : 1,
                        'margin-bottom' : 3
                    }, {queue: false, duration: 400});
                }
            }
        }
        return false;
    });
    
    $('.box').hover(function() {
       $('.interact-5').fadeOut(200); 
    });
    
    //Pies
    $('.pies .key a').click(function() {
        
        $('.pies .key a').not($(this)).addClass('over');
        
        $('.interact-6').fadeOut(200);
        pieType = $(this).parent('li').attr('class') + '-t';
        setPieValues();
        $('.pie').each(function() {
            $(this).addClass('over').addClass(pieType);
        });

        $('.'+pieType).find('.white-disc').queue(function(next){
            $(this).addClass('open-white-disc');
            next();
        });
        //$('.'+pieType).find('.pie-stat').delay(70).queue(function(next){
        //    $(this).addClass('open-stat').css('opacity',1);
        //    next();
        //});
        $('.'+pieType).find('.colour-disc').delay(100).queue(function(next){
            $(this).addClass('open-colour-disc');
            next();
        });
        
    }, function() {
        
        $('.pies .key a').removeClass('over');
        
        //$('.pie-stat').css('opacity', 0);
        //$('.pie-stat').removeClass('open-stat');        
        $('.open-colour-disc').removeClass('open-colour-disc');
                   
        $('.open-white-disc').removeClass('open-white-disc');
        $('.'+pieType).removeClass('over').removeClass(pieType);
    });
    
    $('#pinterest').click(function(e) {
		var i = $(this).attr('id'),
			u = this.href;
			var c = {h:300, w:600};
		          
		if (c !== false) {
			e.preventDefault();
			var xPos = ($(window).width()-c.w)/2,
				n = window.open(u,i,'height='+c.h+',width='+c.w+',left='+xPos+',top=60,screenX='+xPos+',screenY=60');
		
			var n = window.open(u,i,'height=,width='+c.w);
			if (window.focus) { n.focus(); }
		}
	});
    
    //Switches
    $('.tree-switch .switch').hover(function() {
        $(this).parent().toggleClass('switch-hover');
    });
    
    $('.tree-switch .switch, .tree-switch .text, .tree-switch .hover').click(function() {        
       $('.blobs .stat').toggleClass('active');
       $('.main-tree, .sky .key ul, .tree-switch .switch').toggleClass('switched');
       $('.tree-switch .label').toggleClass('active-label');       
       //$('.sky .key ul').toggleClass('switched');       
    });
    $('.grass-switch').click(function() {        
       $('.boxes').toggleClass('switched');
       $('.lever').toggleClass('switched-lever');
       $('.grass-switch .label').animate({
        opacity : 0 
       }, 200, function() {
        
        if($(this).html() == 'OFF') {
            $(this).html('ON');
        } else {
            $(this).html('OFF');
        }
        
        $(this).delay(500).animate({
            opacity: 1    
        }, 200) 
       })
       return false;
    });
    
    //$('.tree-blobs div').hover(function() {
    //    $('.interact-7').fadeOut();    
    //});
    
    $('.grass-switch').hover(function() {
        $('.interact-8').fadeOut();    
    });
    
});

//Set Box Values
function setBoxValues(y) {
    if(y == '1970') {
        petStat = 47;
        gasStat = 10;
        elecStat = 11;        
        solidStat = 32;        
        otherStat = 2.6;        
    }
    else if(y == '1980') {
        petStat = 44;
        gasStat = 29;
        elecStat = 14;        
        solidStat = 13;        
        otherStat = 2.6; 
    }
    else if(y == '1990') {
        petStat = 43;
        gasStat = 31.3;
        elecStat = 16;        
        solidStat = 9.4;        
        otherStat = 2; 
    }
    else if(y == '2000') {
        petStat = 41.6;
        gasStat = 35.8;
        elecStat = 17.8;        
        solidStat = 2.8;        
        otherStat = 2; 
    }
    else if(y == '2010') {
        petStat = 42.5;
        gasStat = 34;
        elecStat = 19;        
        solidStat = 1.7;        
        otherStat = 2.6; 
    }
    
    $('.box.pet .box-num').html(petStat);
    $('.box.gas .box-num').html(gasStat);    
    $('.box.elec .box-num').html(elecStat);    
    $('.box.solid .box-num').html(solidStat);
    $('.box.other .box-num').html(otherStat);    
}

//Set pie values
function setPieValues() {
    if(pieType == 'p-industry-t') {
        statType = 'industry';
        stat1970 = '43';
        stat1990 = '26';            
        stat2010 = '18.4';            
    }
    else if(pieType == 'p-domestic-t') {
        statType = 'domestic';
        stat1970 = '25';
        stat1990 = '28';            
        stat2010 = '32.3';
    }        
    else if(pieType == 'p-transport-t') {
        statType = 'transport';
        stat1970 = '19';
        stat1990 = '33';            
        stat2010 = '37.1';
    }
    else if(pieType == 'p-other-t') {
        statType = 'other';
        stat1970 = '13';
        stat1990 = '13';            
        stat2010 = '12.2';
    }  
    
    $('.pie-1 .stat-num').html(stat1970);
    $('.pie-2 .stat-num').html(stat1990);        
    $('.pie-3 .stat-num').html(stat2010);
    $('.pie .block').html(statType);  
}

//Sky colour
function skyColour(s) {
    if(s < 400) {            
        p = 100 - (100 * s/400);
        p = p.toFixed(0);
        if(p > 99) {$('.morning-sky').css({'opacity':1});}
        else if(p < 10) {$('.morning-sky').css({'opacity':0});}
        else {$('.morning-sky').css('opacity','.'+p);}
    } else {
        $('.morning-sky').css({'opacity':0});
    }
}

//Function to determine if nav arrows should be active or not
function activeCheck(e) {
    e.closest('.time').find('.inactive').removeClass('inactive');
    if(e.is(':last-child')) {e.closest('.time').find('.next').addClass('inactive');}
    else if(e.is(':first-child')) {e.closest('.time').find('.prev').addClass('inactive');}
}