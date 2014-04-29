package assets.mustache.crown

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.crown.declaration.{WithPlacesService, DeclarationPdfMustache}
import play.api.test.FakeApplication
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.service.PlacesService
import uk.gov.gds.ier.transaction.crown.InprogressCrown

/**
 * Test rendering of Mustache template from given model
 */
class DeclarationPdfTemplateTest
  extends FlatSpec
  with StepMustache
  with MockitoSugar
  with WithSerialiser
  with WithPlacesService
  with DeclarationPdfMustache
  with Matchers {

  val placesService = mock[PlacesService]
  val serialiser = mock[JsonSerialiser]

  it should "properly render all properties from the model with just election authority URL" in {
    running(FakeApplication()) {
      val data = DeclarationPdfModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          title = "Page title ABC"
        ),
        declarationPdfUrl = "http://test/pdf_download",
        showAuthorityUrl = false,
        authorityName = "Haringey Borough Council"
      )

      val templateName = mustache.asInstanceOf[MustacheTemplate[InprogressCrown]].mustachePath
      val html = Mustache.render(templateName, data)
      val renderedContent = html.toString
      val doc = Jsoup.parse(renderedContent)

      renderedContent should include ("http://test/pdf_download")
      renderedContent should include ("Haringey Borough Council")

      val f = doc.select("form").first() // there should be only one form in the template
      f should not be(null)
      f.attr("action") should be ("http://some.server/post_url")

      val h = doc.select("header").first() // there should be only one header in the template
      h should not be(null)
      h.text should include ("Page title ABC")
    }
  }
}