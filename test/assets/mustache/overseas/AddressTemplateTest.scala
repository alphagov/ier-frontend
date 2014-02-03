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
      val data = OverseasAddressModel(
        question = Question(postUrl = "/register-to-vote/overseas/address",
        backUrl = "/register-to-vote/overseas/nino",
        number = "11",
        title = "Where do you live?"
        ),
        countrySelect = Field(
          id = "overseasAddressCountryId",
          name = "overseasAddressCountryName",
          classes = "overseasAddressCountryClass",
          value = "United Kingdom"
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
      println (doc)

      //country select
      val abc = doc.select("label[for=overseasAddressCountryId]").size() 
      abc should be (1)
      
      val countrySelectDiv = doc.select("div[class*=overseasAddressCountryClass]").first()
      
      val countrySelect = countrySelectDiv.select("select")
//      countrySelect.attr("id") should be("overseasAddressCountryId")
//      countrySelect.attr("name") should be("overseasAddressCountryName")
//      countrySelect.attr("class") should include("overseasAddressCountryClass")
//      countrySelect.attr("value") should be("United Kingdom")


      //address details
      doc.select("label[for=overseasAddressDetailsId]").size should be (1)

      val addressDetailsInput = doc.select("textarea[id=overseasAddressDetailsId]").first()
      addressDetailsInput.attr("id") should be("overseasAddressDetailsId")
      addressDetailsInput.attr("name") should be("overseasAddressDetailsName")
      addressDetailsInput.text should be("some address")
      addressDetailsInput.attr("class") should include("overseasAddressDetailsClass")
    }
  }
}