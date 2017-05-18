package com.lp.lockpatternview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.R.attr.y;
import static android.os.Build.VERSION_CODES.M;

/**
 * 图形锁
 * Created by Lipeng on 2017/2/20.
 */

public class LockPatternView extends View {
    //创建画笔
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final static int POINT_SIZE = 5;
    //矩阵
    private Matrix matrix = new Matrix();
    //9个点
    private Point[][] points = new Point[3][3];
    private boolean isInit = false;
    private boolean isSelect, isFinish, movingNoPoint;
    private float width, heigth, offsetsX, offsetsY, bitmapR, movingX, movingY;
    private Bitmap pointNormal, pointPressed, pointError, linePressed, lineError;
    //监听器
    private OnPatternChangeListener onPatternChangeListener;

    private List<Point> pointsList = new ArrayList<Point>();

    public LockPatternView(Context context) {
        super(context);
    }

    public LockPatternView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public LockPatternView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initPoints();
        }
        //画点
        points2Canvas(canvas);
        //画线
        if (pointsList.size() > 0) {
            Point a = pointsList.get(0);
            //绘制九宫格里的点
            for (int i = 0; i < pointsList.size(); i++) {
                Point b = pointsList.get(i);
                line2Canvas(canvas, a, b);
                a = b;
            }
            //绘制鼠标坐标点
            if (movingNoPoint) {
                line2Canvas(canvas, a, new Point(movingX, movingY));
            }
        }

    }

    /**
     * 将点画到画布上
     *
     * @param canvas 画布
     */
    private void points2Canvas(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (point.state == Point.STATE_PRESSED) {
                    canvas.drawBitmap(pointPressed, Point.x - bitmapR, Point.y - bitmapR, paint);
                } else if (point.state == Point.STATE_ERROR) {
                    canvas.drawBitmap(pointError, Point.x - bitmapR, Point.y - bitmapR, paint);
                } else {
                    canvas.drawBitmap(pointNormal, Point.x - bitmapR, Point.y - bitmapR, paint);
                }
            }
        }

    }

    /**
     * 画线
     * @param canvas 画布
     * @param a      第一个点
     * @param b      第二个点
     */
    private void line2Canvas(Canvas canvas, Point a, Point b) {
        float lineLength = Point.distance(a, b);
        float degrees = getDegrees(a, b);
        canvas.rotate(degrees, a.x, a.y);
        if (a.state == Point.STATE_PRESSED) {
            matrix.setScale(lineLength / linePressed.getWidth(), 1);
            matrix.postTranslate(a.x - linePressed.getWidth() / 2, a.y - linePressed.getHeight() / 2);
            canvas.drawBitmap(linePressed, matrix, paint);
        } else {
            matrix.setScale(lineLength / lineError.getWidth(), 1);
            matrix.postTranslate(a.x - lineError.getWidth() / 2, a.y - lineError.getHeight() / 2);
            canvas.drawBitmap(lineError, matrix, paint);
        }
        canvas.rotate(-degrees, a.x, a.y);
    }

    /**
     * 旋转角度
     * @param a 第一个点
     * @param b 第二点
     * @return 角度
     */
    private float getDegrees(Point a, Point b) {
        float ax = a.x;
        float ay = a.y;
        float bx = b.x;
        float by = b.y;
        float degrees = 0;
        if (bx == ax){       //在Y轴上 90或270度
           if (by > ay){     //b点在Y的正半轴上
              degrees = 90;
           } else if (by < ay){           //b点在Y的负半轴上
               degrees = 270;
           }
        } else if (by == ay){  //在X轴上 0或180度
           if (bx > ax){      //b点在X轴的正半轴上
               degrees = 0;
           } else if (bx < ax){           //b点在X轴的负半轴上
               degrees = 180;
           }
        } else {
            if (bx > ax) {    //在Y轴的右边 270-90度
                if (by > ay) {
                    degrees = 0;   //在第一限
                    degrees = degrees + switchDegrees(Math.abs(by - ay), Math.abs(bx - ax));
                } else if (by < ay) {  //在第四限
                    degrees = 360;
                    degrees = degrees - switchDegrees(Math.abs(by - ax), Math.abs(bx - ax));
                }
            } else if (bx < ax) {
                if (by > ay) {       //在第二限
                    degrees = 90;
                    degrees = degrees + switchDegrees(Math.abs(bx - ax), Math.abs(by - ay));
                } else if (by < ay) {   //在第三限
                    degrees = 270;
                    degrees = degrees - switchDegrees(Math.abs(bx - ax), Math.abs(by - ay));
                }
            }
        }
        return degrees;
    }

    private float switchDegrees(float x, float y) {
        //弧度转化为角度
        return (float) Math.toDegrees(Math.atan2(x, y));

    }

    /**
     * 初始化点
     */
    private void initPoints() {
        //1.获取布局宽高
        width = getWidth();
        heigth = getHeight();

        //2.偏移量
        //横屏
        if (width > heigth) {
            offsetsX = (width - heigth) / 2;
            width = heigth;
            //竖屏
        } else {
            offsetsY = (heigth - width) / 2;
            heigth = width;
        }

        //3.图片资源
        pointNormal = BitmapFactory.decodeResource(getResources(), R.drawable.oval_normal);
        pointPressed = BitmapFactory.decodeResource(getResources(), R.drawable.oval_pressed);
        pointError = BitmapFactory.decodeResource(getResources(), R.drawable.oval_error);
        linePressed = BitmapFactory.decodeResource(getResources(), R.drawable.line_pressed);
        lineError = BitmapFactory.decodeResource(getResources(), R.drawable.line_error);

        //4.点的坐标
        points[0][0] = new Point(offsetsX + width / 4, offsetsY + width / 4);
        points[0][1] = new Point(offsetsX + width / 2, offsetsY + width / 4);
        points[0][2] = new Point(offsetsX + width - width / 4, offsetsY + width / 4);

        points[1][0] = new Point(offsetsX + width / 4, offsetsY + width / 2);
        points[1][1] = new Point(offsetsX + width / 2, offsetsY + width / 2);
        points[1][2] = new Point(offsetsX + width - width / 4, offsetsY + width / 2);

        points[2][0] = new Point(offsetsX + width / 4, offsetsY + width - width / 4);
        points[2][1] = new Point(offsetsX + width / 2, offsetsY + width - width / 4);
        points[2][2] = new Point(offsetsX + width - width / 4, offsetsY + width - width / 4);

        //5.图片资源的半径
        bitmapR = pointNormal.getHeight() / 2;

        //6.设置密码
        int index = 1；
        for (Point[] points: points){
            for (Point point: points){
                point.index = index;
                index++;
            }
        }

        //7.初始化完成
        isInit = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        movingNoPoint = false;
        isFinish = false;
        movingX = event.getX();
        movingY = event.getY();
        Point point = null;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //重新绘制
                if (onPatternChangeListener != null){
                    onPatternChangeListener.onPatternStart(true);
                }
                resetPoint();
                point = checkSelectPoint();
                if (point != null) {
                    isSelect = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isSelect) {
                    point = checkSelectPoint();
                    if (point == null) {
                        movingNoPoint = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isFinish = true;
                isSelect = false;
                break;
            default:
                break;
        }
        //选中重复检查
        if (!isFinish && isSelect && point != null) {
            //交叉点
            if (crossPoint(point)) {
                movingNoPoint = true;
                //新点
            } else {
                point.state = Point.STATE_PRESSED;
                pointsList.add(point);
            }
        }

        //绘制结束
        if (isFinish) {
            //绘制不成立
            if (pointsList.size() == 1) {
                resetPoint();
                //绘制错误
            } else if (pointsList.size() < POINT_SIZE && pointsList.size() > 0) {
                errorPoint();
                if (onPatternChangeListener != null){
                    onPatternChangeListener.onPatternChange(null);
                }
                //绘制成功
            } else{
                if (onPatternChangeListener != null){
                    String passwordStr = "";
                    for (int i = 0; i < pointsList.size(); i++){
                        passwordStr = passwordStr + pointsList.get(i).index;
                    }
                    if (!TextUtils.isEmpty(passwordStr)){
                        onPatternChangeListener.onPatternChange(passwordStr);
                    }
                }
            }

        }
        //刷新View
        postInvalidate();
        return true;
    }

    /**
     * 交叉点
     *
     * @param point 点
     * @return是否交叉
     */
    private boolean crossPoint(Point point) {
        if (pointsList.contains(point)) {
            return true;
        } else {
            return false;
        }
    }

    //设置绘制不成立
    private void resetPoint() {
        for (int i = 0; i < pointsList.size(); i++){
            Point point = pointsList.get(i);
            point.state = Point.STATE_NORMAL;
        }
        pointsList.clear();
    }

    //设置绘制错误
    private void errorPoint() {
        for (Point point : pointsList) {
            point.state = Point.STATE_ERROR;
        }
    }

    /**
     * 检查是否选中
     */
    private Point checkSelectPoint() {
        for (int x = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (Point.with(Point.x, Point.y, bitmapR, movingX, movingY)) {
                    return point;
                }
            }
        }
        return null;
    }

    /**
     * 自定义的点
     *
     * @author lp
     */
    public static class Point {
        //正常
        public final static int STATE_NORMAL = 0;
        //选中
        public final static int STATE_PRESSED = 1;
        //错误
        public final static int STATE_ERROR = 2;
        public float x, y;
        public int index = 0, state = 0;

        public Point() {
        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        /**
         * 2点之间的距离
         *
         * @param a 点a
         * @param b 点b
         * @return 距离
         */
        public static double distance(Point a, Point b) {
            return Math.sqrt(Math.abs(a.x - b.x) * Math.abs(a.x - b.x) + Math.abs(a.y - b.y) * Math.abs(a.y - b.y));
        }

        /**
         * 是否重合
         *
         * @param x       参考点的X
         * @param y       参考点的y
         * @param r       圆的半径
         * @param movingx 移动点的x
         * @param movingy 移动点的y
         * @return
         */
        public static boolean with(float x, float y, Bitmap r, float movingx, float movingy) {
            return Math.sqrt((x - movingx) * (x - movingx) + (y - movingy) * (y - movingy)) < r;
        }
    }

    /**
     * 图案监听器
     */
    public static interface OnPatternChangeListener{
        /**
         * 图案改变
         * @param passwordStr 图案密码
         */
        void onPatternChange(String passwordStr);

        /**
         * 图案重新绘制
         * @param isStart
         */
        void onPatternStart(boolean isStart);
    }

    /**
     * 设置图案监听器
     * @param onPatternChangeListener
     */
    public void setPatternChangeListener(OnPatternChangeListener onPatternChangeListener){
        if (onPatternChangeListener != null){
            this.onPatternChangeListener = onPatternChangeListener;
        }
    }
}
