package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.transaction.crown.{InprogressCrown, WithCrownControllers}
import controllers.step.crown.routes.ConfirmationController
import controllers.routes.ErrorController

trait CrownStep
  extends StepController[InprogressCrown]
  with WithCrownControllers
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets { self: StepTemplate[InprogressCrown] =>
    val manifestOfT = manifest[InprogressCrown]
    def factoryOfT() = InprogressCrown()
    def timeoutPage() = ErrorController.crownTimeout
    val confirmationRoute = ConfirmationController.get
}

