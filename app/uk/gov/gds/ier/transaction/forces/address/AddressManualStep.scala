package uk.gov.gds.ier.transaction.forces.address

import controllers.step.forces.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.InprogressForces
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm
import controllers.step.forces.PreviousAddressFirstController

class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val encryptionKeys: EncryptionKeys)
  extends ForcesStep
  with AddressMustache
  with AddressForms {

  val validation = manualAddressForm

  val previousRoute = Some(StatementController.get)

  val routes = Routes(
    get = AddressManualController.get,
    post = AddressManualController.post,
    editGet = AddressManualController.editGet,
    editPost = AddressManualController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    PreviousAddressFirstController.previousAddressFirstStep
  }

  def template(
      form: InProgressForm[InprogressForces],
      call: Call,
      backUrl: Option[Call]) = {
    AddressMustache.manualPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url,
      AddressController.get.url
    )
  }
}
