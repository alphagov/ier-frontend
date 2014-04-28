package uk.gov.gds.ier.langs

import play.api.i18n.Lang
import uk.gov.gds.ier.validation.ErrorTransformForm

object Messages {
  import play.api.Play.current

  private lazy val messages = play.api.i18n.Messages.messages

  def messagesForLang(lang:Lang) = {
    messages.filterKeys(_ == lang.language).headOption.map(_._2).getOrElse(Map.empty)
  }

  def translatedGlobalErrors[T](
      form: ErrorTransformForm[T]
  )(implicit lang:Lang): Seq[String] = {
    form.globalErrors.map { error =>
      play.api.i18n.Messages(error.message)
    }
  }
}
