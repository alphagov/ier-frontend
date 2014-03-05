package assets.mustache.ordinary

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.ordinary.address.AddressMustache
import uk.gov.gds.ier.test.TestHelpers

class AddressManualTemplateTest
  extends FlatSpec
  with AddressMustache
  with Matchers
  with WithSerialiser
  with TestHelpers {

  val serialiser = jsonSerialiser

  import AddressMustache._

  it should "properly render" in {
    running(FakeApplication()) {
      val data = new ManualModel(
        question = Question(),
        lookupUrl = "http://lookup",
        postcode = Field(
          id = "postcodeId",
          name = "postcodeName",
          classes = "postcodeClasses",
          value = "postcodeValue"
        ),
        manualAddress = Field(
          id = "manualId",
          name = "manualName",
          classes = "manualClasses",
          value = "manualValue"
        )
      )

      val html = Mustache.render("ordinary/addressManual", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()
      fieldset should not be(null)

      val postcodeSpan = fieldset.select("span[class=postcode]").first()
      postcodeSpan should not be(null)
      postcodeSpan.html() should be("postcodeValue")

      val postcodeInput = fieldset.select("input#postcodeId").first()
      postcodeInput should not be(null)
      postcodeInput.attr("type") should be("hidden")
      postcodeInput.attr("name") should be("postcodeName")
      postcodeInput.attr("value") should be("postcodeValue")

      val manualLabel = fieldset.select("label[for=manualId]")
      manualLabel should not be(null)
      manualLabel.attr("for") should be("manualId")

      val divWrapper = fieldset.select("div").first()
      divWrapper should not be(null)
      divWrapper.attr("class") should include("manualClasses")

      val manualText = divWrapper.select("textarea#manualId").first()
      manualText should not be(null)
      manualText.attr("name") should be("manualName")
      manualText.attr("class") should include("manualClasses")

      val postcodeChangeLink = fieldset.select("a").first()
      postcodeChangeLink should not be(null)
      postcodeChangeLink.attr("href") should be("http://lookup")
    }
  }
}
