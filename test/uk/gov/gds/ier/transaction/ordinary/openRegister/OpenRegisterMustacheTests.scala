package uk.gov.gds.ier.transaction.ordinary.openRegister

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import scala.Some
import uk.gov.gds.ier.model.InprogressOrdinary
import controllers.step.ordinary.routes._

class OpenRegisterMustacheTests
  extends FlatSpec
  with Matchers
  with OpenRegisterForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val openRegisterMustache = new OpenRegisterMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = openRegisterForm
    val openRegisterModel = openRegisterMustache.transformFormStepToMustacheData (
      emptyApplicationForm,
      PostalVoteController.post,
      Some(PreviousAddressFirstController.get))

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/postal-vote")
    openRegisterModel.question.backUrl should be("/register-to-vote/previous-address")

    openRegisterModel.openRegister.value should be("false")
  }

  it should "progress form with open register marked should produce Mustache Model with open " +
    "register value present (true)" in {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressOrdinary(
        openRegisterOptin = Some(true)
      )
    )
    val openRegisterModel = openRegisterMustache.transformFormStepToMustacheData (
      partiallyFilledApplicationForm,
      PostalVoteController.post,
      Some(PreviousAddressFirstController.get))

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/postal-vote")
    openRegisterModel.question.backUrl should be("/register-to-vote/previous-address")

    openRegisterModel.openRegister.attributes should be("")
  }

  it should "progress form with open register marked should produce Mustache Model with open " +
    "register value present (false)" in {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressOrdinary(
        openRegisterOptin = Some(false)
      )
    )
    val openRegisterModel = openRegisterMustache.transformFormStepToMustacheData (
      partiallyFilledApplicationForm,
      PostalVoteController.post,
      Some(PreviousAddressFirstController.get))

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/postal-vote")
    openRegisterModel.question.backUrl should be("/register-to-vote/previous-address")

    openRegisterModel.openRegister.attributes should be("checked=\"checked\"")
  }
}
