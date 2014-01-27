package uk.gov.gds.ier.transaction.ordinary.nino

import controllers.step.ordinary.routes.{NinoController, NameController}
import controllers.step.ordinary.AddressController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}

class NinoStep @Inject ()(val serialiser: JsonSerialiser,
                          val config: Config,
                          val encryptionService : EncryptionService,
                          val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
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

  def template(form: InProgressForm[InprogressOrdinary], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    ninoMustache(form.form, postEndpoint, backEndpoint)
  }
  def nextStep(currentState: InprogressOrdinary) = {
    AddressController.addressStep
  }
}

