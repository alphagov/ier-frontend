package uk.gov.gds.ier.form

import play.api.data.Field

object FieldHelpers {
  implicit class argsMapWithFilter(args:Map[Symbol, String]) {
    def without(keys:Symbol*) = {
      args.filterKeys(key => !keys.contains(key) && !key.name.startsWith("_"))
    }
  }
  def getValue(field:Field, attributes:Map[Symbol, String]) = {
    attributes.get('value) orElse field.value match {
      case Some(value) => value
      case None => attributes.getOrElse('default_value, "")
    }
  }
  def asAttributes = views.html.includes.asAttributes.apply _
}
