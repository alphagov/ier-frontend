package uk.gov.gds.ier.session

import org.joda.time.DateTime

case class SessionToken(
    timestamp: DateTime = DateTime.now,
    history: List[DateTime] = List.empty
) {
  def refreshToken() = {
    this.copy(
      timestamp = DateTime.now,
      history = history :+ timestamp
    )
  }
}
