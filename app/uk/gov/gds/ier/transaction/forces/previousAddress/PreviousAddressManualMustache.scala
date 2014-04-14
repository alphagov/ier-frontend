package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.InprogressForces
import controllers.step.forces.routes.PreviousAddressPostcodeController

trait PreviousAddressManualMustache extends StepTemplate[InprogressForces] {

  val title = "What was your previous UK address?"
  val questionNumber = "3"

  case class ManualModel (
    question: Question,
    lookupUrl: String,
    postcode: Field,
    maLineOne: Field,
    maLineTwo: Field,
    maLineThree: Field,
    maCity: Field)

  val mustache = MustacheTemplate("forces/previousAddressManual") {
    (form, postUrl, backUrl) =>

    implicit val progressForm = form

    val data = ManualModel(
      question = Question(
        postUrl = postUrl.url,
        backUrl = backUrl.map { _.url }.getOrElse(""),
        number = questionNumber,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = PreviousAddressPostcodeController.get.url,
      postcode = TextField(keys.previousAddress.postcode),
      maLineOne = TextField(keys.previousAddress.manualAddress.lineOne),
      maLineTwo = TextField(keys.previousAddress.manualAddress.lineTwo),
      maLineThree = TextField(keys.previousAddress.manualAddress.lineThree),
      maCity = TextField(keys.previousAddress.manualAddress.city)
    )

    MustacheData(data, title)
  }
}

