package org.imozerov.streetartview;

import dagger.Component;

import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity;
import org.imozerov.streetartview.ui.detail.ImageViewActivity;
import org.imozerov.streetartview.ui.explore.ArtListPresenter;
import org.imozerov.streetartview.ui.explore.ExploreArtActivity;

import javax.inject.Singleton;

/**
 * Created by imozerov on 06.02.16.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(ArtListPresenter artListPresenter);
    void inject(DetailArtObjectActivity detailArtObjectActivity);
    void inject(ImageViewActivity imageViewActivity);
    void inject(ExploreArtActivity exploreArtActivity);
}
