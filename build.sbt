val scala3Version = "3.0.0-RC2"

lazy val root = project
  .in(file("."))
  .enablePlugins(
    ScalaJSPlugin, // Enable the Scala.js
    SbtIndigo      //  Enable Indigo plugin
  )
  .settings(
    name := "life",
    organization := "org.life",
    version := "0.1.0",

    scalaVersion := scala3Version,
  )
  .settings(
    showCursor := true,
    title := "Life",
    gameAssetsDirectory := "assets",
    windowStartWidth := 720, // Width of Electron window, used with `indigoRun`.
    windowStartHeight := 480, // Height of Electron window, used with `indigoRun`.
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "indigo" % "0.7.1",
      "io.indigoengine" %%% "indigo-json-circe" % "0.7.1",
    )

  )

addCommandAlias("buildGame", ";compile;fastOptJS;indigoBuild")
addCommandAlias("runGame", ";compile;fastOptJS;indigoRun")
