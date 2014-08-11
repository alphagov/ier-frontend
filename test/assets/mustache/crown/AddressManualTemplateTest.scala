package assets.mustache.crown

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.crown.address.AddressManualMustache

class AddressManualTemplateTest
  extends FlatSpec
  with AddressManualMustache
  with StepMustache
  with Matchers
  with WithSerialiser
  with WithMockCrownControllers
  with WithMockConfig
  with WithMockRemoteAssets
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "properly render" in {
    running(FakeApplication()) {
      val data = new ManualModel(
        question = Question(),
        lookupUrl = "http://lookup",
        postcode = Field(
          id = "postcodeId",
          name = "postcodeName",
          classes = "no-classes-it-is-a-hidden-field",
          value = "WR26NJ"
        ),
        maLineOne = Field(
          id = "maLineOneId",
          name = "maLineOneName",
          classes = "manualClass11 manualClass12",
          value = "Unit 4, Elgar Business Centre"
        ),
        maLineTwo = Field(
          id = "maLineTwoId",
          name = "maLineTwoName",
          classes = "manualClass21 manualClass22",
          value = "Moseley Road"
        ),
        maLineThree = Field(
          id = "maLineThreeId",
          name = "maLineThreeName",
          classes = "manualClass31 manualClass32",
          value = "Hallow"
        ),
        maCity = Field(
          id = "maCityId",
          name = "maCityName",
          classes = "manualClass41 manualClass42",
          value = "Worcester"
        ),
        hasAddress = Field(
          id = "hasAddressId",
          name = "hasAddressName",
          value = "hasAddressValue"
        ),
        maLines = FieldSet(
          classes = "maLinesClasses"
        )
      )

      val html = Mustache.render("crown/addressManual", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()

      val postcodeSpan = fieldset.select("span[class=postcode]").first()
      postcodeSpan.html() should be("WR26NJ")

      val postcodeInput = fieldset.select("input[type=hidden]").first()
      postcodeInput.attr("id") should be("postcodeId")
      postcodeInput.attr("name") should be("postcodeName")
      postcodeInput.attr("value") should be("WR26NJ")

      { // manual address line 1
        val addressLineLabel = fieldset.select("label[for=maLineOneId]")
        addressLineLabel should not be(null)
        addressLineLabel.attr("for") should be("maLineOneId")

        val addressLineInput = fieldset.select("input#maLineOneId").first()
        addressLineInput should not be(null)
        addressLineInput.attr("name") should be("maLineOneName")
        addressLineInput.attr("class") should include("manualClass11")
        addressLineInput.attr("class") should include("manualClass12")
      }

      { // manual address line 2 (no label)

        val addressLineInput = fieldset.select("input#maLineTwoId").first()
        addressLineInput should not be(null)
        addressLineInput.attr("name") should be("maLineTwoName")
        addressLineInput.attr("class") should include("manualClass21")
        addressLineInput.attr("class") should include("manualClass22")
      }

      { // manual address line 3 (no label)

        val addressLineInput = fieldset.select("input#maLineThreeId").first()
        addressLineInput should not be(null)
        addressLineInput.attr("name") should be("maLineThreeName")
        addressLineInput.attr("class") should include("manualClass31")
        addressLineInput.attr("class") should include("manualClass32")
      }

      { // manual address line 4 - city
        val addressLineLabel = fieldset.select("label[for=maCityId]")
        addressLineLabel should not be(null)
        addressLineLabel.attr("for") should be("maCityId")

        val addressLineInput = fieldset.select("input#maCityId").first()
        addressLineInput should not be(null)
        addressLineInput.attr("name") should be("maCityName")
        addressLineInput.attr("class") should include("manualClass41")
        addressLineInput.attr("class") should include("manualClass42")
      }

      val hasAddressInput = doc.select("input[id=hasAddressId]").first()
      hasAddressInput should not be(null)
      hasAddressInput.attr("name") should be("hasAddressName")
      hasAddressInput.attr("value") should be("hasAddressValue")

      val lookupChangeLink = fieldset.select("a").first()
      lookupChangeLink.attr("href") should be("http://lookup")

      val manualAddressLinesWrapper = doc.select(".validation-wrapper").first()
      manualAddressLinesWrapper should not be(null)
      manualAddressLinesWrapper.attr("class") should include("maLinesClasses")
    }
  }
}
