package com.sxd.citic.cep.core.utils

import java.time.LocalDateTime

import com.google.gson._

object JsonUtils {

  private val gson = (new GsonBuilder).
    registerTypeAdapter(classOf[LocalDateTime], new LocalDateTimeAdapter).
    registerTypeAdapter(classOf[BigDecimal], new BigDecimalAdapter).
    create()

  def fromJson[T](json: String, clazz: Class[T]): T = {
    gson.fromJson(json, clazz)
  }

  def toJson[T](bean: T): String = {
    gson.toJson(bean)
  }

  def strToJson(str: String): JsonObject = {
    new JsonParser().parse(str).getAsJsonObject()
  }

  def strToJsonArray(str: String): JsonArray = {
    new JsonParser().parse(str).getAsJsonArray()
  }

}
