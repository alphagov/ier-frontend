package uk.gov.gds.ier.transaction.crown.job

import controllers.step.crown.{DeclarationPdfController, NinoController}
import controllers.step.crown.routes.{NameController, JobController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.model.{WaysToVoteType, WaysToVote}

class JobStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends CrownStep
  with JobForms
  with JobMustache {

  val validation = jobForm
  val previousRoute = Some(NameController.get)

  val routes = Routes(
    get = JobController.get,
    post = JobController.post,
    editGet = JobController.editGet,
    editPost = JobController.editPost
  )

  override val onSuccess = {
    GoToNextStep() // FIXME: unfinished
  }

  def nextStep(currentState: InprogressCrown) = {
    DeclarationPdfController.declarationPdfStep
    //NinoController.ninoStep  // FIXME: not sure
  }
}
