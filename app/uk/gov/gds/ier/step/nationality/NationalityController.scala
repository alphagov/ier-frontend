package uk.gov.gds.ier.step.nationality

import controllers.step._
import controllers.routes.ExitController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.OrdinaryController
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}
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
  extends OrdinaryController
  with NationalityForms {

  val validation = nationalityForm
  val editPostRoute = routes.NationalityController.editPost
  val stepPostRoute = routes.NationalityController.post

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.nationality(form, call)
  }
  def goToNext(currentState: InprogressOrdinary): SimpleResult = {
    val franchises = currentState.nationality match {
      case Some(nationality) => isoCountryService.getFranchises(nationality)
      case None => List.empty
    }

    franchises match {
      case Nil => Redirect(ExitController.noFranchise)
      case list => Redirect(routes.DateOfBirthController.get)
    }
  }
}

