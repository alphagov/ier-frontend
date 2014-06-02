package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.mustache.{StepMustache, GovukMustache}
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails
import uk.gov.gds.ier.langs.Messages
import play.api.i18n.Lang

trait CompleteMustache {
  self: WithRemoteAssets =>

  val _remoteAssets = remoteAssets

  object Complete
    extends StepMustache
    with GovukMustache
    with WithRemoteAssets {

    val remoteAssets = _remoteAssets

    case class CompletePage (
        authority: Option[EroAuthorityDetails],
        refNumber: Option[String],
        hasOtherAddress: Boolean,
        backToStartUrl: String
    ) (implicit val lang : Lang) extends Mustachio("complete") with MessagesForMustache {

      val authorityUrl = authority flatMap {
        auth => auth.urls.headOption
      }

      val authorityName = authority map {
        auth => auth.name + " " + Messages("complete_electoralRegistrationOffice")
      } getOrElse Messages("complete_unspecificElectoralRegistrationOffice")

      override def render() = GovukTemplate(
        htmlLang = lang.code,
        mainContent = super.render(),
        pageTitle = Messages("complete_step_title"),
        contentClasses = "complete"
      )
    }
  }
}
