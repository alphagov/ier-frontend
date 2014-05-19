package uk.gov.gds.ier.assets

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.config.Config

class RemoteAssetsTests extends FlatSpec with Matchers {

  behavior of "RemoteAssets.getAssetPath"

  it should "return an asset URL with the correct assetPath appended" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getAssetPath("some/file.txt")
    assetCall.url should be("/my-asset-path/some/file.txt")
  }

  it should "support extraneous / at the start of the file path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getAssetPath("/some/file.txt")
    assetCall.url should be("/my-asset-path/some/file.txt")
  }

  it should "support extraneous / at the end of the asset path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getAssetPath("some/file.txt")
    assetCall.url should be("/my-asset-path/some/file.txt")
  }

  behavior of "RemoteAssets.getTemplatePath"

  it should "return a template URL with the correct assetPath appended" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getTemplatePath("some/file.txt")
    assetCall.url should be("/my-asset-path/template/some/file.txt")
  }

  it should "support extraneous / at the start of the template file path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getTemplatePath("/some/file.txt")
    assetCall.url should be("/my-asset-path/template/some/file.txt")
  }

  it should "support extraneous / at the end of the asset path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getTemplatePath("some/file.txt")
    assetCall.url should be("/my-asset-path/template/some/file.txt")
  }

  behavior of "RemoteAssets.assetsPath"

  it should "return the assets path to be appended in mustaches" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
    }
    val remoteAssets = new RemoteAssets(config)

    remoteAssets.assetsPath should be("/my-asset-path/")
  }

  behavior of "RemoteAssets.templatePath"

  it should "return the assets path to be appended in mustaches" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
    }
    val remoteAssets = new RemoteAssets(config)

    remoteAssets.templatePath should be("/my-asset-path/template/")
  }
}
