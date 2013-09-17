package uk.gov.gds.ier.model

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Session, Request, AnyContent}
import uk.gov.gds.ier.serialiser.WithSerialiser
import org.joda.time.{LocalDate, DateTime}

trait IerForms {
  self: WithSerialiser =>

  lazy val postcodeRegex = "(?i)((GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY]))))\\s?[0-9][A-Z-[CIKMOV]]{2}))"
  val dobFormat = "yyyy-MM-dd"
  val timeFormat = "yyyy-MM-dd HH:mm:ss"
  val webApplicationForm = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "middleName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "previousLastName" -> nonEmptyText,
      "nino" -> nonEmptyText,
      "dob" -> jodaLocalDate(dobFormat)
    )(WebApplication.apply)(WebApplication.unapply)
  )
  val apiApplicationForm = Form(
    mapping(
      "fn" -> nonEmptyText,
      "mn" -> nonEmptyText,
      "ln" -> nonEmptyText,
      "pln" -> nonEmptyText,
      "gssCode" -> nonEmptyText,
      "nino" -> text,
      "dob" -> jodaLocalDate(dobFormat)
    )(ApiApplication.apply)(ApiApplication.unapply)
  )
  val apiApplicationResponseForm = Form(
    mapping(
      "detail" -> apiApplicationForm.mapping,
      "ierId" -> nonEmptyText,
      "createdAt" -> nonEmptyText,
      "status" -> nonEmptyText,
      "source" -> nonEmptyText
    )(ApiApplicationResponse.apply)(ApiApplicationResponse.unapply)
  )
  val postcodeForm = Form(
    single(
      "postcode" -> nonEmptyText.verifying(_.matches(postcodeRegex))
    )
  )

  val completeApplicationForm = Form(
    mapping(
      "firstName" -> optional(nonEmptyText),
      "middleName" -> optional(nonEmptyText),
      "lastName" -> optional(nonEmptyText),
      "previousLastName" -> optional(nonEmptyText),
      "nino" -> optional(nonEmptyText),
      "dob" -> optional(mapping(
          "day" -> number,
          "month" -> number,
          "year" -> number)
        ((day, month, year) => LocalDate.now().withDayOfMonth(day).withMonthOfYear(month).withYear(year))
        (date => Some(date.getDayOfMonth, date.getMonthOfYear, date.getYear))
      ),
      "nationality" -> optional(nonEmptyText)
    )(CompleteApplication.apply)(CompleteApplication.unapply)
  )

  val inprogressForm = Form(
    mapping(
      "firstName" -> optional(nonEmptyText),
      "middleNames" -> optional(nonEmptyText),
      "lastName" -> optional(nonEmptyText),
      "dobYear" -> optional(nonEmptyText),
      "dobMonth" -> optional(nonEmptyText),
      "dobDay" -> optional(nonEmptyText),
      "nationality" -> optional(nonEmptyText)
    ) (InprogressApplication.apply) (InprogressApplication.unapply)
  )

  implicit class InprogressApplicationToSession(app:InprogressApplication) {
    private val sessionKey = "application"
    def toSession:(String, String) = {
      sessionKey -> toJson(app)
    }
  }

  implicit class InprogressSession(session:Session) {
    private val sessionKey = "application"
    def getApplication = {
      session.get(sessionKey) match {
        case Some(app) => fromJson[InprogressApplication](app)
        case _ => InprogressApplication()
      }
    }
    def merge(application: InprogressApplication):InprogressApplication= {
      val stored = getApplication
      stored.copy(
        firstName = application.firstName.orElse(stored.firstName),
        middleName = application.middleName.orElse(stored.middleName),
        lastName = application.lastName.orElse(stored.lastName),
        dobYear = application.dobYear.orElse(stored.dobYear),
        dobMonth = application.dobMonth.orElse(stored.dobMonth),
        dobDay = application.dobDay.orElse(stored.dobDay),
        nationality = application.nationality.orElse(stored.nationality)
      )
    }
  }

  implicit class BetterForm[A](form: Form[A]) {
    def errorsAsMap = {
      form.errors.groupBy(_.key).mapValues {
        errors =>
          errors.map(e => play.api.i18n.Messages(e.message, e.args: _*))
      }
    }
    def simpleErrors: Map[String, String] = {
      form.errors.foldLeft(Map.empty[String, String]){
        (map, error) => map ++ Map(error.key -> play.api.i18n.Messages(error.message, error.args: _*))
      }
    }
  }
}
