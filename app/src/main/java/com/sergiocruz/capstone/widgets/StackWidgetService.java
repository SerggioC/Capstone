package com.sergiocruz.capstone.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.util.Utils;

import java.util.ArrayList;
import java.util.Random;

import timber.log.Timber;

import static com.sergiocruz.capstone.widgets.StackWidgetProvider.WIDGET_TRAVEL_BUNDLE;
import static com.sergiocruz.capstone.widgets.StackWidgetProvider.WIDGET_TRAVEL_EXTRA;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private ArrayList<Travel> travelList;
    private Intent intent;


    StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        int mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.intent = intent;

        getDataFromBundle(intent);

    }


    private void getDataFromBundle(Intent intent) {
        Bundle bundle = intent.getBundleExtra(WIDGET_TRAVEL_BUNDLE);
        if (bundle != null) {
            travelList = bundle.getParcelableArrayList(WIDGET_TRAVEL_EXTRA);
        }
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
//        for (int i = 0; i < mCount; i++) {
////            mWidgetItems.add(new Travel(null, "name " + i, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
//            mWidgetItems.add("item " + i);
//        }

        Timber.i("oncreate");
        getDataFromBundle(intent);

        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the StackWidgetProvider and should be a sibling of the
        // collection view.
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
    }

    public int getCount() {
        Timber.i("getCount");
        if (travelList == null) return 0;
        return travelList.size();
    }

    public RemoteViews getViewAt(int position) {
        Timber.i("getting view at " + position);

        Travel travel = travelList.get(position);

        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text and image based on the position.
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        remoteViews.setTextViewText(R.id.widget_text, travel.getName() + " " + travel.getCountry());
        //remoteViews.setImageViewUri(R.id.widget_image, Uri.parse(travel.getImages().get(0)));

        // get a random image from the list
        int random = new Random().nextInt(travel.getImages().size());
        remoteViews.setImageViewBitmap(R.id.widget_image, Utils.getBitmapFromURL(travel.getImages().get(random)));

        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        remoteViews.setOnClickFillInIntent(R.id.widget_item_root, fillInIntent);
        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.

        // Return the remote views object.
        return remoteViews;
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        Timber.i("position " + position);

        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        Timber.i("onDataSetChanged");

        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
    }
}