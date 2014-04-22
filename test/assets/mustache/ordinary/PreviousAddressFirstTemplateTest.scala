package assets.mustache.ordinary

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.overseas.waysToVote.WaysToVoteMustache
import uk.gov.gds.ier.transaction.ordinary.previousAddress.PreviousAddressFirstMustache

/**
 * Test rendering of Mustache template from given model
 */
class PreviousAddressFirstTemplateTest
  extends FlatSpec
  with StepMustache
  with PreviousAddressFirstMustache
  with Matchers {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new PreviousAddressFirstModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          number = "123",
          title = "Page title ABC"
        ),
        previousYesUk = Field(
          id = "previousYesUkId",
          name = "previousYesUkName",
          classes = "previousYesUkClasses",
          value = "previousYesUkValue",
          attributes = "foo=\"foo\""
        ),
        previousYesAbroad = Field(
          id = "previousYesAbroadId",
          name = "previousYesAbroadName",
          classes = "previousYesAbroadClasses",
          value = "previousYesAbroadValue",
          attributes = "foo=\"foo\""
        ),
        previousNo = Field(
          id = "previousAddressNoId",
          name = "previousAddressNoName",
          classes = "previousAddressNoClasses",
          value = "previousAddressNoValue",
          attributes = "foo=\"foo\""
        )
      )

      val html = Mustache.render("ordinary/previousAddressFirst", data)
      val doc = Jsoup.parse(html.toString)

      { // YES option
        doc.select("label[for=previousYesUkId]").size() should be(1)
        val r = doc.select("input#previousYesUkId").first()
        r should not be(null)
        r.attr("name") should be("previousYesUkName")
        r.attr("value") should be("previousYesUkValue")
        r.attr("foo") should be("foo")
      }

      { // YES option
        doc.select("label[for=previousYesAbroadId]").size() should be(1)
        val r = doc.select("input#previousYesAbroadId").first()
        r should not be(null)
        r.attr("name") should be("previousYesAbroadName")
        r.attr("value") should be("previousYesAbroadValue")
        r.attr("foo") should be("foo")
      }

      { // NO option
        doc.select("label[for=previousAddressNoId]").size() should be(1)
        val r = doc.select("input#previousAddressNoId").first()
        r should not be(null)
        r.attr("id") should be("previousAddressNoId")
        r.attr("name") should be("previousAddressNoName")
        r.attr("value") should be("previousAddressNoValue")
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
