package uk.gov.gds.ier.step

trait InprogressApplication[T] {
  def merge(other: T):T
}
