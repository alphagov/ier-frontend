package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait OverseaStep
  extends StepController[InprogressOverseas]
  with WithSerialiser
  with WithConfig
  with WithEncryption {
    def factoryOfT() = InprogressOverseas()
    val confirmationRoute = controllers.step.overseas.routes.ConfirmationController.get
}

