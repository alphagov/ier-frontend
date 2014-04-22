package controllers.step.crown

import play.api.mvc.{ResponseHeader, SimpleResult, Action}
import play.api.libs.iteratee.Enumerator
import java.io.File
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Controller
import play.api.http.HeaderNames
import play.api.Play
import play.api.Play.current

object DeclarationPdfDownloadController extends Controller with HeaderNames {

  def download = Action {
    val pdfFileName = "/public/pdf/crown-servant-declaration-blank.pdf"
    val pdfFileUrl = Play.resource(pdfFileName)
    val pdfFile = pdfFileUrl match {
      case Some(pdfFileUrl) => new File(pdfFileUrl.toURI)
      case None => throw new IllegalArgumentException(s"Play.resource($pdfFileName) returned None")
    }
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
