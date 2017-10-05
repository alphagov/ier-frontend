package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.step.StepTemplate

trait PreviousAddressFirstMustache extends StepTemplate[InprogressCrown] {

  val title = "www.gov.uk/register-to-vote - Have you changed your UK address in the last 12 months?"
  val newQuestion = "Have you changed your UK address in the last 12 months?"

  case class PreviousAddressFirstModel(
    question: Question,
    previousYes: Field,
    previousNo: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/previousAddressFirst") { (form, post) =>

    implicit val progressForm = form

    PreviousAddressFirstModel(
      question = Question(
        postUrl = post.url,
        title = title,
        newQuestion = newQuestion,
        errorMessages = form.globalErrors.map { _.message }),
      previousYes = RadioField(
        key = keys.previousAddress.movedRecently,
        value = MovedHouseOption.Yes.name),
      previousNo = RadioField(
        key = keys.previousAddress.movedRecently,
        value = MovedHouseOption.NotMoved.name)
    )
  }
}

