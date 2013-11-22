package uk.gov.gds.ier.validation

import play.api.data.Form
import uk.gov.gds.ier.model.InprogressApplication

case class InProgressForm(form:Form[InprogressApplication]) extends FormKeys{
  def apply(key:Key) = {
    form(key.key)
  }
  def getNationalities = {
    form.value match {
      case Some(application) => application.nationality.map(_.checkedNationalities).filter(_.size > 0)
      case None => None
    }
  }
  def getOtherCountries = {
    form.value match {
      case Some(application) => application.nationality.map(_.otherCountries.filter(_.nonEmpty)).filter(_.size > 0)
      case None => None
    }
  }
  def nationalityIsFilled():Boolean = {
    form.value match {
      case Some(application) => application.nationality.map(
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
    val nationalityString = getNationalities.map(_.mkString(" and "))
    val otherString = getOtherCountries.map("a citizen of " + _.mkString(" and "))
    List(nationalityString.getOrElse(""), otherString.getOrElse("")).filter(_.nonEmpty).mkString(" and ")
  }
}