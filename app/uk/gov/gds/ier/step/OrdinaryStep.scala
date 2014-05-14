package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import controllers.step.ordinary.routes.ConfirmationController
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait OrdinaryStep
  extends StepController[InprogressOrdinary]
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets { self: StepTemplate[InprogressOrdinary] =>
  def factoryOfT() = InprogressOrdinary()
  val confirmationRoute = ConfirmationController.get
}

