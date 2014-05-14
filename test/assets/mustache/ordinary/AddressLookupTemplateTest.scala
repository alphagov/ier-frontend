package assets.mustache.ordinary

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.ordinary.address.AddressMustache
import uk.gov.gds.ier.test.{WithMockRemoteAssets, TestHelpers}

class AddressLookupTemplateTest
  extends FlatSpec
  with AddressMustache
  with StepMustache
  with Matchers
  with WithSerialiser
  with WithMockRemoteAssets
  with TestHelpers {

  val serialiser = jsonSerialiser

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

      val html = Mustache.render("ordinary/addressLookup", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()
      fieldset should not be(null)

      val postcodeLabel = fieldset.select("label").first()
      postcodeLabel should not be(null)
      postcodeLabel.attr("for") should be("postcodeId")

      val divWrapper = fieldset.select("div").first()
      divWrapper should not be(null)
      divWrapper.attr("class") should include("postcodeClasses")

      val postcodeInput = divWrapper.select("input#postcodeId").first()
      postcodeInput should not be(null)
      postcodeInput.attr("name") should be("postcodeName")
      postcodeInput.attr("value") should be("postcodeValue")
      postcodeInput.attr("class") should include("postcodeClasses")
    }
  }
}
