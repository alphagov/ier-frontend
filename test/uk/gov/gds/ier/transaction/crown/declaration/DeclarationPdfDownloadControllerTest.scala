package uk.gov.gds.ier.transaction.crown.declaration

import org.scalatest.{Matchers, FlatSpec}
import play.api.test.Helpers._
import scala.Some
import play.api.test.{FakeRequest, FakeApplication}
import uk.gov.gds.ier.test.TestHelpers
import java.nio.file.{Paths, Files}
import controllers.step.crown.DeclarationPdfDownloadController
import uk.gov.gds.ier.service.DeclarationPdfDownloadService

/**
 * Test DeclarationPdfDownloadController and DeclarationPdfDownloadService
 */
class DeclarationPdfDownloadControllerTest extends FlatSpec with Matchers with TestHelpers {

  val service = new DeclarationPdfDownloadService()

  behavior of "DeclarationPdfDownloadController.download"
  it should "return binary stream exactly matching PDF file in assets" in {
    val expectedContent = Files.readAllBytes(
      Paths.get(
        DeclarationPdfDownloadController.getClass.getResource(
          service.pdfFileName).toURI))
    running(FakeApplication()) {
      route(
        FakeRequest(GET, "/register-to-vote/crown/declaration-pdf-download").withIerSession()
      ) match {
        case Some(result) => {
          status(result) should be(OK)
          contentType(result) should be(Some("application/pdf"))
          header(CONTENT_LENGTH, result) should be(Some(expectedContent.length.toString))
          val downloadedContent = contentAsBytes(result)
          downloadedContent should be(expectedContent)
          downloadedContent.take(4) should be("%PDF".getBytes)
          // PDF file headers should be like %PDF−1.0, %PDF−1.1, %PDF−1.2 ...ignore version
        }
        case None => fail("Request failed, wrong URL?")
      }
    }
  }
}
