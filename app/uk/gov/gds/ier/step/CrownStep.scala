package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait CrownStep
  extends StepController[InprogressCrown]
  with WithSerialiser
  with WithConfig
  with WithEncryption {
    def factoryOfT() = InprogressCrown()
    val confirmationRoute = controllers.step.crown.routes.ConfirmationController.get
}

