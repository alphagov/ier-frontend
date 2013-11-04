package uk.gov.gds.ier.logging

import org.slf4j.LoggerFactory
import org.joda.time.DateTime

trait Logging {
  val logger = LoggerFactory.getLogger(this.getClass)

  def timeThis[A](message:String)(block: => A):A = {
    val timeAtStart = DateTime.now.getMillis
    try {
      block
    } finally {
      val timeAtEnd = DateTime.now.getMillis
      logger.info(s"message: $message timeTaken: ${timeAtEnd - timeAtStart}")
    }
  }
}