package uk.gov.gds.ier.step

import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.session.SessionHandling
import play.api.mvc.{Call, AnyContent, Action, Controller}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.validation.{InProgressForm, ErrorTransformForm}
import play.api.templates.Html


trait ConfirmationStepController[T <: InprogressApplication[T]]
  extends SessionHandling[T]
  with NextStep[T]
  with Controller
  with Logging
  with WithSerialiser
  with WithConfig
  with WithEncryption {

  val routes: Routes
  val validation: ErrorTransformForm[T]
  def template(form:InProgressForm[T]): Html
  def templateWithApplication(form: InProgressForm[T]):T => Html = {
    application:T => template(form)
  }
  def get:Action[AnyContent]
  def post:Action[AnyContent]

  def goToNext(currentState: T) = {
    Redirect(routes.get)
  }
}
