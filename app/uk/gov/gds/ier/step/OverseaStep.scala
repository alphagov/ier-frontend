package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import controllers.step.overseas.routes.ConfirmationController
import controllers.routes._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait OverseaStep
  extends StepController[InprogressOverseas]
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets { self: StepTemplate[InprogressOverseas] =>
    def factoryOfT() = InprogressOverseas()
    def timeoutPage() = ErrorController.ordinaryTimeout
    val confirmationRoute = ConfirmationController.get
}

