package uk.gov.gds.ier.transaction.ordinary.openRegister

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test._
import scala.Some
import controllers.step.ordinary.routes._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class OpenRegisterMustacheTests
  extends FlatSpec
  with Matchers
  with OpenRegisterForms
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with WithMockConfig
  with WithMockRemoteAssets
  with OpenRegisterMustache {

  it should "empty progress form should produce empty Model" in runningApp {
    val emptyApplicationForm = openRegisterForm
    val openRegisterModel = mustache.data(
      emptyApplicationForm,
      PostalVoteController.post,
      InprogressOrdinary()
    ).asInstanceOf[OpenRegisterModel]

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/postal-vote")

    openRegisterModel.openRegister.value should be("false")
  }

  it should "progress form with open register marked should produce Mustache Model with open " +
    "register value present (true)" in runningApp {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressOrdinary(
        openRegisterOptin = Some(true)
      )
    )
    val openRegisterModel = mustache.data(
      partiallyFilledApplicationForm,
      PostalVoteController.post,
      InprogressOrdinary()
    ).asInstanceOf[OpenRegisterModel]

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/postal-vote")

    openRegisterModel.openRegister.attributes should be("")
  }

  it should "progress form with open register marked should produce Mustache Model with open " +
    "register value present (false)" in runningApp {
    val partiallyFilledApplicationForm = openRegisterForm.fill(
      InprogressOrdinary(
        openRegisterOptin = Some(false)
      )
    )
    val openRegisterModel = mustache.data(
      partiallyFilledApplicationForm,
      PostalVoteController.post,
      InprogressOrdinary()
    ).asInstanceOf[OpenRegisterModel]

    openRegisterModel.question.title should be(
        "Do you want to include your name and address on the open register?")
    openRegisterModel.question.postUrl should be("/register-to-vote/postal-vote")

    openRegisterModel.openRegister.attributes should be("checked=\"checked\"")
  }
}
