package uk.gov.gds.ier.langs

import play.api.mvc.Request

object Language {
  val Lang = play.api.i18n.Lang
  type Lang = play.api.i18n.Lang
  def english = Lang("en-GB")
  def welsh = Lang("cy")
  var emailLang = ""

  def getLang(request:Request[Any]):Lang = {
    request.acceptLanguages.headOption.getOrElse(english)
  }

  def setEmailLang(request:Request[Any]){
    if ((request.cookies.get("sessionKey").get.domain.getOrElse("") contains "cymraeg") || (request.cookies.get("sessionKey").get.domain.getOrElse("") contains "welsh")) {
      emailLang="cy"
    }
    else {
      emailLang="en"
    }
  }
}
