import sbtcrossproject.CrossPlugin.autoImport.crossProject

ThisBuild / version := getVersion(0, 1)

val scalaCommonVersion = "1.0.93"

lazy val commonTest = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
      Common.settings ++ Common.publish ++ Seq(
          name := "scala-common-test",
          organization := "org.mule.common",
          libraryDependencies ++= Seq(
              "org.scalatest"   %%% "scalatest"    % "3.2.13",
              "org.mule.common" %%% "scala-common" % scalaCommonVersion
          ),
          resolvers ++= List(Common.releases, Common.snapshots, Resolver.mavenLocal),
          credentials ++= Common.credentials()
      )
  )
  .jvmSettings(libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided")
  .jsSettings(
      scalaJSModuleKind := ModuleKind.CommonJSModule,
      scalacOptions += "-P:scalajs:suppressExportDeprecations"
  )

lazy val commonTestJVM = commonTest.jvm.in(file("./jvm"))
lazy val commonTestJS  = commonTest.js.in(file("./js")).disablePlugins(SonarPlugin, ScoverageSbtPlugin)

def getVersion(major: Int, minor: Int): String = {

  lazy val build  = sys.env.getOrElse("BUILD_NUMBER", "0")
  lazy val branch = sys.env.get("BRANCH_NAME")

  if (branch.contains("master")) s"$major.$minor.$build" else s"$major.${minor + 1}.0-SNAPSHOT"
}

lazy val sonarUrl   = sys.env.getOrElse("SONAR_SERVER_URL", "Not found url.")
lazy val sonarToken = sys.env.getOrElse("SONAR_SERVER_TOKEN", "Not found token.")
lazy val branch     = sys.env.getOrElse("BRANCH_NAME", "develop")

sonarProperties := Map(
    "sonar.login"             -> sonarToken,
    "sonar.projectKey"        -> "mulesoft.scala-common-test",
    "sonar.projectName"       -> "Scala-common-test",
    "sonar.projectVersion"    -> version.value,
    "sonar.sourceEncoding"    -> "UTF-8",
    "sonar.github.repository" -> "aml-org/scala-common-test",
    "sonar.branch.name"       -> branch,
    "sonar.sources"           -> "shared/src/main/scala",
    "sonar.tests"             -> "shared/src/test/scala",
    "sonar.userHome"          -> "${buildDir}/.sonar"
)
