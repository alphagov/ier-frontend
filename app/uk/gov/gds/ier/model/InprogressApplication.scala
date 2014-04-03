package uk.gov.gds.ier.model

trait InprogressApplication[T] {
  def merge(other: T):T
}
