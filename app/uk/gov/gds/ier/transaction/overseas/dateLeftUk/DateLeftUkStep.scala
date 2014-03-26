package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import play.api.templates.Html
import controllers.step.overseas.LastUkAddressController
import controllers.step.overseas.ParentNameController
import uk.gov.gds.ier.step.OverseaStep
import controllers.step.overseas.routes._
import uk.gov.gds.ier.model._
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.step.GoTo
import uk.gov.gds.ier.validation.ErrorTransformForm
import org.joda.time.{Months, DateTime}
import controllers.routes.ExitController
import uk.gov.gds.ier.validation.DateValidator


class DateLeftUkStep @Inject() (val serialiser: JsonSerialiser,
                                val config: Config,
                                val encryptionService: EncryptionService)
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

    val notRegistered = currentState.lastRegisteredToVote match {
	  case Some(LastRegisteredToVote(LastRegisteredType.NotRegistered)) => true
	  case _ => false
	}

    (currentState.dateLeftUk, currentState.dob, notRegistered) match {
      case (Some(dateLeftUk), Some(dateOfBirth), _)
        if DateValidator.dateLeftUkOver15Years(dateLeftUk) =>
          GoTo(ExitController.leftUkOver15Years)
      case (Some(dateLeftUk), Some(dateOfBirth), true)
        if (validateTooOldWhenLeftUk(dateLeftUk, dateOfBirth)) =>
          GoTo(ExitController.tooOldWhenLeftUk)
      case (Some(dateLeftUk), Some(dateOfBirth), true)
        if (!DateValidator.dateLeftUkOver15Years(dateLeftUk) &&
          currentState.dob.isDefined &&
          !validateTooOldWhenLeftUk(dateLeftUk, dateOfBirth)) =>
          ParentNameController.parentNameStep
      case _ => LastUkAddressController.lastUkAddressStep
    }
  }

  def validateTooOldWhenLeftUk(dateLeftUk:DateLeft, dateOfBirth:DOB):Boolean = {
    val birthDateTime = new DateTime(dateOfBirth.year, dateOfBirth.month, dateOfBirth.day,0,0,0,0)
    val leftUk = new DateTime().withMonthOfYear(dateLeftUk.month).withYear(dateLeftUk.year)
    val monthDiff = Months.monthsBetween(birthDateTime, leftUk).getMonths()
    if (monthDiff.toFloat / 12 > 18) true
    else false
  }

  def template(form: ErrorTransformForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    dateLeftUkMustache(form, postEndpoint, backEndpoint)
  }
}
