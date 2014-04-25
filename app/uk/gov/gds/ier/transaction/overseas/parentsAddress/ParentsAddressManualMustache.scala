package uk.gov.gds.ier.transaction.overseas.parentsAddress

import uk.gov.gds.ier.step.StepTemplate
import controllers.step.overseas.routes._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait ParentsAddressManualMustache extends StepTemplate[InprogressOverseas] {

  val title = "What was your parent or guardian's last UK address?"
  val questionNumber = ""

  case class ManualModel (
      question: Question,
      lookupUrl: String,
      postcode: Field,
      maLineOne: Field,
      maLineTwo: Field,
      maCity: Field,
      maCounty: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/parentsAddressManual") { (form, post) =>

    implicit val progressForm = form

    ManualModel(
      question = Question(
        postUrl = post.url,
        number = questionNumber,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = ParentsAddressController.get.url,
      postcode = TextField(keys.parentsAddress.postcode),
      maLineOne = TextField(keys.parentsAddress.manualAddress.lineOne),
      maLineTwo = TextField(keys.parentsAddress.manualAddress.lineTwo),
      maCity = TextField(keys.parentsAddress.manualAddress.city),
      maCounty = TextField(keys.parentsAddress.manualAddress.country)
    )
  }
}
