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
    if (isWelsh(request)) emailLang="cy" else  emailLang="en"
  }

  def isWelsh(request: Request[Any]): Boolean = {
    val domain = getDomain(request).toString
    if ((domain contains "cymraeg") || (domain contains "welsh")) true else false
  }

  def getDomain(request: Request[Any]) = {
    request.headers.get("host") filterNot {
      _ startsWith "localhost"
    } filterNot {
      _ == ""
    } map {
      _.split(":").head
    }
  }
}
