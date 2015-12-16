package uk.gov.gds.ier.transaction.overseas.name

import uk.gov.gds.ier.transaction.overseas.OverseasControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class NameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val overseas: OverseasControllers
) extends OverseaStep
  with NameForms
  with NameMustache {

  val validation = nameForm

  val routing = Routes(
    get = routes.NameStep.get,
    post = routes.NameStep.post,
    editGet = routes.NameStep.editGet,
    editPost = routes.NameStep.editPost
  )

  override val onSuccess = TransformApplication { currentState =>
    //If the OS citizen has selected TRUE for changed name, make sure none of the changed before options are entered.
    val prevNameValue = currentState.previousName.map { currentPreviousName =>
      if (currentPreviousName.hasPreviousNameOption.equalsIgnoreCase("true")) {
        currentPreviousName.copy(changedNameBeforeLeavingUKOption = None)
      }
      else {
        currentPreviousName.copy()
      }
    }
    currentState.copy(previousName = prevNameValue)
  } andThen GoToNextIncompleteStep()

  def nextStep(currentState: InprogressOverseas) = {
    //Does this OS citizen have a previous name TBC??...
    var hasPreviousName = false
    var changedNameBeforeLeavingSelected = false

    if (currentState.previousName.isDefined) {
      hasPreviousName = currentState.previousName.get.hasPreviousName
      if(currentState.previousName.get.changedNameBeforeLeavingUKOption.isDefined) {
        if(currentState.previousName.get.changedNameBeforeLeavingUKOption.toString.equalsIgnoreCase("Some(true)")) {
          changedNameBeforeLeavingSelected = true
        }
      }
    }

    //If either of the above are TRUE, send the OS citizen to the previous name page
    //...else skip it and go to nino page
    if (hasPreviousName | changedNameBeforeLeavingSelected) {
      overseas.PreviousNameStep
    } else {

      overseas.NinoStep
    }
  }
}
