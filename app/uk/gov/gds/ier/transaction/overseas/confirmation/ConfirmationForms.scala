package uk.gov.gds.ier.transaction.overseas.confirmation

import org.joda.time.{YearMonth, Years}
import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, Key, ErrorMessages}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressForms
import uk.gov.gds.ier.transaction.overseas.previouslyRegistered.PreviouslyRegisteredForms
import uk.gov.gds.ier.transaction.overseas.dateLeftSpecial.DateLeftSpecialForms
import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkForms
import uk.gov.gds.ier.transaction.overseas.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote.LastRegisteredToVoteForms
import uk.gov.gds.ier.transaction.overseas.nino.NinoForms
import uk.gov.gds.ier.transaction.overseas.name.NameForms
import uk.gov.gds.ier.transaction.overseas.parentName.ParentNameForms
import uk.gov.gds.ier.transaction.overseas.openRegister.OpenRegisterForms
import uk.gov.gds.ier.transaction.overseas.contact.ContactForms
import uk.gov.gds.ier.transaction.overseas.passport.PassportForms
import uk.gov.gds.ier.transaction.overseas.address.AddressForms
import uk.gov.gds.ier.transaction.overseas.waysToVote.WaysToVoteForms
import uk.gov.gds.ier.transaction.overseas.applicationFormVote.PostalOrProxyVoteForms

