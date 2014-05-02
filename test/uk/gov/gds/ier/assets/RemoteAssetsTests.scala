package uk.gov.gds.ier.assets

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.config.Config

class RemoteAssetsTests extends FlatSpec with Matchers {

  it should "return an asset URL with the correct assetPath appended" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.at("some/file.txt")
    assetCall.url should be("/my-asset-path/some/file.txt")
  }

  it should "support extraneous / at the start of the file path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.at("/some/file.txt")
    assetCall.url should be("/my-asset-path/some/file.txt")
  }

  it should "support extraneous / at the end of the asset path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.at("some/file.txt")
    assetCall.url should be("/my-asset-path/some/file.txt")
  }
}
