package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, Key}
import uk.gov.gds.ier.model.{Name, PreviousName}
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Mapping
import play.api.data.Forms._
import play.api.Logger
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait ParentNameConstraints extends CommonConstraints {
  self:  FormKeys
    with ErrorMessages =>
     
      
    lazy val parentNameNotOptional = Constraint[Option[Name]] (keys.overseasParentName.parentName.key) {
      name =>
        name match {
          case Some(parentName) => Valid
          case None => Invalid("Please enter their full name", 
          keys.overseasParentName.parentName.firstName, keys.overseasParentName.parentName.lastName, keys.overseasParentName.parentName) 
        }
    }  
    
    lazy val parentPreviousNameNotOptionalIfHasPreviousIsTrue = Constraint[Option[PreviousName]] (keys.overseasParentName.parentPreviousName.key) {
      name => 
        if (name.isDefined) {
         if (name.get.hasPreviousName && !name.get.previousName.isDefined) {
           Invalid("Please enter their previous full name", 
               keys.overseasParentName.parentPreviousName.previousName.firstName,
               keys.overseasParentName.parentPreviousName.previousName.lastName)
         }
         else Valid 
        }
        else Invalid("Please answer this quesiton", keys.overseasParentName.parentPreviousName.hasPreviousName)
    }


  lazy val parentPrevNameOptionCheck = Constraint[InprogressOverseas] (keys.overseasParentName.parentPreviousName.key) {
    application =>
      if (application.overseasParentName.isDefined && application.overseasParentName.get.previousName.isDefined) Valid
      else Invalid("Please answer this question", keys.overseasParentName.parentPreviousName)
  }
   def parentFirstNameNotEmpty = Constraint[Name](keys.overseasParentName.parentName.firstName.key) {
    name =>
      if (name.firstName.trim.isEmpty()) Invalid("Please enter their first name",
          keys.overseasParentName.parentName.firstName)
      else Valid
  }
  
  def parentLastNameNotEmpty = Constraint[Name](keys.overseasParentName.parentName.lastName.key) {
    name =>
      if (name.lastName.trim.isEmpty()) Invalid("Please enter their last name", 
          keys.overseasParentName.parentName.lastName)
      else Valid
  }
  
  lazy val parentFirstNameNotTooLong = fieldNotTooLong[Name](keys.overseasParentName.parentName.firstName,
    firstNameMaxLengthError) (_.firstName)

  lazy val parentMiddleNamesNotTooLong = fieldNotTooLong[Name](keys.overseasParentName.parentName.middleNames,
    middleNameMaxLengthError) (_.middleNames.getOrElse(""))

  lazy val parentLastNameNotTooLong = fieldNotTooLong[Name](keys.overseasParentName.parentName.lastName,
    lastNameMaxLengthError) (_.lastName)
  
  
  
  def parentPreviousFirstNameNotEmpty = Constraint[PreviousName](keys.overseasParentName.parentPreviousName.previousName.firstName.key) {
    name =>
      if (name.hasPreviousName) {
        if (name.previousName.isDefined && name.previousName.get.firstName.trim.isEmpty()) Invalid("Please enter their previous first name", keys.overseasParentName.parentPreviousName.previousName.firstName)
        else Valid
      } 
      else Valid 
  }
  
  def parentPreviousLastNameNotEmpty = Constraint[PreviousName](keys.overseasParentName.parentPreviousName.previousName.lastName.key) {
    name =>
      if (name.hasPreviousName) {
      if (name.previousName.isDefined && name.previousName.get.lastName.trim.isEmpty()) Invalid("Please enter their previous last name", keys.overseasParentName.parentPreviousName.previousName.lastName)
      else Valid
      } 
      else Valid 
  }
  
  lazy val parentPrevFirstNameNotTooLong = fieldNotTooLong[PreviousName](
    keys.overseasParentName.parentPreviousName.previousName.firstName,
    previousFirstNameMaxLengthError) (_.previousName.map(_.firstName).getOrElse(""))

  lazy val parentPrevMiddleNamesNotTooLong = fieldNotTooLong[PreviousName](
    keys.overseasParentName.parentPreviousName.previousName.middleNames,
    previousMiddleNameMaxLengthError) (_.previousName.flatMap(_.middleNames).getOrElse(""))

  lazy val parentPrevLastNameNotTooLong = fieldNotTooLong[PreviousName](
    keys.overseasParentName.parentPreviousName.previousName.lastName,
    previousLastNameMaxLengthError) (_.previousName.map(_.lastName).getOrElse(""))

  
}
