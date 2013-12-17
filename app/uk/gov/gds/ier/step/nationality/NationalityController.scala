package uk.gov.gds.ier.step.nationality

import controllers.step._
import controllers.routes.ExitController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html
import uk.gov.gds.ier.service.IsoCountryService
import uk.gov.gds.ier.guice.{WithEncryption, WithIsoCountryService, WithConfig}

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class NationalityController @Inject ()(val serialiser: JsonSerialiser,
                                       val isoCountryService: IsoCountryService,
                                       val config: Config,
                                       val encryptionService : EncryptionService,
                                       val encryptionKeys : EncryptionKeys)
  extends StepController
  with WithSerialiser
  with WithIsoCountryService
  with WithConfig
  with WithEncryption
  with NationalityForms {

  val validation = nationalityForm
  val editPostRoute = routes.NationalityController.editPost
  val stepPostRoute = routes.NationalityController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.nationality(form, call)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    val franchises = currentState.nationality match {
      case Some(nationality) => isoCountryService.getFranchises(nationality)
      case None => List.empty
    }

    franchises match {
      case Nil => Redirect(ExitController.noFranchise)
      case list if list.size > 0 => Redirect(routes.DateOfBirthController.get)
    }
  }
}

