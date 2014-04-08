package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.stubs.StubTemplate

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html

trait CrownStep
  extends StepController[InprogressCrown]
  with WithSerialiser
  with WithConfig
  with WithEncryption { self: StepTemplate[InprogressCrown] =>
    def factoryOfT() = InprogressCrown()
    val confirmationRoute = controllers.step.crown.routes.ConfirmationController.get

    def template(
        form: ErrorTransformForm[InprogressCrown],
        call:Call,
        backUrl: Option[Call]): Html = {
      Html.empty
    }
}

