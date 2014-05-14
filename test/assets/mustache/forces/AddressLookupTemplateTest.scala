package assets.mustache.forces

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.{WithMockRemoteAssets, TestHelpers}
import uk.gov.gds.ier.transaction.forces.address.AddressLookupMustache
import org.jba.Mustache

class AddressLookupTemplateTest
  extends FlatSpec
  with AddressLookupMustache
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

      val html = Mustache.render("forces/addressLookup", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()

      val label = fieldset.select("label").first()
      label.attr("for") should be("postcodeId")

      val divWrapper = fieldset.select("div").first()
      divWrapper.attr("class") should include("postcodeClasses")

      val postcodeInput = divWrapper.select("input#postcodeId").first()
      postcodeInput should not be(null)
      postcodeInput.attr("id") should be("postcodeId")
      postcodeInput.attr("name") should be("postcodeName")
      postcodeInput.attr("value") should be("postcodeValue")
      postcodeInput.attr("class") should include("postcodeClasses")
    }
  }
}
