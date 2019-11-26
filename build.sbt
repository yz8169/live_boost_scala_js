import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

name := "live_boost_scala_js"

version := "0.1"

scalaVersion := "2.12.8"

lazy val server = (project in file("server")).settings(commonSettings).settings(
  scalaJSProjects := Seq(client),
  name := "live_boost_scala_js",
  version := "1.0",
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.1.2",
    guice,
    specs2 % Test,
    ehcache,
    "com.typesafe.play" %% "play-slick" % "3.0.1",
    "com.typesafe.slick" %% "slick-codegen" % "3.2.1",
    "mysql" % "mysql-connector-java" % "5.1.25",
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",
    "commons-io" % "commons-io" % "2.5",
    "org.apache.poi" % "poi-ooxml" % "3.15",
    "com.github.jurajburian" %% "mailer" % "1.2.2",
    "org.apache.commons" % "commons-math3" % "3.6.1",
    "com.aliyun" % "aliyun-java-sdk-core" % "3.7.1",
    "com.aliyun" % "aliyun-java-sdk-dysmsapi" % "1.1.0",
    "com.typesafe.play" %% "play-joda-forms" % "2.6.15",
    "io.github.cloudify" %% "spdf" % "1.4.0",
   "org.apache.xmlgraphics" % "batik-codec" % "1.10"


  )
).enablePlugins(PlayScala).dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(commonSettings).settings(
  scalaJSUseMainModuleInitializer := false,
  resolvers += "jitpack" at "https://jitpack.io",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.5",
    "org.querki" %%% "jquery-facade" % "1.2",
    "com.lihaoyi" %%% "scalatags" % "0.6.7",
    "com.github.karasiq" %%% "scalajs-bootstrap" % "2.3.5",
    "com.github.karasiq" %%% "scalajs-highcharts" % "1.2.1",
    "org.singlespaced" %%% "scalajs-d3" % "0.3.4",
    "com.github.fdietze" % "scala-js-d3v4" % "master-SNAPSHOT"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).dependsOn(sharedJs)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.12.8",
)

onLoad in Global := (onLoad in Global).value andThen { s: State => "project server" :: s }