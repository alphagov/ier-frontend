package assets.mustache.ordinary

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.ordinary.previousAddress.PreviousAddressMustache

/**
 * This test is also a good example how select from list of addresses for postcode looks like,
 * especially what possibleAddresses and possiblePostcode are used for, hence the realistic values.
 */
class PreviousAddressSelectTemplateTest
  extends FlatSpec
  with PreviousAddressMustache
  with Matchers
  with WithSerialiser
  with TestHelpers {

  val serialiser = jsonSerialiser

  import PreviousAddressMustache._

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new SelectModel(
        question = Question(),
        lookupUrl = "http://localhost/previousAddress/select",
        manualUrl = "http://localhost/previousAddress/manual",
        postcode = Field(
          id = "previousAddress_postcode",
          name = "previousAddress.postcode",
          classes = "not-used-it-is-hidden-field",
          value = "WR26NJ"
        ),
        address = Field(
          id = "previousAddress_uprn",
          name = "previousAddress.uprn",
          classes = "addressSelectorClass1 addressSelectorClass2",
          value = "26742627",
          optionList = List(
            SelectOption(
              value = "26742627",
              text = "Beaumont, Moseley Road, Hallow, Worcester, Worcestershire",
              selected = """ selected="selected" """
            ),
            SelectOption(
              value = "26742666",
              text = "2 The Cottages, Moseley Road, Hallow, Worcester, Worcestershire",
              selected = ""
            )
          )
        ),
        possibleJsonList = Field(
          id = "possibleAddresses_jsonList",
          name = "possibleAddresses.jsonList",
          classes = "not-used-it-is-hidden-field",
          value = "{\"addresses\":[" +
            "{\"addressLine\":\"2 The Cottages, Moseley Road, Hallow, Worcester, Worcestershire\"," +
            "\"uprn\":\"26742666\",\"postcode\":\"WR2 6NJ\",\"manualAddress\":null}," +
            "{\"addressLine\":\"Beaumont, Moseley Road, Hallow, Worcester, Worcestershire\"," +
            "\"uprn\":\"26742627\",\"postcode\":\"WR2 6NJ\",\"manualAddress\":null}" +
            "]}"
        ),
        possiblePostcode = Field(
          id = "possibleAddresses_postcode",
          name = "possibleAddresses.postcode",
          classes = "not-used-it-is-hidden-field",
          value = "WR26NJ"
        ),
        hasAddresses = true
      )

      val html = Mustache.render("ordinary/previousAddressSelect", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()

      val postcodeSpan = doc.select("span[class=postcode]").first()
      postcodeSpan should not be (null)
      postcodeSpan.html() should be("WR26NJ")

      val postcodeInput = fieldset.select("input#previousAddress_postcode").first()
      postcodeInput should not be(null)
      postcodeInput.attr("type") should be("hidden")
      postcodeInput.attr("name") should be("previousAddress.postcode")
      postcodeInput.attr("value") should be("WR26NJ")

      val lookupLink = doc.select("a[class=change-postcode-button]").first()
      lookupLink should not be (null)
      lookupLink.attr("href") should be("http://localhost/previousAddress/select")

      val manualLink = doc.select("a[href=http://localhost/previousAddress/manual]").first()
      manualLink should not be(null)

      val addressLabel = fieldset.select("label[for=previousAddress_uprn]").first()
      addressLabel should not be(null)
      addressLabel.attr("for") should be("previousAddress_uprn")

      val addressDiv = fieldset.select("div").first()
      addressDiv should not be(null)
      addressDiv.attr("class") should include("addressSelectorClass1")
      addressDiv.attr("class") should include("addressSelectorClass2")

      val addressSelect = fieldset.select("select#previousAddress_uprn").first()
      addressSelect should not be(null)
      addressSelect.attr("name") should be("previousAddress.uprn")
      addressSelect.attr("class") should include("addressSelectorClass1")
      addressSelect.attr("class") should include("addressSelectorClass2")

      val firstAddressFromList = addressSelect.children().select("option").get(0)
      firstAddressFromList should not be(null)
      firstAddressFromList.attr("value") should be("26742627")
      firstAddressFromList.attr("selected") should be("selected")
      firstAddressFromList.html() should be("" +
        "Beaumont, Moseley Road, Hallow, Worcester, Worcestershire")

      val secondAddressFromList = addressSelect.children().select("option").get(1)
      secondAddressFromList should not be(null)
      secondAddressFromList.attr("value") should be("26742666")
      secondAddressFromList.attr("selected") should be("")
      secondAddressFromList.html() should be("" +
        "2 The Cottages, Moseley Road, Hallow, Worcester, Worcestershire")

      val hiddenJsonListInput = doc.select("input#possibleAddresses_jsonList").first()
      hiddenJsonListInput should not be(null)
      hiddenJsonListInput.attr("type") should be("hidden")
      hiddenJsonListInput.attr("name") should be("possibleAddresses.jsonList")
      hiddenJsonListInput.attr("value") should be("{\"addresses\":[" +
        "{\"addressLine\":\"2 The Cottages, Moseley Road, Hallow, Worcester, Worcestershire\"," +
        "\"uprn\":\"26742666\",\"postcode\":\"WR2 6NJ\",\"manualAddress\":null}," +
        "{\"addressLine\":\"Beaumont, Moseley Road, Hallow, Worcester, Worcestershire\"," +
        "\"uprn\":\"26742627\",\"postcode\":\"WR2 6NJ\",\"manualAddress\":null}" +
        "]}")

      val hiddenPostcodeInput = doc.select("input#possibleAddresses_postcode").first()
      hiddenPostcodeInput should not be(null)
      hiddenPostcodeInput.attr("type") should be("hidden")
      hiddenPostcodeInput.attr("name") should be("possibleAddresses.postcode")
      hiddenPostcodeInput.attr("value") should be("WR26NJ")
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

      val html = Mustache.render("ordinary/previousAddressSelect", data)
      val doc = Jsoup.parse(html.toString)

      val wrapper = doc.select("div").first()
      wrapper.html() should include(
        "Sorry - we couldn't find any addresses for that postcode"
      )

      doc.select("select").size should be(0)
    }
  }
}

