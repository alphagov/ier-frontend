package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}
import uk.gov.gds.ier.form.AddressHelpers

case class InProgressForm[T <: InprogressApplication[T]](form:ErrorTransformForm[T])
  extends FormKeys with AddressHelpers {

  def apply(key:Key) = {
    form(key.key)
  }

  def apply(key:String) = {
    form(key)
  }
  def getNationalities = {
    form.value match {
      case Some(application:InprogressOrdinary) => application.nationality.map(_.checkedNationalities).filter(_.size > 0)
      case None => None
      case applicationOfUnknownType => throw new IllegalArgumentException(s"Application of unknown type: $applicationOfUnknownType")
    }
  }
  def getOtherCountries = {
    form.value match {
      case Some(application:InprogressOrdinary) => application.nationality.map(_.otherCountries.filter(_.nonEmpty)).filter(_.size > 0)
      case None => None
      case applicationOfUnknownType => throw new IllegalArgumentException(s"Application of unknown type: $applicationOfUnknownType")
    }
  }
  def nationalityIsFilled():Boolean = {
    form.value match {
      case Some(application:InprogressOrdinary) => application.nationality.map(
        nationality =>
          nationality.british == Some(true) || nationality.irish == Some(true) || nationality.otherCountries.exists(_.nonEmpty)).exists(b => b)
      case None => false
      case applicationOfUnknownType => throw new IllegalArgumentException(s"Application of unknown type: $applicationOfUnknownType")
    }
  }
  def hasNoNationalityReason = {
    form(keys.nationality.noNationalityReason.key).value.exists(_.nonEmpty)
  }
  def hasNationality(thisNationality:String) = {
    form(keys.nationality.nationalities.key).value.exists(_.contains(thisNationality))
  }
  def confirmationNationalityString = {
    val allCountries = getNationalities.getOrElse(List.empty) ++ getOtherCountries.getOrElse(List.empty)
    val nationalityString = List(allCountries.dropRight(1).mkString(", "), allCountries.takeRight(1).mkString("")).filter(_.nonEmpty)
    s"a citizen of ${nationalityString.mkString(" and ")}"
  }

  def manualAddressToOneLine(manualAddressKey: Key): Option[String] = {
    manualAddressToOneLine(this, manualAddressKey)
  }

  def isManualAddressDefined(manualAddressKey: Key): Boolean = {
    isManualAddressDefined(this, manualAddressKey)
  }
}
