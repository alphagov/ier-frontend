package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html
import play.api.mvc.Call

case class MustacheData(data: Any, title:String)

trait MustacheTemplate[T] {
  val mustachePath: String
  val data: (ErrorTransformForm[T],Call,T) => MustacheData

  def apply(
      form:ErrorTransformForm[T],
      postUrl:Call,
      application:T
  ):MustacheRenderer[T] = {
    new MustacheRenderer(this, form, postUrl, application)
  }
}

trait MustacheTemplateFactories[T] {
  class MustacheTemplateMaker[T](name:String) {
    def apply(
        data: (ErrorTransformForm[T], Call, T) => MustacheData
    ): MustacheTemplate[T] = {
      makeMustacheTemplate(name, data)
    }

    def apply(
        data: (ErrorTransformForm[T], Call) => MustacheData
    ): MustacheTemplate[T] = {
      makeMustacheTemplate(
        name,
        (form, post, application) => data(form, post)
      )
    }

    def apply(
        data: (ErrorTransformForm[T]) => MustacheData
    ) : MustacheTemplate[T] = {
      makeMustacheTemplate(
        name,
        (form, post, application) => data(form)
      )
    }

    private def makeMustacheTemplate(
        mustachePath:String,
        data: (ErrorTransformForm[T], Call, T) => MustacheData
    ) : MustacheTemplate[T] = {
      val _mustachePath = mustachePath
      val _data = data
      new MustacheTemplate[T] {
        val mustachePath = _mustachePath
        val data = _data
      }
    }
  }
  object MustacheTemplate {
    def apply(mustachePath:String):MustacheTemplateMaker[T] = {
      new MustacheTemplateMaker[T](mustachePath)
    }
  }
}
