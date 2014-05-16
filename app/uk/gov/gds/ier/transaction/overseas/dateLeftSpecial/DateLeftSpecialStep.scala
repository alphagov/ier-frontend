package uk.gov.gds.ier.transaction.overseas.dateLeftSpecial

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import controllers.step.overseas.LastUkAddressController
import uk.gov.gds.ier.step.{OverseaStep, Routes, GoTo}
import uk.gov.gds.ier.model._
import controllers.step.overseas.routes._
import org.joda.time.{Months, DateTime}
import controllers.routes.ExitController
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.assets.RemoteAssets

class DateLeftArmyStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets
) extends DateLeftSpecialStep {

  val service = "member of the armed forces"

  val routes = Routes(
    get = DateLeftArmyController.get,
    post = DateLeftArmyController.post,
    editGet = DateLeftArmyController.editGet,
    editPost = DateLeftArmyController.editPost
  )
}

class DateLeftCrownStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets
) extends DateLeftSpecialStep {

  val service = "Crown Servant"

  val routes = Routes(
    get = DateLeftCrownController.get,
    post = DateLeftCrownController.post,
    editGet = DateLeftCrownController.editGet,
    editPost = DateLeftCrownController.editPost
 )
}

class DateLeftCouncilStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets
) extends DateLeftSpecialStep {

 val service = "British Council employee"

 val routes = Routes(
    get = DateLeftCouncilController.get,
    post = DateLeftCouncilController.post,
    editGet = DateLeftCouncilController.editGet,
    editPost = DateLeftCouncilController.editPost
 )
}


abstract class DateLeftSpecialStep
  extends OverseaStep
    with DateLeftSpecialForms
    with DateLeftSpecialMustache {

  val validation = dateLeftSpecialForm

  def nextStep(currentState: InprogressOverseas) = {
    currentState.dateLeftSpecial match {
      case Some(dateLeftSpecial) if dateLeftUkOver15Years(dateLeftSpecial.date) => {
        GoTo(ExitController.leftSpecialOver15Years)
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
