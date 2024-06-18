package libra.http

import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import org.scalajs.dom.*

import scala.concurrent.*
import scala.scalajs.js

/** Кастомный HTTP-клиент - обёртка над Fetch API. */
class HttpClient(using ExecutionContext):

  /** HTTP-метод POST.
    *
    * @param url
    *   адрес ресурса
    * @param data
    *   данные для отправки на сервер
    * @tparam T
    *   тип данных, отправляемых на сервер - экземпляр тайп класса Encoder
    * @tparam R
    *   тип данных, возвращаемых с сервера - экземпляр тайп класса Decoder
    */
  def post[T: Encoder, R: Decoder](
      url: String,
      data: T
  ): Future[Either[Throwable, R]] =
    val jsonBody = data.asJson.toString
    val request = Request(
      url,
      init = new RequestInit {
        method = HttpMethod.POST
        headers = js.Array(
          js.Array("accept", "application/json"),
          js.Array("Content-Type", "application/json")
        )
        body = jsonBody
      }
    )
    Fetch
      .fetch(request)
      .toFuture
      .flatMap((response: Response) => response.to[R])
      .recoverWith { case err: Throwable => Future.successful(Left(err)) }
  end post

  extension (response: Response)
    private def to[T: Decoder](using
        ExecutionContext
    ): Future[Either[Throwable, T]] =
      response
        .text()
        .toFuture
        .flatMap { json =>
          if response.ok then Future.successful(decode[T](json))
          else Future.successful(decode[ResponseError](json).flatMap(Left(_)))
        }

end HttpClient
