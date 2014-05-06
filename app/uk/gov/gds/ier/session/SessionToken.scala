package uk.gov.gds.ier.session

import org.joda.time.DateTime

case class SessionToken(
    timestamp: DateTime = DateTime.now
)
