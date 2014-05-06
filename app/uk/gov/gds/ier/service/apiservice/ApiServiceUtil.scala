package uk.gov.gds.ier.service.apiservice

object ApiServiceUtil {

  def removeSpecialCharacters (apiMap: Map[String, String]): Map[String, String] = {
    apiMap.par.map {
      case (key, value) => key -> value.replaceAll("[<>|]", "")
    }.seq
  }
}