package uk.gov.gds.ier.step

import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.session.SessionHandling
import play.api.mvc.{AnyContent, Action, Controller}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.validation.{InProgressForm, ErrorTransformForm}
import play.api.templates.Html


trait ConfirmationController[T <: InprogressApplication[T]]
  extends SessionHandling[T]
  with Controller
  with Logging
  with WithSerialiser
  with WithConfig
  with WithEncryption {

  val validation: ErrorTransformForm[T]
  def template(form:InProgressForm[T]): Html
  def get:Action[AnyContent]
  def post:Action[AnyContent]
}