package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.stubs.StubTemplate
import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html

trait ForcesStep
  extends StepController[InprogressForces]
  with WithSerialiser
  with WithConfig
  with WithEncryption { self: StepTemplate[InprogressForces] =>
    def factoryOfT() = InprogressForces()
    val confirmationRoute = controllers.step.forces.routes.ConfirmationController.get
    def template(form: ErrorTransformForm[InprogressForces], call: Call, backUrl: Option[Call]):Html = Html.empty
}

