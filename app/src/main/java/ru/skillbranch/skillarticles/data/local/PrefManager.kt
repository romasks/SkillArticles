package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {

  val preferences : SharedPreferences by lazy {
    context.getSharedPreferences("default", Context.MODE_PRIVATE)
  }

  fun clearAll() {
    preferences.all.clear()
  }
}
