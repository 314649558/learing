package com.sxd.citic.cep.core.utils

import java.lang.reflect.Type

import com.google.gson._
class BigDecimalAdapter extends JsonSerializer[BigDecimal] with JsonDeserializer[BigDecimal]{
  override def serialize(src: BigDecimal, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
    new JsonPrimitive(src.bigDecimal)
  }
  override def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BigDecimal = {
    try {
      json.getAsBigDecimal
    } catch  {
      case ex: Exception => {
        throw new NumberFormatException(s"${json}不能被转换为BigDecimal类型，${ex.getMessage}")
      }
    }
  }
}
