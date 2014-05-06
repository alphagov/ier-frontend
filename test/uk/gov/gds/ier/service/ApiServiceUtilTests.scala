package uk.gov.gds.ier.service

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.service.apiservice.ApiServiceUtil

class ApiServiceUtilTests extends FlatSpec with Matchers{

  it should "keep the string as it is if it doesn't contain special characters" in {
    val apiMap = Map ("a" -> "Hello world", "b" -> "Hello gds")
    val result = ApiServiceUtil.removeSpecialCharacters(apiMap)
    result should be (apiMap)
  }

  it should "remove defined special characters" in {
    val apiMap = Map ("a" -> "Hel<l>o world|", "b" -> "H<e>llo gd|s")
    val result = ApiServiceUtil.removeSpecialCharacters(apiMap)
    val expect = Map ("a" -> "Hello world", "b" -> "Hello gds")

    result should be (expect)

  }
}