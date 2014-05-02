package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PreviousAddressPostcodeMustache extends StepTemplate[InprogressOrdinary] {

  case class PostcodeModel (
      question: Question,
      postcode: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("ordinary/previousAddressPostcode") {
    (form, post) =>
    implicit val progressForm = form

    val movedRecently = form(keys.previousAddress.movedRecently).value.map {
      str => MovedHouseOption.parse(str)
    }

    val title = movedRecently match {
      case Some(MovedHouseOption.MovedFromAbroadRegistered) => "What was your last UK address before moving abroad?"
      case _ => "What was your previous address?"
    }

    PostcodeModel(
      question = Question(
        postUrl = post.url,
        number = "8 of 11",
        title = title,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = TextField(keys.previousAddress.previousAddress.postcode)
    )
  }
}
