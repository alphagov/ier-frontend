package uk.gov.gds.ier.transaction.overseas.previouslyRegistered

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.model.{PreviouslyRegistered, InprogressOverseas}
import uk.gov.gds.ier.validation.{ErrorTransformForm, InProgressForm}
import play.api.mvc.Call
import play.api.templates.Html
import play.api.mvc.SimpleResult
import controllers.step.overseas.routes._
import uk.gov.gds.ier.step.OverseaStep

class PreviouslyRegisteredStep @Inject() (val serialiser: JsonSerialiser,
                                                val config: Config,
                                                val encryptionService: EncryptionService,
                                                val encryptionKeys: EncryptionKeys)
  extends OverseaStep
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with PreviouslyRegisteredForms
  with PreviousRegisteredMustache {

  val validation = previouslyRegisteredForm
  val editPostRoute = PreviouslyRegisteredController.editPost
  val stepPostRoute = PreviouslyRegisteredController.post

  def goToNext(currentState: InprogressOverseas): SimpleResult = {
    currentState.previouslyRegistered match {
      case Some(PreviouslyRegistered(true)) => Redirect(FirstTimeRegisteredController.get)
      case Some(PreviouslyRegistered(false)) => Redirect(DateLeftUkController.get)
      case _ => Redirect(PreviouslyRegisteredController.get)
    }
  }
  def template(form: InProgressForm[InprogressOverseas],call: Call): Html = {
    previousRegisteredMustache(form.form, call)
  }
}
