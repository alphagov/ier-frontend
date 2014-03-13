package uk.gov.gds.ier.transaction.forces.rank

import controllers.step.forces.ContactAddressController
import controllers.step.forces.routes.{ServiceController, RankController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressForces
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}

class RankStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends ForcesStep
  with RankForms
  with RankMustache {

  val validation = rankForm
  val previousRoute = Some(ServiceController.get)

  val routes = Routes(
    get = RankController.get,
    post = RankController.post,
    editGet = RankController.editGet,
    editPost = RankController.editPost
  )

  def template(
      form:InProgressForm[InprogressForces],
      call:Call,
      backUrl: Option[Call]): Html = {
    rankMustache(form.form, call, backUrl)
  }

  def nextStep(currentState: InprogressForces) = {
    ContactAddressController.contactAddressStep
  }
}
