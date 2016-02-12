package org.imozerov.streetartview;

import org.imozerov.streetartview.ui.detail.DetailArtObjectActivity;
import org.imozerov.streetartview.ui.explore.ArtListPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by imozerov on 06.02.16.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(ArtListPresenter artListPresenter);
    void inject(DetailArtObjectActivity activity);
}
