package uk.gov.gds.ier.transaction.crown.waysToVote

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.specs2.mock.Mockito
import com.google.inject.Singleton
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{WaysToVote, WaysToVoteType, PostalOrProxyVote, PostalVoteDeliveryMethod} 

class WaysToVoteStepTests extends FlatSpec with TestHelpers with Matchers with Mockito {

  it should "clean postal or proxy vote info if the applicant choose to vote in person " +
    "when submitting the form successfully" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]

    val waysToVoteStep = new WaysToVoteStep(mockedJsonSerialiser, mockedConfig,
        mockedEncryptionService)

    val currentState = completeCrownApplication.copy(waysToVote = Some(WaysToVote(
        WaysToVoteType.InPerson)), 
        postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      )))

    val transferedState = waysToVoteStep.postSuccess(currentState)
    transferedState.postalOrProxyVote should be (None)
  }
  
  it should "set forceRedirectToPostal to true if the user wants to edit the page" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]

    val waysToVoteStep = new WaysToVoteStep(mockedJsonSerialiser, mockedConfig,
        mockedEncryptionService)

    val currentState = completeCrownApplication.copy(waysToVote = Some(WaysToVote(
        WaysToVoteType.ByPost)), 
        postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(true),
        deliveryMethod = Some(PostalVoteDeliveryMethod(
          deliveryMethod = Some("post"),
          emailAddress = None
        ))
      )))

    val transferedState = waysToVoteStep.postSuccess(currentState)
    transferedState.postalOrProxyVote.exists(_.forceRedirectToPostal == true) should be (true)
  }
}
