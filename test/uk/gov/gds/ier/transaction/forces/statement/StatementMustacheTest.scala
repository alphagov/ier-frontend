package uk.gov.gds.ier.transaction.forces.statement

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{Statement}
import scala.Some
import controllers.step.forces.routes._
import uk.gov.gds.ier.transaction.forces.InprogressForces


class StatementMustacheTest
  extends FlatSpec
  with Matchers
  with ErrorMessages
  with FormKeys
  with StatementMustache
  with StatementForms
  with TestHelpers {

  val statementMustache = new StatementMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = statementForm

    val statementhModel =mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/forces/statement"),
      InprogressForces()
    ).data.asInstanceOf[StatementModel]

    statementhModel.question.title should be("Which of these statements applies to you?")
    statementhModel.question.postUrl should be("/register-to-vote/forces/statement")

    statementhModel.statementFieldSet.classes should be("")
    statementhModel.statementMemberForcesCheckbox.attributes should be("")
    statementhModel.statementPartnerForcesCheckbox.attributes should be("")
  }

  it should "fully filled applicant statement should produce Mustache Model with statement values present" in {
    val filledForm = statementForm.fillAndValidate(InprogressForces(
      statement = Some(Statement(memberForcesFlag = Some(true), partnerForcesFlag = Some(true)))))

    val statementModel = mustache.data(
      filledForm,
      Call("POST", "/register-to-vote/forces/statement"),
      InprogressForces()
    ).data.asInstanceOf[StatementModel]

    statementModel.question.title should be("Which of these statements applies to you?")
    statementModel.question.postUrl should be("/register-to-vote/forces/statement")

    statementModel.statementFieldSet.classes should be("")
    statementModel.statementMemberForcesCheckbox.attributes should be("checked=\"checked\"")
    statementModel.statementPartnerForcesCheckbox.attributes should be("checked=\"checked\"")
  }
}
