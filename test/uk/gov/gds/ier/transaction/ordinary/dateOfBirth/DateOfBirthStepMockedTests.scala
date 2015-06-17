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

class DateOfBirthStepMockedTests extends MockingTestSuite {

  it should "clear dob reason if the date of birth is provided" in runningApp {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]
    val mockedControllers = mock[OrdinaryControllers]
    val mockNameStep = mock[NameStep]
    val mockRoutes = mock[Routes]

    val dobStep = new DateOfBirthStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets,
      mockedControllers
    )

    when(mockedControllers.NameStep).thenReturn(mockNameStep)
    when(mockNameStep.isStepComplete(any[InprogressOrdinary])).thenReturn(false)
    when(mockNameStep.routing).thenReturn(mockRoutes)
    when(mockRoutes.get).thenReturn(Call("GET", "/name"))

    val currentState = completeOrdinaryApplication.copy(
      dob = Some(DateOfBirth(
        dob = Some(DOB(1988, 1, 1)),
        noDob = Some(noDOB(
          reason = Some("test reason"),
          range = Some(DateOfBirthConstants.is18to70)
        ))
      ))
    )

    val transferedState = dobStep.onSuccess(currentState, dobStep)
    val resultApplication = transferedState._1

    resultApplication.dob should be(Some(DateOfBirth(
      dob = Some(DOB(1988, 1, 1)),
      noDob = None)))
  }

  it should "clear nino and open reg objects if citizen is young scot" in runningApp {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]
    val mockedControllers = mock[OrdinaryControllers]
    val mockNameStep = mock[NameStep]
    val mockRoutes = mock[Routes]

    val dobStep = new DateOfBirthStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets,
      mockedControllers
    )

    when(mockedControllers.NameStep).thenReturn(mockNameStep)
    when(mockNameStep.isStepComplete(any[InprogressOrdinary])).thenReturn(false)
    when(mockNameStep.routing).thenReturn(mockRoutes)
    when(mockRoutes.get).thenReturn(Call("GET", "/name"))

    val currentState = completeOrdinaryApplication.copy(
      dob = Some(DateOfBirth(
        dob = Some(DOB(2000, 1, 1)),
        noDob = None
      )),
      country = Some(Country("Scotland",false)),
      nino = Some(Nino(
        nino = Some("AB123456C"),
        noNinoReason = None
      )),
      openRegisterOptin = Some(false)
    )

    val transferedState = dobStep.onSuccess(currentState, dobStep)
    val resultApplication = transferedState._1

    resultApplication.dob should be(Some(DateOfBirth(
      dob = Some(DOB(2000, 1, 1)),
      noDob = None)))
    resultApplication.nino should be(None)
    resultApplication.openRegisterOptin should be(None)

  }
}
