package uk.gov.gds.ier.serialiser

trait WithSerialiser {
  def toJson(obj: AnyRef): String
  def fromJson[T](json: String)(implicit m: Manifest[T]): T
}