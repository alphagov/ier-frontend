package uk.gov.gds.ier.transaction.crown.declaration

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import controllers.step.crown.routes.DeclarationPdfController
import uk.gov.gds.common.model.LocalAuthority
import uk.gov.gds.ier.serialiser.WithSerialiser

trait DeclarationPdfMustache extends StepTemplate[InprogressCrown] {
  self: WithPlacesService with WithSerialiser =>

  case class DeclarationPdfModel(
    question: Question,
    declarationPdfUrl: String,
    showAuthorityUrl: Boolean,
    authorityUrl: String,
    authorityName: String
  )

  val pageTitle = "Download your service declaration form"

  val mustache = MustacheTemplate("crown/declarationPdf") { (form, postUrl, backUrl, application) =>

    val postcode = application.address flatMap {_.address} map {_.postcode}
    val authorityDetails : Option[LocalAuthority] = postcode match {
      case Some("") => None
      case Some(postCode) => placesService.lookupAuthority(postCode)
      case None => None
    }

    implicit val progressForm = form
    val data = DeclarationPdfModel(
      question = Question(
        postUrl = postUrl.url,
        backUrl = backUrl.map(_.url).getOrElse(""),
        showBackUrl = backUrl.isDefined,
        number = "7",
        title = pageTitle,
        errorMessages = form.globalErrors.map ( _.message )
      ),
      declarationPdfUrl = DeclarationPdfController.download.url,
      showAuthorityUrl = false,
      authorityUrl = "", //TODO: service providing authority URL is supposedly soon to be ready
      authorityName = authorityDetails map {
        auth => auth.name + " electoral registration office"
      } getOrElse "your local electoral registration office"
    )
    MustacheData(data, pageTitle)
  }
}
