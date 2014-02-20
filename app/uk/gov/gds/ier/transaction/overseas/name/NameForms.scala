package uk.gov.gds.ier.transaction.overseas.name

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressOverseas, Name, PreviousName}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NameConstraints
import uk.gov.gds.ier.model.OverseasName

trait NameForms extends NameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  private lazy val generalNameMapping = mapping(
    keys.firstName.key -> required(text, "Please enter your first name"),
    keys.middleNames.key -> optional(nonEmptyText),
    keys.lastName.key -> required(text, "Please enter your last name")
  ) (
    Name.apply
  ) (
    Name.unapply
  )

  private lazy val prevNameMapping = generalNameMapping verifying(
    firstNameNotTooLong, middleNamesNotTooLong, lastNameNotTooLong)

  lazy val nameMapping = generalNameMapping verifying(
    firstNameNotTooLong, middleNamesNotTooLong, lastNameNotTooLong)

  lazy val previousNameMapping = mapping(
    keys.hasPreviousName.key -> boolean,
    keys.previousName.key -> optional(prevNameMapping)
  ) (
    PreviousName.apply
  ) (
    PreviousName.unapply
  ) verifying prevNameFilledIfHasPrevIsTrue
  
  lazy val overseasNameMapping = mapping(
      keys.name.key -> optional(nameMapping).verifying(nameNotOptional),
      keys.previousName.key -> optional(previousNameMapping)
//      keys.previousName.key -> required(optional(previousNameMapping), "Please answer this question")
    )(OverseasName.apply)(OverseasName.unapply)
//    ((name: Option[Name], previousName: Option[PreviousName])) 
    
//    ((name, previousName) => InprogressOverseas(overseasName = Some(OverseasName(name, previousName))))

  val nameForm = ErrorTransformForm(
    overseasNameMapping.transform[InprogressOverseas](
    overseasName => InprogressOverseas(overseasName = Some(overseasName)), 
    inprogress => inprogress.overseasName.get)
    )
//    (
//      (name, previousName) => InprogressOverseas(name = name, previousName = previousName)
//    ) (
//      inprogress => Some(inprogress.name, inprogress.previousName)
//    )
  
}
