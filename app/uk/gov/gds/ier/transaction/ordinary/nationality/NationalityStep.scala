package uk.gov.gds.ier.transaction.ordinary.nationality

import controllers.step.ordinary.routes._
import controllers.step.routes._
import controllers.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.templates.Html
import uk.gov.gds.ier.service.IsoCountryService
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.OrdinaryStep
import uk.gov.gds.ier.model.InprogressOrdinary


class NationalityStep @Inject ()(val serialiser: JsonSerialiser,
                                       val isoCountryService: IsoCountryService,
                                       val config: Config,
                                       val encryptionService : EncryptionService,
                                       val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with NationalityForms {

  val validation = nationalityForm
  val editPostRoute = NationalityController.editPost
  val stepPostRoute = NationalityController.post
  val prevousRoute = CountryController.get 
      //CountryController.get

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[String]): Html = {
    views.html.steps.nationality(form, call, backUrl)
  }
  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    val franchises = currentState.nationality match {
      case Some(nationality) => isoCountryService.getFranchises(nationality)
      case None => List.empty
    }

    franchises match {
      case Nil => Redirect(ExitController.noFranchise)
      case list => Redirect(DateOfBirthController.get)
    }
  }
  def backToPrevious(currentState: InprogressOrdinary): SimpleResult = {
    Redirect(prevousRoute)  
  }
  
}

