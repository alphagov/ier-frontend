package uk.gov.gds.ier.transaction.overseas.nino

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStepWithNewMustache, Routes}
import controllers.step.overseas.routes._
import controllers.step.overseas.AddressController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class NinoStep @Inject ()(val serialiser: JsonSerialiser,
                          val config: Config,
                          val encryptionService : EncryptionService)
  extends OverseaStepWithNewMustache
  with NinoForms
  with NinoMustache {

  val validation = ninoForm
  val previousRoute = Some(NameController.get)

  val routes = Routes(
    get = NinoController.get,
    post = NinoController.post,
    editGet = NinoController.editGet,
    editPost = NinoController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    AddressController.addressStep
  }
}

