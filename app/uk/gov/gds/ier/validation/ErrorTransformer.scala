package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.KeyForError
import play.api.data.Form

class ErrorTransformer {

  def transform[T](errorForm: Form[T]):Form[T] = {
    val transformedErrors = errorForm.errors.flatMap(
      error => {
        val parseKeysForError = error.args.foldLeft(Seq.empty[Key]) (
          (sequence, arg) => if (arg.isInstanceOf[Key]) {
            sequence :+ arg.asInstanceOf[Key]
          } else {
            sequence
          }
        )

        val seqOfErrors = parseKeysForError.map( k =>
          error.copy(key = k.key)
        )

        error.copy(key = "") +: {if (seqOfErrors.isEmpty) Seq(error) else seqOfErrors}
      }
    )
    errorForm.copy(errors = transformedErrors)
  }
}

trait WithErrorTransformer {
  val errorTransformer:ErrorTransformer
}
