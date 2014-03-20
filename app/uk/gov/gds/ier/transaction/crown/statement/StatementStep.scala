package uk.gov.gds.ier.transaction.crown.statement

import com.google.inject.Inject
import controllers.step.crown.routes.StatementController
import controllers.step.crown.AddressController
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressCrown
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation.InProgressForm

class StatementStep @Inject ()(
    val serialiser:JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService
) extends CrownStep
    with StatementForms
    with StatementMustache {

  val validation = statementForm
  val previousRoute = None

  val routes = Routes(
    get = StatementController.get,
    post = StatementController.post,
    editGet = StatementController.editGet,
    editPost = StatementController.editPost
  )

  def template(
      form: InProgressForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint: Option[Call]) = {
    statementMustache(form.form, postEndpoint, backEndpoint)
  }

  def nextStep(currentState: InprogressCrown) = {
    AddressController.addressStep
  }
}