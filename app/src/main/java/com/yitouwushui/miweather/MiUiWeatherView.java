package com.yitouwushui.miweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ding on 2017/11/30.
 */

public class MiUiWeatherView extends View {

    /**蓝色*/
    private static int DEFAULT_BULE = 0XFF00BFFF;
    /**灰色*/
    private static int DEFAULT_GRAY = Color.GRAY;

    private int backgroundColor;

    //控件的最低高度
    private int minViewHeight; //控件的最低高度
    private int minPointHeight;//折线最低点的高度
    private int lineInterval; //折线线段长度
    private float pointRadius; //折线点的半径
    private float textSize; //字体大小
    private float pointGap; //折线单位高度差
    private int defaultPadding; //折线坐标图四周留出来的偏移量
    private float iconWidth;  //天气图标的边长
    private int viewHeight;
    private int viewWidth;
    private int screenWidth;
    private int screenHeight;

    private Paint linePaint;//线条画笔
    private Paint textPaint;//文字画笔
    private Paint circlePaint;//圆点画笔

    private List<WeatherBean> data = new ArrayList<>(); //元数据
    private List<Pair<Integer, String>> weatherDatas = new ArrayList<>();  //对元数据中天气分组后的集合
    private List<Float> dashDatas = new ArrayList<>(); //不同天气之间虚线的x坐标集合
    private List<PointF> points = new ArrayList<>(); //折线拐点的集合
    private Map<String, Bitmap> icons = new HashMap<>(); //天气图标集合
    private int maxTemperature;//元数据中的最高和最低温度
    private int minTemperature;

    /**
     * 可以滑动的背景板块
     */
    private Scroller scroller;
    private ViewConfiguration viewConfiguration;
    private VelocityTracker velocityTracker;


    public MiUiWeatherView(Context context) {
        super(context);
    }

    public MiUiWeatherView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MiUiWeatherView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        viewConfiguration = ViewConfiguration.get(context);

        @SuppressLint({"CustomViewStyleable", "Recycle"})
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MiuiWeatherView);
        minPointHeight = (int) ta.getDimension(R.styleable.MiuiWeatherView_min_point_height, dp2pxF(context, 60));
        lineInterval = (int) ta.getDimension(R.styleable.MiuiWeatherView_line_interval, dp2pxF(context, 60));
        backgroundColor = ta.getColor(R.styleable.MiuiWeatherView_background_color, Color.WHITE);
        ta.recycle();

        setBackgroundColor(backgroundColor);

        initSize(context);

        initPaint(context);

        initIcons();
    }



    /**
     * 初始化笔
     * @param context
     */
    private void initPaint(Context context) {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(dp2px(context,1));

        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStrokeWidth(dp2pxF(context,1));


    }

    /**
     * 初始化尺寸
     * @param context
     */
    private void initSize(Context context) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        minViewHeight = 3 * minPointHeight;//默认3倍
        pointRadius = dp2pxF(context,2.5f);
        textSize = sp2pxF(context,10);
        defaultPadding = (int) (0.5 * minPointHeight);//默认0.5倍内间距
        iconWidth = (1.0f/3.0f) * lineInterval;//默认1/3倍
    }

    /**
     * 初始化天气图标集合
     * （涉及解析、缩放等耗时操作，故不要在ondraw里再去获取图片，提前解析好放在集合里)
     */
    private void initIcons() {
        icons.clear();
        String[] weathers = WeatherBean.getAllWeathers();
        for (int i = 0; i < weathers.length; i++) {
            Bitmap bmp = getWeatherIcon(weathers[i], iconWidth, iconWidth);
            icons.put(weathers[i], bmp);
        }
    }

    /**
     * 根据天气获取对应的图标，并且缩放到指定大小
     * @param weather
     * @param requestW
     * @param requestH
     * @return
     */
    private Bitmap getWeatherIcon(String weather, float requestW, float requestH) {
        int resId = getIconResId(weather);
        Bitmap bmp;
        int outWidth, outHeight;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        outWidth = options.outWidth;
        outHeight = options.outHeight;
        options.inSampleSize = 1;
        if (outWidth > requestW || outHeight > requestH) {
            int ratioW = Math.round(outWidth / requestW);
            int ratioH = Math.round(outHeight / requestH);
            options.inSampleSize = Math.max(ratioW, ratioH);
        }
        options.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeResource(getResources(), resId, options);
        return bmp;
    }

    private int getIconResId(String weather) {
        int resId;
        switch (weather) {
            case WeatherBean.SUN:
                resId = R.drawable.sun;
                break;
            case WeatherBean.CLOUDY:
                resId = R.drawable.cloudy;
                break;
            case WeatherBean.RAIN:
                resId = R.drawable.rain;
                break;
            case WeatherBean.SNOW:
                resId = R.drawable.snow;
                break;
            case WeatherBean.SUN_CLOUD:
                resId = R.drawable.sun_cloud;
                break;
            case WeatherBean.THUNDER:
            default:
                resId = R.drawable.thunder;
                break;
        }
        return resId;
    }

    public static int dp2px(Context c, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context c, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }

    public static float dp2pxF(Context c, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static float sp2pxF(Context c, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }
}
