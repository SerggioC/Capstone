package com.sergiocruz.capstone.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.repository.FirebaseRepository;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class StackWidgetProvider extends AppWidgetProvider {
    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";
    public static final String WIDGET_TRAVEL_EXTRA = "travel_extra";
    public static final String WIDGET_TRAVEL_BUNDLE = "travel_bundle";


    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.i("onReceive");

        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched view " + viewIndex + "\n Widget ID: " + appWidgetId, Toast.LENGTH_LONG).show();
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter

        getRemoteViews(context, appWidgetManager, appWidgetIds);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void getRemoteViews(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        FirebaseRepository repository = FirebaseRepository.getInstance();

        repository.getTravelPacks().observeForever(new Observer<List<Travel>>() {
            @Override
            public void onChanged(@Nullable List<Travel> travels) {
                ArrayList<Travel> list = new ArrayList<>();
                list.addAll(travels);

                for (int i = 0; i < appWidgetIds.length; i++) {

                    // Here we setup the intent which points to the StackViewService which will
                    // provide the views for this collection.
                    Intent serviceIntent = new Intent(context, StackWidgetService.class);
                    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(WIDGET_TRAVEL_EXTRA, list);
                    serviceIntent.putExtra(WIDGET_TRAVEL_BUNDLE, bundle);

                    // When intents are compared, the extras are ignored, so we need to embed the extras
                    // into the data so that the extras will not be ignored.
                    serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

                    //remoteViews.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);
                    remoteViews.setRemoteAdapter(R.id.stack_view, serviceIntent);

                    // The empty view is displayed when the collection has no items. It should be a sibling
                    // of the collection view.
                    remoteViews.setEmptyView(R.id.widget_root, R.id.empty_view);

                    // Here we setup the a pending intent template. Individuals items of a collection
                    // cannot setup their own pending intents, instead, the collection as a whole can
                    // setup a pending intent template, and the individual items can set a fillInIntent
                    // to create unique before on an item to item basis.
                    Intent toastIntent = new Intent(context, StackWidgetProvider.class);
                    toastIntent.setAction(StackWidgetProvider.TOAST_ACTION);
                    toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

                    serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                    PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    remoteViews.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

                    appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
                }

                repository.getTravelPacks().removeObserver(this);
            }

        });

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }
}