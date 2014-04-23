package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait AddressFirstMustache extends StepTemplate[InprogressCrown] {

  val pageTitle = "Do you have a UK address?"
  val questionNumber = "2"

  case class AddressFirstModel(
    question: Question,
    hasAddressYes: Field,
    hasAddressNo: Field
  )

  val mustache = MustacheTemplate("crown/addressFirst") { (form, postUrl) =>
    implicit val progressForm = form
  
    val data = AddressFirstModel(
      question = Question(
        postUrl = postUrl.url,
        number = questionNumber,
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
      hasAddressYes = RadioField(
        key = keys.address.hasUkAddress,
        value = "true"),
      hasAddressNo = RadioField(
        key = keys.address.hasUkAddress,
        value = "false")
    )

    MustacheData(data, pageTitle)
  }
}

