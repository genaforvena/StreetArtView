package org.imozerov.streetartview.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.imozerov.streetartview.IntentUtils

import org.imozerov.streetartview.R
import org.imozerov.streetartview.ui.catalog.ArtListFragment
import org.imozerov.streetartview.ui.detail.ArtObjectDetailOpener
import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity
import org.imozerov.streetartview.ui.helper.replaceFragment

class MainActivity : AppCompatActivity(), ArtObjectDetailOpener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(R.id.main_content, ArtListFragment.newInstance())
    }

    override fun openDetailActivity(id: String?) {
        val intent = Intent(this, DetailArtObjectActivity::class.java)
        intent.putExtra(IntentUtils.EXTRA_KEY_ART_OBJECT_DETAIL_ID, id)
        startActivity(intent)
    }

}