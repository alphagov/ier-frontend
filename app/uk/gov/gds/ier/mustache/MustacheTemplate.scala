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

trait MustacheTemplateFactories[T] {
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
  
    def apply (
        title: String,
        mustachePath: String,
        data: (ErrorTransformForm[T]) => Any
    ) : MustacheTemplate[T] = {
      this.apply(
        title = title,
        mustachePath = mustachePath,
        data = (form, post, back, application) => data(form)
      )
    }
  
    def apply (
        title: String,
        mustachePath: String,
        data: (ErrorTransformForm[T], Call, Option[Call]) => Any
    ) : MustacheTemplate[T] = {
      this.apply(
        title = title,
        mustachePath = mustachePath,
        data = (form, post, back, application) => data(form, post, back)
      )
    }
  
    def apply (
        title: String,
        mustachePath: String,
        data: (ErrorTransformForm[T], Call) => Any
    ) : MustacheTemplate[T] = {
      this.apply(
        title = title,
        mustachePath = mustachePath,
        data = (form, post, back, application) => data(form, post)
      )
    }
  }
}
