package common

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*

class VideoRoutes:
	private var videoService: VideoService = VideoService()

	var routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
		case GET -> Root / "video" =>
			videoService.get
		case /*req @*/ POST -> Root / "video" / id =>
			videoService.create(id)
	}