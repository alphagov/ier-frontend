package controllers.step.crown

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.crown.declaration.DeclarationPdfStep
import play.api.mvc.{ResponseHeader, SimpleResult, Action}
import play.api.libs.iteratee.Enumerator
import java.io.File
import play.api.libs.concurrent.Execution.Implicits._
import controllers.routes.Assets

object DeclarationPdfController extends DelegatingController[DeclarationPdfStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def declarationPdfStep = delegate

  def download = Action {
    val pdfOutsideFileUrl = Assets.at("pdf/crown-servant-declaration-blank.pdf").url
    val pdfFileUrl = getClass.getResource("/public/pdf/crown-servant-declaration-blank.pdf")
    val pdfFile = new File(pdfFileUrl.toURI)
    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(pdfFile)

    SimpleResult(
      header = ResponseHeader(200,
        Map(
          CONTENT_LENGTH -> pdfFile.length.toString,
          CONTENT_DISPOSITION -> "attachment; filename=\"crown-servant-declaration.pdf\""
        )),
      body = fileContent
    )
  }
}
