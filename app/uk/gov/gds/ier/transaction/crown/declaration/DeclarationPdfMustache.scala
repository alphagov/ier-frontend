package uk.gov.gds.ier.transaction.crown.declaration

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import controllers.step.crown._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.service.WithDeclarationPdfDownloadService

trait DeclarationPdfMustache extends StepTemplate[InprogressCrown] {
  self: WithSerialiser with WithDeclarationPdfDownloadService =>

  case class DeclarationPdfModel(
    question: Question,
    declarationPdfUrl: String,
    pdfFileSize: String
  ) extends MustacheData

  val pageTitle = "Download your service declaration form"

  val mustache = MustacheTemplate("crown/declarationPdf") { (form, postUrl) =>
    implicit val progressForm = form
    DeclarationPdfModel(
      question = Question(
        postUrl = postUrl.url,
        number = "7",
        title = pageTitle,
        errorMessages = form.globalErrors.map ( _.message )
      ),
      declarationPdfUrl = routes.DeclarationPdfDownloadController.download.url,
      pdfFileSize = declarationPdfDownloadService.fileSizeWithUnit
    )
  }
}
