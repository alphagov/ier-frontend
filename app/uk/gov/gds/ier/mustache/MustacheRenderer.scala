package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html
import play.api.mvc.Call

class MustacheRenderer[T](
    template: MustacheTemplate[T],
    form: ErrorTransformForm[T],
    postUrl: Call,
    backUrl: Option[Call],
    application: T
) extends StepMustache {

  def html:Html = {
    val model = template.data(form, postUrl, backUrl, application)
    val content = Mustache.render(template.mustachePath, model.data)
    MainStepTemplate(content, model.title)
  }
}
