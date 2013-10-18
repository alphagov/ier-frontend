package uk.gov.gds.ier.validation

import org.joda.time.DateTime

object DateOfBirthConstants {
  lazy val days = (1 to 31).toSeq.map(i => (i.toString, i.toString))

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
  ).zip((1 to 12).map(_.toString)).
    map { case (name, number) => (number, name) }

  lazy val monthsByNumber = months.toMap

  lazy val years = {
    lazy val now = new DateTime()
    (now.getYear to 1899 by -1).toSeq.map(i => (i.toString, i.toString))
  }
}
