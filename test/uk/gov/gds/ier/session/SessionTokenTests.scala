package uk.gov.gds.ier.session

import org.scalatest.{Matchers, FlatSpec}
import org.joda.time.{Minutes, Seconds, DateTime}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.security.{Base64EncodingService, EncryptionService}
import uk.gov.gds.ier.controller.MockConfig
import java.nio.charset.Charset

class SessionTokenTests
  extends FlatSpec
  with Matchers
  with TestHelpers {

  behavior of "SessionToken.init"
  it should "init with a new Timestamp" in {
    val token = SessionToken()
    token.start.getMillis should equal(DateTime.now.getMillis +- 500)
    token.latest.getMillis should equal(DateTime.now.getMillis +- 500)
  }

  it should "init with an empty history" in {
    val token = SessionToken()
    token.history should be(List.empty)
  }

  it should "init with a new id" in {
    val token1 = SessionToken()
    val token2 = SessionToken()

    token1.id should not be token2.id
    token1.id should not be SessionToken().id
    token2.id should not be SessionToken().id
  }

  behavior of "SessionToken.refreshToken"
  it should "keep the initial timestamp" in {
    val token = SessionToken(
      start = DateTime.now.minusMinutes(5),
      latest = DateTime.now.minusMinutes(2)
    )

    val refreshed = token.refreshToken()
    refreshed.start should be(token.start)
    refreshed.latest should not be token.latest
  }

  it should "store a timestamp delta in the history" in {
    val token = SessionToken(
      start = DateTime.now.minusSeconds(6),
      latest = DateTime.now.minusSeconds(5)
    )
    val refreshed = token.refreshToken()
    refreshed.history.size should be(1)
    refreshed.history should contain(5)
  }

  it should "maintain a long running history" in {
    val sessionToken = SessionToken()

    val result = (1 to 100).foldLeft(sessionToken) { case (token, i) =>
      val refreshed = token.refreshToken()
      refreshed.history should contain(
        Seconds.secondsBetween(token.latest, refreshed.latest).getSeconds
      )
      //Fake time between each request
      refreshed.copy(latest = DateTime.now.minusSeconds(i))
    }

    result.history.size should be(50)
  }

  it should "have a maximum limit to the history" in {
    val sessionToken = SessionToken()

    val result = (1 to 200).foldLeft(sessionToken) { case (token, i) =>
      val refreshed = token.refreshToken()
      refreshed.history should contain(
        Seconds.secondsBetween(token.latest, refreshed.latest).getSeconds
      )
      //Fake time between each request
      refreshed.copy(latest = DateTime.now.minusSeconds(i))
    }

    result.history.size should not be 200
    result.history.size should be(50)
  }

  it should "keep the initial id" in {
    val token = SessionToken()
    val refreshed = token.refreshToken()
    refreshed.id should be(token.id)
  }

  it should "generate a new id if no id was deserialised" in {
    val sessionToken = SessionToken(
      id = None
    )
    val json = jsonSerialiser.toJson(sessionToken)
    
    val token = jsonSerialiser.fromJson[SessionToken](json)

    token should have(
      'id (None)
    )

    token.refreshToken should not have(
      'id (None)
    )
  }

  behavior of "SessionToken when serialised and encrypted"
  it should "be under half a KB when serialised" in {
    //20 mins in seconds (the max size of a session delta)
    val history = (1 to 50).map(_ => 20 * 60).toList
    val sessionToken = SessionToken(
      start = DateTime.now.minusMinutes(30),
      latest = DateTime.now.minusMinutes(2),
      history = history
    )

    val json = jsonSerialiser.toJson(sessionToken)
    val encryption = new EncryptionService (new Base64EncodingService, new MockConfig)
    val (hash, iv) = encryption.encrypt(json)

    hash.getBytes(Charset.defaultCharset()).size should be < 560
  }
}
