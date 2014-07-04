package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import controllers.step.forces.routes.ConfirmationController
import controllers.routes.ErrorController

trait ForcesStep
  extends StepController[InprogressForces]
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets{ self: StepTemplate[InprogressForces] =>
    val manifestOfT = manifest[InprogressForces]
    def factoryOfT() = InprogressForces()
    def timeoutPage() = ErrorController.forcesTimeout
    val confirmationRoute = ConfirmationController.get
}

