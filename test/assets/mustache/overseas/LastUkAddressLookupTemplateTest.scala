package assets.mustache.overseas

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressMustache
import uk.gov.gds.ier.test.TestHelpers

class LastUkAddressLookupTemplateTest
  extends FlatSpec
  with LastUkAddressMustache
  with Matchers
  with WithSerialiser
  with TestHelpers {

  val serialiser = jsonSerialiser

  import LastUkAddressMustache._

  it should "properly render" in {
    running(FakeApplication()) {
      val data = new LookupModel(
        question = Question(),
        postcode = Field(
          id = "postcodeId",
          name = "postcodeName",
          classes = "postcodeClasses",
          value = "postcodeValue"
        )
      )

      val html = Mustache.render("overseas/lastUkAddressLookup", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()

      val label = fieldset.select("label").first()
      label.attr("for") should be("postcodeId")

      val divWrapper = fieldset.select("div").first()
      divWrapper.attr("class") should include("postcodeClasses")
      
      val input = divWrapper.select("input").first()
      input.attr("id") should be("postcodeId")
      input.attr("name") should be("postcodeName")
      input.attr("value") should be("postcodeValue")
      input.attr("class") should include("postcodeClasses")
    }
  }
}
