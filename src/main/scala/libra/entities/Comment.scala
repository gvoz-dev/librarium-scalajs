package libra.entities

import io.circe.*
import io.circe.generic.semiauto.*

import java.time.LocalDateTime
import java.util.UUID

/** Сущность "Комментарий".
  *
  * @param id
  *   уникальный идентификатор
  * @param userId
  *   уникальный идентификатор пользователя
  * @param bookId
  *   уникальный идентификатор книги
  * @param text
  *   текст комментария
  * @param isPrivate
  *   является или нет комментарий приватным
  * @param time
  *   время публикации комментария
  * @param lastModifiedTime
  *   время последнего изменения
  */
final case class Comment(
    id: Option[UUID],
    userId: UUID,
    bookId: UUID,
    text: String,
    isPrivate: Boolean,
    time: Option[LocalDateTime],
    lastModifiedTime: Option[LocalDateTime]
)

object Comment:

  given Codec[Comment] = deriveCodec

end Comment
