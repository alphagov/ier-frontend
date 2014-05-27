package uk.gov.gds.ier.assets

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.config.Config
import play.api.test.FakeRequest

class RemoteAssetsTests extends FlatSpec with Matchers {

  behavior of "RemoteAssets.getAssetPath"

  it should "return an asset URL with the correct assetPath appended" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getAssetPath("some/file.txt")
    assetCall should have(
      'url ("/my-asset-path/abcdef1234567890abcdef1234567890abcdef12/some/file.txt")
    )
  }

  it should "support extraneous / at the start of the file path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getAssetPath("/some/file.txt")
    assetCall should have(
      'url ("/my-asset-path/abcdef1234567890abcdef1234567890abcdef12/some/file.txt")
    )
  }

  it should "support extraneous / at the end of the asset path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getAssetPath("some/file.txt")
    assetCall should have(
      'url ("/my-asset-path/abcdef1234567890abcdef1234567890abcdef12/some/file.txt")
    )
  }

  behavior of "RemoteAssets.getTemplatePath"

  it should "return a template URL with the correct assetPath appended" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getTemplatePath("some/file.txt")
    assetCall should have(
      'url ("/my-asset-path/abcdef1234567890abcdef1234567890abcdef12/template/some/file.txt")
    )
  }

  it should "support extraneous / at the start of the template file path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getTemplatePath("/some/file.txt")
    assetCall should have(
      'url ("/my-asset-path/abcdef1234567890abcdef1234567890abcdef12/template/some/file.txt")
    )
  }

  it should "support extraneous / at the end of the asset path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    val assetCall = remoteAssets.getTemplatePath("some/file.txt")
    assetCall should have(
      'url ("/my-asset-path/abcdef1234567890abcdef1234567890abcdef12/template/some/file.txt")
    )
  }

  behavior of "RemoteAssets.assetsPath"

  it should "return the assets path to be appended in mustaches" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    remoteAssets.assetsPath should be(
      "/my-asset-path/abcdef1234567890abcdef1234567890abcdef12/"
    )
  }

  behavior of "RemoteAssets.templatePath"

  it should "return the assets path to be appended in mustaches" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    remoteAssets.templatePath should be(
      "/my-asset-path/abcdef1234567890abcdef1234567890abcdef12/template/"
    )
  }

  behavior of "RemoteAssets.stripGitSha"

  it should "not influence non-asset paths" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    val fakeRequest = FakeRequest("GET", "/register-to-vote/name")

    remoteAssets.stripGitSha(fakeRequest) should have(
      'path ("/register-to-vote/name"),
      'uri ("/register-to-vote/name")
    )
  }

  it should "not influence asset paths without a git sha in them" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    val fakeRequest = FakeRequest("GET", "/assets/stylesheets/fonts.css")

    remoteAssets.stripGitSha(fakeRequest) should have(
      'path ("/assets/stylesheets/fonts.css"),
      'uri ("/assets/stylesheets/fonts.css")
    )
  }

  it should "strip a git sha from an asset path" in {
    val config = new Config {
      override def assetsPath = "/my-asset-path/"
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
    val remoteAssets = new RemoteAssets(config)

    val fakeRequest = FakeRequest(
      "GET",
      "/assets/1da3e56aea484b525430ac05abd4146503144416/stylesheets/fonts.css"
    )

    remoteAssets.stripGitSha(fakeRequest) should have(
      'path ("/assets/stylesheets/fonts.css"),
      'uri ("/assets/stylesheets/fonts.css")
    )
  }
}
