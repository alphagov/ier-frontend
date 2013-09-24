package uk.gov.gds.ier.model

import play.api.data.Form
import play.api.data.Forms._
import org.joda.time.LocalDate

trait IerForms {

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

  val nameMapping = mapping(
    "firstName" -> nonEmptyText,
    "middleNames" -> optional(nonEmptyText),
    "lastName" -> nonEmptyText) (Name.apply) (Name.unapply)

  val contactMapping = mapping(
    "contactType" -> nonEmptyText,
    "post" -> optional(nonEmptyText),
    "phone" -> optional(nonEmptyText),
    "textNum" -> optional(nonEmptyText),
    "email" -> optional(nonEmptyText)) (Contact.apply) (Contact.unapply)

  val nationalityMapping = mapping(
    "nationalities" -> optional(list(nonEmptyText)),
    "hasOtherCountries" -> optional(nonEmptyText),
    "otherCountries" -> optional(list(nonEmptyText)),
    "noNationalityReason" -> optional(nonEmptyText)
  ) (Nationality.apply) (Nationality.unapply)

  val inprogressForm = Form(
    mapping(
      "name" -> optional(nameMapping),
      "previousName" -> optional(nameMapping),
      "dobYear" -> optional(nonEmptyText),
      "dobMonth" -> optional(nonEmptyText),
      "dobDay" -> optional(nonEmptyText),
      "nationality" -> optional(nationalityMapping),
      "NINO" -> optional(nonEmptyText),
      "address" -> optional(nonEmptyText),
      "postcode" -> optional(nonEmptyText),
      "movedRecently" -> optional(nonEmptyText),
      "previousAddress" -> optional(nonEmptyText),
      "previousPostcode" -> optional(nonEmptyText),
      "hasOtherAddress" -> optional(nonEmptyText),
      "openRegisterOptin" -> optional(nonEmptyText),
      "contact" -> optional(contactMapping)
    ) (InprogressApplication.apply) (InprogressApplication.unapply)
  )

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
