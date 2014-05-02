package uk.gov.gds.ier.langs

import play.api.mvc._

class MessagesController extends Controller {

  def all = Action {
    Ok(Messages.jsMessages.all(Some("GOVUK.registerToVote.messages")))
  }
}
