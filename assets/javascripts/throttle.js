var random = Math.random(),
    currentPage = window.location.href,
    cookieValue = getCookieValue("throttle"),
    throttleRatio = document.getElementById("throttleRatio"),
    expiryMinutes = document.getElementById("expiryMinutes"),
    redirectURL = document.getElementById("redirectURL");

if (throttleRatio) {throttleRatio = throttleRatio.value} else {throttleRatio = 1}
if (expiryMinutes) {expiryMinutes = expiryMinutes.value} else {expiryMinutes = 5}
if (redirectURL) {redirectURL = redirectURL.value} else {redirectURL = "/register-to-vote/exit/too-busy"};

if (random >= throttleRatio){
    if (!cookieValue && currentPage.includes("register-to-vote/country-of-residence")){
        setCookie("throttle",true,expiryMinutes);
        window.location.href = redirectURL;
    }
}

if (cookieValue && currentPage.includes("register-to-vote/country-of-residence")){
    window.location.href = redirectURL;
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