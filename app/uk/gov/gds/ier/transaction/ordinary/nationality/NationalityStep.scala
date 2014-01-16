package uk.gov.gds.ier.transaction.ordinary.nationality

import controllers.step.ordinary.DateOfBirthController
import controllers.step.ordinary.routes.NationalityController
import controllers.routes.ExitController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.templates.Html
import uk.gov.gds.ier.service.IsoCountryService
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes, Exit}

class NationalityStep @Inject ()(val serialiser: JsonSerialiser,
                                       val isoCountryService: IsoCountryService,
                                       val config: Config,
                                       val encryptionService : EncryptionService,
                                       val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with NationalityForms {

  val validation = nationalityForm

  val routes = Routes(
    get = NationalityController.get,
    post = NationalityController.post
  )

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.nationality(form, call)
  }
  def nextStep(currentState: InprogressOrdinary) = {
    val franchises = currentState.nationality match {
      case Some(nationality) => isoCountryService.getFranchises(nationality)
      case None => List.empty
    }

    franchises match {
      case Nil => Exit(ExitController.noFranchise)
      case list => DateOfBirthController.dateOfBirthStep
    }
  }
}

