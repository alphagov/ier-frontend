package uk.gov.gds.ier.step

import uk.gov.gds.ier.mustache.StepModels
import uk.gov.gds.ier.validation.ErrorTransformForm

trait StepTemplate[T] extends StepModels {

  type Call = play.api.mvc.Call
  type Html = play.api.templates.Html
  type MustacheTemplate[A] = uk.gov.gds.ier.mustache.MustacheTemplate[A]
  type MustacheRenderer[A] = uk.gov.gds.ier.mustache.MustacheRenderer[A]

  val mustache: MustacheTemplate[T]

  object MustacheTemplate {
    def apply (
        title: String,
        mustachePath: String,
        data: (ErrorTransformForm[T], Call, Option[Call], T) => Any
    ) : MustacheTemplate[T] = {
      val _title = title
      val _mustachePath = mustachePath
      val _data = data
      new MustacheTemplate[T] {
        val mustachePath = _mustachePath
        val title = _title
        val data = _data
      }
    }
  }
}
