package uk.gov.gds.ier.step

import org.joda.time.DateTime
import play.api.mvc._
import uk.gov.gds.ier.client.StatsdClient
import uk.gov.gds.ier.logging.Logging

trait StatsdWrapper extends Logging {

  def statsDTime[A](block: => Result)(implicit request: Request[A]) = {
    logger.info("Processing request with path  %s".format(request.path))

    val start = new DateTime()
    val response = block
    val timeTakenMs = DateTime.now.minus(start.getMillis).getMillis

    logger.info("Time taken to process request with method %s and path %s was %d".format(request.method, request.path, timeTakenMs))
    StatsdClient.timing("HttpRequestProcessingTime", timeTakenMs)

    response
  }
}