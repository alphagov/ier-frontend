package assets.mustache.crown

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.crown.previousAddress.PreviousAddressFirstMustache
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.test.WithMockRemoteAssets

/**
 * Test rendering of Mustache template from given model
 */
class PreviousAddressFirstTemplateTest
  extends FlatSpec
  with PreviousAddressFirstMustache
  with StepMustache
  with WithMockRemoteAssets
  with Matchers {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new PreviousAddressFirstModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          number = "123",
          title = "Page title ABC"
        ),
        previousYesAndLivingThere = Field(
          id = "previousYesAndLivingThereId",
          name = "previousYesAndLivingThereName",
          classes = "previousYesAndLivingThereClass previousYesAndLivingThereClass2",
          value = "true",
          attributes = "foo=\"foo\""
        ),
        previousYesAndNotLivingThere = Field(
          id = "previousYesAndNotLivingThereId",
          name = "previousYesAndNotLivingThereName",
          classes = "previousYesAndNotLivingThereClass previousYesAndNotLivingThereClass2",
          value = "true",
          attributes = "foo=\"foo\""
        ),
        previousNo = Field(
          id = "previousNoId",
          name = "previousNoName",
          classes = "previousNoClass previousNoClass2",
          value = "false",
          attributes = "foo=\"foo\""
        )
      )

      val html = Mustache.render("crown/previousAddressFirst", data)
      val doc = Jsoup.parse(html.toString)

      { // YES living there option
        doc.select("label[for=previousYesAndLivingThereId]").size() should be(1)
        val r = doc.select("input#previousYesAndLivingThereId").first()
        r should not be(null)
        r.attr("name") should be("previousYesAndLivingThereName")
        r.attr("value") should be("true")
        r.attr("foo") should be("foo")
      }

      { // YES not living there option
        doc.select("label[for=previousYesAndNotLivingThereId]").size() should be(1)
        val r = doc.select("input#previousYesAndNotLivingThereId").first()
        r should not be(null)
        r.attr("name") should be("previousYesAndNotLivingThereName")
        r.attr("value") should be("true")
        r.attr("foo") should be("foo")
      }

      { // NO option
        doc.select("label[for=previousNoId]").size() should be(1)
        val r = doc.select("input#previousNoId").first()
        r should not be(null)
        r.attr("id") should be("previousNoId")
        r.attr("name") should be("previousNoName")
        r.attr("value") should be("false")
        r.attr("foo") should be("foo")
      }

      { // page
        val f = doc.select("form").first() // there should be only one form in the template
        f should not be(null)
        f.attr("action") should be ("http://some.server/post_url")

        val h = doc.select("header").first() // there should be only one header in the template
        h should not be(null)
        h.text should include ("123")
        h.text should include ("Page title ABC")
      }
    }
  }
}
