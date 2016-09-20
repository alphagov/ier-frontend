package uk.gov.gds.ier.controller

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.test.ControllerTestSuite

class StatusControllerTests extends ControllerTestSuite {

  val stubGlobal = new DynamicGlobal {
    override def bindings = { binder =>
      binder bind classOf[Config] to classOf[MockConfig]
    }
  }

  behavior of "StatusController.status"

}

class MockConfig extends Config {
  override def revision = "a1b2c3d54"
  override def buildDate = "12/01/2012"
  override def buildNumber = "0001"
  override def branch = "master"

  override def sessionTimeout = 20
  override def cookiesAesKey = "J1gs7djvi9/ecFHj0gNRbHHWIreobplsWmXnZiM2reo="
}
