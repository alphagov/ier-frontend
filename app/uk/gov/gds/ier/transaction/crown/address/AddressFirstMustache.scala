package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressCrown
import uk.gov.gds.ier.mustache.StepMustache
import play.api.templates.Html


trait AddressFirstMustache extends StepMustache {

  val title = "Do you have a UK address?"
  val questionNumber = "2"

  case class AddressFirstModel(
    question: Question,
    hasAddressYes: Field,
    hasAddressNo: Field
  )

  def transformFormStepToMustacheData(
    form: ErrorTransformForm[InprogressCrown],
    postUrl: String,
    backUrl: Option[String]): AddressFirstModel = {
    implicit val progressForm = form

    AddressFirstModel(
      question = Question(
        postUrl = postUrl,
        backUrl = backUrl.getOrElse(""),
        showBackUrl = backUrl.isDefined,
        number = questionNumber,
        title = title,
        errorMessages = form.globalErrors.map { _.message }),
      hasAddressYes = RadioField(
        key = keys.address.hasUkAddress,
        value = "true"),
      hasAddressNo = RadioField(
        key = keys.address.hasUkAddress,
        value = "false")
    )
  }

  def addressFirstStepMustache(
    form:ErrorTransformForm[InprogressCrown],
    postUrl: String,
    backUrl: Option[String]
  ): Html = {
    val data = transformFormStepToMustacheData(form, postUrl, backUrl)
    val content = Mustache.render("crown/addressFirst", data)
    MainStepTemplate(content, title)
  }
}
