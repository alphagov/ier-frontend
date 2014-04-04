package uk.gov.gds.ier.stubs

import uk.gov.gds.ier.step.{StepController, StepTemplate}
import uk.gov.gds.ier.step.InprogressApplication
import uk.gov.gds.ier.mustache.MustacheRenderer
import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call

trait StubTemplate[T <: InprogressApplication[T]] extends StepTemplate[T] {
    self: StepController[T] =>

  case class StubModel(foo:String)

  val mustache = {
    new MustacheTemplate[T] {
      val data = (
          form:ErrorTransformForm[T],
          postUrl:Call,
          backUrl:Option[Call],
          application:T
      ) => {
        StubModel("foo")
      }
      val mustachePath: String = ""
      val title: String = ""
      val _this = this
      override def apply(
          form:ErrorTransformForm[T],
          postUrl:Call,
          backUrl:Option[Call],
          application:T
      ):MustacheRenderer[T] = {
        new MustacheRenderer[T](_this, form, postUrl, backUrl, application) {
          override def html = templateWithApplication(
            form,
            postUrl,
            backUrl
          )(application)
        }
      }
    }
  }
}
