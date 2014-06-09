package assets.mustache.overseas

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressSelectMustache
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import org.scalatest.mock.MockitoSugar

class LastUkAddressSelectTemplateTest
  extends FlatSpec
  with StepMustache
  with LastUkAddressSelectMustache
  with Matchers
  with MockitoSugar
  with WithSerialiser
  with WithAddressService
  with WithMockRemoteAssets
  with WithMockConfig
  with TestHelpers {

  val addressService = mock[AddressService]
  val serialiser = jsonSerialiser

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
        ),
        hasAddresses = true
      )

      val html = Mustache.render("overseas/lastUkAddressSelect", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()

      val postcodeSpan = doc.select("span[class=postcode]").first()
      postcodeSpan.html() should be("postcodeValue")

      val postcodeInput = fieldset.select("input[type=hidden]").first()
      postcodeInput.attr("id") should be("postcodeId")
      postcodeInput.attr("name") should be("postcodeName")
      postcodeInput.attr("value") should be("postcodeValue")

      val lookupLink = doc.select("a[class=change-postcode-button]").first()
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


  it should "should display error message if no addresses provided" in {
    running(FakeApplication()) {
      val data = new SelectModel(
        question = Question(),
        lookupUrl = "",
        manualUrl = "",
        postcode = Field(id = "",name = "",classes = "",value = ""),
        address = Field(
          id = "",
          name = "",
          classes = "",
          value = "",
          optionList = List.empty
        ),
        possibleJsonList = Field(id = "",name = "",value = ""),
        possiblePostcode = Field(id = "",name = "",value = ""),
        hasAddresses = false
      )

      val html = Mustache.render("overseas/lastUkAddressSelect", data)
      val doc = Jsoup.parse(html.toString)

      val wrapper = doc.select("div").first()
      wrapper.html() should include(
        "Sorry - we couldn't find any addresses for that postcode"
      )

      doc.select("select").size should be(0)
    }
  }
}

