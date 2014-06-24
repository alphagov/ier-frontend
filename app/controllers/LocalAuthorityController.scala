package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.localAuthority.{LocalAuthorityController => GuicedController}

object LocalAuthorityController extends DelegatingController[GuicedController]{

  def showLookup(sourcePath:Option[String]) = delegate.showLookup(sourcePath)
  def doLookup(sourcePath:Option[String]) = delegate.doLookup(sourcePath)
  def ero(gssCode:String, sourcePath:Option[String]) = delegate.ero(gssCode, sourcePath)
}
