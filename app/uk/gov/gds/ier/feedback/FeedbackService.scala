package uk.gov.gds.ier.feedback

trait FeedbackService {

  val feedbackClient: FeedbackClient

  def submit(request: FeedbackRequest, browserDetails: Option[String]) {
    feedbackClient.submit(
      FeedbackSubmissionData(
        fixedTicketSubject,
        fudgeTicketBodyText(request, browserDetails)
      )
    )
  }

  val fixedTicketSubject = "ier-frontend-feedback page"

  val separatorBetweenCommentAndAppendedFields = "\n"

  /**
   * Append contact and browser details to a ticket body text as there are no proper fields for
   * them in Zendesk API and it is common practice
   */
  private[feedback] def fudgeTicketBodyText(
    request: FeedbackRequest,
    browserDetails: Option[String]) = {
    List(
      request.comment,
      separatorBetweenCommentAndAppendedFields,
      request.contactName map {
        name => s"Contact name: ${name}"} getOrElse("No contact name was provided"),
      request.contactEmail map {
        email => s"Contact email: ${email}"} getOrElse("No contact email was provided"),
      browserDetails map {
        details => s"Browser details: ${details}"} getOrElse("No browser details were provided")
    ).mkString("\n")
  }
}
