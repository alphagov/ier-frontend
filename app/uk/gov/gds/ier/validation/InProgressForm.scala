package uk.gov.gds.ier.validation

import play.api.data.Form
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}


case class InProgressForm[T <: InprogressApplication[T]](form:ErrorTransformForm[T]) extends FormKeys{

  def apply(key:Key) = {
    form(key.key)
  }
  def getNationalities = {
    form.value match {
      case Some(application:InprogressOrdinary) => application.nationality.map(_.checkedNationalities).filter(_.size > 0)
      case None => None
    }
  }
  def getOtherCountries = {
    form.value match {
      case Some(application:InprogressOrdinary) => application.nationality.map(_.otherCountries.filter(_.nonEmpty)).filter(_.size > 0)
      case None => None
    }
  }
  def nationalityIsFilled():Boolean = {
    form.value match {
      case Some(application:InprogressOrdinary) => application.nationality.map(
        nationality =>
          nationality.british == Some(true) || nationality.irish == Some(true) || nationality.otherCountries.exists(_.nonEmpty)).exists(b => b)
      case None => false
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
}
