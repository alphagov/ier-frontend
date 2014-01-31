package uk.gov.gds.ier.transaction.overseas.openRegister

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait OpenRegisterMustache extends StepMustache {

  case class OpenRegisterModel(question:Question,
                               openRegister: Field)

  def openRegisterMustache(form:ErrorTransformForm[InprogressOverseas],
                           post: Call,
                           back: Option[Call]): Html = {

    implicit val progressForm = form

    val data = OpenRegisterModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "9",
        title = "Do you want to include your name and address on the open register?"
      ),
      openRegister = TextField (key = keys.openRegister.optIn)
    )

    val content = Mustache.render("overseas/openRegister", data)
    MainStepTemplate(content, data.question.title)
  }
}
