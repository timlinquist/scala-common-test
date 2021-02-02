import sbtcrossproject.CrossPlugin.autoImport.crossProject

version in ThisBuild := getVersion(0, 0)

val scalaCommonVersion = "0.5.71"

lazy val commonTest = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    Common.settings ++ Common.publish ++ Seq(
      name := "scala-common-test",
      organization := "com.github.amlorg",
      libraryDependencies ++= Seq(
        "org.scalatest" %%% "scalatest" % "3.0.5",
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


def getVersion(major: Int, minor: Int): String = {

  lazy val build = sys.env.getOrElse("BUILD_NUMBER", "0")
  lazy val branch = sys.env.get("BRANCH_NAME")

  if (branch.contains("master")) s"$major.$minor.$build" else s"$major.${minor + 1}.0-SNAPSHOT"
}
