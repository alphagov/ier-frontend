package uk.gov.gds.ier.transaction.overseas.parentName

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.specs2.mock.Mockito


import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{Name,PreviousName, OverseasName}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test.TestHelpers

class ParentNameStepTests extends FlatSpec with TestHelpers with Matchers with Mockito {

  it should "reset the previous names if the has previous is false when submitting the form successfully" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]

    val parentNameStep = new ParentNameStep(mockedJsonSerialiser, mockedConfig,
        mockedEncryptionService)

    val currentState = completeOverseasApplication.copy(overseasParentName = Some(OverseasName(
        name = None, previousName =
      Some(PreviousName(false, Some(Name("john", None, "smith")))))))

    val transferedState = parentNameStep.postSuccess(currentState)
    transferedState.overseasParentName.get.previousName.isDefined should be (true)
    transferedState.overseasParentName.get.previousName.get.previousName should be (None)
  }
}
