package com.xrm

import cats.effect.*
import com.comcast.ip4s.*
import common.VideoRoutes
import org.http4s.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import org.http4s.server.Router

object Http4sServer extends IOApp.Simple {

	// Montar las rutas en un router
	val httpVidApp: HttpApp[IO] = Router(
		"/" -> VideoRoutes().routes
	).orNotFound

	// Configurar y levantar el servidor
	val run: IO[Unit] = EmberServerBuilder
		.default[IO]
		.withHost(ipv4"0.0.0.0")
		.withPort(port"8080")
		.withHttpApp(httpVidApp)
		.build
		.useForever
}