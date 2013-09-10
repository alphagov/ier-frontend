package uk.gov.gds.ier.model

import org.joda.time.DateTime

object DateOfBirthConstants {
  lazy val days = (1 to 31).toSeq

  lazy val months = Seq(
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
  )

  lazy val years = {
    lazy val now = new DateTime()
    (now.getYear to 1899 by -1).toSeq
  }
}
