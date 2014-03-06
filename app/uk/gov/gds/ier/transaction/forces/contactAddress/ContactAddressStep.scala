package uk.gov.gds.ier.transaction.forces.contactAddress

import controllers.step.forces.OpenRegisterController
import controllers.step.forces.routes.{ContactAddressController, RankController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressForces
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{ForcesStep, Routes}

class ContactAddressStep @Inject ()(val serialiser: JsonSerialiser,
                                       val config: Config,
                                       val encryptionService : EncryptionService,
                                       val encryptionKeys : EncryptionKeys)
  extends ForcesStep
  with ContactAddressForms
  with ContactAddressMustache{

  val validation = contactAddressForm
  val previousRoute = Some(RankController.get)

  val routes = Routes(
    get = ContactAddressController.get,
    post = ContactAddressController.post,
    editGet = ContactAddressController.editGet,
    editPost = ContactAddressController.editPost
  ) 

  def template(
      form:InProgressForm[InprogressForces],
      postEndpoint:Call,
      backEndpoint: Option[Call]): Html = {
    contactAddressMustache(form.form, postEndpoint, backEndpoint)
  }

  def nextStep(currentState: InprogressForces) = {
    OpenRegisterController.openRegisterStep
  }
}

