var random = Math.random();
var currentPage = window.location.href;
var cookieValue = getCookieValue("throttle");

if (cookieValue !== null && currentPage.includes('register-to-vote/country-of-residence')){
    window.location.href = '/register-to-vote/exit/too-busy';
}else{
    if (random >= 0.8){
        setCookie("throttle",true,5);
    }
    if (random < 0.8){

    }
}

function getCookieValue(name){
    var re = new RegExp(name + "=([^;]+)");
    var value = re.exec(document.cookie);
    return (value != null) ? unescape(value[1]) : null;
}

function setCookie(name,value,expiry_mins){
    var expiry_date=new Date();
    expiry_date=new Date(expiry_date.getTime() + expiry_mins * 60000);
    document.cookie=encodeURIComponent(name)
      + "=" + encodeURIComponent(value)
      + (!expiry_mins ? "" : "; expires="+expiry_date.toUTCString());
      ;
}

