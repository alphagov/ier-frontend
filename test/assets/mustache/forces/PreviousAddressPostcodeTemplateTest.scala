package assets.mustache.forces

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.forces.previousAddress.PreviousAddressPostcodeMustache
import org.jba.Mustache
import uk.gov.gds.ier.mustache.StepMustache

class PreviousAddressPostcodeTemplateTest
  extends FlatSpec
  with PreviousAddressPostcodeMustache
  with StepMustache
  with Matchers
  with WithSerialiser
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new PostcodeModel(
        question = Question(
          postUrl = "http://some.server/previousAddress/select",
          number = "123",
          title = "Page title ABC"
        ),
        postcode = Field(
          id = "postcodeId",
          name = "postcodeName",
          classes = "postcodeClass1 postcodeClass2",
          value = "WR26NJ"
        )
      )

      val html = Mustache.render("forces/previousAddressPostcode", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()
      fieldset should not be (null)

      val label = fieldset.select("label").first()
      label should not be (null)
      label.attr("for") should be("postcodeId")

      val divWrapper = fieldset.select("div").first()
      divWrapper should not be (null)
      divWrapper.attr("class") should include("postcodeClass1")
      divWrapper.attr("class") should include("postcodeClass2")

      val input = divWrapper.select("input#postcodeId").first()
      input should not be (null)
      input.attr("name") should be("postcodeName")
      input.attr("value") should be("WR26NJ")
      input.attr("class") should include("postcodeClass1")
      input.attr("class") should include("postcodeClass2")
    }
  }
}
