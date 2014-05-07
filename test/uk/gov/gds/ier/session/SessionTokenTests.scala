package uk.gov.gds.ier.session

import org.scalatest.{Matchers, FlatSpec}
import org.joda.time.DateTime

class SessionTokenTests extends FlatSpec with Matchers {

  behavior of "SessionToken.init"
  it should "init with a new Timestamp" in {
    val token = SessionToken()
    token.timestamp.getMillis should equal(DateTime.now.getMillis +- 500)
  }

  it should "init with an empty history" in {
    val token = SessionToken()
    token.history should be(List.empty)
  }

  behavior of "SessionToken.refreshToken"
  it should "return a fresh timestamp" in {
    val token = SessionToken()

    Thread.sleep(5)

    val refreshed = token.refreshToken()
    refreshed.timestamp should not be token.timestamp
  }

  it should "store the previous timestamp in the history" in {
    val token = SessionToken()
    val refreshed = token.refreshToken()
    refreshed.history.size should be(1)
    refreshed.history should contain(token.timestamp)
  }

  it should "maintain a long running history" in {
    val sessionToken = SessionToken()

    val result = (1 to 100).foldLeft(sessionToken) { case (token, i) =>
      val refreshed = token.refreshToken()
      refreshed.history should contain(token.timestamp)
      refreshed
    }

    result.history.size should be(100)
    result.history should contain(sessionToken.timestamp)
  }

  it should "have a maximum limit to the history" in {
    val sessionToken = SessionToken()

    val result = (1 to 200).foldLeft(sessionToken) { case (token, i) =>
      val refreshed = token.refreshToken()
      refreshed.history should contain(token.timestamp)
      refreshed
    }

    result.history.size should not be 200
    result.history.size should be(100)
    result.history should not contain sessionToken.timestamp
  }
}
