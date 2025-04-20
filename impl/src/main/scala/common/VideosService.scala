package common

import cats.effect.IO
import org.http4s.{Request, Response}
import org.http4s.dsl.io.*

class VideosService:
	private var video: Option[String] = None

	def get: IO[Response[IO]] = {
		this.video match
			case Some(x) => Ok(x)
			case _ => NotFound("Resource doesn't exist")
	}

	def list(req: Request[IO]): IO[Response[IO]] = {
		req.attributes
	}
