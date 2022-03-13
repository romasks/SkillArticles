package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extentions.dpToIntPx
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.CheckableImageView

class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        setupToolbar()

        findViewById<CheckableImageView>(R.id.btn_like).setOnClickListener {
            Snackbar.make(findViewById(R.id.coordinator_container), "test", Snackbar.LENGTH_LONG)
                .setAnchorView(findViewById(R.id.bottombar))
                .show()
        }

        findViewById<CheckableImageView>(R.id.btn_settings).setOnClickListener {
            val submenu = findViewById<ArticleSubmenu>(R.id.submenu)
            if (submenu.isOpen) submenu.close() else submenu.open()
        }

        findViewById<SwitchMaterial>(R.id.switch_mode).setOnClickListener {
            delegate.localNightMode = if ((it as SwitchMaterial).isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val logo = if (toolbar.childCount > 2) toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP

        (logo?.layoutParams as? Toolbar.LayoutParams)?.let {
            it.width = this.dpToIntPx(40)
            it.height = this.dpToIntPx(40)
            it.marginEnd = this.dpToIntPx(16)
            logo.layoutParams = it
        }
    }
}