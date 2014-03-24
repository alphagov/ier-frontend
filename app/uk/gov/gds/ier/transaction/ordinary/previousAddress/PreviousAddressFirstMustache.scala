package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{InprogressOrdinary, MovedHouseOption}
import uk.gov.gds.ier.mustache.StepMustache
import play.api.templates.Html


trait PreviousAddressFirstMustache extends StepMustache {

  val title = "Have you moved from another UK address in the last 12 months?"
  val questionNumber = "8 of 11"

  case class PreviousAddressFirstModel(
    question: Question,
    previousYes: Field,
    previousNo: Field
  )

  def transformFormStepToMustacheData(
    form: ErrorTransformForm[InprogressOrdinary],
    postUrl: String,
    backUrl: Option[String]): PreviousAddressFirstModel = {
    implicit val progressForm = form

    PreviousAddressFirstModel(
      question = Question(
        postUrl = postUrl,
        backUrl = backUrl.getOrElse(""),
        showBackUrl = backUrl.isDefined,
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
  }

  def previousAddressFirstStepMustache(
    form:ErrorTransformForm[InprogressOrdinary],
    postUrl: String,
    backUrl: Option[String]
  ): Html = {
    val data = transformFormStepToMustacheData(form, postUrl, backUrl)
    val content = Mustache.render("ordinary/previousAddressFirst", data)
    MainStepTemplate(content, title)
  }
}
