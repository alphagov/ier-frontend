package uk.gov.gds.ier.langs

import play.api.mvc._

class MessagesController extends Controller {

  def all = Action {
    Ok(Messages.jsMessages.all(Some("var GOVUK = { 'registerToVote' : {} }; GOVUK.registerToVote.messages")))
  }

  def forLang(langCode:String) = Action {
    implicit val lang = Language.Lang(langCode)
    Ok(Messages.jsMessages(Some("var GOVUK = { 'registerToVote' : {} }; GOVUK.registerToVote.messages")))
  }
}
