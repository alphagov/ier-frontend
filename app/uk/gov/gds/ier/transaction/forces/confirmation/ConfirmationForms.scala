package uk.gov.gds.ier.transaction.forces.confirmation

import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.forces.statement.StatementForms

trait ConfirmationForms
  extends FormKeys
  with ErrorMessages
  with WithSerialiser
  with StatementForms
  with CommonConstraints {

  val stubAddressMapping = mapping(
    "foo" -> text
  ) (foo => PartialAddress (addressLine = None, uprn = None,
    postcode = "", manualAddress = None )) (stub => Some("foo"))

  val stubNationalityMapping = mapping(
    "foo" -> text
  ) (foo => PartialNationality (british = None, irish = None, hasOtherCountry = None,
    otherCountries = List.empty, noNationalityReason = None )) (stub => Some("foo"))

  val stubDateOfBirthMapping = mapping(
    "foo" -> text
  ) (foo => DateOfBirth (dob = None, noDob = None)) (stub => Some("foo"))

  val stubNameMapping = mapping(
    "foo" -> text
  ) (foo => Name (firstName = "", middleNames = None, lastName = "")) (stub => Some("foo"))

  val stubNinoMapping = mapping(
    "foo" -> text
  ) (foo => Nino (nino = None, noNinoReason = None)) (stub => Some("foo"))

  val stubServiceMapping = mapping(
    "foo" -> text
  ) (foo => Service (serviceName = None, regiment = None)) (stub => Some("foo"))

  val stubRankMapping = mapping(
    "foo" -> text
  ) (foo => Rank (serviceNumber = None, rank = None)) (stub => Some("foo"))

  val stubContactAddressMapping = mapping(
    "foo" -> text
  ) (foo => ContactAddress (country = None, addressLine1 = None, addressLine2 = None,
    addressLine3 = None, addressLine4 = None, addressLine5 = None)) (stub => Some("foo"))

  val stubOpenRegisterMapping = mapping(
    "foo" -> text
  ) (foo => false) (stub => Some("foo"))

  val stubWaysToVoteMapping = mapping(
    "foo" -> text
  ) (foo => WaysToVote(WaysToVoteType.ByPost)) (stub => Some("foo"))

  val stubPostalOrProxyVoteMapping = mapping(
    "foo" -> text
  ) (foo => PostalOrProxyVote(WaysToVoteType.ByPost,postalVoteOption = None,
    deliveryMethod = None)) (stub => Some("foo"))

  val stubContactMapping = mapping(
    "foo" -> text
  ) (foo => Contact(post = false, phone = None, email = None)) (stub => Some("foo"))

  val stubPossibleAddressMapping = mapping(
    "foo" -> text
  ) (foo => PossibleAddress(Addresses(List.empty),"")) (stub => Some("foo"))

  val optInMapping = single(
    keys.optIn.key -> boolean
  )

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.statement.key -> optional(statementMapping),
      keys.address.key -> optional(stubAddressMapping),
      keys.nationality.key -> optional(stubNationalityMapping),
      keys.dob.key -> optional(stubDateOfBirthMapping),
      keys.name.key -> optional(stubNameMapping),
      keys.nino.key -> optional(stubNinoMapping),
      keys.service.key -> optional(stubServiceMapping),
      keys.rank.key -> optional(stubRankMapping),
      keys.contactAddress.key -> optional(stubContactAddressMapping),
      keys.openRegister.key -> optional(stubOpenRegisterMapping),
      keys.waysToVote.key -> optional(stubWaysToVoteMapping),
      keys.postalOrProxyVote.key -> optional(stubPostalOrProxyVoteMapping),
      keys.contact.key -> optional(stubContactMapping),
      keys.possibleAddresses.key -> optional(stubPossibleAddressMapping)
    )
    (InprogressForces.apply)
    (InprogressForces.unapply)
  )
}
