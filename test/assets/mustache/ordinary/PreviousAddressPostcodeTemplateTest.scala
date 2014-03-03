package assets.mustache.ordinary

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressMustache
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.ordinary.previousAddress.PreviousAddressMustache

class PreviousAddressPostcodeTemplateTest
  extends FlatSpec
  with PreviousAddressMustache
  with Matchers
  with WithSerialiser
  with TestHelpers {

  val serialiser = jsonSerialiser

  import PreviousAddressMustache._

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new PostcodeModel(
        question = Question(
          postUrl = "http://some.server/previousAddress/select",
          backUrl = "http://some.server/previousAddress",
          showBackUrl = true,
          number = "123",
          title = "Page title ABC"
        ),
        postcode = Field(
          id = "previousAddress_postcode",
          name = "previousAddress.postcode",
          classes = "postcodeClass1 postcodeClass2",
          value = "WR26NJ"
        )
      )

      val html = Mustache.render("ordinary/previousAddressPostcode", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()
      fieldset should not be (null)

      val label = fieldset.select("label").first()
      label should not be (null)
      label.attr("for") should be("previousAddress_postcode")

      val divWrapper = fieldset.select("div").first()
      divWrapper should not be (null)
      divWrapper.attr("class") should include("postcodeClass1")
      divWrapper.attr("class") should include("postcodeClass2")

      val input = divWrapper.select("input#previousAddress_postcode").first()
      input should not be (null)
      input.attr("name") should be("previousAddress.postcode")
      input.attr("value") should be("WR26NJ")
      input.attr("class") should include("postcodeClass1")
      input.attr("class") should include("postcodeClass2")
    }
  }
}
