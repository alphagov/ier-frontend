package uk.gov.gds.ier.langs

import play.api.i18n.Lang

object Messages {
  import play.api.Play.current

  private lazy val messages = play.api.i18n.Messages.messages

  def messagesForLang(lang:Lang) = {
    messages.filterKeys(_ == lang.language).headOption.map(_._2).getOrElse(Map.empty)
  }
}
