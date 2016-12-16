package social.news.inrss.ads;

import java.io.File;

/**
 * Created by admin on 12/7/2016.
 */

public class ItemWrapper {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_AD = 1;
    public static final int TYPE_COUNT = 2;

    public ListObject item;
    public AdObject adItem;
    public File file;
    public int type;

    public ItemWrapper(ListObject item) {
        this.type = TYPE_NORMAL;
        this.item = item;
    }

    public ItemWrapper(File file) {
        this.type = TYPE_NORMAL;
        this.file = file;
    }

    public ItemWrapper(AdObject adItem) {
        this.type = TYPE_AD;
        this.adItem = adItem;
    }
}