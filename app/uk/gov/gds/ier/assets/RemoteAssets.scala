package uk.gov.gds.ier.assets

import play.api.mvc.Call
import controllers.routes.{Assets => PlayAssetRouter}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config

class RemoteAssets @Inject() (config : Config) {

  def getAssetPath(file:String) : Call = {
    val playAsset : Call = PlayAssetRouter.at(file)
    playAsset.copy(
      url = appendAssetPath(playAsset.url)
    )
  }

  private def appendAssetPath(url:String):String = {
    config.assetsPath.stripSuffix("/") + "/" + url.stripPrefix("/assets/").stripPrefix("/")
  }
}
