package assets.mustache.ordinary

import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import org.jsoup.Jsoup
import play.api.test.Helpers._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.transaction.ordinary.nationality.NationalityMustache

class NationalityTemplateTest
  extends FlatSpec
  with StepMustache
  with NationalityMustache
  with Matchers {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = NationalityModel(
        question = Question(postUrl = "/whatever-url",
        number = "1",
        title = "nationality title"
        ),
        britishOption = Field(
          id = "britishOptionId",
          name = "britishOptionName",
          attributes = "foo=\"foo\""
        ),
        irishOption = Field(
          id = "irishOptionId",
          name = "irishOptionName",
          attributes = "foo=\"foo\""
        ),
        hasOtherCountryOption = Field(
          id = "hasOtherCountryOptionId",
          name = "hasOtherCountryOptionName",
          attributes = "foo=\"foo\""
        ),
        otherCountriesHead = Field(
          id = "otherCountriesHeadId",
          name = "otherCountriesHeadName",
          value = "otherCountriesHeadValue"
        ),
        otherCountriesTail = List.empty,
        moreThanOneOtherCountry = false,
        noNationalityReason = Field (
          id = "noNationalityReasonId",
          name = "noNationalityReasonName",
          value = "noNationalityReasonValue"
        ),
        noNationalityReasonShowFlag = Text (
          value = "noNationalityReasonShowFlag"
        )
      )

      val html = Mustache.render("ordinary/nationality", data)
      val doc = Jsoup.parse(html.toString)

      val britishOptionInput = doc.select("input[id=britishOptionId]").first()
      britishOptionInput.attr("id") should be("britishOptionId")
      britishOptionInput.attr("name") should be("britishOptionName")
      britishOptionInput.attr("foo") should be("foo")

      val irishOptionInput = doc.select("input[id=irishOptionId]").first()
      irishOptionInput.attr("id") should be("irishOptionId")
      irishOptionInput.attr("name") should be("irishOptionName")
      irishOptionInput.attr("foo") should be("foo")
      
      val hasOtherCountryOptionInput = doc.select("input[id=hasOtherCountryOptionId]").first()
      hasOtherCountryOptionInput.attr("id") should be("hasOtherCountryOptionId")
      hasOtherCountryOptionInput.attr("name") should be("hasOtherCountryOptionName")
      hasOtherCountryOptionInput.attr("foo") should be("foo")
    }
  }
}
