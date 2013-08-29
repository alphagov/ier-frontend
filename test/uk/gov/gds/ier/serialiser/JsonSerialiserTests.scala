package uk.gov.gds.ier.serialiser

import org.specs2.mutable._
import uk.gov.gds.common.json.JsonSerializer
import uk.gov.gds.ier.model.Success

class JsonSerialiserTests extends Specification {

  "JsonSerialiser" should {
    "be able to serialise a simple class" in {
      val jsonSerialiser = new JsonSerialiser()

      val jsonString = JsonSerializer.toJson(Success("bar"))
      jsonString must contain("bar")

      val mightbeFoo = JsonSerializer.fromJson[Success](jsonString)
      mightbeFoo.body mustEqual "bar"
    }
  }
}