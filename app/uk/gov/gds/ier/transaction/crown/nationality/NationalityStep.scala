package uk.gov.gds.ier.transaction.crown.nationality

import controllers.step.crown.routes.{AddressController,NationalityController}
import controllers.step.crown.DateOfBirthController
import controllers.routes.ExitController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.service.IsoCountryService
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.model.InprogressCrown
import uk.gov.gds.ier.step.{CrownStep, Routes, Exit}

class NationalityStep @Inject ()(
    val serialiser: JsonSerialiser,
    val isoCountryService: IsoCountryService,
    val config: Config,
    val encryptionService : EncryptionService)
  extends CrownStep
    with NationalityForms
    with NationalityMustache {

  val validation = nationalityForm
  val previousRoute = Some(AddressController.get)

  val routes = Routes(
    get = NationalityController.get,
    post = NationalityController.post,
    editGet = NationalityController.editGet,
    editPost = NationalityController.editPost
  )

  def template(
      form: InProgressForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint:Option[Call]): Html = Html.empty

  override def templateWithApplication(
      form: InProgressForm[InprogressCrown],
      call: Call,
      backUrl: Option[Call]):InprogressCrown => Html = {
    application:InprogressCrown =>
      nationalityMustache(application, form.form, call, backUrl)
  }

  def nextStep(currentState: InprogressCrown) = {

    if (currentState.nationality.flatMap(_.noNationalityReason) == None) {
      val franchises = currentState.nationality match {
        case Some(nationality) => isoCountryService.getFranchises(nationality)
        case None => List.empty
      }

      franchises match {
        case Nil => Exit(ExitController.noFranchise)
        case list => DateOfBirthController.dateOfBirthStep
      }
    }
    else DateOfBirthController.dateOfBirthStep
  }
}

