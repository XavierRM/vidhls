package common

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io.{->, /, GET, Root}

class VideosRoutes:
	private var videosService: VideosService = VideosService()

	var routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
		case req @ GET -> Root / "videos" / "live" =>
			videosService.list()
	}
