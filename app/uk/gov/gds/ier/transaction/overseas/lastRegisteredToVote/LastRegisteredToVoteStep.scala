package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{InprogressOverseas, LastRegisteredType}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm
import controllers.step.overseas.routes.LastRegisteredToVoteController
import controllers.step.overseas.routes.PreviouslyRegisteredController
import controllers.step.overseas.DateLeftUkController
import controllers.step.overseas.{DateLeftArmyController, DateLeftCrownController, DateLeftCouncilController}

class LastRegisteredToVoteStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService)
  extends OverseaStep
  with LastRegisteredToVoteForms
  with LastRegisteredToVoteMustache {

  val validation = lastRegisteredToVoteForm
  val previousRoute = Some(PreviouslyRegisteredController.get)

  val routes = Routes(
    get = LastRegisteredToVoteController.get,
    post = LastRegisteredToVoteController.post,
    editGet = LastRegisteredToVoteController.editGet,
    editPost = LastRegisteredToVoteController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.lastRegisteredToVote.map(_.lastRegisteredType) match {
      case Some(LastRegisteredType.Ordinary) => DateLeftUkController.dateLeftUkStep
      case Some(LastRegisteredType.Forces) =>  DateLeftArmyController.dateLeftArmyStep
      case Some(LastRegisteredType.Crown) => DateLeftCrownController.dateLeftCrownStep
      case Some(LastRegisteredType.Council) => DateLeftCouncilController.dateLeftCouncilStep
      case Some(LastRegisteredType.NotRegistered) => DateLeftUkController.dateLeftUkStep
      case _ => this
    }
  }
  
  override def postSuccess(currentState: InprogressOverseas):InprogressOverseas = {
    currentState.copy (dateLeftUk = None, dateLeftSpecial = None)
  }

  def template(
      form: InProgressForm[InprogressOverseas],
      postEndpoint: Call,
      backEndpoint:Option[Call]) = {
    LastRegisteredToVoteMustache.lastRegisteredPage(form.form, postEndpoint, backEndpoint)
  }
}
