package uk.gov.gds.ier.transaction.ordinary.address

import controllers.step.ordinary.routes.AddressController
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait AddressManualMustache extends StepTemplate[InprogressOrdinary] {
    val title = "What is your address?"
    val questionNumber = "6 of 11"

    case class ManualModel (
        question: Question,
        lookupUrl: String,
        postcode: Field,
        maLineOne: Field,
        maLineTwo: Field,
        maLineThree: Field,
        maCity: Field
    )

    val mustache = MustacheTemplate("ordinary/addressManual") {
      (form, post, back) =>

      implicit val progressForm = form

      val data = ManualModel(
        question = Question(
          postUrl = post.url,
          backUrl = back.map { _.url }.getOrElse(""),
          number = questionNumber,
          title = title,
          errorMessages = progressForm.globalErrors.map(_.message)
        ),
        lookupUrl = AddressController.get.url,
        postcode = TextField(keys.address.postcode),
        maLineOne = TextField(keys.address.manualAddress.lineOne),
        maLineTwo = TextField(keys.address.manualAddress.lineTwo),
        maLineThree = TextField(keys.address.manualAddress.lineThree),
        maCity = TextField(keys.address.manualAddress.city)
      )

      MustacheData(data, title)
    }
}
