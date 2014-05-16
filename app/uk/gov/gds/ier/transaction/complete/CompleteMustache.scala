package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.mustache.{StepMustache, GovukMustache}
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails

trait CompleteMustache {
  self: WithRemoteAssets =>

  val _remoteAssets = remoteAssets

  object Complete extends StepMustache with GovukMustache with WithRemoteAssets {

    val remoteAssets = _remoteAssets

    case class CompletePage (
        authority: Option[EroAuthorityDetails],
        refNumber: Option[String],
        hasOtherAddress: Boolean,
        backToStartUrl: String
    ) extends GovukPage ("complete", "Application Complete") {
      val authorityUrl = authority flatMap {
        auth => auth.urls.headOption
      }

      val authorityName = authority map {
        auth => auth.name + " electoral registration office"
      } getOrElse "your local electoral registration office"
    }
  }
}
