package uk.gov.gds.ier.transaction.forces.address

import controllers.step.overseas.routes.{
  LastUkAddressController,
  LastUkAddressManualController,
  DateLeftUkController}
import controllers.step.overseas.NameController
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm

class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val encryptionKeys: EncryptionKeys)
  extends OverseaStep
  with AddressMustache
  with LastUkAddressForms {

  val validation = manualAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = LastUkAddressManualController.get,
    post = LastUkAddressManualController.post,
    editGet = LastUkAddressManualController.editGet,
    editPost = LastUkAddressManualController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    NameController.nameStep
  }

  def template(
      form: InProgressForm[InprogressOverseas],
      call: Call,
      backUrl: Option[Call]) = {
    LastUkAddressMustache.manualPage(
      form,
      backUrl.map(_.url).getOrElse(""),
      call.url,
      LastUkAddressController.get.url
    )
  }
}
