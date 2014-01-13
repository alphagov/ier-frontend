package uk.gov.gds.ier.model

case class InprogressOverseas(previouslyRegistered: Option[PreviouslyRegistered] = None) extends InprogressApplication[InprogressOverseas] {

  def merge(other:InprogressOverseas) = {
    other.copy(this.previouslyRegistered.orElse(other.previouslyRegistered))
  }
}

case class PreviouslyRegistered(hasPreviouslyRegistered:Boolean)