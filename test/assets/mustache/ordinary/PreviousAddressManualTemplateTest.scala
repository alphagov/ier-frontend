package assets.mustache.ordinary

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressMustache
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.ordinary.previousAddress.PreviousAddressMustache

class PreviousAddressManualTemplateTest
  extends FlatSpec
  with PreviousAddressMustache
  with Matchers
  with WithSerialiser
  with TestHelpers {

  val serialiser = jsonSerialiser

  import PreviousAddressMustache._

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new ManualModel(
        question = Question(),
        lookupUrl = "http://lookup",
        postcode = Field(
          id = "previousAddress_postcode",
          name = "previousAddress.postcode",
          classes = "no-classes-it-is-a-hidden-field",
          value = "WR26NJ"
        ),
        manualAddress = Field(
          id = "previousAddress_manualAddress",
          name = "previousAddress.manualAddress",
          classes = "manualClass1 manualClass2",
          value = "123 Fake road\nFakerton\nMiddlessex"
        )
      )

      val html = Mustache.render("ordinary/previousAddressManual", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()

      val postcodeSpan = fieldset.select("span[class=postcode]").first()
      postcodeSpan.html() should be("WR26NJ")

      val postcodeInput = fieldset.select("input[type=hidden]").first()
      postcodeInput.attr("id") should be("previousAddress_postcode")
      postcodeInput.attr("name") should be("previousAddress.postcode")
      postcodeInput.attr("value") should be("WR26NJ")

      val manualLabel = fieldset.select("label[for=previousAddress_manualAddress]")
      manualLabel.attr("for") should be("previousAddress_manualAddress")

      val divWrapper = fieldset.select("div").first()
      divWrapper.attr("class") should include("manualClass1")
      divWrapper.attr("class") should include("manualClass2")

      val manualText = divWrapper.select("textarea").first()
      manualText.attr("id") should be("previousAddress_manualAddress")
      manualText.attr("name") should be("previousAddress.manualAddress")
      manualText.attr("class") should include("manualClass1")
      manualText.attr("class") should include("manualClass2")

      val lookupChangeLink = fieldset.select("a").first()
      lookupChangeLink.attr("href") should be("http://lookup")
    }
  }
}
