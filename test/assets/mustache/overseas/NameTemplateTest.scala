package assets.mustache.overseas

import uk.gov.gds.ier.transaction.overseas.name.NameMustache
import uk.gov.gds.ier.test._

class NameTemplateTest
  extends TemplateTestSuite
  with NameMustache {

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
        ),
        hasPreviousName = FieldSet(classes = "hasPreviousNameClass"),
        hasPreviousNameTrue = Field(
          id = "hasPreviousTrueId",
          name = "hasPreviousTrueName",
          attributes = "foo=\"foo\""
        ),
        hasPreviousNameFalse = Field(
          id = "hasPreviousFalseId",
          name = "hasPreviousFalseName",
          attributes = "foo=\"foo\""
        ),
        previousFirstName = Field(
          id = "previousFirstNameId",
          name = "previousFirstNameName",
          classes = "previousFirstNameClass",
          value = "previousFirstNameValue"
        ),
        previousMiddleNames = Field(
          id = "previousMiddleNameId",
          name = "previousMiddleNameName",
          classes = "previousMiddleNameClass",
          value = "previousMiddleNameValue"
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

      val html = Mustache.render("overseas/name", data)
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
        .select("label[for=previousMiddleNameId]")
        .first()
        .attr("for") should be("previousMiddleNameId")

      val previousMiddleNameInput = doc.select("input[id=previousMiddleNameId]").first()
      previousMiddleNameInput.attr("id") should be("previousMiddleNameId")
      previousMiddleNameInput.attr("name") should be("previousMiddleNameName")
      previousMiddleNameInput.attr("value") should be("previousMiddleNameValue")
      previousMiddleNameInput.attr("class") should include("previousMiddleNameClass")


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

      //Has Previous Name
      val hasPreviousFieldset = doc.select("fieldset[class*=hasPreviousNameClass").first()
      hasPreviousFieldset.attr("class") should include("hasPreviousNameClass")

      val hasPreviousTrueLabel = hasPreviousFieldset.select("label[for=hasPreviousTrueId]").first()
      hasPreviousTrueLabel.attr("for") should be("hasPreviousTrueId")

      val hasPreviousTrueInput = hasPreviousTrueLabel.select("input").first()
      hasPreviousTrueInput.attr("id") should be("hasPreviousTrueId")
      hasPreviousTrueInput.attr("name") should be("hasPreviousTrueName")
      hasPreviousTrueInput.attr("foo") should be("foo")

      val hasPreviousFalseLabel = hasPreviousFieldset.select("label[for=hasPreviousFalseId]").first()
      hasPreviousFalseLabel.attr("for") should be("hasPreviousFalseId")

      val hasPreviousFalseInput = hasPreviousFalseLabel.select("input").first()
      hasPreviousFalseInput.attr("id") should be("hasPreviousFalseId")
      hasPreviousFalseInput.attr("name") should be("hasPreviousFalseName")
      hasPreviousFalseInput.attr("foo") should be("foo")


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
