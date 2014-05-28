package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.model.HasAddressOption

trait AddressFirstMustache extends StepTemplate[InprogressForces] {

  val pageTitle = "Do you have a UK address?"
  val questionNumber = "2"

  case class AddressFirstModel(
    question: Question,
    hasAddressYes: Field,
    hasAddressNo: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/addressFirst") { (form, postUrl, backUrl) =>
    implicit val progressForm = form

    AddressFirstModel(
      question = Question(
        postUrl = postUrl.url,
        number = questionNumber,
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
      hasAddressYes = RadioField(
        key = keys.address.hasAddress,
        value = HasAddressOption.YesAndLivingThere.name
      ),
      hasAddressNo = RadioField(
        key = keys.address.hasAddress,
        value = HasAddressOption.No.name
      )
    )
  }
}

