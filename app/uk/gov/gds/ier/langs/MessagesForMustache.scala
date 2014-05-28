package uk.gov.gds.ier.langs

import play.api.i18n.Lang

trait MessagesForMustache {
  val lang: Lang
  val messages = Messages.messagesForLang(lang)
}
