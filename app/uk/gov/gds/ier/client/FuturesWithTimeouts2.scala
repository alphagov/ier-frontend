package uk.gov.gds.ier.client

import scala.concurrent._
import akka.pattern.after
import scala.concurrent.duration._
import play.libs.Akka

/**
 * Working version of future timeout via implicits.
 * Works, but timer is not cancelled so the timeoutHandler is executed end ot the duration !!!
 */
trait FuturesWithTimeouts2 {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  implicit class FutureOps[T](future: Future[T]) {

    def withTimeout(timeout: FiniteDuration)(timeoutHandler: => Unit): Future[T] = {
      val timeoutFuture = after[T](timeout, using = Akka.system.scheduler) {
        timeoutHandler
        future
      }
      val combinedFuture = Future.firstCompletedOf[T](Seq(
        future,
        timeoutFuture
      ))
      combinedFuture
    }
  }
  // problem is, timeoutFuture does not have any cancel method, Future in Scala generally does not
  // have cancel functionality (see SO discussion)
 }
