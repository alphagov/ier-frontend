package uk.gov.gds.ier.transaction.forces.previousAddress

import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.ForcesStep
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some
import controllers.step.forces.NationalityController

class PreviousAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionKeys : EncryptionKeys,
    val encryptionService: EncryptionService)
  extends ForcesStep
  with PreviousAddressMustache
  with PreviousAddressForms {

  val validation = manualAddressFormForPreviousAddress

  val previousRoute = Some(PreviousAddressSelectController.get)

  val routes = Routes(
    get = PreviousAddressManualController.get,
    post = PreviousAddressManualController.post,
    editGet = PreviousAddressManualController.editGet,
    editPost = PreviousAddressManualController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    NationalityController.nationalityStep
  }

  def template(
      form: InProgressForm[InprogressForces],
      call: Call,
      backUrl: Option[Call]) = {
    PreviousAddressMustache.manualPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url,
      PreviousAddressPostcodeController.get.url
    )
  }
}
