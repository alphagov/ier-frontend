package uk.gov.gds.ier.client

import scala.concurrent._
import scala.concurrent.duration._
import play.libs.Akka
import akka.actor.{Cancellable, Scheduler}

trait FuturesWithTimeouts3 {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  implicit class FutureOps[T](future: Future[T]) {

    def withTimeout(timeout: FiniteDuration)(timeoutHandler: => Unit): Future[T] = {
      val (timeoutFuture, timeoutCancellable) = scheduleAfter[T](timeout, scheduler = Akka.system.scheduler)(timeoutHandler)
      future.onComplete {
        case result => {
          println(s"cancel timeout with ${result}")
          timeoutCancellable.cancel()
        }
      }
      val combinedFuture = Future.firstCompletedOf[T](Seq(
        future,
        timeoutFuture
      ))
      combinedFuture
    }

    /**
     * rip off akka.pattern.FutureTimeoutSupport#after() (from akka.pattern.after)
     * original version does not return cancellable, only future
     */
    private def scheduleAfter[T](duration: FiniteDuration, scheduler: Scheduler)(handler: => Unit): (Future[T], Cancellable) = {
      val p = Promise[T]()
      val c: Cancellable = scheduler.scheduleOnce(duration)(handler)
      (p.future, c)
    }
  }
 }
