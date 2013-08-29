package uk.gov.gds.ier.serialiser

import play.api.libs.json._
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.common.json.JsonSerializer
import java.text.DateFormat

class JsonSerialiser extends Logging {
  def toJson(obj: AnyRef) = JsonSerializer.toJson(obj)
  def fromJson[T](json: String)(implicit m: Manifest[T]): T = JsonSerializer.fromJson[T](json)(m)
}
