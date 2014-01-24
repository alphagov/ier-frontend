package uk.gov.gds.ier.transaction.ordinary.otherAddress

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{OtherAddress, InprogressOrdinary}
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

    val result = mustache.transformFormStepToMustacheData(form, "/some-post-url", Some("/some-back-url"))
    result.question.postUrl should be("/some-post-url")
    result.question.backUrl should be("/some-back-url")
    result.question.number should be("8 of 11")
    result.question.title should be("Do you live at a second UK address where you're registered to vote?")
    result.question.errorMessages should be(Seq.empty)

    result.hasOtherAddressTrue.name should be("otherAddress.hasOtherAddress")
    result.hasOtherAddressTrue.id should be("otherAddress_hasOtherAddress_true")
    result.hasOtherAddressTrue.attributes should be("")
    result.hasOtherAddressFalse.name should be("otherAddress.hasOtherAddress")
    result.hasOtherAddressFalse.id should be("otherAddress_hasOtherAddress_false")
    result.hasOtherAddressFalse.attributes should be("")

    result.hasOtherAddress.classes should be("")
  }

  it should "mark true checkbox when hasOtherAddress = true" in {
    val mustache = new OtherAddressMustache {}
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary(
      otherAddress = Some(OtherAddress(true))
    ))

    val result = mustache.transformFormStepToMustacheData(form, "/some-post-url", Some("/some-back-url"))
    result.hasOtherAddressTrue.attributes should be("checked=\"checked\"")
    result.hasOtherAddressFalse.attributes should be("")
  }

  it should "mark false checkbox when hasOtherAddress = false" in {
    val mustache = new OtherAddressMustache {}
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary(
      otherAddress = Some(OtherAddress(false))
    ))

    val result = mustache.transformFormStepToMustacheData(form, "/some-post-url", Some("/some-back-url"))
    result.hasOtherAddressTrue.attributes should be("")
    result.hasOtherAddressFalse.attributes should be("checked=\"checked\"")
  }

  it should "display invalid with emtpy validated form" in {
    val mustache = new OtherAddressMustache {}
    val form = otherAddressForm.fillAndValidate(InprogressOrdinary())

    val result = mustache.transformFormStepToMustacheData(form, "/some-post-url", Some("/some-back-url"))
    result.hasOtherAddress.classes should be("invalid")
  }
}
