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
import uk.gov.gds.ier.transaction.overseas.parentsAddress.ParentsAddressForms

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
  with ParentsAddressForms
  with OpenRegisterForms
  with NameForms
  with PassportForms
  with WaysToVoteForms
  with PostalOrProxyVoteForms
  with ContactForms
  with OverseasFormImplicits
  with CommonConstraints {

  val optInMapping = single(
    keys.optIn.key -> boolean
  )

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.overseasName.key -> optional(overseasNameMapping),
      keys.previouslyRegistered.key -> optional(previouslyRegisteredMapping),
      keys.dateLeftSpecial.key -> optional(dateLeftSpecialTypeMapping),
      keys.dateLeftUk.key -> optional(dateLeftUkMapping),
      keys.overseasParentName.key -> optional(overseasParentNameMapping),
      keys.lastRegisteredToVote.key -> optional(lastRegisteredToVoteMapping),
      keys.dob.key -> optional(dobMapping),
      keys.nino.key -> optional(ninoMapping),
      keys.lastUkAddress.key -> optional(partialAddressMapping),
      keys.parentsAddress.key -> optional(parentsPartialAddressMapping),
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

  private def validatePostalOrProxyVote (
      waysToVote: Option[WaysToVote],
      postalOrProxyVote: Option[PostalOrProxyVote]): Boolean = {
    waysToVote match {
      case None  => true
      case Some(WaysToVote(WaysToVoteType.InPerson)) if (!postalOrProxyVote.isDefined)  => true
      case Some(WaysToVote(WaysToVoteType.ByPost)) if (postalOrProxyVote.isDefined)  => true
      case Some(WaysToVote(WaysToVoteType.ByProxy)) if (postalOrProxyVote.isDefined) => true
      case _ => false
    }
  }
  
  lazy val validateOverseas = Constraint[InprogressOverseas]("validateOverseas") { app =>
    import uk.gov.gds.ier.model.ApplicationType._
    
    lazy val dobValidation = if (app.dob.isDefined) None else Some(keys.dob)
    def previouslyRegisteredValidation (hasPreviouslyRegisterd: Boolean)=       
      if (!app.previouslyRegistered.exists(_.hasPreviouslyRegistered == hasPreviouslyRegisterd))
        Some(keys.previouslyRegistered) else None
    lazy val lastRegisteredToVoteValidation = 
      if (app.lastRegisteredToVote.isDefined) None else Some(keys.lastRegisteredToVote)
    lazy val dateLeftUkValidation = if (app.dateLeftUk.isDefined) None else Some(keys.dateLeftUk)
    lazy val parentPreviousNameValidation = 
      if (app.overseasParentName.flatMap(_.previousName).isDefined) None
      else Some(keys.overseasParentName.parentPreviousName)
    lazy val parentNameValidation = 
      if (app.overseasParentName.flatMap(_.name).isDefined) None
      else Some(keys.overseasParentName.parentName)
    lazy val parentAddresValidation = 
      if (app.parentsAddress.isDefined) None 
      else Some(Key("parentsAddress"))
    lazy val passportValidation = if (app.passport.isDefined) None else Some(keys.passport)
    lazy val previousNameValidation = 
      if (app.overseasName.flatMap(_.previousName).isDefined) None
      else Some(keys.overseasName.previousName)
    lazy val nameValidation = 
      if (app.overseasName.flatMap(_.name).isDefined) None else Some(keys.overseasName.name)
    lazy val ninoValidation = if (app.nino.isDefined) None else Some(keys.nino)
    lazy val addressValidation = if (app.address.isDefined) None else Some(keys.overseasAddress)
    lazy val openRegisterValidation = 
      if (app.openRegisterOptin.isDefined) None 
      else Some(keys.openRegister)
    lazy val waysToVoteValidation = if (app.waysToVote.isDefined) None else Some(keys.waysToVote)
    lazy val postalOrProxyValidation = 
      if (validatePostalOrProxyVote(app.waysToVote, app.postalOrProxyVote)) None
      else Some(keys.postalOrProxyVote) 
    lazy val contactValidation = if (app.contact.isDefined) None else Some(keys.contact)
    lazy val dateLeftSpecialValidation = 
      if (app.dateLeftSpecial.isDefined) None else Some(keys.dateLeftSpecial)
    lazy val lastUkAddressValidation = 
      if (app.lastUkAddress.isDefined) None else Some(keys.lastUkAddress)
    
    lazy val youngerVoterErrorKeys = List(dobValidation, previouslyRegisteredValidation(false), 
      lastRegisteredToVoteValidation, dateLeftUkValidation, parentPreviousNameValidation,
      parentNameValidation, parentAddresValidation, passportValidation, previousNameValidation,
      nameValidation, ninoValidation, addressValidation, openRegisterValidation, 
      waysToVoteValidation, postalOrProxyValidation, contactValidation).flatten

    lazy val specialVoterErrorKeys = List(dobValidation, previouslyRegisteredValidation(false), 
      lastRegisteredToVoteValidation, dateLeftSpecialValidation, lastUkAddressValidation,
      passportValidation, previousNameValidation, nameValidation, ninoValidation,
      addressValidation, openRegisterValidation, waysToVoteValidation, postalOrProxyValidation,
      contactValidation).flatten
    
    lazy val newVoterErrorKeys = List(dobValidation, previouslyRegisteredValidation(false), 
      dateLeftUkValidation, lastUkAddressValidation, passportValidation,  
      previousNameValidation, nameValidation, ninoValidation,
      addressValidation, openRegisterValidation, waysToVoteValidation, postalOrProxyValidation,
      contactValidation).flatten
        
    lazy val renewerVoterErrorKeys = List(dobValidation, previouslyRegisteredValidation(true), 
      dateLeftUkValidation, lastUkAddressValidation, previousNameValidation, nameValidation, 
      ninoValidation, addressValidation, openRegisterValidation, waysToVoteValidation, 
      postalOrProxyValidation, contactValidation).flatten
      
    app.identifyApplication match {
      case YoungVoter => validateKeys(youngerVoterErrorKeys)
      case NewVoter => validateKeys(newVoterErrorKeys)
      case RenewerVoter => validateKeys(renewerVoterErrorKeys)
      case SpecialVoter => validateKeys(specialVoterErrorKeys)
      case DontKnow => validateBaseSetRequired(app)
    }
  }

  private def validateKeys(errorKeys: List[Key]) = { 
      if (errorKeys.size == 0) {
        Valid
      } else {
        Invalid ("Please complete this step", errorKeys:_*)
      }
  }
  
  lazy val validateBaseSetRequired = Constraint[InprogressOverseas]("validateBaseSet") {
    application => Invalid("Base set criteria not met", keys.name)
  }
}
