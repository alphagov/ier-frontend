package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.LastRegisteredType
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import controllers.step.overseas.routes.LastRegisteredToVoteController
import controllers.step.overseas.DateLeftUkController
import controllers.step.overseas.{DateLeftArmyController, DateLeftCrownController, DateLeftCouncilController}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class LastRegisteredToVoteStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService)
  extends OverseaStep
  with LastRegisteredToVoteForms
  with LastRegisteredToVoteMustache {

  val validation = lastRegisteredToVoteForm

  val routes = Routes(
    get = LastRegisteredToVoteController.get,
    post = LastRegisteredToVoteController.post,
    editGet = LastRegisteredToVoteController.editGet,
    editPost = LastRegisteredToVoteController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    import LastRegisteredType._

    currentState.lastRegisteredToVote.map(_.lastRegisteredType) match {
      case Some(Overseas) => DateLeftUkController.dateLeftUkStep
      case Some(Ordinary) => DateLeftUkController.dateLeftUkStep
      case Some(Forces) =>  DateLeftArmyController.dateLeftArmyStep
      case Some(Crown) => DateLeftCrownController.dateLeftCrownStep
      case Some(Council) => DateLeftCouncilController.dateLeftCouncilStep
      case Some(NotRegistered) => DateLeftUkController.dateLeftUkStep
      case _ => this
    }
  }

  override val onSuccess = TransformApplication { currentState =>
    currentState.copy (dateLeftUk = None, dateLeftSpecial = None)
  } andThen GoToNextIncompleteStep()
}
