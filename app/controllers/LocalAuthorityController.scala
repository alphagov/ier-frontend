package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.localAuthority.{LocalAuthorityController => GuicedController}

object LocalAuthorityController extends DelegatingController[GuicedController]{

  def show = delegate.show
  def showLookup(sourcePath:Option[String]) = delegate.showLookup(sourcePath)
  def ero(gssCode:String, sourcePath:Option[String]) = delegate.ero(gssCode, sourcePath)
}
