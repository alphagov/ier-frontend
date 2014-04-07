package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import controllers.step.crown.routes.AddressController

trait AddressManualMustache extends StepTemplate[InprogressCrown] {

  private def pageTitle(hasUkAddress: Option[String]): String = {
    hasUkAddress match {
      case Some(hasUkAddress) if (hasUkAddress.toBoolean) => "What is your UK address?"
      case _ => "What was your last UK address?"
    }
  }

  val questionNumber = "2"

  case class ManualModel (
    question: Question,
    lookupUrl: String,
    postcode: Field,
    maLineOne: Field,
    maLineTwo: Field,
    maLineThree: Field,
    maCity: Field,
    hasUkAddress: Field
  )

  val mustache = MustacheTemplate("crown/addressLookup") { (form, postUrl, backUrl) =>
    implicit val progressForm = form
  
    val title = pageTitle(form(keys.hasUkAddress).value)

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
      maCity = TextField(keys.address.manualAddress.city),
      hasUkAddress = HiddenField(
        key = keys.hasUkAddress,
        value = form(keys.hasUkAddress).value.getOrElse("")
      )
    )

    MustacheData(data, title)
  }
}

