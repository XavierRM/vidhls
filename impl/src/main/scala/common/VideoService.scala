package common

import cats.effect.IO
import org.http4s.Response
import org.http4s.dsl.io.*

class VideoService:
	private var video: Option[String] = None

	def get: IO[Response[IO]] = {
		this.video match
			case Some(x) => Ok(x)
			case _ => NotFound("Resource doesn't exist")
	}

	def create(video: String): IO[Response[IO]] = {
		this.video = Some(video)
		NoContent()
	}
