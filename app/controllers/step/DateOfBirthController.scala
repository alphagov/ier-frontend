package controllers.step

import play.api._
import play.api.mvc._
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.step.dateOfBirth.{DateOfBirthController => GuicedController}

object DateOfBirthController extends DelegatingController[GuicedController] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
}
