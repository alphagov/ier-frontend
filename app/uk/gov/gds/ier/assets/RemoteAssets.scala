package uk.gov.gds.ier.assets

import play.api.mvc.Call
import controllers.routes.{Assets => PlayAssetRouter, MessagesController}
import controllers.routes.{Template => TemplateAssetRouter}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config

class RemoteAssets @Inject() (config : Config) {

  def at = getAssetPath _

  def templateAt = getTemplatePath _

  def getAssetPath(file:String) : Call = {
    val playAsset : Call = PlayAssetRouter.at(file)
    playAsset.copy(
      url = appendAssetPath(playAsset.url)
    )
  }

  def getTemplatePath(file:String) : Call = {
    val templateAsset : Call = TemplateAssetRouter.at(file)
    templateAsset.copy(
      url = appendAssetPath(templateAsset.url)
    )
  }

  def messages() : Call = {
    val playRoutedMessages = MessagesController.all()
    playRoutedMessages.copy(
      url = appendAssetPath(playRoutedMessages.url)
    )
  }

  def assetsPath = config.assetsPath.stripSuffix("/") + "/"

  private def appendAssetPath(url:String):String = {
    config.assetsPath.stripSuffix("/") + "/" + url.stripPrefix("/assets/").stripPrefix("/")
  }
}