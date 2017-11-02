package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PreviousAddressPostcodeMustache extends StepTemplate[InprogressOrdinary] {

  case class PostcodeModel (
      question: Question,
      postcode: Field
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/previousAddressPostcode") { implicit lang =>
    (form, post) =>
    implicit val progressForm = form

    val movedRecently = form(keys.previousAddress.movedRecently).value.map {
      str => MovedHouseOption.parse(str)
    }

    val title = movedRecently match {
      case Some(MovedHouseOption.MovedFromAbroadRegistered) => Messages("title") +" "+ Messages("ordinary_previousAddress_yesFromAbroadWasRegistered_title")
      case _ => Messages("title") +" "+ Messages("ordinary_previousAddress_yesFromUk_title")
    }

      val newQuestion = movedRecently match {
        case Some(MovedHouseOption.MovedFromAbroadRegistered) => Messages("ordinary_previousAddress_yesFromAbroadWasRegistered_title")
        case _ => Messages("ordinary_previousAddress_yesFromUk_title")
      }

    PostcodeModel(
      question = Question(
        postUrl = post.url,
        title = title,
        newQuestion = newQuestion,
        errorMessages = Messages.translatedGlobalErrors(form)
      ),
      postcode = TextField(keys.previousAddress.previousAddress.postcode)
    )
  }
}
