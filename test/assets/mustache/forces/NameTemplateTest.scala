package assets.mustache.forces

import org.jsoup.Jsoup
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.transaction.forces.name.NameMustache

class NameTemplateTest
  extends FlatSpec
  with NameMustache
  with Matchers {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new NameModel(
        question = Question(),
        firstName = Field(
          id = "firstNameId",
          name = "firstNameName",
          classes = "firstNameClass",
          value = "firstNameValue"
        ),
        middleNames = Field(
          id = "middleNameId",
          name = "middleNameName",
          classes = "middleNameClass",
          value = "middleNameValue"
        ),
        lastName = Field(
          id = "lastNameId",
          name = "lastNameName",
          classes = "lastNameClass",
          value = "lastNameValue"
        )
      )

      val html = Mustache.render("forces/name", data)
      val doc = Jsoup.parse(html.toString)

      //First Name
      doc
        .select("label[for=firstNameId]")
        .first()
        .attr("for") should be("firstNameId")

      val firstNameDiv = doc.select("div[class*=firstNameClass]").first()
      firstNameDiv.attr("class") should include("firstNameClass")
      val firstNameInput = firstNameDiv.select("input").first()
      firstNameInput.attr("id") should be("firstNameId")
      firstNameInput.attr("name") should be("firstNameName")
      firstNameInput.attr("value") should be("firstNameValue")
      firstNameInput.attr("class") should include("firstNameClass")


      //Middle Name
      doc
        .select("label[for=middleNameId]")
        .first()
        .attr("for") should be("middleNameId")

      val middleNameInput = doc.select("input[id=middleNameId]").first()
      middleNameInput.attr("id") should be("middleNameId")
      middleNameInput.attr("name") should be("middleNameName")
      middleNameInput.attr("value") should be("middleNameValue")
      middleNameInput.attr("class") should include("middleNameClass")


      //Last Name
      doc
        .select("label[for=lastNameId]")
        .first()
        .attr("for") should be("lastNameId")

      val lastNameDiv = doc.select("div[class*=lastNameClass]").first()
      lastNameDiv.attr("class") should include("lastNameClass")
      val lastNameInput = lastNameDiv.select("input").first()
      lastNameInput.attr("id") should be("lastNameId")
      lastNameInput.attr("name") should be("lastNameName")
      lastNameInput.attr("value") should be("lastNameValue")
      lastNameInput.attr("class") should include("lastNameClass")

    }
  }
}
