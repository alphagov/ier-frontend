package assets.mustache.overseas

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressMustache
import uk.gov.gds.ier.test.TestHelpers

class LastUkAddressSelectTemplateTest
  extends FlatSpec
  with LastUkAddressMustache
  with Matchers
  with WithSerialiser
  with TestHelpers {

  val serialiser = jsonSerialiser

  import LastUkAddressMustache._

  it should "properly render" in {
    running(FakeApplication()) {
      val data = new SelectModel(
        question = Question(),
        lookupUrl = "http://lookup",
        manualUrl = "http://manual",
        postcode = Field(
          id = "postcodeId",
          name = "postcodeName",
          classes = "postcodeClasses",
          value = "postcodeValue"
        ),
        address = Field(
          id = "addressId",
          name = "addressName",
          classes = "addressClasses",
          value = "addressValue",
          optionList = List(
            SelectOption(
              value = "optionValue",
              text = "optionText",
              selected = """ foo="foo" """
            )
          )
        ),
        possibleJsonList = Field(
          id = "possibleJsonId",
          name = "possibleJsonName",
          value = "possibleJsonValue"
        ),
        possiblePostcode = Field(
          id = "possiblePostcodeId",
          name = "possiblePostcodeName",
          value = "possiblePostcodeValue"
        )
      )

      val html = Mustache.render("overseas/lastUkAddressSelect", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()

      val postcodeLabel = fieldset.select("label[class=hidden]").first()
      postcodeLabel.attr("for") should be("postcodeId")

      val postcodeSpan = fieldset.select("span[class=postcode]").first()
      postcodeSpan.html() should be("postcodeValue")

      val postcodeInput = fieldset.select("input[type=hidden]").first()
      postcodeInput.attr("id") should be("postcodeId")
      postcodeInput.attr("name") should be("postcodeName")
      postcodeInput.attr("value") should be("postcodeValue")

      val lookupLink = fieldset.select("a[class=change-postcode-button]").first()
      lookupLink.attr("href") should be("http://lookup")

      val manualLink = doc.select("a[href=http://manual]").first()
      manualLink.attr("href") should be("http://manual")

      val addressLabel = fieldset.select("label[for=addressId]").first()
      addressLabel.attr("for") should be("addressId")

      val addressDiv = fieldset.select("div").first()
      addressDiv.attr("class") should include("addressClasses")

      val addressSelect = fieldset.select("select").first()
      addressSelect.attr("id") should be("addressId")
      addressSelect.attr("name") should be("addressName")
      addressSelect.attr("class") should include("addressClasses")

      val option = addressSelect.children().select("option").first()
      option.attr("value") should be("optionValue")
      option.attr("foo") should be("foo")
      option.html() should be("optionText")

      val hiddenJsonListInput = doc.select("input[type=hidden]").get(1)
      val hiddenPostcodeInput = doc.select("input[type=hidden]").get(2)

      hiddenJsonListInput.attr("id") should be("possibleJsonId")
      hiddenJsonListInput.attr("name") should be("possibleJsonName")
      hiddenJsonListInput.attr("value") should be("possibleJsonValue")

      hiddenPostcodeInput.attr("id") should be("possiblePostcodeId")
      hiddenPostcodeInput.attr("name") should be("possiblePostcodeName")
      hiddenPostcodeInput.attr("value") should be("possiblePostcodeValue")
    }
  }
}

