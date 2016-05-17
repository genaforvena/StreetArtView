package org.imozerov.streetartview.ui.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.imozerov.streetartview.R

class DetailArtObjectActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val id = intent.getStringExtra(DetailArtObjectFragment.EXTRA_KEY_ART_OBJECT_DETAIL_ID)
        val fragment = DetailArtObjectFragment.newInstance(id)

        supportFragmentManager.beginTransaction()
                .replace(R.id.detail_fragment_container, fragment)
                .commit();
    }
}
