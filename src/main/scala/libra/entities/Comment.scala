package libra.entities

import io.circe.*
import io.circe.generic.semiauto.*

import java.time.LocalDateTime
import java.util.UUID

/** Сущность "Комментарий".
  *
  * @param id
  *   уникальный идентификатор
  * @param text
  *   текст комментария
  * @param isPrivate
  *   является или нет комментарий приватным
  * @param date
  *   дата и время комментария
  */
final case class Comment(
    id: Option[UUID],
    text: String,
    isPrivate: Boolean,
    date: Option[LocalDateTime]
)

object Comment:

  given Codec[Comment] = deriveCodec

end Comment
