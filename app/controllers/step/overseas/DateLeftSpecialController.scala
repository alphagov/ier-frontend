package controllers.step.overseas

import uk.gov.gds.ier.model.{DateLeftSpecial}
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.dateLeftSpecial.{DateLeftArmyStep, DateLeftCrownStep, 
  DateLeftCouncilStep}

object DateLeftArmyController extends DelegatingController[DateLeftArmyStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def dateLeftArmyStep = delegate
}

object DateLeftCrownController  extends DelegatingController[DateLeftCrownStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def dateLeftCrownStep = delegate
}

object DateLeftCouncilController  extends DelegatingController[DateLeftCouncilStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def dateLeftCouncilStep = delegate
}