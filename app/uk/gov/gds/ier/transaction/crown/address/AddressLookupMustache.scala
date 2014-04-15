package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait AddressLookupMustache extends StepTemplate[InprogressCrown] {

  private def pageTitle(hasUkAddress: Option[String]): String = {
    hasUkAddress match {
      case Some(hasUkAddress) if (!hasUkAddress.isEmpty && hasUkAddress.toBoolean) => "What is your UK address?"
      case _ => "What was your last UK address?"
    }
  }

  val questionNumber = "2"
  case class LookupModel (
      question: Question,
      postcode: Field,
      hasUkAddress: Field
  )

  val mustache = MustacheTemplate("crown/addressLookup") { (form, postUrl) =>
    implicit val progressForm = form
  
    val title = pageTitle(form(keys.hasUkAddress).value)

    val data = LookupModel(
      question = Question(
        postUrl = postUrl.url,
        number = questionNumber,
        title = title,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = Field(
        id = keys.address.postcode.asId(),
        name = keys.address.postcode.key,
        value = form(keys.address.postcode).value.getOrElse(""),
        classes = if (form(keys.address.postcode).hasErrors) {
          "invalid"
        } else {
          ""
        }
      ),
      hasUkAddress = HiddenField(
        key = keys.hasUkAddress,
        value = form(keys.hasUkAddress).value.getOrElse("")
      )
    )

    MustacheData(data, title)
  }
}

