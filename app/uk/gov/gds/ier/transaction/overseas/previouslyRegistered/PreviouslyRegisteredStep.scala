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
import controllers.step.overseas.routes.PreviouslyRegisteredController
import controllers.step.routes.CountryController
import controllers.step.overseas.{FirstTimeRegisteredController, DateLeftUkController}
import uk.gov.gds.ier.step.OverseaStep
import uk.gov.gds.ier.step.Routes

class PreviouslyRegisteredStep @Inject() (val serialiser: JsonSerialiser,
                                                val config: Config,
                                                val encryptionService: EncryptionService,
                                                val encryptionKeys: EncryptionKeys)
  extends OverseaStep
  with PreviouslyRegisteredForms
  with PreviousRegisteredMustache {

  val validation = previouslyRegisteredForm
  val routes = Routes(
    get = PreviouslyRegisteredController.get,
    post = PreviouslyRegisteredController.post,
    editGet = PreviouslyRegisteredController.editGet,
    editPost = PreviouslyRegisteredController.editPost
  )
  val previousRoute = Some(CountryController.get)

  def nextStep(currentState: InprogressOverseas) = {
    currentState.previouslyRegistered match {
      case Some(PreviouslyRegistered(true)) => FirstTimeRegisteredController.firstTimeStep
      case Some(PreviouslyRegistered(false)) => DateLeftUkController.dateLeftUkStep
    }
  }

  def template(form: InProgressForm[InprogressOverseas],
               postEndpoint: Call,
               backEndpoint:Option[Call]): Html = {
    previousRegisteredMustache(form.form, postEndpoint, backEndpoint)
  }
}
