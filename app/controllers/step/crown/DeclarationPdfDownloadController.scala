package controllers.step.crown

import play.api.mvc.{ResponseHeader, SimpleResult, Action}
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Controller
import play.api.http.HeaderNames
import play.api.Play
import play.api.Play.current
import uk.gov.gds.ier.logging.Logging
import scala.concurrent.Await
import scala.concurrent.duration._

object DeclarationPdfDownloadController extends Controller with HeaderNames with Logging {

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
    logger.info("About to stream out " + pdfFileName)
    val pdfFileStream = Play.resourceAsStream(pdfFileName) match {
      case Some(pdfFileStream) => pdfFileStream
      case None => throw new IllegalArgumentException(s"Play.resource($pdfFileName) returned None")
    }
    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromStream(pdfFileStream)
    val result = SimpleResult(
      header = ResponseHeader(200,
        Map(
          CONTENT_TYPE -> "application/pdf",
          CONTENT_LENGTH -> fileContentLength.toString,
          CONTENT_DISPOSITION -> "attachment; filename=\"crown-servant-declaration.pdf\""
        )),
      body = fileContent
    )
    logger.info("Successfully prepared streaming out " + pdfFileName)
    result
  }
}
