package uk.gov.gds.ier.controller

import uk.gov.gds.ier.test.{WithMockRemoteAssets, FakeApplicationRedefined, ControllerTestSuite}
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.config.Config
import play.api.GlobalSettings
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.mustache.ErrorPageMustache
import uk.gov.gds.ier.guice.WithRemoteAssets
import uk.gov.gds.ier.assets.RemoteAssets
import play.api.mvc.Call
import controllers.routes._
import play.api.mvc.Call
import scala.Some

import play.api.mvc.{RequestHeader, Call}
import controllers.routes.{Assets => PlayAssetRouter, MessagesController}
import controllers.routes.{Template => TemplateAssetRouter}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config

class AssetsControllerTests extends ControllerTestSuite {

//  val stubGlobal = new DynamicGlobal {
//    override lazy val config = new Config {
//      override def revision = "abcdef1234567890abcdef1234567890abcdef10"
//    }
//    override def onStart(app: Application) { println("Hello world!") }
//  }

  val fakeConfig =  new Config {
    override def revision = "abcdef1234567890abcdef1234567890abcdef12"
  }

  class FakeSettings
    extends GlobalSettings
    with Logging
    with WithRemoteAssets {

    val remoteAssets = null//new FakeRemoteAssets

    val config = null

  }

  class FakeRemoteAssets extends RemoteAssets(fakeConfig){
    override def getAssetPath(file:String) : Call = {
      throw new Exception("Aasdfasdf")

      PlayAssetRouter.at(file)
    }

    override def assetsPath: String = throw new Exception("asdfasdf")

    override def templatePath: String = throw new Exception("asdfasdf")

  }

  val stubGlobal = new FakeSettings

  behavior of "Retrieving assets"
  it should "return asset without adding pragma: no-cache to the header for known sha" in {

    running(FakeApplication(withGlobal = Some(stubGlobal))) {
      val Some(result) = route(FakeRequest(GET,
        "/assets/abcdef1234567890abcdef1234567890abcdef12/template/stylesheets/fonts.css"))

      status(result) should be(OK)
      headers(result) should not contain("Pragma" -> "no-cache")
    }
  }

//  it should "return asset without adding pragma: no-cache to the header for no sha" in {
//    running(FakeApplication(withGlobal = Some(stubGlobal))) {
//      val Some(result) = route(FakeRequest(GET,
//        "/assets/template/stylesheets/fonts.css"))
//
//      status(result) should be(OK)
//      headers(result) should not contain("Pragma" -> "no-cache")
//    }
//  }
//
//  it should "return asset with pragma: no-cache for unrecognised sha" in {
//    running(FakeApplication(withGlobal = Some(stubGlobal))) {
//      val Some(result) = route(FakeRequest(GET,
//        "/assets/atestf1234567890atestf1234567890atestf00/template/stylesheets/fonts.css"))
//
//      status(result) should be(OK)
//      headers(result) should contain("Pragma" -> "no-cache")
//    }
//  }

}
