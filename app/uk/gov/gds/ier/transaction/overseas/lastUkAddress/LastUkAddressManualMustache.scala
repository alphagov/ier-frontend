package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.step.StepTemplate
import controllers.step.overseas.routes._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait LastUkAddressManualMustache extends StepTemplate[InprogressOverseas] {

  val title = "What was the UK address where you were last registered to vote?"
  val questionNumber = ""

  case class ManualModel (
      question: Question,
      lookupUrl: String,
      postcode: Field,
      maLineOne: Field,
      maLineTwo: Field,
      maLineThree: Field,
      maCity: Field
  )

  val mustache = MustacheTemplate("overseas/lastUkAddressManual") { (form, post) =>

    implicit val progressForm = form

    val data = ManualModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = progressForm.globalErrors.map(_.message)
      ),
      lookupUrl = LastUkAddressController.get.url,
      postcode = TextField(keys.lastUkAddress.postcode),
      maLineOne = TextField(keys.lastUkAddress.manualAddress.lineOne),
      maLineTwo = TextField(keys.lastUkAddress.manualAddress.lineTwo),
      maLineThree = TextField(keys.lastUkAddress.manualAddress.lineThree),
      maCity = TextField(keys.lastUkAddress.manualAddress.city)
    )

    MustacheData(data, title)
  }

}
