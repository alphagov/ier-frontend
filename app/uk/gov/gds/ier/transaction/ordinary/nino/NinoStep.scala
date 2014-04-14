package uk.gov.gds.ier.transaction.ordinary.nino

import controllers.step.ordinary.routes.{NinoController, NameController}
import controllers.step.ordinary.AddressController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class NinoStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService
) extends OrdinaryStep
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

  def nextStep(currentState: InprogressOrdinary) = {
    AddressController.addressStep
  }
}

