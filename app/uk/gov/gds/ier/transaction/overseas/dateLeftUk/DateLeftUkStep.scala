package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import play.api.templates.Html
import controllers.step.overseas.LastUkAddressController
import controllers.step.overseas.ParentNameController
import uk.gov.gds.ier.step.OverseaStep
import controllers.step.overseas.routes._
import uk.gov.gds.ier.model._
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.step.Exit
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some
import org.joda.time.{Months, DateTime}
import controllers.routes.ExitController
import uk.gov.gds.ier.validation.DateValidator


class DateLeftUkStep @Inject() (val serialiser: JsonSerialiser,
                                val config: Config,
                                val encryptionService: EncryptionService,
                                val encryptionKeys: EncryptionKeys)
  extends OverseaStep
    with DateLeftUkForms
    with DateLeftUkMustache {

  val validation = dateLeftUkForm
  val routes = Routes(
    get = DateLeftUkController.get,
    post = DateLeftUkController.post,
    editGet = DateLeftUkController.editGet,
    editPost = DateLeftUkController.editPost
  )
  val previousRoute = Some(PreviouslyRegisteredController.get)

  def nextStep(currentState: InprogressOverseas) = {
    currentState.dateLeftUk match {
      case Some(dateLeftUk) if dateLeftUkOver15Years(dateLeftUk) => {
        Exit(ExitController.leftUkOver15Years)
      }
      case Some(dateLeftUk) if validateTooOldWhenLeftUk(dateLeftUk, currentState.dob, currentState.lastRegisteredToVote) => {
        Exit(ExitController.tooOldWhenLeftUk)
      }
      case Some(dateLeftUk) if (!dateLeftUkOver15Years(dateLeftUk) && 
          DateValidator.isLessEighteen(currentState.dob)) => {
        ParentNameController.parentNameStep
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

  def validateTooOldWhenLeftUk(dateLeftUk:DateLeft, dateOfBirth:Option[DOB], lastRegisteredToVote:Option[LastRegisteredToVote]):Boolean = {
    if (lastRegisteredToVote.exists(_.lastRegisteredType == LastRegisteredType.NotRegistered))
      dateOfBirth match {
        case Some(DOB(year,month,day)) => {
          val birthDateTime = new DateTime(year,month,day,0,0,0,0)
          val leftUk = new DateTime().withMonthOfYear(dateLeftUk.month).withYear(dateLeftUk.year)
          val monthDiff = Months.monthsBetween(birthDateTime, leftUk).getMonths()
          if (monthDiff.toFloat / 12 > 18) true
          else false
        }
        case _ => false
      }
    else false
  }

  def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    dateLeftUkMustache(form.form, postEndpoint, backEndpoint)
  }
}
