package org.thosp.yourlocalweather.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

import org.thosp.yourlocalweather.R;
import org.thosp.yourlocalweather.model.CurrentWeatherDbHelper;
import org.thosp.yourlocalweather.model.Location;
import org.thosp.yourlocalweather.model.LocationsDbHelper;
import org.thosp.yourlocalweather.model.Weather;
import org.thosp.yourlocalweather.model.WidgetSettingsDbHelper;
import org.thosp.yourlocalweather.utils.AppPreference;
import org.thosp.yourlocalweather.utils.Constants;
import org.thosp.yourlocalweather.utils.TemperatureUtil;
import org.thosp.yourlocalweather.utils.Utils;
import org.thosp.yourlocalweather.utils.WidgetUtils;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;

public class MoreWidgetProvider extends AbstractWidgetProvider {

    private static final String TAG = "WidgetMoreInfo";

    private static final String WIDGET_NAME = "MORE_WIDGET";

    @Override
    protected void preLoadWeather(Context context, RemoteViews remoteViews, int appWidgetId) {
        final CurrentWeatherDbHelper currentWeatherDbHelper = CurrentWeatherDbHelper.getInstance(context);
        final LocationsDbHelper locationsDbHelper = LocationsDbHelper.getInstance(context);
        WidgetSettingsDbHelper widgetSettingsDbHelper = WidgetSettingsDbHelper.getInstance(context);

        Long locationId = widgetSettingsDbHelper.getParamLong(appWidgetId, "locationId");

        Location location;
        if (locationId == null) {
            location = locationsDbHelper.getLocationByOrderId(0);
            if (!location.isEnabled()) {
                location = locationsDbHelper.getLocationByOrderId(1);
            }
        } else {
            location = locationsDbHelper.getLocationById(locationId);
        }

        if (location == null) {
            return;
        }

        CurrentWeatherDbHelper.WeatherRecord weatherRecord = currentWeatherDbHelper.getWeather(location.getId());

        if (weatherRecord != null) {
            Weather weather = weatherRecord.getWeather();

            String lastUpdate = Utils.setLastUpdateTime(context, weatherRecord.getLastUpdatedTime(), location.getLocationSource());

            remoteViews.setTextViewText(R.id.widget_city, Utils.getCityAndCountry(context, location.getOrderId()));
            remoteViews.setTextViewText(R.id.widget_temperature, TemperatureUtil.getTemperatureWithUnit(
                    context,
                    weather,
                    location.getLatitude(),
                    weatherRecord.getLastUpdatedTime()));
            String secondTemperature = TemperatureUtil.getSecondTemperatureWithUnit(
                    context,
                    weather,
                    location.getLatitude(),
                    weatherRecord.getLastUpdatedTime());
            if (secondTemperature != null) {
                remoteViews.setViewVisibility(R.id.widget_second_temperature, View.VISIBLE);
                remoteViews.setTextViewText(R.id.widget_second_temperature, secondTemperature);
            } else {
                remoteViews.setViewVisibility(R.id.widget_second_temperature, View.GONE);
            }
            remoteViews.setTextViewText(R.id.widget_description, Utils.getWeatherDescription(context, weather));

            WidgetUtils.setWind(context, remoteViews, weather.getWindSpeed());
            WidgetUtils.setHumidity(context, remoteViews, weather.getHumidity());
            WidgetUtils.setPressure(context, remoteViews, weather.getPressure());
            WidgetUtils.setClouds(context, remoteViews, weather.getClouds());

            Utils.setWeatherIcon(remoteViews, context, weatherRecord);
            remoteViews.setTextViewText(R.id.widget_last_update, lastUpdate);
        } else {
            remoteViews.setTextViewText(R.id.widget_city, context.getString(R.string.location_not_found));
            remoteViews.setTextViewText(R.id.widget_temperature, TemperatureUtil.getTemperatureWithUnit(
                    context,
                    null,
                    location.getLatitude(),
                    0));
            remoteViews.setTextViewText(R.id.widget_second_temperature, TemperatureUtil.getTemperatureWithUnit(
                    context,
                    null,
                    location.getLatitude(),
                    0));
            remoteViews.setTextViewText(R.id.widget_description, "");

            WidgetUtils.setWind(context, remoteViews, 0);
            WidgetUtils.setHumidity(context, remoteViews, 0);
            WidgetUtils.setPressure(context, remoteViews, 0);
            WidgetUtils.setClouds(context, remoteViews, 0);

            Utils.setWeatherIcon(remoteViews, context, weatherRecord);
            remoteViews.setTextViewText(R.id.widget_last_update, "");
        }
    }

    public static void setWidgetTheme(Context context, RemoteViews remoteViews) {
        int textColorId = AppPreference.getTextColor(context);
        int backgroundColorId = AppPreference.getBackgroundColor(context);
        int windowHeaderBackgroundColorId = AppPreference.getWindowHeaderBackgroundColorId(context);

        remoteViews.setInt(R.id.widget_root, "setBackgroundColor", backgroundColorId);
        remoteViews.setTextColor(R.id.widget_temperature, textColorId);
        remoteViews.setTextColor(R.id.widget_second_temperature, textColorId);
        remoteViews.setTextColor(R.id.widget_description, textColorId);
        remoteViews.setTextColor(R.id.widget_description, textColorId);
        remoteViews.setTextColor(R.id.widget_wind, textColorId);
        remoteViews.setTextColor(R.id.widget_humidity, textColorId);
        remoteViews.setTextColor(R.id.widget_pressure, textColorId);
        remoteViews.setTextColor(R.id.widget_clouds, textColorId);
        remoteViews.setInt(R.id.header_layout, "setBackgroundColor", windowHeaderBackgroundColorId);
    }

    @Override
    protected int getWidgetLayout() {
        return R.layout.widget_more_3x3;
    }

    @Override
    protected Class<?> getWidgetClass() {
        return MoreWidgetProvider.class;
    }

    @Override
    protected String getWidgetName() {
        return WIDGET_NAME;
    }
}
