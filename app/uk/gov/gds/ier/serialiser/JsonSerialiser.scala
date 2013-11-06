package uk.gov.gds.ier.serialiser

import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.common.json.JsonSerializer

class JsonSerialiser extends Logging {
  def toJson(obj: AnyRef):String = {
    val json = JsonSerializer.toJson(obj)
    json
  }
  def fromJson[T](json: String)(implicit m: Manifest[T]): T = JsonSerializer.fromJson[T](json)(m)
}
