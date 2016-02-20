package org.imozerov.streetartview.ui.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail.*
import org.imozerov.streetartview.R
import org.imozerov.streetartview.StreetArtViewApp
import org.imozerov.streetartview.storage.DataSource
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by sergei on 08.02.16.
 */
class DetailArtObjectActivity : AppCompatActivity() {

    private var compositeSubscription: CompositeSubscription? = null

    @Inject
    lateinit var dataSource: DataSource
    private var artObjectId: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        (application as StreetArtViewApp).appComponent.inject(this)

        artObjectId = intent.getStringExtra(EXTRA_KEY_ART_OBJECT_DETAIL_ID)
    }

    override fun onStart() {
        super.onStart()
        this.compositeSubscription = CompositeSubscription()
        startFetchingArtObject()
    }

    public override fun onStop() {
        super.onStop()
        compositeSubscription!!.unsubscribe()
    }

    private fun startFetchingArtObject() {
        this.compositeSubscription!!.add(
                dataSource.getArtObject(artObjectId!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { artObjectUi ->
                            // TODO: Picasso.with(this).load(artObjectUi.thumbPicUrl).into(art_object_detail_image);
                            // when server is ready
                            Glide.with(this).load(R.drawable.einstein).into(art_object_detail_image)
                            art_object_detail_name.text = artObjectUi.name
                            art_object_detail_author.text = artObjectUi.author.name
                            art_object_detail_description.text = artObjectUi.description
                        })
    }

    companion object {
        val EXTRA_KEY_ART_OBJECT_DETAIL_ID = "EXTRA_KEY_ART_OBJECT_DETAIL_ID"
    }
}
