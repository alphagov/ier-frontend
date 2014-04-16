package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.step.StepTemplate
import controllers.step.forces.routes.AddressController
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait AddressManualMustache extends StepTemplate[InprogressForces] {

  val questionNumber = "2"

  case class ManualModel (
     question: Question,
     lookupUrl: String,
     postcode: Field,
     maLineOne: Field,
     maLineTwo: Field,
     maLineThree: Field,
     maCity: Field)

  val mustache = MustacheTemplate("forces/addressManual") { (form, postUrl, backUrl) =>
    implicit val progressForm = form
  
    val title = "What is your UK address?"

    val data = ManualModel(
      question = Question(
        postUrl = postUrl.url,
        backUrl = backUrl.map { _.url }.getOrElse(""),
        number = questionNumber,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = AddressController.get.url,
      postcode = TextField(keys.address.postcode),
      maLineOne = TextField(keys.address.manualAddress.lineOne),
      maLineTwo = TextField(keys.address.manualAddress.lineTwo),
      maLineThree = TextField(keys.address.manualAddress.lineThree),
      maCity = TextField(keys.address.manualAddress.city))

    MustacheData(data, title)
  }
}
