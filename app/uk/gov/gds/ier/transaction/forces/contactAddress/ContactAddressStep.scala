package uk.gov.gds.ier.transaction.forces.contactAddress

import controllers.step.forces.OpenRegisterController
import controllers.step.forces.routes.{ContactAddressController, RankController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class ContactAddressStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
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

  override def templateWithApplication(
      form: ErrorTransformForm[InprogressForces],
      postEndpoint:Call,
      backEndpoint: Option[Call]) = {
    application: InprogressForces => {
      contactAddressMustache(form, postEndpoint, backEndpoint, application)
    }
  }

  override def template(
    form: ErrorTransformForm[InprogressForces],
    postEndpoint: Call,
    backEndpoint:Option[Call]): Html = Html.empty

  def nextStep(currentState: InprogressForces) = {
    OpenRegisterController.openRegisterStep
  }
}

