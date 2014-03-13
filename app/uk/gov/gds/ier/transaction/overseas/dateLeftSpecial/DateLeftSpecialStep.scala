package uk.gov.gds.ier.transaction.overseas.dateLeftSpecial

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import play.api.templates.Html
import controllers.step.overseas.LastUkAddressController
import uk.gov.gds.ier.step.OverseaStep
import uk.gov.gds.ier.model._
import play.api.mvc.Call
import controllers.step.overseas.routes._
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.step.Exit
import uk.gov.gds.ier.validation.InProgressForm
import org.joda.time.{Months, DateTime}
import controllers.routes.ExitController

class DateLeftArmyStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService) extends DateLeftSpecialStep {
  
 val routes = Routes(
    get = DateLeftArmyController.get,
    post = DateLeftArmyController.post,
    editGet = DateLeftArmyController.editGet,
    editPost = DateLeftArmyController.editPost
  ) 
 def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    dateLeftSpecialMustache(form.form, postEndpoint, backEndpoint, "member of the armed forces")
  }
}

class DateLeftCrownStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService) extends DateLeftSpecialStep {
 
 val routes = Routes(
    get = DateLeftCrownController.get,
    post = DateLeftCrownController.post,
    editGet = DateLeftCrownController.editGet,
    editPost = DateLeftCrownController.editPost
 )
  
 def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    dateLeftSpecialMustache(form.form, postEndpoint, backEndpoint, "Crown Servant")
  }
}

class DateLeftCouncilStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService) extends DateLeftSpecialStep {

 val routes = Routes(
    get = DateLeftCouncilController.get,
    post = DateLeftCouncilController.post,
    editGet = DateLeftCouncilController.editGet,
    editPost = DateLeftCouncilController.editPost
 )  
 def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    dateLeftSpecialMustache(form.form, postEndpoint, backEndpoint, "British Council employee")
  }
}


abstract class DateLeftSpecialStep
  extends OverseaStep
    with DateLeftSpecialForms
    with DateLeftSpecialMustache {

  val validation = dateLeftSpecialForm
  
  val previousRoute = Some(LastRegisteredToVoteController.get)

  def nextStep(currentState: InprogressOverseas) = {
    currentState.dateLeftSpecial match {
      case Some(dateLeftSpecial) if dateLeftUkOver15Years(dateLeftSpecial.date) => {
        Exit(ExitController.leftSpecialOver15Years)
      }
      case _ => LastUkAddressController.lastUkAddressStep
    }
  }

  def dateLeftUkOver15Years(dateLeftUk:DateLeft):Boolean = {
    val leftUk = new DateTime().withMonthOfYear(dateLeftUk.month).withYear(dateLeftUk.year)
    val monthDiff = Months.monthsBetween(leftUk, DateTime.now()).getMonths()
    if (monthDiff >= 15 * 12) true
    else false
  }
}
