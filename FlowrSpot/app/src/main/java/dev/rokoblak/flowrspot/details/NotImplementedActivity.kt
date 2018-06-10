package dev.rokoblak.flowrspot.details

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import dev.rokoblak.flowrspot.R
import org.jetbrains.anko.find

class NotImplementedActivity : AppCompatActivity() {

    private val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_not_implemented)

        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.title = ""

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}