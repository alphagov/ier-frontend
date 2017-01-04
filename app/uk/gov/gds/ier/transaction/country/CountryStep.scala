package uk.gov.gds.ier.transaction.country

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CountryConstraints
import uk.gov.gds.ier.model.{Country}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OrdinaryStep, Routes, GoTo}
import uk.gov.gds.ier.controller.routes.ExitController
import uk.gov.gds.ier.controller.routes.RegisterToVoteController
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.service.ScotlandService

class CountryStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config:Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers,
    val scotlandService: ScotlandService
) extends OrdinaryStep
  with CountryConstraints
  with CountryForms
  with CountryMustache {

  val validation = countryForm

  val routing = Routes(
    get = routes.CountryStep.get,
    post = routes.CountryStep.post,
    editGet = routes.CountryStep.editGet,
    editPost = routes.CountryStep.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    currentState.country match {
      case Some(Country("Northern Ireland", true)) => GoTo(ExitController.niOverseas)
      case Some(Country("Northern Ireland", _)) => GoTo(ExitController.northernIreland)
      case Some(Country("Scotland", _)) if !config.availableForScotland => GoTo(ExitController.scotland)
      case Some(Country("British Islands", _)) => GoTo(ExitController.britishIslands)
      case Some(Country(_, true)) => GoTo(RegisterToVoteController.registerToVoteOverseasStart)
      case _ => ordinary.NationalityStep
    }
  }

  override val onSuccess = TransformApplication { currentState =>
    //Before moving on, check the noDOB range option is accurate based on this new address selected
    //Wipe the noDOB range option selected if it is NOT acceptable for this new address chosen
    //(ie. the noDOB option must be of a selection appropriate for this address's country)
    if(scotlandService.resetNoDOBRange(currentState)) {
      val currentStateRESET = currentState.copy(
        dob = None
      )
      currentStateRESET
    }
    else currentState

  } andThen GoToNextIncompleteStep()
}
