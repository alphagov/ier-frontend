import play.PlaySourceGenerators
import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends IERBuild {

  val appName         = "ier-frontend"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "uk.gov.gds" %% "govuk-guice-utils" % "0.2-SNAPSHOT",
    "uk.gov.gds" %% "gds-scala-utils" % "0.7.5-SNAPSHOT",
    "joda-time" % "joda-time" % "2.1",
    anorm,
    new ModuleID("org.codehaus.janino", "janino", "2.6.1")
  )

  lazy val main = play.Project(appName, appVersion, appDependencies).settings(
    templateKey <<= baseDirectory(_ / "govuk_template_play")(Seq(_)),
    sourceGenerators in Compile <+= (state, templateKey, sourceManaged in Compile, templatesTypes, templatesImport) map ScalaTemplates,
    playAssetsDirectories <+= baseDirectory { _ / "govuk_template_play" / "assets" },
    updateTemplateTask,
    compile <<= (compile in Compile) dependsOn updateTemplate
  )
  val templateKey = SettingKey[Seq[File]]("template-dir", "Template directory for govuk_template_play")
  val updateTemplate = TaskKey[Unit]("update-template", "Updates the govuk_template_play")
  val updateTemplateTask = updateTemplate := {
    "./update-template.sh".!
  }
}

abstract class IERBuild extends Build {
  override def settings = super.settings ++ Seq(
    resolvers ++= Seq(
      Resolver.defaultLocal,
      "GDS maven repo snapshots" at "http://alphagov.github.com/maven/snapshots",
      "GDS maven repo releases" at "http://alphagov.github.com/maven/releases"
    )
  )
}
