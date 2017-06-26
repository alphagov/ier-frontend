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
        hasPreviousNameOption = FieldSet(classes = "hasPreviousNameOptionClass"),
        hasPreviousNameOptionFalse = Field(
          id = "hasPreviousOptionFalseId",
          name = "hasPreviousOptionFalseName",
          attributes = "foo=\"foo\""
        ),
        hasPreviousNameOptionTrue = Field(
          id = "hasPreviousOptionTrueId",
          name = "hasPreviousOptionTrueName",
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
        changedNameBeforeLeavingUKOption = FieldSet(classes = "changedNameBeforeLeavingUKOptionClass"),
        changedNameBeforeLeavingUKOptionFalse = Field(
          id = "changedNameBeforeLeavingUKOptionFalseId",
          name = "changedNameBeforeLeavingUKOptionFalseName",
          attributes = "foo=\"foo\""
        ),
        changedNameBeforeLeavingUKOptionTrue = Field(
          id = "changedNameBeforeLeavingUKOptionTrueId",
          name = "changedNameBeforeLeavingUKOptionTrueName",
          attributes = "foo=\"foo\""
        ),
          changedNameBeforeLeavingUKOptionOther = Field(
          id = "changedNameBeforeLeavingUKOptionOtherId",
          name = "changedNameBeforeLeavingUKOptionOtherName",
          attributes = "foo=\"foo\""
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

      //Changed Name Before Leaving UK Option
      val changedNameBeforeLeavingUKOptionFieldset = doc.select("fieldset[class*=changedNameBeforeLeavingUKOptionClass").first()
      changedNameBeforeLeavingUKOptionFieldset.attr("class") should include("changedNameBeforeLeavingUKOptionClass")

      val changedNameBeforeLeavingUKOptionFalseLabel = changedNameBeforeLeavingUKOptionFieldset.select("label[for=changedNameBeforeLeavingUKOptionFalseId]").first()
      changedNameBeforeLeavingUKOptionFalseLabel.attr("for") should be("changedNameBeforeLeavingUKOptionFalseId")

      val changedNameBeforeLeavingUKOptionFalseInput = changedNameBeforeLeavingUKOptionFalseLabel.select("input").first()
      changedNameBeforeLeavingUKOptionFalseInput.attr("id") should be("changedNameBeforeLeavingUKOptionFalseId")
      changedNameBeforeLeavingUKOptionFalseInput.attr("name") should be("changedNameBeforeLeavingUKOptionFalseName")
      changedNameBeforeLeavingUKOptionFalseInput.attr("foo") should be("foo")

      val changedNameBeforeLeavingUKOptionTrueLabel = changedNameBeforeLeavingUKOptionFieldset.select("label[for=changedNameBeforeLeavingUKOptionTrueId]").first()
      changedNameBeforeLeavingUKOptionTrueLabel.attr("for") should be("changedNameBeforeLeavingUKOptionTrueId")

      val changedNameBeforeLeavingUKOptionTrueInput = changedNameBeforeLeavingUKOptionTrueLabel.select("input").first()
      changedNameBeforeLeavingUKOptionTrueInput.attr("id") should be("changedNameBeforeLeavingUKOptionTrueId")
      changedNameBeforeLeavingUKOptionTrueInput.attr("name") should be("changedNameBeforeLeavingUKOptionTrueName")
      changedNameBeforeLeavingUKOptionTrueInput.attr("foo") should be("foo")

      val changedNameBeforeLeavingUKOptionOtherLabel = changedNameBeforeLeavingUKOptionFieldset.select("label[for=changedNameBeforeLeavingUKOptionOtherId]").first()
      changedNameBeforeLeavingUKOptionOtherLabel.attr("for") should be("changedNameBeforeLeavingUKOptionOtherId")

      val changedNameBeforeLeavingUKOptionOtherInput = changedNameBeforeLeavingUKOptionOtherLabel.select("input").first()
      changedNameBeforeLeavingUKOptionOtherInput.attr("id") should be("changedNameBeforeLeavingUKOptionOtherId")
      changedNameBeforeLeavingUKOptionOtherInput.attr("name") should be("changedNameBeforeLeavingUKOptionOtherName")
      changedNameBeforeLeavingUKOptionOtherInput.attr("foo") should be("foo")

      //Has Previous Name
      val hasPreviousFieldset = doc.select("fieldset[class*=hasPreviousNameOptionClass").first()
      hasPreviousFieldset.attr("class") should include("hasPreviousNameOptionClass")

      val hasPreviousOptionFalseLabel = hasPreviousFieldset.select("label[for=hasPreviousOptionFalseId]").first()
      hasPreviousOptionFalseLabel.attr("for") should be("hasPreviousOptionFalseId")

      val hasPreviousOptionFalseInput = hasPreviousOptionFalseLabel.select("input").first()
      hasPreviousOptionFalseInput.attr("id") should be("hasPreviousOptionFalseId")
      hasPreviousOptionFalseInput.attr("name") should be("hasPreviousOptionFalseName")
      hasPreviousOptionFalseInput.attr("foo") should be("foo")

      val hasPreviousOptionTrueLabel = hasPreviousFieldset.select("label[for=hasPreviousOptionTrueId]").first()
      hasPreviousOptionTrueLabel.attr("for") should be("hasPreviousOptionTrueId")

      val hasPreviousOptionTrueInput = hasPreviousOptionTrueLabel.select("input").first()
      hasPreviousOptionTrueInput.attr("id") should be("hasPreviousOptionTrueId")
      hasPreviousOptionTrueInput.attr("name") should be("hasPreviousOptionTrueName")
      hasPreviousOptionTrueInput.attr("foo") should be("foo")

    }
  }
}
