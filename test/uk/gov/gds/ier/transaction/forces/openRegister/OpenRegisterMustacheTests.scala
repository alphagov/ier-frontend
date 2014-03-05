package uk.gov.gds.ier.transaction.forces.openRegister

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import scala.Some
import uk.gov.gds.ier.model.InprogressForces
import controllers.step.forces.routes._

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
      OpenRegisterController.post,
      Some(ContactAddressController.get))

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/forces/open-register")
    openRegisterModel.question.backUrl should be("/register-to-vote/forces/contact-address")

    openRegisterModel.openRegister.value should be("false")
  }

  it should "progress form with open register marked should produce Mustache Model with open " +
    "register value present (true)" in {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressForces(
        openRegisterOptin = Some(true)
      )
    )
    val openRegisterModel = openRegisterMustache.transformFormStepToMustacheData (
      partiallyFilledApplicationForm,
      OpenRegisterController.post,
      Some(ContactAddressController.get))

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/forces/open-register")
    openRegisterModel.question.backUrl should be("/register-to-vote/forces/contact-address")

    openRegisterModel.openRegister.attributes should be("")
  }

  it should "progress form with open register marked should produce Mustache Model with open " +
    "register value present (false)" in {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressForces(
        openRegisterOptin = Some(false)
      )
    )
    val openRegisterModel = openRegisterMustache.transformFormStepToMustacheData (
      partiallyFilledApplicationForm,
      OpenRegisterController.post,
      Some(ContactAddressController.get))

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/forces/open-register")
    openRegisterModel.question.backUrl should be("/register-to-vote/forces/contact-address")

    openRegisterModel.openRegister.attributes should be("checked=\"checked\"")
  }
}