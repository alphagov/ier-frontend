package uk.gov.gds.ier.transaction.ordinary.dateOfBirth

import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test.MockingTestSuite
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.ordinary.{InprogressOrdinary, OrdinaryControllers}
import uk.gov.gds.ier.transaction.ordinary.name.NameStep
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.DateOfBirth
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.Country
import uk.gov.gds.ier.model.noDOB
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.model.DOB
import scala.Some
import org.joda.time.LocalDate
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.service.ScotlandService
import org.mockito.Mockito._
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.Country
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.model.DOB
import scala.Some
import play.api.mvc.Call
import uk.gov.gds.ier.model.noDOB

class DateOfBirthStepMockedTests extends MockingTestSuite {

  it should "clear dob reason if the date of birth is provided" in runningApp {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]
    val mockedControllers = mock[OrdinaryControllers]

    val mockedAddressService = mock[AddressService]
    val mockedScotlandService = mock[ScotlandService]
    val mockNameStep = mock[NameStep]
    val mockRoutes = mock[Routes]

    val dobStep = new DateOfBirthStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets,
      mockedControllers,
      mockedAddressService,
      mockedScotlandService
    )

    when(mockedControllers.NameStep).thenReturn(mockNameStep)
    when(mockNameStep.isStepComplete(any[InprogressOrdinary])).thenReturn(false)
    when(mockNameStep.routing).thenReturn(mockRoutes)
    when(mockRoutes.get).thenReturn(Call("GET", "/name"))
    when(mockedScotlandService.isYoungScot(any[InprogressOrdinary])).thenReturn(false)

    val currentState = completeOrdinaryApplication.copy(
      dob = Some(DateOfBirth(
        dob = Some(DOB(1988, 1, 1)),
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is18to75)
        ))
      ))
    )

    val transferedState = dobStep.onSuccess(currentState, dobStep)
    val resultApplication = transferedState._1

    resultApplication.dob should be(Some(DateOfBirth(
      dob = Some(DOB(1988, 1, 1)),
      noDob = None)))
  }

  it should "clear nino and open reg objects if citizen is young scot (Address=None, Country = Scotland)" in runningApp {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]
    val mockedControllers = mock[OrdinaryControllers]
    val mockedAddressService = mock[AddressService]
    val mockedScotlandService = mock[ScotlandService]
    val mockNameStep = mock[NameStep]
    val mockRoutes = mock[Routes]

    val dobStep = new DateOfBirthStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets,
      mockedControllers,
      mockedAddressService,
      mockedScotlandService
    )

    when(mockedControllers.NameStep).thenReturn(mockNameStep)
    when(mockNameStep.isStepComplete(any[InprogressOrdinary])).thenReturn(false)
    when(mockNameStep.routing).thenReturn(mockRoutes)
    when(mockRoutes.get).thenReturn(Call("GET", "/name"))
    when(mockedScotlandService.isYoungScot(any[InprogressOrdinary])).thenReturn(true)

    val currentState = completeOrdinaryApplicationYoungScot.copy(
      address = None,
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None
      )),
      openRegisterOptin = Some(false)
    )

    val transferedState = dobStep.onSuccess(currentState, dobStep)
    val resultApplication = transferedState._1

    resultApplication.dob should be(Some(DateOfBirth(
      dob = Some(DOB(LocalDate.now.minusYears(15).getYear, 1, 1)),
      noDob = None)))
    resultApplication.nino should be(None)
    resultApplication.openRegisterOptin should be(None)

  }

  it should "clear nino and open reg objects if citizen is young scot (Current Address in Scotland, Country = Scotland)" in runningApp {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]
    val mockedControllers = mock[OrdinaryControllers]
    val mockedAddressService = mock[AddressService]
    val mockedScotlandService = mock[ScotlandService]
    val mockNameStep = mock[NameStep]
    val mockRoutes = mock[Routes]

    val dobStep = new DateOfBirthStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets,
      mockedControllers,
      mockedAddressService,
      mockedScotlandService
    )

    when(mockedControllers.NameStep).thenReturn(mockNameStep)
    when(mockNameStep.isStepComplete(any[InprogressOrdinary])).thenReturn(false)
    when(mockNameStep.routing).thenReturn(mockRoutes)
    when(mockRoutes.get).thenReturn(Call("GET", "/name"))
    when(mockedScotlandService.isYoungScot(any[InprogressOrdinary])).thenReturn(true)

    val currentState = completeOrdinaryApplicationYoungScot.copy(
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None
      )),
      openRegisterOptin = Some(false)
    )

    val transferedState = dobStep.onSuccess(currentState, dobStep)
    val resultApplication = transferedState._1

    resultApplication.dob should be(Some(DateOfBirth(
      dob = Some(DOB(LocalDate.now.minusYears(15).getYear, 1, 1)),
      noDob = None)))
    resultApplication.nino should be(None)
    resultApplication.openRegisterOptin should be(None)

  }

  it should "NOT clear nino and open reg objects if citizen is _NOT_ young Scot (Current Address in England)" in runningApp {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]
    val mockedControllers = mock[OrdinaryControllers]
    val mockedAddressService = mock[AddressService]
    val mockedScotlandService = mock[ScotlandService]
    val mockNameStep = mock[NameStep]
    val mockRoutes = mock[Routes]

    val dobStep = new DateOfBirthStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets,
      mockedControllers,
      mockedAddressService,
      mockedScotlandService
    )

    when(mockedControllers.NameStep).thenReturn(mockNameStep)
    when(mockNameStep.isStepComplete(any[InprogressOrdinary])).thenReturn(false)
    when(mockNameStep.routing).thenReturn(mockRoutes)
    when(mockRoutes.get).thenReturn(Call("GET", "/name"))
    when(mockedScotlandService.isYoungScot(any[InprogressOrdinary])).thenReturn(false)

    val currentState = completeOrdinaryApplication.copy(
      address = Some(PartialAddress(
        addressLine = Some("123 Fake Street, Fakerton, Fakeville"),
        uprn =  Some("1234567890"),
        postcode = "L77AJ",
        manualAddress = None,
        gssCode = None
      )),
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
        noDob = None
      )),
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None
      )),
      openRegisterOptin = Some(false)
    )

    val transferedState = dobStep.onSuccess(currentState, dobStep)
    val resultApplication = transferedState._1

    resultApplication.dob should be(Some(DateOfBirth(
      dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
      noDob = None)))
    resultApplication.nino should be(Some(Nino(
      nino = Some("AB123456C"),
      noNinoReason = None
    )))
    resultApplication.openRegisterOptin should be(Some(false))

  }

  it should "NOT clear nino and open reg objects if citizen is _NOT_ young Scot (Address = None, Country = England)" in runningApp {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]
    val mockedControllers = mock[OrdinaryControllers]
    val mockedAddressService = mock[AddressService]
    val mockedScotlandService = mock[ScotlandService]
    val mockNameStep = mock[NameStep]
    val mockRoutes = mock[Routes]

    val dobStep = new DateOfBirthStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets,
      mockedControllers,
      mockedAddressService,
      mockedScotlandService
    )

    when(mockedControllers.NameStep).thenReturn(mockNameStep)
    when(mockNameStep.isStepComplete(any[InprogressOrdinary])).thenReturn(false)
    when(mockNameStep.routing).thenReturn(mockRoutes)
    when(mockRoutes.get).thenReturn(Call("GET", "/name"))
    when(mockedScotlandService.isYoungScot(any[InprogressOrdinary])).thenReturn(false)

    val currentState = completeOrdinaryApplication.copy(
      address = None,
      dob = Some(DateOfBirth(
        dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
        noDob = None
      )),
      country = Some(Country("England",false)),
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None
      )),
      openRegisterOptin = Some(false)
    )

    val transferedState = dobStep.onSuccess(currentState, dobStep)
    val resultApplication = transferedState._1

    resultApplication.dob should be(Some(DateOfBirth(
      dob = Some(DOB(LocalDate.now.minusYears(20).getYear, 1, 1)),
      noDob = None)))
    resultApplication.nino should be(Some(Nino(
      nino = Some("AB123456C"),
      noNinoReason = None
    )))
    resultApplication.openRegisterOptin should be(Some(false))

  }
}
