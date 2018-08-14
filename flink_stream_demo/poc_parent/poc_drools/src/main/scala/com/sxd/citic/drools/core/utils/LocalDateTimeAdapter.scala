package com.sxd.citic.drools.core.utils

import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.google.gson._

class LocalDateTimeAdapter extends JsonSerializer[LocalDateTime] with JsonDeserializer[LocalDateTime] {
  private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")

  override def serialize(src: LocalDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
    new JsonPrimitive(src.format(DATE_TIME_FORMATTER))
  }

  override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime = {
    val dateTimeString = json.getAsString
    try{
      LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER)
    }catch{
      case ex: Exception => throw new IllegalArgumentException(s"${dateTimeString}不能转换成LocalDateTime类型, ${ex.getMessage}")
    }
  }
}
