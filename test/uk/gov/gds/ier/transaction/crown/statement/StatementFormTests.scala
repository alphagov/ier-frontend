package uk.gov.gds.ier.transaction.crown.statement

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.test.TestHelpers

class StatementFormTests
  extends FlatSpec
  with Matchers
  with StatementForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers{

  val serialiser = jsonSerialiser

  it should "successfully bind flag of member to a valid form" in {
    val js = Json.toJson(
      Map(
        "statement.crownMember" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.statement.isDefined should be(true)
        val statement = success.statement.get
        statement.crownMember should be(Some(true))
        statement.partnerCrownMember should be(None)
        statement.britishCouncilMember should be(None)
        statement.partnerBritishCouncilMember should be(None)
      }
    )
  }

  it should "successfully bind flag of partner to a valid form" in {
    val js = Json.toJson(
      Map(
        "statement.partnerCrownMember" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.statement.isDefined should be(true)
        val statement = success.statement.get
        statement.crownMember should be(None)
        statement.partnerCrownMember should be(Some(true))
        statement.britishCouncilMember should be(None)
        statement.partnerBritishCouncilMember should be(None)
      }
    )
  }

  it should "successfully bind member and partner to a valid form" in {
    val js = Json.toJson(
      Map(
        "statement.crownMember" -> "true",
        "statement.partnerBritishCouncilMember" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.statement.isDefined should be(true)
        val statement = success.statement.get
        statement.crownMember should be(Some(true))
        statement.partnerCrownMember should be(None)
        statement.britishCouncilMember should be(None)
        statement.partnerBritishCouncilMember should be(Some(true))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    statementForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("statement") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "statement.crownMember" -> "",
        "statement.partnerCrownMember" -> "",
        "statement.britishCouncilMember" -> "",
        "statement.partnerBritishCouncilMember" -> ""
      )
    )
    statementForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("statement") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

}
