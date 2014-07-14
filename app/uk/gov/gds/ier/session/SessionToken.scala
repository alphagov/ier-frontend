package uk.gov.gds.ier.session

import org.joda.time.{Seconds, DateTime}

case class SessionToken(
    start: DateTime = DateTime.now,
    latest: DateTime = DateTime.now,
    history: List[Int] = List.empty,
    id: Option[String] = Some(java.util.UUID.randomUUID.toString)
) {
  require(history.size <= 50)

  def timeTaken(): String = {
    Seconds.secondsBetween(start, latest).getSeconds.toString
  }

  def refreshToken() = {
    val now = DateTime.now
    val delta = Seconds.secondsBetween(latest, now).getSeconds
    this.copy(
      latest = now,
      history = (delta +: history).take(50),
      id = id orElse Some(java.util.UUID.randomUUID.toString)
    )
  }
}
