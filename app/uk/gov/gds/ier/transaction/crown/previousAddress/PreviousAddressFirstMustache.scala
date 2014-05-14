package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.step.StepTemplate

trait PreviousAddressFirstMustache extends StepTemplate[InprogressCrown] {

  val title = "Have you changed your UK address in the last 12 months?"
  val questionNumber = ""

  case class PreviousAddressFirstModel(
    question: Question,
    previousYesAndLivingThere: Field,
    previousYesAndNotLivingThere: Field,
    previousNo: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/previousAddressFirst") { (form, post) =>

    implicit val progressForm = form

    PreviousAddressFirstModel(
      question = Question(
        postUrl = post.url,
        number = questionNumber,
        title = title,
        errorMessages = form.globalErrors.map { _.message }),
      previousYesAndLivingThere = RadioField(
        key = keys.previousAddress.movedRecently,
        value = MovedHouseOption.YesAndLivingThere.name),
      previousYesAndNotLivingThere = RadioField(
        key = keys.previousAddress.movedRecently,
        value = MovedHouseOption.YesAndNotLivingThere.name),
      previousNo = RadioField(
        key = keys.previousAddress.movedRecently,
        value = MovedHouseOption.NotMoved.name)
    )
  }
}

