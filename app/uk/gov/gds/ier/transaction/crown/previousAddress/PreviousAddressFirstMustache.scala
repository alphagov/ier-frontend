package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{MovedHouseOption}
import uk.gov.gds.ier.mustache.StepMustache
import play.api.templates.Html
import uk.gov.gds.ier.transaction.crown.InprogressCrown


trait PreviousAddressFirstMustache extends StepMustache {

  val title = "Have you changed your UK address in the last 12 months?"
  val questionNumber = ""

  case class PreviousAddressFirstModel(
    question: Question,
    previousYes: Field,
    previousNo: Field
  )

  def transformFormStepToMustacheData(
    form: ErrorTransformForm[InprogressCrown],
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
    form:ErrorTransformForm[InprogressCrown],
    postUrl: String,
    backUrl: Option[String]
  ): Html = {
    val data = transformFormStepToMustacheData(form, postUrl, backUrl)
    val content = Mustache.render("crown/previousAddressFirst", data)
    MainStepTemplate(content, title)
  }
}
