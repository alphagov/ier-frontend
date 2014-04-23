package uk.gov.gds.ier.transaction.overseas.previouslyRegistered

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait PreviousRegisteredMustache extends StepTemplate[InprogressOverseas] {

  val title = "Was your previous registration as an overseas voter?"

  case class PreviouslyRegisteredModel(
      question:Question,
      previouslyRegistered: FieldSet,
      previouslyRegisteredTrue: Field,
      previouslyRegisteredFalse: Field
  )

  val mustache = MustacheTemplate("overseas/previouslyRegistered") { (form, post) =>

    implicit val progressForm = form

    val prevRegKey = keys.previouslyRegistered.hasPreviouslyRegistered

    val data = PreviouslyRegisteredModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = title
      ),
      previouslyRegistered = FieldSet(
        classes = if (form(prevRegKey).hasErrors) "invalid" else ""
      ),
      previouslyRegisteredTrue = Field(
        name = prevRegKey.key,
        id = prevRegKey.asId("true"),
        attributes = if (form(prevRegKey).value == Some("true")) "checked=\"checked\"" else ""
      ),
      previouslyRegisteredFalse = Field(
        name = prevRegKey.key,
        id = prevRegKey.asId("false"),
        attributes = if (form(prevRegKey).value == Some("false")) "checked=\"checked\"" else ""
      )
    )
    MustacheData(data, title)
  }
}
