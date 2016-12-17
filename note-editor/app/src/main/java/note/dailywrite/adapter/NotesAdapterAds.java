package note.dailywrite.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import note.dailywrite.R;
import note.dailywrite.ads.AdObject;
import note.dailywrite.ads.ItemWrapper;
import note.dailywrite.model.Constants;

/**
 * Created by jeff on 2014-04-11.
 */
public class NotesAdapterAds extends ArrayAdapter<ItemWrapper> implements Filterable {

    public static final String EMPTY_STRING = "";
    private Context context;
    private List<ItemWrapper> data;
    private List<ItemWrapper> filteredData;

    public NotesAdapterAds(Context context, int resource, List<ItemWrapper> objects, AdObject adObject) {
        super(context, resource, objects);
        this.context = context;
        this.data = objects;
        this.filteredData = data;
    }

    @Override
    public int getCount() {
        try {
            if (filteredData != null && filteredData.size()>0)
                return filteredData.size();
            else
                return 0;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public ItemWrapper getItem(int i) {
        return filteredData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        String theme = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_theme_key), "");
//
//        View row = inflater.inflate(R.layout.file_item, viewGroup, false);
//        TextView noteTitle = (TextView) row.findViewById(R.id.note_title);
//        TextView noteExtra = (TextView) row.findViewById(R.id.note_extra);
//        ImageView fileIdentifierImageView = (ImageView) row.findViewById(R.id.file_identifier_icon);

        if(getItem(i).type==0) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            String theme = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_theme_key), "");

            View row = inflater.inflate(R.layout.file_item, viewGroup, false);
            TextView noteTitle = (TextView) row.findViewById(R.id.note_title);
            TextView noteExtra = (TextView) row.findViewById(R.id.note_extra);
            ImageView fileIdentifierImageView = (ImageView) row.findViewById(R.id.file_identifier_icon);

            noteTitle.setText(Constants.MD_EXTENSION.matcher(getItem(i).file.getName()).replaceAll(EMPTY_STRING));

            if (getItem(i).file.isDirectory()) {
                noteExtra.setText(generateExtraForFile(i));
            } else {
                noteExtra.setText(generateExtraForDirectory(i));
            }

            // Theme Adjustments
            if (theme.equals(context.getString(R.string.theme_dark))) {
                noteTitle.setTextColor(context.getResources().getColor(android.R.color.white));

                if (getItem(i).file.isDirectory()) {
                    fileIdentifierImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_folder_light));
                } else {
                    fileIdentifierImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_notes_light));
                }
            } else {
                noteTitle.setTextColor(context.getResources().getColor(R.color.dark_grey));

                if (getItem(i).file.isDirectory()) {
                    fileIdentifierImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_folder));
                } else {
                    fileIdentifierImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_notes));
                }
            }
            return row;
        }else{
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            String theme = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_theme_key), "");
            ImageView ivIcon;
            TextView tvTitle;
            TextView sponsored;
            TextView tvDescription;
            LinearLayout rootView;

            View row = inflater.inflate(R.layout.native_layout, viewGroup, false);
            sponsored = (TextView) row.findViewById(R.id.sponsored);
            ivIcon = (ImageView) row.findViewById(R.id.iconApp);
            tvTitle = (TextView) row.findViewById(R.id.title);
            tvDescription = (TextView) row.findViewById(R.id.description);
            rootView = (LinearLayout) row.findViewById(R.id.rootView);
            sponsored.setText("Sponsored");
            tvTitle.setText(getItem(i).adItem.title);
            String description = getItem(i).adItem.content;
            tvDescription.setText(description.length()>100?description.substring(0,100):description);

            if("instal".equals(getItem(i).adItem.type)){
                getItem(i).adItem.nativeResponse.registerView(rootView);
            }
            if("startapp".equals(getItem(i).adItem.type)){
                ivIcon.setImageBitmap(getItem(i).adItem.bitmap);
            }else{
                new DownloadImageTask(ivIcon).execute(getItem(i).adItem.imageUrl);
            }
            return row;
        }

//        return row;
    }

    /**
     * create bitmap from Url.
     * set Icon bitmap
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private String generateExtraForFile(int i) {
        int fileAmount = ((getItem(i).file.listFiles() == null) ? 0 : getItem(i).file.listFiles().length);
        return String.format(context.getString(R.string.number_of_files), fileAmount);
    }

    private String generateExtraForDirectory(int i) {
        String formattedDate = DateUtils.formatDateTime(context, getItem(i).file.lastModified(),
                (DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NUMERIC_DATE));
        return String.format(context.getString(R.string.last_modified), formattedDate);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults searchResults = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    searchResults.values = data;
                    searchResults.count = data.size();
                } else {
                    ArrayList<ItemWrapper> searchResultsData = new ArrayList<ItemWrapper>();

                    for (ItemWrapper item : data) {
                        if (item.file.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            searchResultsData.add(item);
                        }
                    }

                    searchResults.values = searchResultsData;
                    searchResults.count = searchResultsData.size();
                }
                return searchResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<ItemWrapper>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}