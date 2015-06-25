package uk.gov.gds.ier.transaction.ordinary.address

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{GoTo, OrdinaryStep, Routes}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.controller.routes.ExitController
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.transaction.ordinary.OrdinaryControllers
import uk.gov.gds.ier.validation.DateValidator
import views.html.helper.form

@Singleton
class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with AddressMustache
  with AddressForms {

  val validation = lookupAddressForm

  val routing = Routes(
    get = routes.AddressStep.get,
    post = routes.AddressStep.post,
    editGet = routes.AddressStep.editGet,
    editPost = routes.AddressStep.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    val optAddress = currentState.address

    optAddress match {
      case Some(partialAddress) => {
        val postcode = partialAddress.postcode.trim.toUpperCase
        if (postcode.isEmpty)
          this
        else if (postcode.startsWith("BT"))
          GoTo (ExitController.northernIreland)
        else if (addressService.isScotAddress(postcode)) {
          //LIVING IN SCOTLAND? > CARRY ON
          ordinary.AddressSelectStep
        }
        else {
          //LIVING OUTSIDE SCOTLAND? > PROCEED _UNLESS_ AGE=14 OR 15
          if(currentState.dob.exists(_.dob.isDefined)) {
            if (DateValidator.isValidYoungScottishVoter(currentState.dob.get.dob.get)) {
              if (DateValidator.getAge(currentState.dob.get.dob.get) == 14) {
                GoTo(ExitController.tooYoungNotScotland14)
              } else {
                GoTo(ExitController.tooYoungNotScotland15)
              }
            }
            else ordinary.AddressSelectStep
          }
          else ordinary.AddressSelectStep
        }
      }
      case None => this
    }
  }

  override val onSuccess = {
     GoToNextStep()
  }
}
