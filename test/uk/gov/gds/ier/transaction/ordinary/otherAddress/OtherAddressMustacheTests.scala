package uk.gov.gds.ier.transaction.ordinary.otherAddress

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{OtherAddress, InprogressOrdinary}
import uk.gov.gds.ier.model.OtherAddress._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}

class OtherAddressMustacheTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers
  with OtherAddressForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys {

  val serialiser = jsonSerialiser

  behavior of "OtherAddressMustache"
  it should "create model from empty form correctly" in {
    val mustache = new OtherAddressMustache {}
    val form = otherAddressForm

    val result = mustache.transformFormStepToMustacheData(
      form,
      "/some-post-url",
      Some("/some-back-url")
    )
    result.question.postUrl should be("/some-post-url")
    result.question.backUrl should be("/some-back-url")
    result.question.number should be("7 of 11")
    result.question.title should be("Do you also live at a second address?")
    result.question.errorMessages should be(Seq.empty)

    result.hasOtherAddressHome.name should be("otherAddress.hasOtherAddress")
    result.hasOtherAddressHome.id should be("otherAddress_hasOtherAddress_secondHome")
    result.hasOtherAddressHome.attributes should be("")
    result.hasOtherAddressStudent.name should be("otherAddress.hasOtherAddress")
    result.hasOtherAddressStudent.id should be("otherAddress_hasOtherAddress_student")
    result.hasOtherAddressStudent.attributes should be("")
    result.hasOtherAddressNone.name should be("otherAddress.hasOtherAddress")
    result.hasOtherAddressNone.id should be("otherAddress_hasOtherAddress_none")
    result.hasOtherAddressNone.attributes should be("")

    result.hasOtherAddress.classes should be("")
  }

  it should "mark true checkbox when hasOtherAddress = student" in {
    val mustache = new OtherAddressMustache {}
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary(
      otherAddress = Some(OtherAddress(StudentOtherAddress))
    ))

    val result = mustache.transformFormStepToMustacheData(
      form,
      "/some-post-url",
      Some("/some-back-url")
    )
    result.hasOtherAddressStudent.attributes should be("checked=\"checked\"")
    result.hasOtherAddressHome.attributes should be("")
    result.hasOtherAddressNone.attributes should be("")
  }

  it should "mark true checkbox when hasOtherAddress = secondHome" in {
    val mustache = new OtherAddressMustache {}
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary(
      otherAddress = Some(OtherAddress(HomeOtherAddress))
    ))

    val result = mustache.transformFormStepToMustacheData(
      form,
      "/some-post-url",
      Some("/some-back-url")
    )
    result.hasOtherAddressHome.attributes should be("checked=\"checked\"")
    result.hasOtherAddressStudent.attributes should be("")
    result.hasOtherAddressNone.attributes should be("")
  }

  it should "mark false checkbox when hasOtherAddress = none" in {
    val mustache = new OtherAddressMustache {}
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary(
      otherAddress = Some(OtherAddress(NoOtherAddress))
    ))

    val result = mustache.transformFormStepToMustacheData(
      form,
      "/some-post-url",
      Some("/some-back-url")
    )
    result.hasOtherAddressStudent.attributes should be("")
    result.hasOtherAddressHome.attributes should be("")
    result.hasOtherAddressNone.attributes should be("checked=\"checked\"")
  }

  it should "display invalid with emtpy validated form" in {
    val mustache = new OtherAddressMustache {}
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary())

    val result = mustache.transformFormStepToMustacheData(
      form,
      "/some-post-url",
      Some("/some-back-url")
    )
    result.hasOtherAddress.classes should be("invalid")
  }
}
