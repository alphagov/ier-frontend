package assets.mustache.ordinary

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.ordinary.address.AddressSelectMustache
import uk.gov.gds.ier.test._

class AddressSelectTemplateTest
  extends FlatSpec
  with AddressSelectMustache
  with StepMustache
  with MockitoSugar
  with Matchers
  with WithMockConfig
  with WithMockRemoteAssets
  with WithSerialiser
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

      val html = Mustache.render("ordinary/addressSelect", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()
      fieldset should not be(null)

      val postcodeSpan = doc.select("span[class=postcode]").first()
      postcodeSpan should not be(null)
      postcodeSpan.html() should be("postcodeValue")

      val postcodeInput = fieldset.select("input#postcodeId").first()
      postcodeInput should not be(null)
      postcodeInput.attr("type") should be("hidden")
      postcodeInput.attr("name") should be("postcodeName")
      postcodeInput.attr("value") should be("postcodeValue")

      val changePostcodeLink = doc.select("a[class=change-postcode-button]").first()
      changePostcodeLink should not be(null)
      changePostcodeLink.attr("href") should be("http://lookup")

      val manualLink = doc.select("a[href=http://manual]").first()
      manualLink should not be(null)
      manualLink.attr("href") should be("http://manual")

      val addressLabel = fieldset.select("label[for=addressId]").first()
      addressLabel should not be(null)
      addressLabel.attr("for") should be("addressId")

      val addressDiv = fieldset.select("div").first()
      addressDiv should not be(null)
      addressDiv.attr("class") should include("addressClasses")

      val addressSelect = fieldset.select("select#addressId").first()
      addressSelect should not be(null)
      addressSelect.attr("name") should be("addressName")
      addressSelect.attr("class") should include("addressClasses")

      val firstAddressInSelect = addressSelect.children().select("option").first()
      firstAddressInSelect should not be(null)
      firstAddressInSelect.attr("value") should be("optionValue")
      firstAddressInSelect.attr("foo") should be("foo")
      firstAddressInSelect.html() should be("optionText")

      val hiddenJsonListInput = doc.select("input#possibleJsonId").first()
      hiddenJsonListInput should not be(null)
      hiddenJsonListInput.attr("type") should be("hidden")
      hiddenJsonListInput.attr("name") should be("possibleJsonName")
      hiddenJsonListInput.attr("value") should be("possibleJsonValue")

      val hiddenPostcodeInput = doc.select("input#possiblePostcodeId").first()
      hiddenPostcodeInput should not be(null)
      hiddenPostcodeInput.attr("type") should be("hidden")
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

      val html = Mustache.render("ordinary/addressSelect", data)
      val doc = Jsoup.parse(html.toString)

      val wrapper = doc.select("div").first()
      wrapper.html() should include(
        "Sorry - we couldn’t find any addresses for that postcode"
      )

      doc.select("select").size should be(0)
    }
  }
}

