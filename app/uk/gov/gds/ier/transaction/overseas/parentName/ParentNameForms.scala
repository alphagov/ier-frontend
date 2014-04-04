package uk.gov.gds.ier.transaction.overseas.parentName

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model._
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.ParentNameConstraints
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.model.PreviousName
import scala.Some
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait ParentNameForms extends ParentNameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  private lazy val generalNameMapping = mapping(
    keys.firstName.key -> text,
    keys.middleNames.key -> optional(nonEmptyText),
    keys.lastName.key -> text
  ) (Name.apply) (Name.unapply)
  
  lazy val parentNameMapping = generalNameMapping verifying(
	  parentFirstNameNotEmpty, parentLastNameNotEmpty,
	  parentFirstNameNotTooLong, parentMiddleNamesNotTooLong, parentLastNameNotTooLong
  )

  lazy val parentPrevNameMapping = mapping(
    keys.hasPreviousName.key -> boolean,
    keys.previousName.key -> optional(generalNameMapping)
  ) (PreviousName.apply) (PreviousName.unapply) verifying (
    parentPreviousFirstNameNotEmpty, parentPreviousLastNameNotEmpty,
    parentPrevFirstNameNotTooLong, parentPrevMiddleNamesNotTooLong, parentPrevLastNameNotTooLong
  )
  
    
  lazy val overseasParentNameMapping = mapping(
    keys.parentName.key -> optional(parentNameMapping).verifying(parentNameNotOptional),
    keys.parentPreviousName.key -> required(optional(parentPrevNameMapping).verifying(parentPreviousNameNotOptionalIfHasPreviousIsTrue), "Please answer this question")
  ) (OverseasParentName.apply) (OverseasParentName.unapply)

  
  val parentNameForm = ErrorTransformForm(
    mapping(keys.overseasParentName.key -> overseasParentNameMapping)
    (overseasParentName => InprogressOverseas(overseasParentName = Some(overseasParentName)))
    (inprogress => inprogress.overseasParentName)
  )
}