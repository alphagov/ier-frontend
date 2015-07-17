package assets.mustache.ordinary

import uk.gov.gds.ier.transaction.ordinary.dateOfBirth.DateOfBirthMustache
import uk.gov.gds.ier.test._

class DateOfBirthTemplateTest
  extends TemplateTestSuite
  with DateOfBirthMustache
  with WithMockScotlandService {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = DateOfBirthModel(
        question = Question(postUrl = "/register-to-vote/date-of-birth",
        number = "1",
        title = "What is your date of birth?"
        ),
        day = Field(
          id = "dayId",
          name = "dayName",
          classes = "dayClass",
          value = "12"
        ),
        month = Field(
          id = "monthId",
          name = "monthName",
          classes = "monthClass",
          value = "12"
        ),
        year = Field(
          id = "yearId",
          name = "yearName",
          classes = "yearClass",
          value = "1980"
        ),
        noDobReason = Field(
          id = "noDobReasonId",
          name = "noDobReasonName",
          classes = "noDobReasonClass",
          value = "noDobReasonValue"
        ),
        isScot = false,
        rangeFieldSet = FieldSet(
          classes = ""
        ),
        rangeUnder18 = Field(
          id = "rangeUnder18Id",
          name = "rangeUnder18Name",
          classes = "rangeUnder18Class",
          attributes = "foo=\"foo\""
        ),
        rangeOver70 = Field(
          id = "rangeOver70Id",
          name = "rangeOver70Name",
          classes = "rangeOver70Class",
          attributes = "foo=\"foo\""
        ),
        range18to70 = Field(
          id = "range18to70Id",
          name = "range18to70Name",
          classes = "range18to70Class",
          attributes = "foo=\"foo\""
        ),
        range14to15_YoungScot = Field(
          id = "range14to15Id",
          name = "range14to15Name",
          classes = "range14to15Class",
          attributes = "foo=\"foo\""
        ),
        range16to17_YoungScot = Field(
          id = "range16to17Id",
          name = "range16to17Name",
          classes = "range16to17Class",
          attributes = "foo=\"foo\""
        ),
        rangeOver18_YoungScot = Field(
          id = "rangeOver18Id",
          name = "rangeOver18Name",
          classes = "rangeOver18Class",
          attributes = "foo=\"foo\""
        ),
        rangeDontKnow = Field(
          id = "rangeDontKnowId",
          name = "rangeDontKnowName",
          classes = "rangeDontKnowClass",
          attributes = "foo=\"foo\""
        ),
        noDobReasonShowFlag = Text(
          value = "noDobReasonShowFlag"
        )
      )

      val html = Mustache.render("ordinary/dateOfBirth", data)
      val doc = Jsoup.parse(html.toString)

      doc.select("label[for=dayId]").first().attr("for") should be("dayId")

      val dayInput = doc.select("input[id=dayId]").first()
      dayInput.attr("id") should be("dayId")
      dayInput.attr("name") should be("dayName")
      dayInput.attr("value") should be("12")
      dayInput.attr("class") should include("dayClass")

      doc.select("label[for=dayId]").first().attr("for") should be("dayId")

      val monthInput = doc.select("input[id=monthId]").first()
      monthInput.attr("id") should be("monthId")
      monthInput.attr("name") should be("monthName")
      monthInput.attr("value") should be("12")
      monthInput.attr("class") should include("monthClass")

      doc.select("label[for=yearId]").first().attr("for") should be("yearId")

      val yearInput = doc.select("input[id=yearId]").first()
      yearInput.attr("id") should be("yearId")
      yearInput.attr("name") should be("yearName")
      yearInput.attr("value") should be("1980")
      yearInput.attr("class") should include("yearClass")

      val noDobReasonTextArea = doc.select("textarea[id=noDobReasonId]").first()
      noDobReasonTextArea.attr("id") should be("noDobReasonId")
      noDobReasonTextArea.attr("name") should be("noDobReasonName")
      noDobReasonTextArea.text() should be("noDobReasonValue")
      noDobReasonTextArea.attr("class") should include("noDobReasonClass")

      if (data.isScot) {
        val range14to15Input = doc.select("input[id=range14to15Id]").first()
        range14to15Input.attr("id") should be("range14to15Id")
        range14to15Input.attr("name") should be("range14to15Name")
        range14to15Input.attr("foo") should include("foo")

        val range16to17Input = doc.select("input[id=range16to17Id]").first()
        range16to17Input.attr("id") should be("range16to17Id")
        range16to17Input.attr("name") should be("range16to17Name")
        range16to17Input.attr("foo") should include("foo")

        val rangeOver18Input = doc.select("input[id=rangeOver18Id]").first()
        rangeOver18Input.attr("id") should be("rangeOver18Id")
        rangeOver18Input.attr("name") should be("rangeOver18Name")
        rangeOver18Input.attr("foo") should include("foo")

      } else {

        val rangeUnder18Input = doc.select("input[id=rangeUnder18Id]").first()
        rangeUnder18Input.attr("id") should be("rangeUnder18Id")
        rangeUnder18Input.attr("name") should be("rangeUnder18Name")
        rangeUnder18Input.attr("foo") should include("foo")

        val range18to70Input = doc.select("input[id=range18to70Id]").first()
        range18to70Input.attr("id") should be("range18to70Id")
        range18to70Input.attr("name") should be("range18to70Name")
        range18to70Input.attr("foo") should include("foo")

        val rangeOver70Input = doc.select("input[id=rangeOver70Id]").first()
        rangeOver70Input.attr("id") should be("rangeOver70Id")
        rangeOver70Input.attr("name") should be("rangeOver70Name")
        rangeOver70Input.attr("foo") should include("foo")
      }

      val rangeDontKnowInput = doc.select("input[id=rangeDontKnowId]").first()
      rangeDontKnowInput.attr("id") should be("rangeDontKnowId")
      rangeDontKnowInput.attr("name") should be("rangeDontKnowName")
      rangeDontKnowInput.attr("foo") should include("foo")

    }
  }
}
