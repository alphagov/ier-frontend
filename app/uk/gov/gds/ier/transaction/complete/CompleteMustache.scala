package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.mustache.{StepMustache, GovukMustache}
import uk.gov.gds.common.model.Ero
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails

trait CompleteMustache {
  self: WithRemoteAssets =>

  val _remoteAssets = remoteAssets

  object Complete extends StepMustache with GovukMustache with WithRemoteAssets {

    val remoteAssets = _remoteAssets

    case class CompleteModel(
        authorityName: String,
        authorityUrl: Option[String],
        refNumber: Option[String],
        hasOtherAddress: Boolean,
        backToStartUrl: String)

    def completePage(
        authority: Option[EroAuthorityDetails],
        refNumber: Option[String],
        hasOtherAddress: Boolean,
        backToStartUrl: String) = {
      val data = CompleteModel(
        authorityName = authority map {
          auth => auth.name + " electoral registration office"
        } getOrElse "your local electoral registration office",
        authorityUrl = authority flatMap {
          auth => auth.urls.headOption
        },
        refNumber = refNumber,
        hasOtherAddress = hasOtherAddress,
        backToStartUrl = backToStartUrl
      )
      MainStepTemplate(
        content = Mustache.render("complete", data),
        title = "Application Complete",
        insideHeader = Govuk.search(),
        scripts = Govuk.scripts(),
        header = Govuk.stylesheets(),
        contentClasses = Some("complete")
      )
    }
  }
}
