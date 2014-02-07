package uk.gov.gds.ier.transaction.overseas.openRegister

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.OverseaStep
import controllers.step.overseas.routes._
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some
import controllers.step.overseas.WaysToVoteController

class OpenRegisterStep @Inject ()(val serialiser: JsonSerialiser,
                                        val config: Config,
                                        val encryptionService : EncryptionService,
                                        val encryptionKeys : EncryptionKeys)
  extends OverseaStep
  with OpenRegisterForms
  with OpenRegisterMustache {

  val validation = openRegisterForm
  val previousRoute = Some(AddressController.get)

  val routes = Routes(
    get = OpenRegisterController.get,
    post = OpenRegisterController.post,
    editGet = OpenRegisterController.editGet,
    editPost = OpenRegisterController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    WaysToVoteController.waysToVoteStep
  }

  def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    openRegisterMustache(form.form, postEndpoint, backEndpoint)
  }
  
  override def isStepComplete(currentState: InprogressOverseas) = {
    currentState.openRegisterOptin.isDefined
  }
}
