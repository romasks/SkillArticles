package ru.skillbranch.skillarticles.data.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val name: String, private val defaultValue: T) : ReadWriteProperty<PrefManager, T?> {

  override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? = with(thisRef.preferences) {
    val res: Any? = when (defaultValue) {
      is Boolean -> getBoolean(name, defaultValue)
      is String -> getString(name, defaultValue)
      is Float -> getFloat(name, defaultValue)
      is Int -> getInt(name, defaultValue)
      is Long -> getLong(name, defaultValue)
      else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
    }
    @Suppress("UNCHECKED_CAST")
    res as T
  }

  override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) = with(thisRef.preferences.edit()) {
    when (value) {
      is Boolean -> putBoolean(name, value)
      is String -> putString(name, value)
      is Float -> putFloat(name, value)
      is Int -> putInt(name, value)
      is Long -> putLong(name, value)
      else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
    }.apply()
  }
}
