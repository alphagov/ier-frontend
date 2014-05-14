package uk.gov.gds.ier.transaction.overseas.parentName

import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.model.{OverseasParentName, PreviousName}
import controllers.step.overseas.routes.ParentNameController
import controllers.step.overseas.routes.DateLeftUkController
import controllers.step.overseas.ParentsAddressController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

class ParentNameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets
) extends OverseaStep
  with ParentNameForms
  with ParentNameMustache {

  val validation = parentNameForm

  val routes = Routes(
    get = ParentNameController.get,
    post = ParentNameController.post,
    editGet = ParentNameController.editGet,
    editPost = ParentNameController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    ParentsAddressController.parentsAddressStep
  }

  def resetParentName = TransformApplication { currentState =>
    currentState.overseasParentName match {
      case Some(OverseasParentName(optParentName, Some(parentPreviousName)))
        if (!parentPreviousName.hasPreviousName) =>
          currentState.copy(
            overseasParentName = Some(OverseasParentName(optParentName, Some(PreviousName(false, None))))
          )
      case _ => currentState
    }
  }

  override val onSuccess = resetParentName andThen GoToNextIncompleteStep()
}
