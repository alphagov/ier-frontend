package uk.gov.gds.ier.transaction.ordinary.soleOccupancy

import uk.gov.gds.ier.model.{PartialAddress, PartialManualAddress, SoleOccupancyOption}
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class SoleOccupancyMustacheTest
  extends MustacheTestSuite
  with SoleOccupancyForms
  with SoleOccupancyMustache {

  it should "empty progress form should produce empty Model" in runningApp {
    val emptyApplicationForm = soleOccupancyForm
    val soleOccupancyModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/foo/sole-occupancy"),
      InprogressOrdinary()
    ).asInstanceOf[SoleOccupancyModel]

    soleOccupancyModel.question.title should be("Are you the only person aged 16 or over living at this address? (optional)")
    soleOccupancyModel.question.postUrl should be("/foo/sole-occupancy")

    soleOccupancyModel.soleOccupancyYes.attributes should be("")
    soleOccupancyModel.soleOccupancyNo.attributes should be("")
    soleOccupancyModel.soleOccupancyNotSure.attributes should be("")
    soleOccupancyModel.soleOccupancySkipThisQuestion.attributes should be("")

    soleOccupancyModel.addressLine should be("")
    soleOccupancyModel.postcode should be("")
    soleOccupancyModel.displayAddress should be(false)

  }

  it should "display address with address line" in runningApp {
    val partiallyFilledApplicationForm = soleOccupancyForm.fill(InprogressOrdinary(
      soleOccupancy = Some(SoleOccupancyOption(true, "yes")),
      address = Some(PartialAddress(
        addressLine = Some("21 Jump Street, Bangor"),
        manualAddress = None,
        postcode = "BT1 2AB",
        uprn = None))
    ))

    val soleOccupancyModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/foo/sole-occupancy"),
      InprogressOrdinary()
    ).asInstanceOf[SoleOccupancyModel]

    soleOccupancyModel.question.title should be("Are you the only person aged 16 or over living at this address? (optional)")
    soleOccupancyModel.question.postUrl should be("/foo/sole-occupancy")

    soleOccupancyModel.soleOccupancyYes.attributes should be("checked=\"checked\"")
    soleOccupancyModel.soleOccupancyNo.attributes should be("")
    soleOccupancyModel.soleOccupancyNotSure.attributes should be("")
    soleOccupancyModel.soleOccupancySkipThisQuestion.attributes should be("")

    soleOccupancyModel.addressLine should be("21 Jump Street, Bangor")
    soleOccupancyModel.postcode should be("BT1 2AB")
    soleOccupancyModel.displayAddress should be(true)
  }

  it should "display address with manual address" in runningApp {
    val partiallyFilledApplicationForm = soleOccupancyForm.fill(InprogressOrdinary(
      soleOccupancy = Some(SoleOccupancyOption(true, "yes")),
      address = Some(PartialAddress(
        addressLine = None,
        manualAddress = Some(PartialManualAddress(
          lineOne = Some("21 Jump Street"),
          lineThree = Some("Bangor")
        )),
        postcode = "BT1 2AB",
        uprn = None))
    ))

    val soleOccupancyModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/foo/sole-occupancy"),
      InprogressOrdinary()
    ).asInstanceOf[SoleOccupancyModel]

    soleOccupancyModel.question.title should be("Are you the only person aged 16 or over living at this address? (optional)")
    soleOccupancyModel.question.postUrl should be("/foo/sole-occupancy")

    soleOccupancyModel.soleOccupancyYes.attributes should be("checked=\"checked\"")
    soleOccupancyModel.soleOccupancyNo.attributes should be("")
    soleOccupancyModel.soleOccupancyNotSure.attributes should be("")
    soleOccupancyModel.soleOccupancySkipThisQuestion.attributes should be("")

    soleOccupancyModel.addressLine should be("21 Jump Street, Bangor")
    soleOccupancyModel.postcode should be("BT1 2AB")
    soleOccupancyModel.displayAddress should be(true)
  }
}
