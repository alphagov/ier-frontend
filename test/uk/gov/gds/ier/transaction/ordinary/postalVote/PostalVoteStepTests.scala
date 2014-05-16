package uk.gov.gds.ier.transaction.ordinary.postalVote

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.specs2.mock.Mockito
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{PostalVote,PostalVoteDeliveryMethod}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.assets.RemoteAssets

class PostalVoteStepTests extends FlatSpec with TestHelpers with Matchers with Mockito {

  it should "reset the delivery method if postval voe is no when submitting the form successfully" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]
    val mockedRemoteAssets = mock[RemoteAssets]

    val postalVoteStep = new PostalVoteStep(
      mockedJsonSerialiser,
      mockedConfig,
      mockedEncryptionService,
      mockedRemoteAssets
    )

    val currentState = completeOrdinaryApplication.copy(postalVote = Some(PostalVote(
        postalVoteOption = Some(false), deliveryMethod =
      Some(PostalVoteDeliveryMethod(Some("email"), Some("test@test.com"))))))

    val transferedState = postalVoteStep.resetPostalVote.apply(currentState, postalVoteStep)
    transferedState._1.postalVote.get.deliveryMethod should be (None)
    transferedState._1.postalVote.get.postalVoteOption should be (Some(false))
  }
}
