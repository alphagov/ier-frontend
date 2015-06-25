package uk.gov.gds.ier.transaction.ordinary.dateOfBirth

import uk.gov.gds.ier.controller.routes.ExitController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model.{Country, DateOfBirth, noDOB}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OrdinaryStep, Routes, GoTo}
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class DateOfBirthStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with DateOfBirthForms
  with DateOfBirthMustache {

  val validation = dateOfBirthForm

  val routing = Routes(
    get = routes.DateOfBirthStep.get,
    post = routes.DateOfBirthStep.post,
    editGet = routes.DateOfBirthStep.editGet,
    editPost = routes.DateOfBirthStep.editPost
  )

  override val onSuccess = TransformApplication { currentState =>
    //IF AN ACTUAL DOB IS ENTERED, ENSURE NODOB IS WIPED & VICE-VERSA...
    val dateOfBirth = currentState.dob.map { currentDob =>
      if (currentDob.dob.isDefined) currentDob.copy(noDob = None)
      else {
        currentDob.copy(dob = None)
      }
    }
    currentState.copy(dob = dateOfBirth)

    //IF YOUNG SCOT, BLANK THE NINO & OPEN REGISTER VALUES...
    val currentStateNewDOB = currentState.copy(dob = dateOfBirth)
    if(currentStateNewDOB.dob.exists(_.dob.isDefined)) {
      if (
          CountryValidator.isScotland(currentState.country) &&
          DateValidator.isValidYoungScottishVoter(currentStateNewDOB.dob.get.dob.get)
      ) {
        currentStateNewDOB.copy(
          nino = None,
          openRegisterOptin = None
        )
      } else currentStateNewDOB
    } else currentStateNewDOB

  } andThen GoToNextIncompleteStep()

  def nextStep(currentState: InprogressOrdinary) = {
    currentState.dob match {
      //FOR SCOTTISH CITIZENS ONLY...
      case Some(DateOfBirth(Some(dob), _)) if (CountryValidator.isScotland(currentState.country) && DateValidator.isTooYoungToRegisterScottish(dob) ) => {
        GoTo(ExitController.tooYoungScotland)
      }
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range))))) if range == DateOfBirthConstants.is14to15 => {
        GoTo(ExitController.under16)
      }
      //FOR ANY OTHER CITIZEN...
      case Some(DateOfBirth(Some(dob), _)) if (!CountryValidator.isScotland(currentState.country) && DateValidator.isTooYoungToRegister(dob) ) => {
        GoTo(ExitController.tooYoung)
      }
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range))))) if range == DateOfBirthConstants.under18 => {
        GoTo(ExitController.under18)
      }
      //FOR ANY CITIZEN THAT DOES NOT PROVIDE THEIR DOB (REGARDLESS OF COUNTRY OF RESIDENCE)...
      case Some(DateOfBirth(_, Some(noDOB(Some(reason), Some(range))))) if range == DateOfBirthConstants.dontKnow => {
        if(CountryValidator.isScotland(currentState.country)) {
          GoTo(ExitController.dontKnowScotland)
        } else {
          GoTo(ExitController.dontKnow)
        }
      }
      //DEFAULT...
      case _ => ordinary.NameStep
    }
  }
}

