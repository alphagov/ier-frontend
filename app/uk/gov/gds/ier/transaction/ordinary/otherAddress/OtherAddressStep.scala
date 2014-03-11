package uk.gov.gds.ier.transaction.ordinary.otherAddress

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.step.OrdinaryStep
import controllers.step.ordinary.PreviousAddressFirstController

class OtherAddressStep @Inject ()(val serialiser: JsonSerialiser,
                                        val config: Config,
                                        val encryptionService : EncryptionService)
  extends OrdinaryStep
  with OtherAddressForms
  with OtherAddressMustache {

  val validation = otherAddressForm
  val previousRoute = Some(AddressController.get)

  val routes = Routes(
    get = OtherAddressController.get,
    post = OtherAddressController.post,
    editGet = OtherAddressController.editGet,
    editPost = OtherAddressController.editPost
  )

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    otherAddressMustache(form.form, call, backUrl.map(_.url))
  }
  def nextStep(currentState: InprogressOrdinary) = {
    PreviousAddressFirstController.previousAddressFirstStep
  }
}

