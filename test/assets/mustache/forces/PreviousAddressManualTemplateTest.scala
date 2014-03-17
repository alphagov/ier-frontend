package assets.mustache.forces

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.forces.previousAddress.PreviousAddressMustache

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
        maLineOne = Field(
          id = "address_manualAddress_lineOne",
          name = "address.manualAddress.lineOne",
          classes = "manualClass11 manualClass12",
          value = "Unit 4, Elgar Business Centre"
        ),
        maLineTwo = Field(
          id = "address_manualAddress_lineTwo",
          name = "address.manualAddress.lineTwo",
          classes = "manualClass21 manualClass22",
          value = "Moseley Road"
        ),
        maLineThree = Field(
          id = "address_manualAddress_lineThree",
          name = "address.manualAddress.lineThree",
          classes = "manualClass31 manualClass32",
          value = "Hallow"
        ),
        maCity = Field(
          id = "address_manualAddress_city",
          name = "address.manualAddress.city",
          classes = "manualClass41 manualClass42",
          value = "Worcester"
        )
      )

      val html = Mustache.render("forces/previousAddressManual", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()
      fieldset should not be(null)

      val postcodeSpan = fieldset.select("span[class=postcode]").first()
      postcodeSpan should not be(null)
      postcodeSpan.html() should be("WR26NJ")

      val postcodeInput = fieldset.select("input[type=hidden]").first()
      postcodeInput should not be(null)
      postcodeInput.attr("id") should be("previousAddress_postcode")
      postcodeInput.attr("name") should be("previousAddress.postcode")
      postcodeInput.attr("value") should be("WR26NJ")

      { // manual address line 1
      val addressLineLabel = fieldset.select("label[for=address_manualAddress_lineOne]")
        addressLineLabel should not be(null)
        addressLineLabel.attr("for") should be("address_manualAddress_lineOne")

        val divWrapper = fieldset.select("div[class*=manualClass11]").first()
        divWrapper should not be(null)
        divWrapper.attr("class") should include("manualClass11")
        divWrapper.attr("class") should include("manualClass12")

        val addressLineInput = divWrapper.select("input#address_manualAddress_lineOne").first()
        addressLineInput should not be(null)
        addressLineInput.attr("name") should be("address.manualAddress.lineOne")
        addressLineInput.attr("class") should include("manualClass11")
        addressLineInput.attr("class") should include("manualClass12")
      }

      { // manual address line 2 (no label)
      val divWrapper = fieldset.select("div[class*=manualClass21]").first()
        divWrapper should not be(null)
        divWrapper.attr("class") should include("manualClass21")
        divWrapper.attr("class") should include("manualClass22")

        val addressLineInput = divWrapper.select("input#address_manualAddress_lineTwo").first()
        addressLineInput should not be(null)
        addressLineInput.attr("name") should be("address.manualAddress.lineTwo")
        addressLineInput.attr("class") should include("manualClass21")
        addressLineInput.attr("class") should include("manualClass22")
      }

      { // manual address line 3 (no label)
      val divWrapper = fieldset.select("div[class*=manualClass31]").first()
        divWrapper should not be(null)
        divWrapper.attr("class") should include("manualClass31")
        divWrapper.attr("class") should include("manualClass32")

        val addressLineInput = divWrapper.select("input#address_manualAddress_lineThree").first()
        addressLineInput should not be(null)
        addressLineInput.attr("name") should be("address.manualAddress.lineThree")
        addressLineInput.attr("class") should include("manualClass31")
        addressLineInput.attr("class") should include("manualClass32")
      }

      { // manual address line 4 - city
      val addressLineLabel = fieldset.select("label[for=address_manualAddress_city]")
        addressLineLabel should not be(null)
        addressLineLabel.attr("for") should be("address_manualAddress_city")

        val divWrapper = fieldset.select("div[class*=manualClass41]").first()
        divWrapper should not be(null)
        divWrapper.attr("class") should include("manualClass41")
        divWrapper.attr("class") should include("manualClass42")

        val addressLineInput = divWrapper.select("input#address_manualAddress_city").first()
        addressLineInput should not be(null)
        addressLineInput.attr("name") should be("address.manualAddress.city")
        addressLineInput.attr("class") should include("manualClass41")
        addressLineInput.attr("class") should include("manualClass42")
      }

      val lookupChangeLink = fieldset.select("a").first()
      lookupChangeLink should not be(null)
      lookupChangeLink.attr("href") should be("http://lookup")
    }
  }
}
