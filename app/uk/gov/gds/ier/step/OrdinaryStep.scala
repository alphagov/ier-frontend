package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import controllers.step.ordinary.routes.ConfirmationController
import controllers.routes._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait OrdinaryStep
  extends StepController[InprogressOrdinary]
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets { self: StepTemplate[InprogressOrdinary] =>
  val manifestOfT = manifest[InprogressOrdinary]
  def factoryOfT() = InprogressOrdinary()
  def timeoutPage() = ErrorController.ordinaryTimeout
  val confirmationRoute = ConfirmationController.get
}

