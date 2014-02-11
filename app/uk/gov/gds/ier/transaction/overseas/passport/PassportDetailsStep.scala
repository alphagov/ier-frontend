package uk.gov.gds.ier.transaction.overseas.passport

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import controllers.step.overseas.routes._
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.step.OverseaStep
import controllers.step.overseas.routes.PassportDetailsController
import controllers.step.overseas.routes.PassportCheckController
import controllers.step.overseas.NameController

class PassportDetailsStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val encryptionKeys : EncryptionKeys)
  extends OverseaStep
  with PassportForms
  with PassportMustache {

  val validation = passportDetailsForm
  val previousRoute = Some(PassportCheckController.get)

  val routes = Routes(
    get = PassportDetailsController.get,
    post = PassportDetailsController.post,
    editGet = PassportDetailsController.editGet,
    editPost = PassportDetailsController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    NameController.nameStep
  }

  def template(
      form: InProgressForm[InprogressOverseas],
      postEndpoint: Call,
      backEndpoint:Option[Call]): Html = {
    PassportMustache.passportDetailsPage(
      form.form,
      postEndpoint,
      backEndpoint
    )
  }
}

