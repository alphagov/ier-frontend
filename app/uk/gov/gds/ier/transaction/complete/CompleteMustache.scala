package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.mustache.{StepMustache, GovukMustache}
import uk.gov.gds.common.model.{Ero, LocalAuthority}

trait CompleteMustache {

  object Complete extends StepMustache with GovukMustache {

    case class CompleteModel(
        authorityUrl: String,
        authorityName: String,
        refNumber: Option[String],
        hasOtherAddress: Boolean,
        backToStartUrl: String)

    def completePage(
        authority: Option[LocalAuthority],
        refNumber: Option[String],
        hasOtherAddress: Boolean,
        backToStartUrl: String) = {
      val data = CompleteModel(
        authorityUrl = "#", // TODO: authority URL is not provided from Places Service
        authorityName = authority map {
          auth => auth.name + " electoral registration office"
        } getOrElse "your local electoral registration office",
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
