package uk.gov.gds.ier.serialiser

import play.api.libs.json._
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.common.json.JsonSerializer
import java.text.DateFormat
import com.fasterxml.jackson.databind.ObjectMapper

class JsonSerialiser extends Logging {
  def toJson(obj: AnyRef):String = JsonSerializer.toJson(obj)
  def fromJson[T](json: String)(implicit m: Manifest[T]): T = JsonSerializer.fromJson[T](json)(m)
}
