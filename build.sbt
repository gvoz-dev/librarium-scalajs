import org.scalajs.linker.interface.ModuleSplitStyle

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaVersion := "3.4.2",
    scalacOptions ++= Seq("-encoding", "utf-8", "-deprecation", "-feature"),
    name := "libra",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("libra")))
    },
    externalNpm := baseDirectory.value
  )

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0"
libraryDependencies += "com.raquo" %%% "laminar" % "17.0.0"
libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.18" % Test

val circeVersion = "0.14.7"

libraryDependencies ++= Seq(
  "io.circe" %%% "circe-core",
  "io.circe" %%% "circe-generic",
  "io.circe" %%% "circe-parser"
).map(_ % circeVersion)