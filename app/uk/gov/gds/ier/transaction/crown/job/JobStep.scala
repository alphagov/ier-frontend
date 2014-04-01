package uk.gov.gds.ier.transaction.crown.job

import controllers.step.crown.NinoController
import controllers.step.crown.routes.{NameController, JobController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import uk.gov.gds.ier.model.InprogressCrown
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes}

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


  def template(
    form: ErrorTransformForm[InprogressCrown],
    call: Call,
    backUrl:Option[Call]): Html = Html.empty

  override def templateWithApplication(
      form: ErrorTransformForm[InprogressCrown],
      call:Call,
      backUrl: Option[Call]) = {
    application:InprogressCrown =>
      jobMustache(application, form, call, backUrl)
  }

  def nextStep(currentState: InprogressCrown) = {
    NinoController.ninoStep
  }
}
