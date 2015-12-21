package assets.mustache.overseas

import uk.gov.gds.ier.transaction.overseas.previousName.PreviousNameMustache
import uk.gov.gds.ier.test._

class PreviousNameTemplateTest
  extends TemplateTestSuite
  with PreviousNameMustache {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new PreviousNameModel(
        question = Question(),
        previousFirstName = Field(
          id = "previousFirstNameId",
          name = "previousFirstNameName",
          classes = "previousFirstNameClass",
          value = "previousFirstNameValue"
        ),
        previousMiddleNames = Field(
          id = "previousMiddleNamesId",
          name = "previousMiddleNamesName",
          classes = "previousMiddleNameClass",
          value = "previousMiddleNamesValue"
        ),
        previousLastName = Field(
          id = "previousLastNameId",
          name = "previousLastNameName",
          classes = "previousLastNameClass",
          value = "previousLastNameValue"
        ),
        nameChangeReason = Field(
          id = "nameChangeReasonId",
          name = "nameChangeReasonName",
          classes = "nameChangeReasonClass",
          value = "nameChangeReasonValue"
        )
      )

      val html = Mustache.render("overseas/previousName", data)
      val doc = Jsoup.parse(html.toString)

      //Previous First Name
      doc
        .select("label[for=previousFirstNameId]")
        .first()
        .attr("for") should be("previousFirstNameId")

      val previousFirstNameDiv = doc.select("div[class*=previousFirstNameClass]").first()
      previousFirstNameDiv.attr("class") should include("previousFirstNameClass")

      val previousFirstNameInput = previousFirstNameDiv.select("input").first()

      previousFirstNameInput.attr("id") should be("previousFirstNameId")
      previousFirstNameInput.attr("name") should be("previousFirstNameName")
      previousFirstNameInput.attr("value") should be("previousFirstNameValue")
      previousFirstNameInput.attr("class") should include("previousFirstNameClass")


      //Previous Middle Name
      doc
        .select("label[for=previousMiddleNamesId]")
        .first()
        .attr("for") should be("previousMiddleNamesId")

      val previousMiddleNameInput = doc.select("input[id=previousMiddleNamesId]").first()
      previousMiddleNameInput.attr("id") should be("previousMiddleNamesId")
      previousMiddleNameInput.attr("name") should be("previousMiddleNamesName")
      previousMiddleNameInput.attr("value") should be("previousMiddleNamesValue")
      //previousMiddleNameInput.attr("class") should include("previousMiddleNameClass")


      //Previous Last Name
      doc
        .select("label[for=previousLastNameId]")
        .first()
        .attr("for") should be("previousLastNameId")

      val previousLastNameDiv =doc.select("div[class*=previousLastNameClass]").first()
      previousLastNameDiv.attr("class") should include("previousLastNameClass")

      val previousLastNameInput = previousLastNameDiv.select("input").first()
      previousLastNameInput.attr("id") should be("previousLastNameId")
      previousLastNameInput.attr("name") should be("previousLastNameName")
      previousLastNameInput.attr("value") should be("previousLastNameValue")
      previousLastNameInput.attr("class") should include("previousLastNameClass")

      val nameChangeReasonLabel = doc.select("label[for=nameChangeReasonId]").first()
      nameChangeReasonLabel.attr("for") should be("nameChangeReasonId")

      val nameChangeReasonDiv = doc.select("div[class*=nameChangeReasonClass]").first()

      val nameChangeReasonInput = nameChangeReasonDiv.select("textarea").first()
      nameChangeReasonInput.attr("id") should be("nameChangeReasonId")
      nameChangeReasonInput.attr("name") should be("nameChangeReasonName")
      nameChangeReasonInput.html should be("nameChangeReasonValue")
      nameChangeReasonInput.attr("class") should include("nameChangeReasonClass")

    }
  }
}