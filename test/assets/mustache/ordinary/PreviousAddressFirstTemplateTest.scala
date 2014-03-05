package assets.mustache.ordinary

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.overseas.waysToVote.WaysToVoteMustache
import uk.gov.gds.ier.transaction.ordinary.previousAddress.PreviousAddressFirstMustache

/**
 * Test rendering of Mustache template from given model
 */
class PreviousAddressFirstTemplateTest
  extends FlatSpec
  with PreviousAddressFirstMustache
  with Matchers {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new PreviousAddressFirstModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          backUrl = "http://some.server/back_url",
          showBackUrl = true,
          number = "123",
          title = "Page title ABC"
        ),
        previousYes = Field(
          id = "previousAddress_movedRecently_true",
          name = "previousAddress.movedRecently",
          classes = "previousYesClass previousYesClass2",
          value = "true",
          attributes = "checked=\"checked1\""
        ),
        previousNo = Field(
          id = "previousAddress_movedRecently_false",
          name = "previousAddress.movedRecently",
          classes = "previousNoClass previousNoClass2",
          value = "false",
          attributes = "checked=\"checked2\""
        )
      )

      val html = Mustache.render("ordinary/previousAddressFirst", data)
      val doc = Jsoup.parse(html.toString)

      { // YES option
        doc.select("label[for=previousAddress_movedRecently_true]").size() should be(1)
        val r = doc.select("input#previousAddress_movedRecently_true").first()
        r should not be(null)
        r.attr("name") should be("previousAddress.movedRecently")
        r.attr("value") should be("true")
        r.attr("class") should include("previousYesClass")
        r.attr("class") should include("previousYesClass2")
        r.attr("checked") should be("checked1")
      }

      { // NO option
        doc.select("label[for=previousAddress_movedRecently_false]").size() should be(1)
        val r = doc.select("input#previousAddress_movedRecently_false").first()
        r should not be(null)
        r.attr("id") should be("previousAddress_movedRecently_false")
        r.attr("name") should be("previousAddress.movedRecently")
        r.attr("value") should be("false")
        r.attr("class") should include("previousNoClass")
        r.attr("class") should include("previousNoClass2")
        r.attr("checked") should be("checked2")
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
