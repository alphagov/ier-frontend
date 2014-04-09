package uk.gov.gds.ier.step

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.stubs.StubTemplate
import play.api.templates.Html
import uk.gov.gds.ier.validation.ErrorTransformForm

trait OverseaStep
  extends StepController[InprogressOverseas]
  with StubTemplate[InprogressOverseas]
  with WithSerialiser
  with WithConfig
  with WithEncryption {
    def factoryOfT() = InprogressOverseas()
    val confirmationRoute = controllers.step.overseas.routes.ConfirmationController.get
}

trait OverseaStepWithNewMustache
  extends StepController[InprogressOverseas]
  with WithSerialiser
  with WithConfig
  with WithEncryption { self: StepTemplate[InprogressOverseas] =>
    def factoryOfT() = InprogressOverseas()
    val confirmationRoute = controllers.step.overseas.routes.ConfirmationController.get
    def template(
        form: ErrorTransformForm[InprogressOverseas],
        call: Call,
        backUrl: Option[Call]):Html = Html.empty
}