trait ConfirmationForms
  extends FormKeys
  with ErrorMessages
  with WithSerialiser
  with PreviouslyRegisteredForms
  with DateLeftSpecialForms
  with DateLeftUkForms
  with ParentNameForms
  with DateOfBirthForms
  with LastRegisteredToVoteForms
  with NinoForms
  with AddressForms
  with LastUkAddressForms
  with OpenRegisterForms
  with NameForms
  with PassportForms
  with WaysToVoteForms
  with PostalOrProxyVoteForms
  with ContactForms
  with OverseasFormImplicits
  with CommonConstraints {

  val stubMapping = mapping(
    "foo" -> text
  ) (foo => Stub()) (stub => Some("foo"))

  val optInMapping = single(
    keys.optIn.key -> boolean
  )

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.overseasName.key -> optional(overseasNameMapping),
//      keys.name.key -> optional(nameMapping),
//      keys.previousName.key -> optional(previousNameMapping),
      keys.previouslyRegistered.key -> optional(previouslyRegisteredMapping),
      keys.dateLeftSpecial.key -> optional(dateLeftSpecialTypeMapping),
      keys.dateLeftUk.key -> optional(dateLeftUkMapping),
      keys.overseasParentName.key -> optional(overseasParentNameMapping),
//      keys.parentName.key -> optional(parentNameMapping),
//      keys.parentPreviousName.key -> optional(parentPrevNameMapping),
      "parentsAddress" -> optional(stubMapping),
      keys.lastRegisteredToVote.key -> optional(lastRegisteredToVoteMapping),
      keys.dob.key -> optional(dobMapping),
      keys.nino.key -> optional(ninoMapping),
      keys.lastUkAddress.key -> optional(partialAddressMapping),
      keys.overseasAddress.key -> optional(addressMapping),
      keys.openRegister.key -> optional(optInMapping),
      keys.waysToVote.key -> optional(waysToVoteMapping),
      keys.postalOrProxyVote.key -> optional(postalOrProxyVoteMapping),
      keys.contact.key -> optional(contactMapping),
      keys.passport.key -> optional(passportMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    )
    (InprogressOverseas.apply)
    (InprogressOverseas.unapply)
    verifying (validateOverseas)
  )

  lazy val validateOverseas = Constraint[InprogressOverseas]("validateOverseas") { application =>
    import uk.gov.gds.ier.model.ApplicationType._
    application.identifyApplication match {
      case YoungVoter => validateYoungVoter(application)
      case NewVoter => validateNewVoter(application)
      case RenewerVoter => validateRenewerVoter(application)
      case SpecialVoter => validateSpecialVoter(application)
      case DontKnow => validateBaseSetRequired(application)
    }
  }

  lazy val validateBaseSetRequired = Constraint[InprogressOverseas]("validateBaseSet") {
    application => Invalid("Base set criteria not met", keys.name)
  }

  lazy val validateYoungVoter = Constraint[InprogressOverseas]("validateYoungVoter") { app =>
    val errorKeys = List(
      if (app.dob.isDefined) None else Some(keys.dob),
      if (!app.previouslyRegistered.exists(_.hasPreviouslyRegistered == false))
        Some(keys.previouslyRegistered) else None,
      if (app.lastRegisteredToVote.isDefined) None else Some(keys.lastRegisteredToVote),  
      if (app.dateLeftUk.isDefined) None else Some(keys.dateLeftUk),
      app.overseasParentName.map { overseasParentName => 
        overseasParentName.name match { 
          case Some(name) => None
          case None => Some(keys.overseasParentName.parentName)
        }
      },
      app.overseasParentName.map { overseasParentName => 
        overseasParentName.previousName match { 
          case Some(previousName) => None
          case None => Some(keys.overseasParentName.parentPreviousName)
        }
      },
//      if (app.parentName.isDefined) None else Some(keys.parentName),
//      if (app.parentPreviousName.isDefined) None else Some(keys.parentPreviousName),
      if (app.parentsAddress.isDefined) None else Some(Key("parentsAddress")),
      if (app.passport.isDefined) None else Some(keys.passport),
      app.overseasName match {
        case Some(overseasName) if (overseasName.name.isDefined) => None 
        case _ => Some(keys.overseasName.name)
      },
      app.overseasName match {
        case Some(overseasName) if (overseasName.previousName.isDefined) => None 
        case _ => Some(keys.overseasName.previousName)
      },
      if (app.nino.isDefined) None else Some(keys.nino),
      if (app.address.isDefined) None else Some(keys.overseasAddress),
      if (app.openRegisterOptin.isDefined) None else Some(keys.openRegister),
      if (app.waysToVote.isDefined) None else Some(keys.waysToVote),
      if (app.postalOrProxyVote.isDefined) None else Some(keys.postalOrProxyVote),
      if (app.contact.isDefined) None else Some(keys.contact)
//      if (app.parentsName.isDefined) None else Some(Key("parentsName")),
//      if (app.parentsPreviousName.isDefined) None else Some(Key("parentsPreviousName")),

    ).flatten
    if (errorKeys.size == 0) {
      Valid
    } else {
      Invalid ("Please complete this step", errorKeys:_*)
    }
  }

  lazy val validateSpecialVoter = Constraint[InprogressOverseas]("validateSpecialVoter") { app =>
    val errorKeys = List(
      if (app.dob.isDefined) None else Some(keys.dob),
      if (!app.previouslyRegistered.exists(_.hasPreviouslyRegistered == false))
        Some(keys.previouslyRegistered) else None,
      if (app.lastRegisteredToVote.isDefined) None else Some(keys.lastRegisteredToVote),
      if (app.dateLeftSpecial.isDefined) None else Some(keys.dateLeftSpecial),
      if (app.lastUkAddress.isDefined) None else Some(keys.lastUkAddress),
      if (app.passport.isDefined) None else Some(keys.passport),
      app.overseasName match {
        case Some(overseasName) if (overseasName.name.isDefined) => None 
        case _ => Some(keys.overseasName.name)
      },
      app.overseasName match {
        case Some(overseasName) if (overseasName.previousName.isDefined) => None 
        case _ => Some(keys.overseasName.previousName)
      },
      if (app.nino.isDefined) None else Some(keys.nino),
      if (app.address.isDefined) None else Some(keys.overseasAddress),
      if (app.openRegisterOptin.isDefined) None else Some(keys.openRegister),
      if (app.waysToVote.isDefined) None else Some(keys.waysToVote),
      if (app.postalOrProxyVote.isDefined) None else Some(keys.postalOrProxyVote),
      if (app.contact.isDefined) None else Some(keys.contact)
    ).flatten
    if (errorKeys.size == 0) {
      Valid
    } else {
      Invalid ("Please complete this step", errorKeys:_*)
    }
  }

  lazy val validateNewVoter = Constraint[InprogressOverseas]("validateNewVoter") { app =>
    val errorKeys = List(
      if (app.dob.isDefined) None else Some(keys.dob),
      if (!app.previouslyRegistered.exists(_.hasPreviouslyRegistered == false))
        Some(keys.previouslyRegistered) else None,
      if (app.dateLeftUk.isDefined) None else Some(keys.dateLeftUk),
      if (app.lastUkAddress.isDefined) None else Some(keys.lastUkAddress),
      if (app.passport.isDefined) None else Some(keys.passport),
      app.overseasName match {
        case Some(overseasName) if (overseasName.name.isDefined) => None 
        case _ => Some(keys.overseasName.name)
      },
      app.overseasName match {
        case Some(overseasName) if (overseasName.previousName.isDefined) => None 
        case _ => Some(keys.overseasName.previousName)
      },
      if (app.nino.isDefined) None else Some(keys.nino),
      if (app.address.isDefined) None else Some(keys.overseasAddress),
      if (app.openRegisterOptin.isDefined) None else Some(keys.openRegister),
      if (app.waysToVote.isDefined) None else Some(keys.waysToVote),
      if (app.postalOrProxyVote.isDefined) None else Some(keys.postalOrProxyVote),
      if (app.contact.isDefined) None else Some(keys.contact)
    ).flatten
    if (errorKeys.size == 0) {
      Valid
    } else {
      Invalid ("Please complete this step", errorKeys:_*)
    }
  }

  lazy val validateRenewerVoter = Constraint[InprogressOverseas]("validateRenewerVoter") { app =>
    val validationErrors = Seq (
      if (app.dob.isDefined) None else Some(keys.dob),
      if (!app.previouslyRegistered.exists(_.hasPreviouslyRegistered == true))
        Some(keys.previouslyRegistered) else None,
      if (app.dateLeftUk.isDefined) None else Some(keys.dateLeftUk),
      if (app.lastUkAddress.isDefined) None else Some(keys.lastUkAddress),
      app.overseasName match {
        case Some(overseasName) if (overseasName.name.isDefined) => None 
        case _ => Some(keys.overseasName.name)
      },
      app.overseasName match {
        case Some(overseasName) if (overseasName.previousName.isDefined) => None 
        case _ => Some(keys.overseasName.previousName)
      },
      if (app.nino.isDefined) None else Some(keys.nino),
      if (app.address.isDefined) None else Some(keys.overseasAddress),
      if (app.openRegisterOptin.isDefined) None else Some(keys.openRegister),
      if (app.waysToVote.isDefined) None else Some(keys.waysToVote),
      if (app.postalOrProxyVote.isDefined) None else Some(keys.postalOrProxyVote),
      if (app.contact.isDefined) None else Some(keys.contact)
    ).flatten

    if (validationErrors.size == 0)
      Valid
    else
      Invalid ("Please complete this step", validationErrors:_*)
  }
}
