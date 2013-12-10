package uk.gov.gds.ier.logging

import org.slf4j.LoggerFactory
import org.joda.time.DateTime
import uk.gov.gds.ier.validation.ErrorTransformForm

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
  implicit class EasyGetErrorMessageError(form: ErrorTransformForm[_]) {
    def errorMessages(key:String) = form.errors(key).map(_.message)
    def globalErrorMessages = form.globalErrors.map(_.message)
    def prettyPrint = form.errors.map(error => s"${error.key} -> ${error.message}")
  }
}