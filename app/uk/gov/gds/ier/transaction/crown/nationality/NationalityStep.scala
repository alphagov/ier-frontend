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
import uk.gov.gds.ier.step.{CrownStep, Routes, GoTo}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

class NationalityStep @Inject ()(
    val serialiser: JsonSerialiser,
    val isoCountryService: IsoCountryService,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets)
  extends CrownStep
    with NationalityForms
    with NationalityMustache {

  val validation = nationalityForm

  val routing = Routes(
    get = NationalityController.get,
    post = NationalityController.post,
    editGet = NationalityController.editGet,
    editPost = NationalityController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {

    if (currentState.nationality.flatMap(_.noNationalityReason) == None) {
      val franchises = currentState.nationality match {
        case Some(nationality) => isoCountryService.getFranchises(nationality)
        case None => List.empty
      }

      franchises match {
        case Nil => GoTo(ExitController.noFranchise)
        case list => DateOfBirthController.dateOfBirthStep
      }
    }
    else DateOfBirthController.dateOfBirthStep
  }
}

