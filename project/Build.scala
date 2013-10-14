import play.PlaySourceGenerators
import sbt._
import sbt.Keys._
import play.Project._
import net.litola.SassPlugin
import net.litola.SassPlugin._
import net.litola.SassCompiler

object ApplicationBuild extends IERBuild {

  val appName         = "ier-frontend"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "uk.gov.gds" %% "govuk-guice-utils" % "0.2-SNAPSHOT",
    "uk.gov.gds" %% "gds-scala-utils" % "0.7.6-SNAPSHOT",
    "joda-time" % "joda-time" % "2.1",
    "org.bouncycastle" % "bcpg-jdk16" % "1.46",
    anorm,
    new ModuleID("org.codehaus.janino", "janino", "2.6.1")
  )

  lazy val main = play.Project(appName, appVersion, appDependencies)
    .settings(GovukTemplatePlay.playSettings:_*)
    .settings(GovukToolkit.playSettings:_*)
    .settings(SassPlugin.sassSettings:_*)
    .settings(sassOptions := Seq("--load-path", "/Users/michael/Projects/gds/ier/frontend/app/assets/govuk_template_play/stylesheets", "--debug-info"))
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

  val playSettings = Seq(
    templateKey <<= baseDirectory(_ / "app" / "assets" / "govuk_template_play")(Seq(_)),
    sourceGenerators in Compile <+= (state, templateKey, sourceManaged in Compile, templatesTypes, templatesImport) map ScalaTemplates,
    playAssetsDirectories <+= baseDirectory { _ / "app" / "assets" / "govuk_template_play" / "assets" },
    updateTemplateTask
  )
}

object GovukToolkit extends Plugin {
  lazy val toolkitKey = SettingKey[Seq[File]]("template-dir", "Directory for assets from the govuk_frontend_toolkit")

  val playSettings = Seq(
    playAssetsDirectories <+= baseDirectory { _ / "app" / "assets" / "govuk_frontend_toolkit" }
  )
}