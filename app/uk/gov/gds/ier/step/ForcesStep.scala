package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import controllers.step.forces.routes.ConfirmationController

trait ForcesStep
  extends StepController[InprogressForces]
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets{ self: StepTemplate[InprogressForces] =>
    def factoryOfT() = InprogressForces()
    val confirmationRoute = ConfirmationController.get
}

