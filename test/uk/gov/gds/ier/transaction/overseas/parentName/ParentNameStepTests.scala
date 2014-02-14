package uk.gov.gds.ier.transaction.overseas.parentName

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.specs2.mock.Mockito

import com.google.inject.Singleton

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{ParentName,ParentPreviousName}
import uk.gov.gds.ier.security.{EncryptionKeys,EncryptionService}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test.TestHelpers

class ParentNameStepTests extends FlatSpec with TestHelpers with Matchers with Mockito {

  it should "reset the previous names if the has previous is false when submitting the form successfully" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedEncryptionKeys = mock[EncryptionKeys]
    
    val parentNameStep = new ParentNameStep(mockedJsonSerialiser, mockedConfig, 
        mockedEncryptionService, mockedEncryptionKeys)
    
    val currentState = completeOverseasApplication.copy(parentPreviousName = 
      Some(ParentPreviousName(false, Some(ParentName("john", None, "smith")))))
    
    val transferedState = parentNameStep.postSuccess(currentState)
    transferedState.parentPreviousName.isDefined should be (true)
    transferedState.parentPreviousName.get.previousName should be (None)
  }
}