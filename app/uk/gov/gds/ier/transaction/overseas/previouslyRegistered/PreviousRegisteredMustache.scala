package uk.gov.gds.ier.transaction.overseas.previouslyRegistered

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait PreviousRegisteredMustache extends StepMustache {

  case class PreviouslyRegisteredModel(
      question:Question,
      previouslyRegistered: FieldSet,
      previouslyRegisteredTrue: Field,
      previouslyRegisteredFalse: Field
  )

  def previousRegisteredMustache(
      form:ErrorTransformForm[InprogressOverseas],
      post: Call,
      back: Option[Call]): Html = {
    val prevRegKey = keys.previouslyRegistered.hasPreviouslyRegistered

    val data = PreviouslyRegisteredModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = "Was your previous registration as an overseas voter?"
      ),
      previouslyRegistered = FieldSet(
        classes = if (form(prevRegKey.key).hasErrors) "invalid" else ""
      ),
      previouslyRegisteredTrue = Field(
        name = prevRegKey.key,
        id = prevRegKey.asId("true"),
        attributes = if (form(prevRegKey.key).value == Some("true")) "checked=\"checked\"" else ""
      ),
      previouslyRegisteredFalse = Field(
        name = prevRegKey.key,
        id = prevRegKey.asId("false"),
        attributes = if (form(prevRegKey.key).value == Some("false")) "checked=\"checked\"" else ""
      )
    )
    val content = Mustache.render("overseas/previouslyRegistered", data)
    MainStepTemplate(content, data.question.title)
  }
}
