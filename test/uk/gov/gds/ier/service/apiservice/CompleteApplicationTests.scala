package uk.gov.gds.ier.service.apiservice

import org.scalatest.{Matchers, FlatSpec}

class CompleteApplicationTests extends FlatSpec with Matchers with CompleteApplication {

   val toApiMap:Map[String, String] = Map.empty

  it should "keep the string as it is if it doesn't contain special characters" in {
    val apiMap = Map ("a" -> "Hello world", "b" -> "Hello gds")
    val result = removeSpecialCharacters(apiMap)
    result should be (apiMap)
  }

  it should "remove defined special characters" in {
    val apiMap = Map ("a" -> "Hel<l>o world|", "b" -> "H<e>llo gd|s")
    val expect = Map ("a" -> "Hello world", "b" -> "Hello gds")
    val result = removeSpecialCharacters(apiMap)
    result should be (expect)
  }
}