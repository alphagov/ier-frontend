package assets.mustache.forces

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.forces.address.AddressMustache

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

      val html = Mustache.render("forces/addressManual", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()

      val postcodeSpan = fieldset.select("span[class=postcode]").first()
      postcodeSpan.html() should be("postcodeValue")

      val postcodeInput = fieldset.select("input[type=hidden]").first()
      postcodeInput.attr("id") should be("postcodeId")
      postcodeInput.attr("name") should be("postcodeName")
      postcodeInput.attr("value") should be("postcodeValue")

      val manualLabel = fieldset.select("label[for=manualId]")
      manualLabel.attr("for") should be("manualId")

      val divWrapper = fieldset.select("div").first()
      divWrapper.attr("class") should include("manualClasses")

      val manualText = divWrapper.select("textarea#manualId").first()
      manualText should not be(null)
      manualText.attr("id") should be("manualId")
      manualText.attr("name") should be("manualName")
      manualText.attr("class") should include("manualClasses")

      val lookupChangeLink = fieldset.select("a").first()
      lookupChangeLink.attr("href") should be("http://lookup")
    }
  }
}
