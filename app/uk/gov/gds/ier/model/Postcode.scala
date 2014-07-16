package uk.gov.gds.ier.model

object Postcode extends ModelMapping {

  def toApiFormat(postcode: String) = toFormat(postcode)

  def toDisplayFormat(postcode: String) = toFormat(postcode)


  private def toFormat(postcode: String) = {
    val cleanPostode = postcode.replaceAllLiterally(" ", "").toUpperCase
    if(cleanPostode.length > 3) {
      cleanPostode.take(cleanPostode.length-3)+" "+cleanPostode.takeRight(3)
    } else cleanPostode
  }

}
