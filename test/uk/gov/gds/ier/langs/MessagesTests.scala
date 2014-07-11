package uk.gov.gds.ier.langs

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers

class MessagesTests
  extends FlatSpec
  with Matchers
  with TestHelpers {

  behavior of "Messages.jsMessages"

  it should "include both English and Welsh messages" in runningApp {
    Messages.jsMessages.allMessages.keySet should be(Set("en", "cy", "default"))
  }

  it should "not include any play default errors" in runningApp {
    val enMap = Messages.jsMessages.allMessages("en")
    enMap.filterKeys(_ startsWith "error") should be(Map.empty)

    val cyMap = Messages.jsMessages.allMessages("cy")
    cyMap.filterKeys(_ startsWith "error") should be(Map.empty)
  }

  it should "have the same messages in cy and en" in runningApp {
    val enKeySet = Messages.jsMessages.allMessages("en").keySet.map{ a => (a -> a) }.toMap
    val cyKeySet = Messages.jsMessages.allMessages("cy").keySet.map{ a => (a -> a) }.toMap

    enKeySet should matchMap(cyKeySet)
  }
}
