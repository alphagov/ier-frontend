package uk.gov.gds.ier.transaction.forces.statement

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.forces.statement.StatementForms

class StatementFormTests
  extends FlatSpec
  with Matchers
  with StatementForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers{

  val serialiser = jsonSerialiser

  it should "successfully bind flag of forces member to a valid form" in {
    val js = Json.toJson(
      Map(
        "statement.forcesMember" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.statement.isDefined should be(true)
        val statement = success.statement.get
        statement.memberForcesFlag should be(Some(true))
        statement.partnerForcesFlag should be(None)
      }
    )
  }

  it should "successfully bind flag of forces partner to a valid form" in {
    val js = Json.toJson(
      Map(
        "statement.partnerForcesMember" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.statement.isDefined should be(true)
        val statement = success.statement.get
        statement.partnerForcesFlag should be(Some(true))
        statement.memberForcesFlag should be(None)
      }
    )
  }

  it should "successfully bind all to a valid form" in {
    val js = Json.toJson(
      Map(
        "statement.forcesMember" -> "true",
        "statement.partnerForcesMember" -> "true"
      )
    )
    statementForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.statement.isDefined should be(true)
        val statement = success.statement.get
        statement.partnerForcesFlag should be(Some(true))
        statement.memberForcesFlag should be(Some(true))
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
        "statement.forcesMember" -> "",
        "statement.partnerForcesMember" -> ""
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
