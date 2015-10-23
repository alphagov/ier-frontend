package uk.gov.gds.ier.transaction.ordinary.nationality

import uk.gov.gds.ier.validation.{ErrorTransformForm, EmailValidator, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PartialNationality, Contact, ContactDetail}
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import play.api.data.validation.Constraint
import uk.gov.gds.ier.validation.constants.NationalityConstants
import play.api.data.validation._

trait NationalityForms extends NationalityConstraints {
  self:  FormKeys
    with ErrorMessages =>

  val nationalityForm = ErrorTransformForm(
    mapping(
      keys.nationality.key -> optional(PartialNationality.mapping),
      keys.contact.key -> optional(Contact.mapping)
    ) (
      (nationality, contact) => InprogressOrdinary(
        nationality = nationality,
        contact = contact
      )
    ) (
      inprogress => Some(inprogress.nationality, inprogress.contact)
    ) verifying (
      nationalityIsChosen,
      notTooManyNationalities,
      otherCountry0IsValid,
      otherCountry1IsValid,
      otherCountry2IsValid,
      atleastOneOtherCountryIfHasOtherCountry,
      emailIsValidIfItIsProvided
    )
  )
}

trait NationalityConstraints extends FormKeys with ErrorMessages {

  lazy val atleastOneOtherCountryIfHasOtherCountry = Constraint[InprogressOrdinary] (
    keys.nationality.otherCountries.key
  ) { application =>
    val numberOtherCoutries = application.nationality.foldLeft(0) {
      (zero, nationality) => nationality.otherCountries.size
    }
    val hasOtherCountry = application.nationality.flatMap(_.hasOtherCountry)

    (hasOtherCountry, numberOtherCoutries) match {
      case (Some(true), 0) => Invalid(
        "ordinary_nationality_error_pleaseAnswer",
        keys.nationality.otherCountries
      )
      case _ => Valid
    }
  }

  lazy val emailIsValidIfItIsProvided = Constraint[InprogressOrdinary](keys.nationality.key, keys.contact.key) {
    application =>
      application.contact.flatMap(_.email).getOrElse(false) match {
      case Some(ContactDetail(true, Some(emailAddress))) => {
        if (EmailValidator.isValid(emailAddress)) Valid
        else Invalid("ordinary_contact_error_pleaseEnterValidEmail", keys.contact.email.detail)
      }
      case _ => Valid
    }
  }

  lazy val notTooManyNationalities = Constraint[InprogressOrdinary](keys.nationality.key) {
    application =>
      val numberOtherCoutries = application.nationality.foldLeft(0) {
        (zero, nationality) => nationality.otherCountries.size
      }
      if (numberOtherCoutries > NationalityConstants.numberMaxOfOtherCountries) {
        Invalid(
          "ordinary_nationality_error_noMoreFiveCountries",
          keys.nationality.otherCountries
        )
      } else {
        Valid
      }
  }

  lazy val nationalityIsChosen = Constraint[InprogressOrdinary](keys.nationality.key) {
    application =>
      val britishChecked = application.nationality.flatMap(_.british).getOrElse(false)
      val irishChecked = application.nationality.flatMap(_.irish).getOrElse(false)
      val hasOtherCountry = application.nationality.flatMap(_.hasOtherCountry).getOrElse(false)
      val otherCountryFilled = application.nationality.map{ nat => 
        nat.otherCountries.size > 0
      }.getOrElse(false)

      val nationalityFilled = britishChecked || irishChecked || otherCountryFilled || hasOtherCountry

      val excuseFilled = application.nationality.flatMap(_.noNationalityReason).exists(_.nonEmpty)

      if (nationalityFilled || excuseFilled) {
        Valid
      } else {
        Invalid(
          "ordinary_nationality_error_pleaseAnswer",
          keys.nationality
        )
      }
  }

  lazy val otherCountry0IsValid = otherCountryIsValid(0)
  lazy val otherCountry1IsValid = otherCountryIsValid(1)
  lazy val otherCountry2IsValid = otherCountryIsValid(2)

  private def otherCountryIsValid(i:Int) = Constraint[InprogressOrdinary](
    keys.nationality.otherCountries.key
  ) { application =>
    val otherCountry = application.nationality.flatMap(_.otherCountries.lift(i))
    val otherCountryValid = otherCountry.exists { country =>
      NationalityConstants.validNationalitiesList.contains(country)
    }

    (otherCountry, otherCountryValid) match {
      case (Some(c), false) => Invalid(
        "ordinary_nationality_error_notValid",
        keys.nationality.otherCountries.item(i)
      )
      case _ => Valid
    }
  }
}

