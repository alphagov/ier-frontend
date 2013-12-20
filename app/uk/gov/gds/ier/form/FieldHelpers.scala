package uk.gov.gds.ier.form

import play.api.data.Field

object FieldHelpers {
  implicit class argsMapWithFilter(args:Map[Symbol, String]) {
    def without(keys:Symbol*) = {
      args.filterKeys(key => !keys.contains(key) && !key.name.startsWith("_"))
    }
  }
  def getValue(field:Field, attributes:Seq[(Symbol, String)]) = {
    attributes.toMap.get('value) orElse field.value match {
      case Some(value) => value
      case None => attributes.toMap.getOrElse('default_value, "")
    }
  }
  def asAttributes = views.html.includes.asAttributes.apply _
  def amountDescription(amount:Int, description:Map[String, String]) = {
    if (amount == 1) {
      amount.toString().concat(description("singular"))
    } else {
      amount.toString().concat(description("plural"))
    }
  }
}
