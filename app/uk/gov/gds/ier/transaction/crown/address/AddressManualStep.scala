package uk.gov.gds.ier.transaction.crown.address

import controllers.step.crown.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{LastUkAddress, InprogressCrown}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm
import controllers.step.crown.{PreviousAddressFirstController, NationalityController}

class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService)
  extends CrownStep
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

  def nextStep(currentState: InprogressCrown) = {
    currentState.address match {
      case Some(LastUkAddress(Some(hasUkAddress),_))
        if (hasUkAddress) => PreviousAddressFirstController.previousAddressFirstStep
      case _ => {
        currentState.copy(
          previousAddress = None
        )
        NationalityController.nationalityStep
      }
    }
  }

  def template(
      form: InProgressForm[InprogressCrown],
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
