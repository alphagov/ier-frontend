package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html
import play.api.mvc.Call

trait MustacheTemplate[T] {
  val mustachePath: String
  val title: String
  val data: (ErrorTransformForm[T],Call,Option[Call],T) => Any

  def apply(
      form:ErrorTransformForm[T],
      postUrl:Call,
      backUrl:Option[Call],
      application:T
  ):MustacheRenderer[T] = {
    new MustacheRenderer(this, form, postUrl, backUrl, application)
  }
}
