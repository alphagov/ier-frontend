package uk.gov.gds.ier.form

object FieldHelpers {
  implicit class argsMapWithFilter(args:Map[Symbol, String]) {
    def without(keys:Symbol*) = {
      args.filterKeys(key => !keys.contains(key) && !key.name.startsWith("_"))
    }
  }

  def asAttributes = views.html.includes.asAttributes.apply _
}
