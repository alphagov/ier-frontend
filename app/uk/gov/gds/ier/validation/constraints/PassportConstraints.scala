package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._

trait PassportConstraints extends CommonConstraints{
  self: ErrorMessages
    with FormKeys =>

  lazy val citizenDetailsFilled = Constraint[InprogressOverseas](keys.passport.key) {
    application => application.passport match {
      case Some(passport) if passport.citizen.isDefined => Valid
      case _ => Invalid(
        "Please answer this question",
        keys.passport.citizenDetails,
        keys.passport.citizenDetails.howBecameCitizen,
        keys.passport.citizenDetails.dateBecameCitizen.day,
        keys.passport.citizenDetails.dateBecameCitizen.month,
        keys.passport.citizenDetails.dateBecameCitizen.year,
        keys.passport.citizenDetails.dateBecameCitizen
      )
    }
  }

  lazy val passportDetailsFilled = Constraint[InprogressOverseas](keys.passport.key) {
    application => application.passport match {
      case Some(passport) if passport.details.isDefined => Valid
      case _ => Invalid(
        "Please answer this question",
        keys.passport.passportDetails,
        keys.passport.passportDetails.passportNumber,
        keys.passport.passportDetails.authority,
        keys.passport.passportDetails.issueDate.day,
        keys.passport.passportDetails.issueDate.month,
        keys.passport.passportDetails.issueDate.year,
        keys.passport.passportDetails.issueDate
      )
    }
  }

  lazy val ifDefinedPassportNumberRequired = Constraint[InprogressOverseas](keys.passport.key) {
    application => application.passport match {
      case Some(passport) if !passport.details.isDefined => Valid
      case Some(Passport(_, _, Some(details), _)) if details.passportNumber != "" => Valid
      case _ => Invalid(
        "Please provide your Passport Number",
        keys.passport.passportDetails,
        keys.passport.passportDetails.passportNumber
      )
    }
  }

  lazy val ifDefinedAuthorityRequired = Constraint[InprogressOverseas](keys.passport.key) {
    application => application.passport match {
      case Some(passport) if !passport.details.isDefined => Valid
      case Some(Passport(_, _, Some(details), _)) if details.authority != "" => Valid
      case _ => Invalid(
        "Please provide your Authority or Place of Issue",
        keys.passport.passportDetails,
        keys.passport.passportDetails.authority
      )
    }
  }

  lazy val ifDefinedHowBecameCitizenRequired = Constraint[InprogressOverseas](keys.passport.key){
    application => application.passport match {
      case Some(passport) if !passport.citizen.isDefined => Valid
      case Some(Passport(_, _, _, Some(citizen))) if citizen.howBecameCitizen != "" => Valid
      case _ => Invalid(
        "Please provide your explanation of how you became a British Citizen",
        keys.passport.citizenDetails.howBecameCitizen,
        keys.passport.citizenDetails
      )
    }
  }

  lazy val citizenDetailsNotFilled = Constraint[InprogressOverseas](keys.passport.key) {
    application => application.passport match {
      case Some(passport) if passport.citizen.isDefined => Invalid(
        "You shouldn't have been able to do that",
        keys.passport.citizenDetails
      )
      case _ => Valid
    }
  }

  lazy val passportDetailsNotFilled = Constraint[InprogressOverseas](keys.passport.key) {
    application => application.passport match {
      case Some(passport) if passport.details.isDefined => Invalid(
        "You shouldn't have been able to do that",
        keys.passport.passportDetails
      )
      case _ => Valid
    }
  }

  lazy val passportRequired = Constraint[InprogressOverseas](keys.passport.key) {
    application => application.passport match {
      case Some(passport) if passport.hasPassport => Valid
      case Some(passport) if !passport.hasPassport => Valid
      case _ => Invalid(
        "Please answer this question",
        keys.passport.hasPassport
      )
    }
  }

  lazy val ifNoPassportBornInsideRequired = Constraint[InprogressOverseas](keys.passport.key) {
    application => application.passport match {
      case Some(Passport(true, None, _, _)) => Valid
      case Some(Passport(false, Some(bornInUk), _, _)) => Valid
      case Some(Passport(true, Some(bornInUk), _, _)) => Valid
      case _ => Invalid(
        "Please answer this question",
        keys.passport.bornInsideUk
      )
    }
  }
}