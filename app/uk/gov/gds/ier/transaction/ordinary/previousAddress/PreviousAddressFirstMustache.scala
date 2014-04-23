package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{MovedHouseOption}
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary


trait PreviousAddressFirstMustache extends StepTemplate[InprogressOrdinary] {

  val title = "Have you moved out of another address in the last 12 months?"
  val questionNumber = "8 of 11"

  case class PreviousAddressFirstModel(
    question: Question,
    previousYesUk: Field,
    previousYesAbroad: Field,
    previousNo: Field
  )

  val mustache = MustacheTemplate("ordinary/previousAddressFirst") {
    (form, post) =>

    implicit val progressForm = form

    val data = PreviousAddressFirstModel(
      question = Question(
        postUrl = post.url,
        number = questionNumber,
        title = title,
        errorMessages = form.globalErrors.map { _.message }),
      previousYesUk = RadioField(
        key = keys.previousAddress.movedRecently,
        value = MovedHouseOption.MovedFromUk.name
      ),
      previousYesAbroad = RadioField(
        key = keys.previousAddress.movedRecently,
        value = MovedHouseOption.MovedFromAbroad.name
      ),
      previousNo = RadioField(
        key = keys.previousAddress.movedRecently,
        value = MovedHouseOption.NotMoved.name
      )
    )

    MustacheData(data, title)
  }
}

