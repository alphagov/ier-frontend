package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait ForcesStep
  extends StepController[InprogressForces]
  with WithSerialiser
  with WithConfig
  with WithEncryption {
    def factoryOfT() = InprogressForces()
    val confirmationRoute = controllers.step.forces.routes.ConfirmationController.get
}

