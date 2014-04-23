package controllers.step.crown

import play.api.mvc.{ResponseHeader, SimpleResult, Action}
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Controller
import play.api.http.HeaderNames
import play.api.Play
import play.api.Play.current
import scala.concurrent.Await
import scala.concurrent.duration._

object DeclarationPdfDownloadController extends Controller with HeaderNames {

  val pdfFileName = "/public/pdf/crown-servant-declaration-blank.pdf"

  private lazy val pdfFileStream = Play.resourceAsStream(pdfFileName) match {
    case Some(pdfFileStream) => pdfFileStream
    case None => throw new IllegalArgumentException(s"Play.resource($pdfFileName) returned None")
  }

  private lazy val fileContent: Enumerator[Array[Byte]] = Enumerator.fromStream(pdfFileStream)

  lazy val fileContentLength: Int = Await.result(
    fileContent.run(Iteratee.fold(0){(totalLength, chunk) => totalLength + chunk.length}),
    10 seconds)

  def download = Action {
    val pdfFileStream = Play.resourceAsStream(pdfFileName) match {
      case Some(pdfFileStream) => pdfFileStream
      case None => throw new IllegalArgumentException(s"Play.resource($pdfFileName) returned None")
    }
    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromStream(pdfFileStream)

    SimpleResult(
      header = ResponseHeader(200,
        Map(
          CONTENT_TYPE -> "application/pdf",
          CONTENT_LENGTH -> fileContentLength.toString,
          CONTENT_DISPOSITION -> "attachment; filename=\"crown-servant-declaration.pdf\""
        )),
      body = fileContent
    )
  }

  def fileSizeWithUnit = {
    (fileContentLength / 1024) + "KB"
  }
}
