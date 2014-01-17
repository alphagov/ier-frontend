package uk.gov.gds.ier.transaction.overseas.previouslyRegistered

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait PreviousRegisteredMustache extends StepMustache {

  case class PreviouslyRegisteredModel(postUrl:String,
                                       globalErrors:Seq[String],
                                       backUrl:String,
                                       previouslyRegistered: FieldSet,
                                       previouslyRegisteredTrue: Field,
                                       previouslyRegisteredFalse: Field)

  def previousRegisteredMustache(form:ErrorTransformForm[InprogressOverseas],
                                 post: Call,
                                 back: Option[Call]): Html = {
    val prevRegKey = keys.previouslyRegistered.hasPreviouslyRegistered
    val previouslyRegisteredFieldSet = FieldSet(
      classes = if (form(prevRegKey.key).hasErrors) "invalid" else ""
    )
    val previouslyRegisteredTrue = Field(
      name = prevRegKey.key,
      id = prevRegKey.asId("true"),
      attributes = if (form(prevRegKey.key).value == Some("true")) "checked=\"checked\"" else ""
    )
    val previouslyRegisteredFalse = Field(
      name = prevRegKey.key,
      id = prevRegKey.asId("false"),
      attributes = if (form(prevRegKey.key).value == Some("false")) "checked=\"checked\"" else ""
    )
    val data = PreviouslyRegisteredModel(
      post.url,
      form.globalErrors.map{ _.message },
      back.map { call => call.url }.getOrElse(""),
      previouslyRegisteredFieldSet,
      previouslyRegisteredTrue,
      previouslyRegisteredFalse
    )
    Mustache.render("overseas/previouslyRegistered", data)
  }
}
