package libra.http

import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.parser.*
import io.circe.syntax.*
import org.scalajs.dom.*

import scala.concurrent.*
import scala.scalajs.js

/** Кастомный HTTP-клиент - обёртка над Fetch API. */
class HttpClient(using ExecutionContext):

  /** Тело HTTP-ответа с сообщением об ошибке.
    *
    * @param message
    *   сообщение
    */
  final case class ResponseError(message: String) extends Throwable(message)

  given Decoder[ResponseError] = deriveDecoder[ResponseError]

  /** Методы расширения класса [[Response]], которые позволяют получить [[Future]] с результатом ответа. */
  extension (response: Response)
    /** При успехе ожидается тело ответа, но возможна ошибка. */
    private def to[R: Decoder](using ExecutionContext): Future[Either[Throwable, R]] =
      response
        .text()
        .toFuture
        .flatMap { json =>
          if response.ok then Future.successful(decode[R](json))
          else Future.successful(decode[ResponseError](json).flatMap(Left(_)))
        }

    /** При успехе тело ответа не ожидается (например, код 204), но возможна ошибка. */
    private def toEmpty(using ExecutionContext): Future[Option[Throwable]] =
      response
        .text()
        .toFuture
        .flatMap { json =>
          if response.ok then Future.successful(None)
          else Future.successful(decode[ResponseError](json).toOption)
        }
  end extension

  /** HTTP-метод GET.
    *
    * @param url
    *   адрес ресурса
    * @param headersInit
    *   заголовки
    * @tparam R
    *   тип данных, возвращаемых с сервера - экземпляр тайп класса Decoder
    */
  def get[R: Decoder](
      url: String,
      headersInit: HeadersInit
  ): Future[Either[Throwable, R]] =
    val request = Request(
      url,
      init = new RequestInit {
        method = HttpMethod.GET
        headers = headersInit
      }
    )
    Fetch
      .fetch(request)
      .toFuture
      .flatMap(_.to[R])
      .recoverWith { case err: Throwable => Future.successful(Left(err)) }
  end get

  /** HTTP-метод GET со стандартными заголовками.
    *
    * @param url
    *   адрес ресурса
    * @tparam R
    *   тип данных, возвращаемых с сервера - экземпляр тайп класса Decoder
    */
  def get[R: Decoder](
      url: String
  ): Future[Either[Throwable, R]] =
    val headers: HeadersInit = js.Array(js.Array("accept", "application/json"))
    get(url, headers)
  end get

  /** HTTP-метод GET с аутентификацией.
    *
    * @param url
    *   адрес ресурса
    * @param jwt
    *   JSON Web Token
    * @tparam R
    *   тип данных, возвращаемых с сервера - экземпляр тайп класса Decoder
    */
  def get[R: Decoder](
      url: String,
      jwt: String
  ): Future[Either[Throwable, R]] =
    val headers: HeadersInit = js.Array(
      js.Array("accept", "application/json"),
      js.Array("X-JWT-Auth", jwt)
    )
    get(url, headers)
  end get

  /** HTTP-метод POST.
    *
    * @param url
    *   адрес ресурса
    * @param headersInit
    *   заголовки
    * @param bodyData
    *   данные тела запроса
    * @tparam T
    *   тип данных, отправляемых на сервер - экземпляр тайп класса Encoder
    * @tparam R
    *   тип данных, возвращаемых с сервера - экземпляр тайп класса Decoder
    */
  def post[T: Encoder, R: Decoder](
      url: String,
      headersInit: HeadersInit,
      bodyData: T
  ): Future[Either[Throwable, R]] =
    val jsonBody = bodyData.asJson.toString
    val request  = Request(
      url,
      init = new RequestInit {
        method = HttpMethod.POST
        headers = headersInit
        body = jsonBody
      }
    )
    Fetch
      .fetch(request)
      .toFuture
      .flatMap(_.to[R])
      .recoverWith { case err: Throwable => Future.successful(Left(err)) }
  end post

  /** HTTP-метод POST со стандартными заголовками.
    *
    * @param url
    *   адрес ресурса
    * @param bodyData
    *   данные тела запроса
    * @tparam T
    *   тип данных, отправляемых на сервер - экземпляр тайп класса Encoder
    * @tparam R
    *   тип данных, возвращаемых с сервера - экземпляр тайп класса Decoder
    */
  def post[T: Encoder, R: Decoder](
      url: String,
      bodyData: T
  ): Future[Either[Throwable, R]] =
    val headers: HeadersInit = js.Array(
      js.Array("accept", "application/json"),
      js.Array("Content-Type", "application/json")
    )
    post(url, headers, bodyData)
  end post

  /** HTTP-метод POST с аутентификацией.
    *
    * @param url
    *   адрес ресурса
    * @param jwt
    *   JSON Web Token
    * @param bodyData
    *   данные тела запроса
    * @tparam T
    *   тип данных, отправляемых на сервер - экземпляр тайп класса Encoder
    * @tparam R
    *   тип данных, возвращаемых с сервера - экземпляр тайп класса Decoder
    */
  def post[T: Encoder, R: Decoder](
      url: String,
      jwt: String,
      bodyData: T
  ): Future[Either[Throwable, R]] =
    val headers: HeadersInit = js.Array(
      js.Array("accept", "application/json"),
      js.Array("Content-Type", "application/json"),
      js.Array("X-JWT-Auth", jwt)
    )
    post(url, headers, bodyData)
  end post

  /** HTTP-метод PUT.
    *
    * @param url
    *   адрес ресурса
    * @param headersInit
    *   заголовки
    * @param bodyData
    *   данные тела запроса
    * @tparam T
    *   тип данных, отправляемых на сервер - экземпляр тайп класса Encoder
    * @tparam R
    *   тип данных, возвращаемых с сервера - экземпляр тайп класса Decoder
    */
  def put[T: Encoder, R: Decoder](
      url: String,
      headersInit: HeadersInit,
      bodyData: T
  ): Future[Either[Throwable, R]] =
    val jsonBody = bodyData.asJson.toString
    val request  = Request(
      url,
      init = new RequestInit {
        method = HttpMethod.PUT
        headers = headersInit
        body = jsonBody
      }
    )
    Fetch
      .fetch(request)
      .toFuture
      .flatMap(_.to[R])
      .recoverWith { case err: Throwable => Future.successful(Left(err)) }
  end put

  /** HTTP-метод PUT со стандартными заголовками.
    *
    * @param url
    *   адрес ресурса
    * @param bodyData
    *   данные тела запроса
    * @tparam T
    *   тип данных, отправляемых на сервер - экземпляр тайп класса Encoder
    * @tparam R
    *   тип данных, возвращаемых с сервера - экземпляр тайп класса Decoder
    */
  def put[T: Encoder, R: Decoder](
      url: String,
      bodyData: T
  ): Future[Either[Throwable, R]] =
    val headers: HeadersInit = js.Array(
      js.Array("accept", "application/json"),
      js.Array("Content-Type", "application/json")
    )
    put(url, headers, bodyData)
  end put

  /** HTTP-метод PUT с аутентификацией.
    *
    * @param url
    *   адрес ресурса
    * @param jwt
    *   JSON Web Token
    * @param bodyData
    *   данные тела запроса
    * @tparam T
    *   тип данных, отправляемых на сервер - экземпляр тайп класса Encoder
    * @tparam R
    *   тип данных, возвращаемых с сервера - экземпляр тайп класса Decoder
    */
  def put[T: Encoder, R: Decoder](
      url: String,
      jwt: String,
      bodyData: T
  ): Future[Either[Throwable, R]] =
    val headers: HeadersInit = js.Array(
      js.Array("accept", "application/json"),
      js.Array("Content-Type", "application/json"),
      js.Array("X-JWT-Auth", jwt)
    )
    put(url, headers, bodyData)
  end put

  /** HTTP-метод DELETE.
    *
    * @param url
    *   адрес ресурса
    * @param headersInit
    *   заголовки
    */
  def delete(
      url: String,
      headersInit: HeadersInit
  ): Future[Option[Throwable]] =
    val request = Request(
      url,
      init = new RequestInit {
        method = HttpMethod.DELETE
        headers = headersInit
      }
    )
    Fetch
      .fetch(request)
      .toFuture
      .flatMap(_.toEmpty)
      .recoverWith { case err: Throwable => Future.successful(Some(err)) }
  end delete

  /** HTTP-метод DELETE со стандартными заголовками.
    *
    * @param url
    *   адрес ресурса
    */
  def delete(
      url: String
  ): Future[Option[Throwable]] =
    val headers: HeadersInit = js.Array(js.Array("accept", "application/json"))
    delete(url, headers)
  end delete

  /** HTTP-метод DELETE с аутентификацией.
    *
    * @param url
    *   адрес ресурса
    * @param jwt
    *   JSON Web Token
    */
  def delete(
      url: String,
      jwt: String
  ): Future[Option[Throwable]] =
    val headers: HeadersInit = js.Array(
      js.Array("accept", "application/json"),
      js.Array("X-JWT-Auth", jwt)
    )
    delete(url, headers)
  end delete

end HttpClient
