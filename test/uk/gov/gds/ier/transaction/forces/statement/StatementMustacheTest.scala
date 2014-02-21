package uk.gov.gds.ier.transaction.forces.statement

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{Statement, InprogressForces}
import scala.Some
import controllers.step.forces.routes._


class StatementMustacheTest
  extends FlatSpec
  with Matchers
  with StatementForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val statementMustache = new StatementMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = statementForm
    
    val statementhModel = statementMustache.transformFormStepToMustacheData(
      emptyApplicationForm, StatementController.get, None)

    statementhModel.question.title should be("Which of these statements applies to you?")
    statementhModel.question.postUrl should be("/register-to-vote/forces/statement")
    statementhModel.question.backUrl should be("")

    statementhModel.statementFieldSet.classes should be("")
    statementhModel.statementMemberForcesCheckbox.attributes should be("")
    statementhModel.statementPartnerForcesCheckbox.attributes should be("")
  }

  it should "fully filled applicant statement should produce Mustache Model with statement values present" in {
    val filledForm = statementForm.fillAndValidate(InprogressForces(
      statement = Some(Statement(memberForcesFlag = Some(true), partnerForcesFlag = Some(true)))))
      
    val statementModel = statementMustache.transformFormStepToMustacheData(
      filledForm, StatementController.get, None)

    statementModel.question.title should be("Which of these statements applies to you?")
    statementModel.question.postUrl should be("/register-to-vote/forces/statement")
    statementModel.question.backUrl should be("")

    statementModel.statementFieldSet.classes should be("")
    statementModel.statementMemberForcesCheckbox.attributes should be("checked=\"checked\"")
    statementModel.statementPartnerForcesCheckbox.attributes should be("checked=\"checked\"")
  }
}
