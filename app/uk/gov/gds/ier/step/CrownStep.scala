package uk.gov.gds.ier.step

import uk.gov.gds.ier.model.InprogressCrown
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}

trait CrownStep
  extends StepController[InprogressCrown]
  with WithSerialiser
  with WithConfig
  with WithEncryption {
    def factoryOfT() = InprogressCrown()
    val confirmationRoute = controllers.step.crown.routes.ConfirmationController.get
}

