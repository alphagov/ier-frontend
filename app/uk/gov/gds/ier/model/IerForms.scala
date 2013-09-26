package uk.gov.gds.ier.model

import play.api.data.Form
import play.api.data.Forms._
import org.joda.time.LocalDate

trait IerForms {

  lazy val postcodeRegex = "(?i)((GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY]))))\\s?[0-9][A-Z-[CIKMOV]]{2}))"
  val dobFormat = "yyyy-MM-dd"
  val timeFormat = "yyyy-MM-dd HH:mm:ss"
  val postcodeForm = Form(
    single(
      "postcode" -> nonEmptyText.verifying(_.matches(postcodeRegex))
    )
  )

  val nameMapping = mapping(
    "firstName" -> nonEmptyText,
    "middleNames" -> optional(nonEmptyText),
    "lastName" -> nonEmptyText
  ) (Name.apply) (Name.unapply)

  val contactMapping = mapping(
    "contactType" -> nonEmptyText,
    "post" -> optional(nonEmptyText),
    "phone" -> optional(nonEmptyText),
    "textNum" -> optional(nonEmptyText),
    "email" -> optional(nonEmptyText)
  ) (Contact.apply) (Contact.unapply)

  val nationalityMapping = mapping(
    "nationalities" -> list(nonEmptyText),
    "otherCountries" -> list(nonEmptyText),
    "noNationalityReason" -> optional(nonEmptyText)
  ) (Nationality.apply) (Nationality.unapply)

  val addressMapping = mapping(
    "address" -> nonEmptyText,
    "postcode" -> nonEmptyText
  ) (Address.apply) (Address.unapply)

  val dobMapping = mapping(
    "year" -> nonEmptyText,
    "month" -> nonEmptyText,
    "day" -> nonEmptyText
  ) (DateOfBirth.apply) (DateOfBirth.unapply)

  val inprogressForm = Form(
    mapping(
      "name" -> optional(nameMapping),
      "previousName" -> optional(nameMapping),
      "dob" -> optional(dobMapping),
      "nationality" -> optional(nationalityMapping),
      "NINO" -> optional(nonEmptyText),
      "address" -> optional(addressMapping),
      "movedRecently" -> optional(nonEmptyText),
      "previousAddress" -> optional(addressMapping),
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

object InProgressForm extends IerForms {
  def apply(application:InprogressApplication):InProgressForm = {
    InProgressForm(inprogressForm.fill(application), application)
  }
}

case class InProgressForm(form:Form[InprogressApplication],
                          application:InprogressApplication) {
  def hasNoNationalityReason = {
    application.nationality match {
      case Some(nat) => nat.noNationalityReason.isDefined
      case None => false
    }
  }
  def hasNationality(nationality:String) = {
    application.nationality match {
      case Some(n) => n.nationalities.contains(nationality)
      case None => false
    }
  }
  def getNoNationalityReason = application.nationality match {
    case Some(n) => n.noNationalityReason.getOrElse("")
    case None => ""
  }
}