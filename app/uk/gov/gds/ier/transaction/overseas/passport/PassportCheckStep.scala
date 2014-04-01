package uk.gov.gds.ier.transaction.overseas.passport

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.model.{InprogressOverseas, DOB}
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.OverseaStep
import controllers.step.overseas.routes.LastUkAddressController
import controllers.step.overseas.routes.PassportCheckController
import controllers.step.overseas.PassportDetailsController
import controllers.step.overseas.CitizenDetailsController
import controllers.step.overseas.NameController
import org.joda.time.LocalDate
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants

class PassportCheckStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends OverseaStep
  with PassportHelperConstants
  with PassportForms
  with PassportMustache {

  val validation = passportCheckForm
  val previousRoute = Some(LastUkAddressController.get)

  val routes = Routes(
    get = PassportCheckController.get,
    post = PassportCheckController.post,
    editGet = PassportCheckController.editGet,
    editPost = PassportCheckController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {

    val before1983 = currentState.dob map { case DOB(year, month, day) =>
      val dateOfBirth = new LocalDate()
        .withYear(year)
        .withMonthOfYear(month)
        .withDayOfMonth(day)
      dateOfBirth.isBefore(DateOfBirthConstants.jan1st1983)
    }

    val passport = currentState.passport map { passport => passport.hasPassport }

    val bornInUk = currentState.passport flatMap { passport => passport.bornInsideUk }

    (passport, bornInUk, before1983) match {
      case (`hasPassport`, _, _) => passportDetailsStep
      case (`noPassport`, `notBornInUk`, _) => citizenDetailsStep
      case (`noPassport`, `wasBornInUk`, `notBornBefore1983`) => citizenDetailsStep
      case (`noPassport`, `wasBornInUk`, `wasBornBefore1983`) => nameStep
      case _ => this
    }
  }

  def template(
      form: ErrorTransformForm[InprogressOverseas],
      postEndpoint: Call,
      backEndpoint:Option[Call]): Html = {
    PassportMustache.passportCheckPage(
      form,
      postEndpoint,
      backEndpoint
    )
  }
}

private[passport] trait PassportHelperConstants {

  //Constants needed for the nextStep method
  private[passport] val hasPassport = Some(true)
  private[passport] val noPassport = Some(false)
  private[passport] val wasBornInUk = Some(true)
  private[passport] val notBornInUk = Some(false)
  private[passport] val wasBornBefore1983 = Some(true)
  private[passport] val notBornBefore1983 = Some(false)
  private[passport] lazy val passportDetailsStep = PassportDetailsController.passportDetailsStep
  private[passport] lazy val citizenDetailsStep = CitizenDetailsController.citizenDetailsStep
  private[passport] lazy val nameStep = NameController.nameStep
}
