package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.assets.{RemoteAssets => GuicedAssets}

object RemoteAssets extends DelegatingController[GuicedAssets]{

  def at(file:String) = delegate.getAssetPath(file)
  def messages = delegate.messages
}
