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
    GovukTemplatePlay.playSettings ++ GovukFrontendToolkit.playSettings:_*
  )

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

object GovukTemplatePlay extends Plugin {

  lazy val templateKey = SettingKey[Seq[File]]("template-dir", "Template directory for govuk_template_play")
  lazy val updateTemplate = TaskKey[Unit]("update-template", "Updates the govuk_template_play")
  lazy val updateTemplateTask = updateTemplate := {
    "./scripts/update-template.sh".!
  }

  def playSettings = {
    Seq(
      templateKey <<= baseDirectory(_ / "assets" / "govuk_template_play")(Seq(_)),
      sourceGenerators in Compile <+= (state, templateKey, sourceManaged in Compile, templatesTypes, templatesImport) map ScalaTemplates,
      playAssetsDirectories <+= baseDirectory { _ / "assets" / "govuk_template_play" / "assets" },
      updateTemplateTask,
      compile <<= (compile in Compile) dependsOn updateTemplate
    )
  }
}

object GovukFrontendToolkit extends Plugin {
  lazy val compileSass = TaskKey[Unit]("compile-sass", "Compiles our sass from assets/sass into css in public/stylesheets")
  lazy val compileSassTask = compileSass := {
    "./scripts/compile-sass.sh".!
  }

  def playSettings = {
    Seq(
      compileSassTask,
      compile <<= (compile in Compile) dependsOn compileSass,
      playReload <<= playReload dependsOn compileSass
    )
  }
}
