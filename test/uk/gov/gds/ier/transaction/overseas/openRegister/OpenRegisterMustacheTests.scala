package uk.gov.gds.ier.transaction.overseas.openRegister

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import scala.Some
import controllers.step.overseas.routes._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

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
    val openRegisterModel = openRegisterMustache.transformFormStepToMustacheData (emptyApplicationForm, WaysToVoteController.post, Some(AddressController.get))

    openRegisterModel.question.title should be("Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/overseas/ways-to-vote")
    openRegisterModel.question.backUrl should be("/register-to-vote/overseas/address")

    openRegisterModel.openRegister.value should be("false")
    // value=false is part of a selectable UI widget, the 'empty model' is a bit a misnomer
  }

  it should "progress form with open register marked should produce Mustache Model with open register value present (true)" in {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressOverseas(
        openRegisterOptin = Some(true)
      )
    )
    val openRegisterModel = openRegisterMustache.transformFormStepToMustacheData (partiallyFilledApplicationForm, WaysToVoteController.post, Some(AddressController.get))

    openRegisterModel.question.title should be("Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/overseas/ways-to-vote")
    openRegisterModel.question.backUrl should be("/register-to-vote/overseas/address")

    openRegisterModel.openRegister.attributes should be("")
  }

  it should "progress form with open register marked should produce Mustache Model with open register value present (false)" in {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressOverseas(
        openRegisterOptin = Some(false)
      )
    )
    val openRegisterModel = openRegisterMustache.transformFormStepToMustacheData (partiallyFilledApplicationForm, WaysToVoteController.post, Some(AddressController.get))

    openRegisterModel.question.title should be("Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/overseas/ways-to-vote")
    openRegisterModel.question.backUrl should be("/register-to-vote/overseas/address")

    openRegisterModel.openRegister.attributes should be("checked=\"checked\"")
  }
}
