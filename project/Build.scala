import play.PlaySourceGenerators
import sbt._
import sbt.Keys._
import play.Project._
import net.litola.SassPlugin
import net.litola.SassPlugin._
import net.litola.SassCompiler
import de.johoop.jacoco4sbt.JacocoPlugin._
import org.jba.sbt.plugin.MustachePlugin
import org.jba.sbt.plugin.MustachePlugin._
import org.scalastyle.sbt.ScalastylePlugin
import aether.Aether._
import scala.Some

object ApplicationBuild extends IERBuild {

  val appName         = "ier-frontend"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "uk.gov.gds" %% "govuk-guice-utils" % "0.2-SNAPSHOT",
    "uk.gov.gds" %% "gds-scala-utils" % "0.7.6-SNAPSHOT" exclude("com.google.code.findbugs", "jsr305"),
    "joda-time" % "joda-time" % "2.1",
    anorm,
    new ModuleID("org.codehaus.janino", "janino", "2.6.1"),
    "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
    "org.mockito" % "mockito-core" % "1.9.5",
    "org.jba" %% "play2-mustache" % "1.1.3", // play2.2.0
    "org.jsoup" % "jsoup" % "1.7.2",
    "com.typesafe.play.plugins" %% "play-statsd" % "2.2.0",
    "org.julienrf" %% "play-jsmessages" % "1.6.1"
  )

  lazy val main = play.Project(appName, appVersion, appDependencies)
    .settings(GovukTemplatePlay.playSettings:_*)
    .settings(GovukToolkit.playSettings:_*)
    .settings(Sass.sassSettings:_*)
    .settings(Jacoco.jacocoSettings:_*)
    .settings(Mustache.mustacheSettings:_*)
    .settings(javaOptions in Test ++= Seq(
      "-XX:MaxPermSize=512M",
      "-Xms256M",
      "-Xmx512M",
      "-Xss1M",
      "-XX:+CMSClassUnloadingEnabled",
      "-XX:+UseConcMarkSweepGC",
      "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005",
      "-Dconfig.resource=test.conf"
    ))
    .settings(testOptions in Test += Tests.Argument("-oF"))
    .settings(StyleChecker.settings:_*)
    .settings(watchSources ~= { _.filterNot(_.isDirectory) })
    .settings(credentials += Credentials(Path.userHome / ".sbt/credentials"))
    .settings(organization := "uk.gov.gds")
    .settings(publishMavenStyle := true)
    .settings(publishTo := {
        val nexus = "https://ci.ertp.alphagov.co.uk/nexus/content/repositories/"
        if (version.value.trim.endsWith("SNAPSHOT"))
          Some("IER Nexus Snapshots" at nexus + "snapshots")
        else
          Some("IER Nexus Releases" at nexus + "releases")
      }
    )
    .settings(aetherSettings: _*)

}

abstract class IERBuild extends Build {
}

object StyleChecker {
  import org.scalastyle.sbt.PluginKeys._

  val settings = ScalastylePlugin.Settings ++ Seq(
    scalastyle <<= scalastyle dependsOn (compile in Compile)
  )
}

object Sass {
  val sassSettings = SassPlugin.sassSettings ++ Seq(
    sassOptions := Seq("--load-path", "/Users/michael/Projects/gds/ier/frontend/app/assets/govuk_template_play/stylesheets", "--debug-info")
  )
}

object Mustache {

  val mustacheSettings = Seq(
    resolvers += Resolver.url("julienba.github.com", url("http://julienba.github.com/repo/"))(Resolver.ivyStylePatterns),
    // Mustache settings
    mustacheEntryPoints <<= (sourceDirectory in Compile)( base => {
      base / "assets" / "mustache" +++
      base / "assets" / "mustache" / "govuk_template_mustache" / "views" / "layouts"
    } ** "*.html"),
    mustacheOptions := Seq.empty[String],
    resourceGenerators in Compile <+= MustacheFileCompiler
  )
}

object Jacoco {
  val jacocoSettings = jacoco.settings ++ Seq(
    parallelExecution in jacoco.Config := false,
    watchSources ~= { _.filterNot(_.isDirectory) }
  )
}

object GovukTemplatePlay extends Plugin {

  lazy val templateKey = SettingKey[Seq[File]]("template-dir", "Template directory for govuk_template_play")
  lazy val updateTemplate = TaskKey[Unit]("update-template", "Updates the govuk_template_play")
  lazy val updateTemplateTask = updateTemplate := {
    "./scripts/update-template.sh".!
  }

  val playSettings = Seq(
    templateKey <<= baseDirectory { _ / "app" / "assets" / "mustache" / "govuk_template_mustache" }(Seq(_)),
    templateKey <+= baseDirectory { _ / "app" / "assets" / "govuk_template_play" },
    sourceGenerators in Compile <+= (state, templateKey, sourceManaged in Compile, templatesTypes, templatesImport) map ScalaTemplates,
    playAssetsDirectories <+= baseDirectory { _ / "app" / "assets" / "mustache" / "govuk_template_mustache" / "assets" },
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
