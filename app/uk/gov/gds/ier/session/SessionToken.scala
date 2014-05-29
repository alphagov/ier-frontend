package uk.gov.gds.ier.session

import org.joda.time.DateTime

case class SessionToken(
    timestamp: DateTime = DateTime.now,
    history: List[DateTime] = List.empty
) {
  require(history.size <= 50)

  def refreshToken() = {
    this.copy(
      timestamp = DateTime.now,
      history = (timestamp +: history).take(50)
    )
  }
}
