package uk.gov.gds.ier.transaction.overseas.name

import com.google.inject.Inject
import controllers.step.overseas.NinoController
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.OverseaStep
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation._
import controllers.step.overseas.routes.NameController
import controllers.step.overseas.routes.LastUkAddressController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class NameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends OverseaStep
  with NameForms
  with NameMustache {

  val validation = nameForm

  val routes = Routes(
    get = NameController.get,
    post = NameController.post,
    editGet = NameController.editGet,
    editPost = NameController.editPost
  )
  val previousRoute = Some(LastUkAddressController.get)

  def nextStep(currentState: InprogressOverseas) = {
    NinoController.ninoStep
  }

  def template(
      form: ErrorTransformForm[InprogressOverseas],
      call:Call,
      backUrl: Option[Call]): Html = {
    nameMustache(form, call, backUrl.map(_.url))
  }
}
