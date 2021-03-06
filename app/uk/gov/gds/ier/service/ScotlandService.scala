package uk.gov.gds.ier.service

import com.google.inject.Inject
import uk.gov.gds.ier.model.Country
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.validation.{DateValidator, CountryValidator}
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants

class ScotlandService @Inject()(
    val addressService: AddressService
  ) {

  /*
   * Given a pair of postcode and country...
   * (THIS IS AN ALTERNATIVE isScot() where the calling class does not have access to the full currentState object)
   * 1. If the postcode is not BLANK, is the postcode within a Scottish GSS_CODE? return TRUE/FALSE
   * 2. Else is the country-of-residence selected Scotland? return TRUE/FALSE
   */
  def isScotByPostcodeOrCountry(postcode: String, country: Country):Boolean = {
    if (postcode.nonEmpty) {
      addressService.isScotAddress(postcode)
    } else {
      CountryValidator.isScotland(Some(country))
    }
  }
  /*
    Given a current application...
    1. If there's an address, is the postcode within a Scottish GSS_CODE? return TRUE/FALSE
    2. Else is the country-of-residence selected Scotland? return TRUE/FALSE
    3. Else return FALSE
   */
  def isScot(currentState: InprogressOrdinary):Boolean = {
    if(currentState.address.isDefined) {
      currentState.address match {
        case Some(partialAddress) => {
          val postcode = partialAddress.postcode.trim.toUpperCase
          addressService.isScotAddress(postcode)
        }
        case _ => {false}
      }
    }
    else {
      CountryValidator.isScotland(currentState.country)
    }
  }

  /*
    Given a current application...
    Extract the DOB and pass into the function to determine if 14/15 yrs old
    return TRUE/FALSE
    ASSUMPTION : DOB exists when this function is called
   */
  def isUnderageScot(currentState: InprogressOrdinary):Boolean = {
    DateValidator.isValidYoungScottishVoter(currentState.dob.get.dob.get)
  }

  /*
    Given a current application...
    Call both isScot() && isUnderageScot() and return TRUE only if both criteria are present
    Else return false
   */
  def isYoungScot(currentState: InprogressOrdinary):Boolean = {
    if(currentState.dob.get.noDob.isDefined) {
      isScot(currentState)
    }
    else{
      isScot(currentState) && isUnderageScot(currentState)
    }
  }

  /*
    Given a current application...
    Check the address / country status.
    If an actual DOB exists, then ignore this check entirely.  An actual DOB always takes presedence
    If SCO, then any non-SCO noDOB age range options selected need to be reset to force the citizen to reenter
    If non-SCO, then any SCO noDOB age range options selected need to be reset to force the citizen to reenter
   */
  def resetNoDOBRange(currentState: InprogressOrdinary): Boolean = {
    if(currentState.dob.isDefined) {
      if(currentState.dob.get.noDob.isDefined) {
        val dateOfBirthRangeOption = currentState.dob.get.noDob.get.range.get
        if(isScot(currentState)) {
          //Wipe DOB object if any non-SCO noDOB age range is currently stored
          dateOfBirthRangeOption match {
            case DateOfBirthConstants.under18 | DateOfBirthConstants.is18to75 | DateOfBirthConstants.over75 => return true
            case _ => return false
          }
        }
        else {
          //Wipe DOB object if any SCO noDOB age range is currently stored
          dateOfBirthRangeOption match {
            case DateOfBirthConstants.is14to15 | DateOfBirthConstants.is16to17 | DateOfBirthConstants.over18 => return true
            case _ => return false
          }
        }
      }
    }
    return false
  }
}
