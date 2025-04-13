package com.xrm

import cats.effect.*
import com.comcast.ip4s.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import org.http4s.server.Router

object Http4sServer extends IOApp.Simple {

  // Define tu ruta
  val helloRoute: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "hello" =>
      Ok("Hello, world!")
  }

  // Montar las rutas en un router
  val httpApp: HttpApp[IO] = Router(
    "/" -> helloRoute
  ).orNotFound

  // Configurar y levantar el servidor
  val run: IO[Unit] = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(httpApp)
    .build
    .useForever
}