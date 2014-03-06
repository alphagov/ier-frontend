package uk.gov.gds.ier.transaction.forces.contactAddress

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.model.InprogressForces

trait ContactAddressMustache extends StepMustache {

  case class ContactAddressModel(
      question:Question,
      contactAddressFieldSet: FieldSet,
      ukAddressOption: Field,
      ukAddressLineText: Field,
      BFPOAddressOption: Field,
      BFPOAddressTextLine1: Field,
      BFPOAddressTextLine2: Field,
      BFPOAddressTextLine3: Field,
      BFPOAddressTextLine4: Field,
      BFPOAddressTextLine5: Field,
      BFPOAddressTextBFPO: Field,
      otherAddressOption: Field
  )
  
  def transformFormStepToMustacheData(
      form:ErrorTransformForm[InprogressForces],
      post: Call,
      back: Option[Call]): ContactAddressModel = {

    implicit val progressForm = form

    val application = progressForm.value

    val ukAddressToBeShown = if (application.isDefined) {
      val address = application.map(_.address).getOrElse(None)
      val addressLine = address.map(_.addressLine).getOrElse(None)
      addressLine match {
        case None => address.get.manualAddress
        case _ => addressLine
      }
    }
    else form(keys.contactAddress.ukAddressLine.key).value

    ContactAddressModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map (_.url).getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "9",
        title = "Where should we write to you about your registration?"
      ),
      contactAddressFieldSet = FieldSet (
        classes = if (form(keys.contactAddress.key).hasErrors) "invalid" else ""
      ),
      ukAddressOption = RadioField(
        key = keys.contactAddress.addressType,
        value = "uk"
      ),
      ukAddressLineText = HiddenField (
        key = keys.contactAddress.ukAddressLine,
        value =  ukAddressToBeShown.getOrElse("")
      ),
      BFPOAddressOption = RadioField(
        key = keys.contactAddress.addressType,
        value = "bfpo"
      ),
      BFPOAddressTextLine1 = TextField(
        key = keys.contactAddress.addressLine1
      ),
      BFPOAddressTextLine2 = TextField(
        key = keys.contactAddress.addressLine2
      ),
      BFPOAddressTextLine3 = TextField(
        key = keys.contactAddress.addressLine3
      ),
      BFPOAddressTextLine4 = TextField(
        key = keys.contactAddress.addressLine4
      ),
      BFPOAddressTextLine5 = TextField(
        key = keys.contactAddress.addressLine5
      ),
      BFPOAddressTextBFPO = TextField(
        key = keys.contactAddress.postcode
      ),

      otherAddressOption = RadioField(
        key = keys.contactAddress.addressType,
        value = "other"
      )
    )
  }

  def contactAddressMustache(
        form:ErrorTransformForm[InprogressForces],
        post: Call,
        back: Option[Call]): Html = {
      
    val data = transformFormStepToMustacheData(form, post, back)
    val content = Mustache.render("forces/contactAddress", data)
    MainStepTemplate(content, data.question.title)
  }
}
