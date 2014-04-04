package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import controllers.step.ordinary.routes.ConfirmationController
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.stubs.StubTemplate

trait OrdinaryStep
  extends StepController[InprogressOrdinary]
  with StubTemplate[InprogressOrdinary]
  with WithSerialiser
  with WithConfig
  with WithEncryption {
  def factoryOfT() = InprogressOrdinary()
  val confirmationRoute = ConfirmationController.get
}

trait OrdinaryStepWithNewMustache
  extends StepController[InprogressOrdinary]
  with WithSerialiser
  with WithConfig
  with WithEncryption { self: StepTemplate[InprogressOrdinary] =>
  def factoryOfT() = InprogressOrdinary()
  val confirmationRoute = ConfirmationController.get
}

