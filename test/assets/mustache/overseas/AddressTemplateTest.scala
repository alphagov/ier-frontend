package assets.mustache.overseas

//import org.jba.Mustache
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import org.jsoup.Jsoup
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.overseas.address.AddressMustache
import play.api.mvc.Call

class AddressTemplateTest
  extends FlatSpec
  with AddressMustache
  with Matchers {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = AddressModel(
        question = Question(postUrl = "/register-to-vote/overseas/address",
        backUrl = "/register-to-vote/overseas/nino",
        number = "11",
        title = "Where do you live?"
        ),
        countrySelect = Field(
          id = "overseasAddressCountryId",
          name = "overseasAddressCountryName",
          classes = "overseasAddressCountryClass",
          value = "United Kingdom",
          optionList = List(SelectOption(value = "United Kingdom", text = "United Kingdom", 
              selected = "selected=\"selected\""), 
              SelectOption(value = "France", text = "France"))
        ),
        address = Field(
          id = "overseasAddressDetailsId",
          name = "overseasAddressDetailsName",
          classes = "overseasAddressDetailsClass",
          value = "some address"
        )
      )

      val html = Mustache.render("overseas/address", data)
      val doc = Jsoup.parse(html.toString)

      { //country select label
        doc.select("label[for=overseasAddressCountryId]").size() should be (1)
      }

      { // country selector wrapper
        doc.select("div[class*=overseasAddressCountryClass]").size() should be(1)
      }

      { // country selector
        val e = doc.select("div[class*=overseasAddressCountryClass] select").first()
        e should not be(null)
        e.attr("id") should be("overseasAddressCountryId")
        e.attr("id") should be("overseasAddressCountryId")
        e.attr("name") should be("overseasAddressCountryName")
        e.attr("class") should include("overseasAddressCountryClass")
      }
      
      {
       val e = doc.select("div[class*=overseasAddressCountryClass] select option[selected]").first()
       e should not be(null)
       e.text should be("United Kingdom")
       e.attr("value") should be("United Kingdom")
      }

      { //address details label
        doc.select("label[for=overseasAddressDetailsId]").size should be (1)
      }

      { //address details wrapper
        doc.select("div[class*=overseasAddressDetailsClass]").size should be (1)
      }

      { //address details wrapper
        val e = doc.select("textarea[id=overseasAddressDetailsId]").first()
        e should not be(null)
        e.attr("id") should be("overseasAddressDetailsId")
        e.attr("name") should be("overseasAddressDetailsName")
        e.attr("class") should include("overseasAddressDetailsClass")
        e.text should be("some address")
      }
    }
  }
}
