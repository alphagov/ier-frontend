package uk.gov.gds.ier.transaction.overseas.passport

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import controllers.step.overseas.routes._
import uk.gov.gds.ier.model.{InprogressOverseas, DOB}
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.step.OverseaStep
import controllers.step.overseas.routes.LastUkAddressController
import controllers.step.overseas.routes.PassportCheckController
import controllers.step.overseas.PassportDetailsController
import controllers.step.overseas.CitizenDetailsController
import controllers.step.overseas.NameController
import org.joda.time.LocalDate

class PassportCheckStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val encryptionKeys : EncryptionKeys)
  extends OverseaStep
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
    val jan1st1983 = new LocalDate()
      .withYear(1983)
      .withMonthOfYear(1)
      .withDayOfMonth(1)

    val isBefore1983 = currentState.dob map { case DOB(year, month, day) =>
      val dateOfBirth = new LocalDate()
        .withYear(year)
        .withMonthOfYear(month)
        .withDayOfMonth(day)
      dateOfBirth.isBefore(jan1st1983)
    }

    val hasPassport = currentState.passport map { passport => passport.hasPassport }

    val bornInUk = currentState.passport flatMap { passport => passport.bornInsideUk }

    (hasPassport, bornInUk, isBefore1983) match {
      case (Some(true), _, _) => PassportDetailsController.passportDetailsStep
      case (Some(false), Some(false), _) => CitizenDetailsController.citizenDetailsStep
      case (Some(false), Some(true), Some(false)) => CitizenDetailsController.citizenDetailsStep
      case (Some(false), Some(true), Some(true)) => NameController.nameStep
    }
  }

  def template(
      form: InProgressForm[InprogressOverseas],
      postEndpoint: Call,
      backEndpoint:Option[Call]): Html = {
    PassportMustache.passportCheckPage(
      form.form,
      postEndpoint,
      backEndpoint
    )
  }
}

