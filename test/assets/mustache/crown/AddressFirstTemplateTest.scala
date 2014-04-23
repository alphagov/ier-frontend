package assets.mustache.crown

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.crown.address.AddressFirstMustache
import uk.gov.gds.ier.mustache.StepMustache

class AddressFirstTemplateTest
  extends FlatSpec
  with StepMustache
  with AddressFirstMustache
  with Matchers {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new AddressFirstModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          number = "123",
          title = "Page title ABC"
        ),
        hasAddressYes = Field(
          id = "hasAddressYesId",
          name = "hasAddressYesName",
          classes = "hasAddressYesClass",
          value = "hasAddressYesValue",
          attributes = "foo=\"foo\""
        ),
        hasAddressNo = Field(
          id = "hasAddressNoId",
          name = "hasAddressNoName",
          classes = "hasAddressNoClass",
          value = "hasAddressNoValue",
          attributes = "foo=\"foo\""
        )
      )

      val html = Mustache.render("crown/addressFirst", data)
      val doc = Jsoup.parse(html.toString)

      { // YES option
        doc.select("label[for=hasAddressYesId]").size() should be(1)
        val r = doc.select("input#hasAddressYesId").first()
        r should not be(null)
        r.attr("name") should be("hasAddressYesName")
        r.attr("value") should be("hasAddressYesValue")
        r.attr("foo") should be("foo")
      }

      { // NO option
        doc.select("label[for=hasAddressNoId]").size() should be(1)
        val r = doc.select("input#hasAddressNoId").first()
        r should not be(null)
        r.attr("id") should be("hasAddressNoId")
        r.attr("name") should be("hasAddressNoName")
        r.attr("value") should be("hasAddressNoValue")
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
