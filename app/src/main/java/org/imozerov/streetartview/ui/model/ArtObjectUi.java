package org.imozerov.streetartview.ui.model;

import java.util.List;

/**
 * Created by imozerov on 05.02.16.
 */
public class ArtObjectUi {
    public final String id;
    public final String name;
    public final AuthorUi author;
    public final String description;
    public final String thumbPicUrl;
    public final List<String> picsUrls;

    public ArtObjectUi(String aId,
                       String aName,
                       AuthorUi aAuthor,
                       String aDescription,
                       String aThumbPicUrl,
                       List<String> aPicsUrls) {
        id = aId;
        name = aName;
        author = aAuthor;
        description = aDescription;
        thumbPicUrl = aThumbPicUrl;
        picsUrls = aPicsUrls;
    }
}
