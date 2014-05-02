package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.routes.PreviousAddressPostcodeController
import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PreviousAddressManualMustache extends StepTemplate[InprogressOrdinary] {

  case class ManualModel (
      question: Question,
      lookupUrl: String,
      postcode: Field,
      maLineOne: Field,
      maLineTwo: Field,
      maLineThree: Field,
      maCity: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("ordinary/previousAddressManual") {
    (form, post) =>
    implicit val progressForm = form

    val movedRecently = form(keys.previousAddress.movedRecently).value.map {
      str => MovedHouseOption.parse(str)
    }
    val title = movedRecently match {
      case Some(MovedHouseOption.MovedFromAbroadRegistered) => "What was your last UK address before moving abroad?"
      case _ => "What was your previous address?"
    }

    ManualModel(
      question = Question(
        postUrl = post.url,
        number = "8 of 11",
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = PreviousAddressPostcodeController.get.url,
      postcode = TextField(keys.previousAddress.previousAddress.postcode),
      maLineOne = TextField(keys.previousAddress.previousAddress.manualAddress.lineOne),
      maLineTwo = TextField(keys.previousAddress.previousAddress.manualAddress.lineTwo),
      maLineThree = TextField(keys.previousAddress.previousAddress.manualAddress.lineThree),
      maCity = TextField(keys.previousAddress.previousAddress.manualAddress.city)
    )
  }
}
