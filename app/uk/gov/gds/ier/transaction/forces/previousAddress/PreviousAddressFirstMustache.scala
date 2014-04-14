package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.model.{MovedHouseOption}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait PreviousAddressFirstMustache extends StepTemplate[InprogressForces] {

  val title = "Have you changed your UK address in the last 12 months?"
  val questionNumber = "3"

  case class PreviousAddressFirstModel(
    question: Question,
    previousYes: Field,
    previousNo: Field
  )

  val mustache = MustacheTemplate("forces/previousAddressFirst") { (form, post, back) =>

    implicit val progressForm = form

    val data = PreviousAddressFirstModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { _.url }.getOrElse(""),
        showBackUrl = back.isDefined,
        number = questionNumber,
        title = title,
        errorMessages = form.globalErrors.map { _.message }),
      previousYes = RadioField(
        key = keys.previousAddress.movedRecently,
        value = MovedHouseOption.Yes.name),
      previousNo = RadioField(
        key = keys.previousAddress.movedRecently,
        value = MovedHouseOption.NotMoved.name)
    )
    MustacheData(data, title)
  }
}
