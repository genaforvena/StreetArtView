package org.imozerov.streetartview.ui.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import org.imozerov.streetartview.R

class DetailArtObjectActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val id = intent.getStringExtra(DetailArtObjectFragment.EXTRA_KEY_ART_OBJECT_DETAIL_ID)
        val name = intent.getStringExtra(DetailArtObjectActivity.EXTRA_KEY_NAME)
        val fragment = DetailArtObjectFragment.newInstance(id)

        supportActionBar?.title = name
        supportActionBar?.setHomeButtonEnabled(true)

        supportFragmentManager.beginTransaction()
                .replace(R.id.detail_fragment_container, fragment)
                .commit();
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val EXTRA_KEY_NAME = "art_object_name"
    }
}
