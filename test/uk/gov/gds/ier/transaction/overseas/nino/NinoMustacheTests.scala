package uk.gov.gds.ier.transaction.overseas.nino

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import scala.Some
import uk.gov.gds.ier.model.Nino
import controllers.step.overseas.routes._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class NinoMustacheTests
  extends FlatSpec
  with Matchers
  with NinoForms
  with NinoMustache
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = ninoForm
    val ninoModel = mustache.data(
      emptyApplicationForm,
      NinoController.post,
      Some(AddressController.get),
      InprogressOverseas()
    ).data.asInstanceOf[NinoModel]

    ninoModel.question.title should be("What is your National Insurance number?")
    ninoModel.question.postUrl should be("/register-to-vote/overseas/nino")
    ninoModel.question.backUrl should be("/register-to-vote/overseas/address")

    ninoModel.nino.value should be("")
    ninoModel.noNinoReason.value should be("")
  }

  it should "progress form with filled applicant nino should produce Mustache Model with nino values present" in {
    val partiallyFilledApplicationForm = ninoForm.fill(InprogressOverseas(
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None))))

    val ninoModel = mustache.data(
      partiallyFilledApplicationForm,
      NinoController.post,
      Some(AddressController.get),
      InprogressOverseas()
    ).data.asInstanceOf[NinoModel]

    ninoModel.question.title should be("What is your National Insurance number?")
    ninoModel.question.postUrl should be("/register-to-vote/overseas/nino")
    ninoModel.question.backUrl should be("/register-to-vote/overseas/address")

    ninoModel.nino.value should be("AB123456C")
    ninoModel.noNinoReason.value should be("")
  }

  it should "progress form with filled applicant no nino reason should produce Mustache Model with no nino reason values present" in {
    val partiallyFilledApplicationForm = ninoForm.fill(InprogressOverseas(
      nino = Some(Nino(
        nino = None,
        noNinoReason = Some("dunno!")))))

    val ninoModel = mustache.data(
      partiallyFilledApplicationForm,
      NinoController.post,
      Some(AddressController.get),
      InprogressOverseas()
    ).data.asInstanceOf[NinoModel]

    ninoModel.question.title should be("What is your National Insurance number?")
    ninoModel.question.postUrl should be("/register-to-vote/overseas/nino")
    ninoModel.question.backUrl should be("/register-to-vote/overseas/address")

    ninoModel.nino.value should be("")
    ninoModel.noNinoReason.value should be("dunno!")
  }

  it should "progress form with validation errors should produce Model with error list present" in {
    val partiallyFilledApplicationForm = ninoForm.fillAndValidate(InprogressOverseas(
      nino = Some(Nino(
        nino = Some("ABCDE"),
        noNinoReason = None))))

    val ninoModel = mustache.data(
      partiallyFilledApplicationForm,
      NinoController.post,
      Some(AddressController.get),
      InprogressOverseas()
    ).data.asInstanceOf[NinoModel]

    ninoModel.question.title should be("What is your National Insurance number?")
    ninoModel.question.postUrl should be("/register-to-vote/overseas/nino")
    ninoModel.question.backUrl should be("/register-to-vote/overseas/address")

    ninoModel.nino.value should be("ABCDE")
    ninoModel.noNinoReason.value should be("")

    ninoModel.question.errorMessages.mkString(", ") should be("Your National Insurance number is not correct")
  }
}