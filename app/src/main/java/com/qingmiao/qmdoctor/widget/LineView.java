package com.qingmiao.qmdoctor.widget;

/**
 * company : 青苗
 * Created by 刘伟杰 on 2017/3/30.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class LineView extends View {

    private float viewHeight;
    private float viewWith;
    private float brokenLineWith = 1f;

    private int brokenLineColor = 0xff1ed06d;
    private int brokenLineRedColor = 0xffe80725;
    private int straightLineColor = 0xffe2e2e2;
    private int textNormalColor = 0xff7e7e7e;

    private Path brokenPath;
    private Paint brokenPaint;
    private Paint straightPaint;
    private Paint dottedPaint;
    private Paint textPaint;

    private double maxScore = 100;
    private double minScore = 0;
    private ArrayList<Double> score;
    private int selectMonth;
    private ArrayList<String> timeText;

    private List<Point> scorePoints;
    private int textSize = dipToPx(15);
    private double maxValue;


    public LineView(Context context) {
        this(context, null);
    }

    public LineView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    private void init() {
        brokenPath = new Path();

        brokenPaint = new Paint();
        brokenPaint.setAntiAlias(true);
        brokenPaint.setStyle(Paint.Style.STROKE);
        brokenPaint.setStrokeWidth(dipToPx(brokenLineWith));
        brokenPaint.setStrokeCap(Paint.Cap.ROUND);

        straightPaint = new Paint();
        straightPaint.setAntiAlias(true);
        straightPaint.setStyle(Paint.Style.STROKE);
        straightPaint.setStrokeWidth(brokenLineWith);
        straightPaint.setColor((straightLineColor));
        straightPaint.setStrokeCap(Paint.Cap.ROUND);

        dottedPaint = new Paint();
        dottedPaint.setAntiAlias(true);
        dottedPaint.setStyle(Paint.Style.STROKE);
        dottedPaint.setStrokeWidth(brokenLineWith);
        dottedPaint.setColor((straightLineColor));
        dottedPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor((textNormalColor));
        textPaint.setTextSize(dipToPx(15));
    }

    public void initData() {
        scorePoints = new ArrayList<>();
        float maxScoreYCoordinate = viewHeight * 0.3f;
        float minScoreYCoordinate = viewHeight * 0.67f;

        float newWith = viewWith - (viewWith * 0.25f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        int coordinateX;

        for (int i = 0; i < score.size(); i++) {
            Point point = new Point();
            if (i == 0) {
                coordinateX = (int) (viewWith * 0.20f);
            } else {

                coordinateX = (int) (newWith * ((float) (i) / (timeText.size() - 1)) + (viewWith * 0.20f));
            }

            point.x = coordinateX;
            if (score.get(i) > maxScore) {
                point.y = (int) (((float) (maxScore - score.get(i)) / (maxValue - maxScore)) * (0.2f * (minScoreYCoordinate - maxScoreYCoordinate))
                        + maxScoreYCoordinate - 0.05f * (minScoreYCoordinate - maxScoreYCoordinate));} else if (score.get(i) - minScore <= -(maxScore - minScore) / 5) {
                point.y = (int) (minScoreYCoordinate + 0.2f * (minScoreYCoordinate - maxScoreYCoordinate));
            } else {
                point.y = (int) (((float) (maxScore - score.get(i)) / (maxScore - minScore)) * (minScoreYCoordinate - maxScoreYCoordinate) + maxScoreYCoordinate);
            }
            scorePoints.add(point);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWith = w;
        viewHeight = h;
        initData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDottedLine(canvas, viewWith * 0.1f, viewHeight * 0.3f, viewWith, viewHeight * 0.3f);
        drawDottedLine(canvas, viewWith * 0.1f, viewHeight * 0.67f, viewWith, viewHeight * 0.67f);
        drawText(canvas);
        drawMonthLine(canvas);
        drawBrokenLine(canvas);
        drawPoint(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                boolean isTouch = onActionUpEvent(event);
                if (isTouch) {
                    return true;
                } else {
                    return super.dispatchTouchEvent(event);
                }
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean onActionUpEvent(MotionEvent event) {
        boolean isValidTouch = validateTouch(event.getX(), event.getY());
        if (isValidTouch) {
            invalidate();
        }
        return isValidTouch;
    }

    //是否是有效的触摸范围
    private boolean validateTouch(float x, float y) {

        //曲线触摸区域
        for (int i = 0; i < scorePoints.size(); i++) {
            // dipToPx(8)乘以2为了适当增大触摸面积
            if (x > (scorePoints.get(i).x - dipToPx(8) * 2) && x < (scorePoints.get(i).x + dipToPx(8) * 2)) {
                if (y > (scorePoints.get(i).y - dipToPx(8) * 2) && y < (scorePoints.get(i).y + dipToPx(8) * 2)) {
                    selectMonth = i + 1;
                    return true;
                }
            }
        }
/*

        //月份触摸区域
        //计算每个月份X坐标的中心点
        float monthTouchY = viewHeight * 0.77f - dipToPx(3);//减去dipToPx(3)增大触摸面积

        float newWith = viewWith - (viewWith * 0.25f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        float validTouchX[] = new float[timeText.size()];
        for (int i = 0; i < timeText.size(); i++) {
            validTouchX[i] = newWith * ((float) (i) / (timeText.size() - 1)) + (viewWith * 0.20f);
        }

        if (y > monthTouchY) {
            for (int i = 0; i < validTouchX.length; i++) {
                Log.v("ScoreTrend", "validateTouch: validTouchX:" + validTouchX[i]);
                if (x < validTouchX[i] + dipToPx(8) && x > validTouchX[i] - dipToPx(8)) {
                    Log.v("ScoreTrend", "validateTouch: " + (i + 1));
                    selectMonth = i + 1;
                    return true;
                }
            }
        }
*/

        return false;
    }


    //绘制折线穿过的点
    private void drawPoint(Canvas canvas) {
        if (scorePoints == null) {
            return;
        }
        brokenPaint.setStrokeWidth(dipToPx(1));
        for (int i = 0; i < scorePoints.size(); i++) {

            if (maxScore == 30 && minScore == 10) {
                if (score.get(i) < maxScore) {
                    brokenPaint.setColor(brokenLineRedColor);
                } else {
                    brokenPaint.setColor(brokenLineColor);
                }
            } else {
                if (score.get(i) > maxScore || score.get(i) < minScore) {
                    brokenPaint.setColor(brokenLineRedColor);
                } else {
                    brokenPaint.setColor(brokenLineColor);
                }
            }

            brokenPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(5f), brokenPaint);
            brokenPaint.setColor(Color.WHITE);
            brokenPaint.setStyle(Paint.Style.FILL);
            if (i == selectMonth - 1) {

                if (maxScore == 30 && minScore == 10) {
                    if (score.get(i) < maxScore) {
                        brokenPaint.setColor(0x99e80725);
                    } else {
                        brokenPaint.setColor(0x991ed06d);
                    }
                } else {
                    if (score.get(i) > maxScore || score.get(i) < minScore) {
                        brokenPaint.setColor(0x99e80725);
                    } else {
                        brokenPaint.setColor(0x991ed06d);
                    }
                }

                canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(10f), brokenPaint);
                brokenPaint.setColor(0xff81dddb);
                canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(5f), brokenPaint);

                //绘制浮动文本背景框
                drawFloatTextBackground(canvas, scorePoints.get(i).x, scorePoints.get(i).y - dipToPx(8f), score.get(i));
                textPaint.setColor(0xffffffff);
                textPaint.setTextSize(dipToPx(12));
                //绘制浮动文字
                if (maxScore == 30 && minScore == 10) {
                    String text = "阴性";
                    switch (score.get(i).intValue()) {
                        case 30:
                            text = "阴性";
                            break;
                        case 20:
                            text = "±";
                            break;
                        case 10:
                            text = "阳性";
                            break;
                    }
                    canvas.drawText(text, scorePoints.get(i).x, scorePoints.get(i).y - dipToPx(5f) - textSize, textPaint);
                } else {
                    canvas.drawText(String.valueOf(score.get(i)), scorePoints.get(i).x, scorePoints.get(i).y - dipToPx(5f) - textSize, textPaint);
                }
            }
            brokenPaint.setColor(0xffffffff);
            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(4f), brokenPaint);
            brokenPaint.setStyle(Paint.Style.STROKE);

            if (maxScore == 50 && minScore == 10) {
                if (score.get(i) < maxScore) {
                    brokenPaint.setColor(brokenLineRedColor);
                } else {
                    brokenPaint.setColor(brokenLineColor);
                }
            } else {
                if (score.get(i) > maxScore || score.get(i) < minScore) {
                    brokenPaint.setColor(brokenLineRedColor);
                } else {
                    brokenPaint.setColor(brokenLineColor);
                }
            }

            canvas.drawCircle(scorePoints.get(i).x, scorePoints.get(i).y, dipToPx(5f), brokenPaint);
        }
    }

    //绘制月份的直线(包括刻度)
    private void drawMonthLine(Canvas canvas) {
        straightPaint.setStrokeWidth(dipToPx(1));
        //横线
        canvas.drawLine(viewWith * 0.1f - dipToPx(10), viewHeight * 0.77f + dipToPx(4), viewWith, viewHeight * 0.77f + dipToPx(4), straightPaint);
        //纵线
        canvas.drawLine(viewWith * 0.1f, viewHeight * 0.22f - textSize * 0.25f, viewWith * 0.1f, viewHeight * 0.77f + dipToPx(10), straightPaint);

        float newWith = viewWith - (viewWith * 0.25f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        float coordinateX;//分隔线X坐标
        for (int i = 0; i < timeText.size(); i++) {
            coordinateX = newWith * ((float) (i) / (timeText.size() - 1)) + (viewWith * 0.20f);
            canvas.drawLine(coordinateX, viewHeight * 0.77f, coordinateX, viewHeight * 0.77f + dipToPx(4), straightPaint);
        }
    }

    //绘制折线
    private void drawBrokenLine(Canvas canvas) {
        brokenPath.reset();
        brokenPaint.setColor(0xff666666);
        brokenPaint.setStyle(Paint.Style.STROKE);
        if (score.size() == 0) {
            return;
        }

        brokenPath.moveTo(scorePoints.get(0).x, scorePoints.get(0).y);
        for (int i = 0; i < scorePoints.size(); i++) {
            brokenPath.lineTo(scorePoints.get(i).x, scorePoints.get(i).y);
        }
        canvas.drawPath(brokenPath, brokenPaint);

    }

    //绘制文本
    private void drawText(Canvas canvas) {
        textPaint.setTextSize(dipToPx(12));
        textPaint.setColor(textNormalColor);

        if (maxScore == 30 && minScore == 10) {
            canvas.drawText("阴性", viewWith * 0.1f - dipToPx(16), viewHeight * 0.3f + textSize * 0.25f, textPaint);
            canvas.drawText("阳性", viewWith * 0.1f - dipToPx(16), viewHeight * 0.67f + textSize * 0.25f, textPaint);
        } else {
            canvas.drawText(String.valueOf(maxScore), viewWith * 0.1f - dipToPx(16), viewHeight * 0.3f + textSize * 0.25f, textPaint);
            canvas.drawText(String.valueOf(minScore), viewWith * 0.1f - dipToPx(16), viewHeight * 0.67f + textSize * 0.25f, textPaint);
        }

        textPaint.setColor(0xff7c7c7c);
        float newWith = viewWith - (viewWith * 0.25f) * 2;//分隔线距离最左边和最右边的距离是0.15倍的viewWith
        float coordinateX;//分隔线X坐标
        textPaint.setTextSize(dipToPx(12));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textNormalColor);
        textSize = (int) textPaint.getTextSize();
        canvas.drawText("时间", viewWith * 0.1f, viewHeight * 0.8f + dipToPx(4) + textSize + dipToPx(5), textPaint);
        textPaint.setTextSize(dipToPx(9));
        for (int i = 0; i < timeText.size(); i++) {
            if (i == 0) {
                coordinateX = (viewWith * 0.20f);
            } else {
                coordinateX = newWith * ((float) (i) / (timeText.size() - 1)) + (viewWith * 0.20f);
            }

            if (i == selectMonth - 1) {

                textPaint.setStyle(Paint.Style.STROKE);
                textPaint.setColor(brokenLineColor);
                RectF r2 = new RectF();
                r2.left = coordinateX - textSize - dipToPx(15);
                r2.top = viewHeight * 0.8f + dipToPx(4) + textSize / 2;
                r2.right = coordinateX + textSize + dipToPx(15);
                r2.bottom = viewHeight * 0.8f + dipToPx(4) + textSize + dipToPx(10);
                canvas.drawRoundRect(r2, 10, 10, textPaint);
                //绘制时间
                canvas.drawText(timeText.get(i), coordinateX, viewHeight * 0.8f + dipToPx(4) + textSize + dipToPx(5), textPaint);

            }

            textPaint.setColor(textNormalColor);
        }
    }

    //绘制显示浮动文字的背景
    private void drawFloatTextBackground(Canvas canvas, int x, int y, double score) {
        brokenPath.reset();

        if (maxScore == 30 && minScore == 10) {
            if (score < maxScore) {
                brokenPaint.setColor(brokenLineRedColor);
            } else {
                brokenPaint.setColor(brokenLineColor);
            }
        } else {
            if (score > maxScore || score < minScore) {
                brokenPaint.setColor(brokenLineRedColor);
            } else {
                brokenPaint.setColor(brokenLineColor);
            }
        }
        brokenPaint.setStyle(Paint.Style.FILL);

        //P1
        Point point = new Point(x, y);
        brokenPath.moveTo(point.x, point.y);

        //P2
        point.x = point.x + dipToPx(5);
        point.y = point.y - dipToPx(5);
        brokenPath.lineTo(point.x, point.y);

        //P3
        point.x = point.x + dipToPx(12);
        brokenPath.lineTo(point.x, point.y);

        //P4
        point.y = point.y - dipToPx(17);
        brokenPath.lineTo(point.x, point.y);

        //P5
        point.x = point.x - dipToPx(34);
        brokenPath.lineTo(point.x, point.y);

        //P6
        point.y = point.y + dipToPx(17);
        brokenPath.lineTo(point.x, point.y);

        //P7
        point.x = point.x + dipToPx(12);
        brokenPath.lineTo(point.x, point.y);

        //最后一个点连接到第一个点
        brokenPath.lineTo(x, y);

        canvas.drawPath(brokenPath, brokenPaint);
    }

    /**
     * 画虚线
     *
     * @param canvas 画布
     * @param startX 起始点X坐标
     * @param startY 起始点Y坐标
     * @param stopX  终点X坐标
     * @param stopY  终点Y坐标
     */
    private void drawDottedLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {
        dottedPaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 4));
        dottedPaint.setStrokeWidth(1);
        // 实例化路径
        Path mPath = new Path();
        mPath.reset();
        // 定义路径的起点
        mPath.moveTo(startX, startY);
        mPath.lineTo(stopX, stopY);
        canvas.drawPath(mPath, dottedPaint);

    }

    //动态设置参数方法
    public void setScore(ArrayList<Double> score) {
        this.score = score;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;

    }

    public void setTimeText(ArrayList<String> timeText) {
        this.timeText = timeText;
        selectMonth = timeText.size();
    }


    /**
     * dip 转换成px
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public void setDate() {
        initData();
    }
}
