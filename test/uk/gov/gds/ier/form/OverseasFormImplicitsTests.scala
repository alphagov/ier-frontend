package uk.gov.gds.ier.form

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}

class OverseasFormImplicitsTests
  extends FlatSpec
  with Matchers
  with FormKeys
  with TestHelpers
  with OverseasFormImplicits {

  val serialiser = jsonSerialiser

  behavior of "OverseasFormImplicits.identifyApplication"
  it should "properly identify a young voter" in {
    //young voter:
    //Under 18 when left uk
    //Never registered before
    //Never registered overseas before
    val twentyYearsAgo = new DateTime().minusYears(20).getYear
    val fiveYearsAgo = new DateTime().minusYears(5).getYear

    val youngVoter = InprogressOverseas(
      dob = Some(DOB(year = twentyYearsAgo, month = 12, day = 1)),
      dateLeftUk = Some(DateLeft(year = fiveYearsAgo, month = 1))
    )

    youngVoter.identifyApplication should be(ApplicationType.YoungVoter)
  }

  it should "properly identify a new voter whose been registered in uk" in {
    //new voter:
    //Never registered or registered as uk resident
    //never registered overseas before
    val newVoter = InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(hasPreviouslyRegistered = false)),
      lastRegisteredToVote = Some(LastRegisteredToVote(
        lastRegisteredType = LastRegisteredType.Ordinary
      ))
    )

    newVoter.identifyApplication should be(ApplicationType.NewVoter)
  }

  it should "properly identify a renewer voter" in {
    //renewer voter:
    //registered as overseas previously
    val renewerVoter = InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(hasPreviouslyRegistered = true))
    )

    renewerVoter.identifyApplication should be(ApplicationType.RenewerVoter)
  }

  it should "properly identify a special voter" in {
    //special voter:
    //registered as a forces / crown / council voter previously
    val specialVoter = InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(hasPreviouslyRegistered = false)),
      lastRegisteredToVote = Some(LastRegisteredToVote(
        lastRegisteredType = LastRegisteredType.Forces
      ))
    )

    specialVoter.identifyApplication should be(ApplicationType.SpecialVoter)
  }

  it should "properly identify a DontKnow case" in {
    val dunno = InprogressOverseas()

    dunno.identifyApplication should be(ApplicationType.DontKnow)
  }
}
