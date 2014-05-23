package controllers

import uk.gov.gds.ier.langs.{MessagesController => GuicedController}
import uk.gov.gds.ier.guice.DelegatingController

object MessagesController extends DelegatingController[GuicedController] {
  def all = delegate.all
  def forLang(langCode: String) = delegate.forLang(langCode)
}
