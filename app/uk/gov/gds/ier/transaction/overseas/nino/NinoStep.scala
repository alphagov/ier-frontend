package uk.gov.gds.ier.transaction.overseas.nino

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{Routes, OverseaStep}
import controllers.step.overseas.routes._
import uk.gov.gds.ier.model.InprogressOverseas
import controllers.step.overseas.AddressController

class NinoStep @Inject ()(val serialiser: JsonSerialiser,
                          val config: Config,
                          val encryptionService : EncryptionService)
  extends OverseaStep
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

  def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    ninoMustache(form.form, postEndpoint, backEndpoint)
  }
  def nextStep(currentState: InprogressOverseas) = {
    AddressController.addressStep
  }
}

