package com.haichenyi.aloe.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Range;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.haichenyi.aloe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aloe
 * @time 2018/6/9 22:10
 * @email aloe200@163.com
 */
public class IndexBar extends View {
  /**
   * 文字大小偏移量，使文字有间距.
   */
  private static final float TEXT_SIZE_LIMIT = 1.15F;
  /**
   * 默认文字大小，单位sp.
   */
  private static final byte DEFAULT_TEXT_SIZE = 16;
  /**
   * 默认标题文字大小，单位sp.
   */
  private static final byte DEFAULT_TITLE_TEXT_SIZE = 14;
  /**
   * 默认头部高度，单位dp.
   */
  private static final byte DEFAULT_TITLE_HEIGHT = 24;
  /**
   * 无效的下标.
   */
  private static final byte INVALID_INDEX = -1;
  /**
   * 无效的颜色.
   */
  private static final byte INVALID_COLOR = -1;
  /**
   * 使文字居中处理.
   */
  private static final float HALF = 2.0F;
  /**
   * 宽度.
   */
  private int width;
  /**
   * 高度.
   */
  private int height;
  /**
   * 文字大小.
   */
  private int textSize;
  /**
   * RecyclerView头部数量.
   */
  private int headerViewCount;
  /**
   * 按下时背景.
   */
  private int pressedBackground;
  /**
   * 顶部距离.
   */
  private float top;
  /**
   * 画笔.
   */
  private Paint paint;
  /**
   * 显示数据.
   */
  private List<String> indexData;
  /**
   * 按下时显示的TextView.
   */
  private TextView pressedShowTextView;
  /**
   * 显示矩形区间.
   */
  private final Rect indexBounds = new Rect();
  /**
   * 悬停Decoration.
   */
  private SuspensionDecoration decoration;
  /**
   * 线性布局管理器.
   */
  private LinearLayoutManager layoutManager;
  /**
   * 源数据.
   */
  private List<? extends SuspensionHelper> sourceData;
  /**
   * 按下监听器.
   */
  private OnIndexPressedListener onIndexPressedListener;

  public IndexBar(final Context context) {
    this(context, null);
  }

  public IndexBar(final Context context, final AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public IndexBar(final Context context, final AttributeSet attrs, final int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  /**
   * 绑定RecyclerView.
   *
   * @param recyclerView RecyclerView
   * @param builder      参数
   */
  public final void bindRecyclerView(final RecyclerView recyclerView, final Builder builder) {
    recyclerView.setLayoutManager(layoutManager);
    if (builder != null) {
      headerViewCount = builder.headerViewCount;
      pressedShowTextView = builder.pressedShowTextView;
      decoration = builder.create();
    } else {
      decoration = new SuspensionDecoration();
    }
    recyclerView.addItemDecoration(decoration);
  }

  /**
   * 初始化参数.
   *
   * @param context      {@link Context}
   * @param attrs        xml参数
   * @param defStyleAttr 样式
   */
  private void init(final Context context, final AttributeSet attrs, final int defStyleAttr) {
    layoutManager = new LinearLayoutManager(context);
    textSize = (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, getResources().getDisplayMetrics());
    pressedBackground = Color.BLACK;
    TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IndexBar,
        defStyleAttr, 0);
    int n = typedArray.getIndexCount();
    for (int i = 0; i < n; i++) {
      int attr = typedArray.getIndex(i);
      if (attr == R.styleable.IndexBar_indexBarTextSize) {
        textSize = typedArray.getDimensionPixelSize(attr, textSize);
      } else if (attr == R.styleable.IndexBar_indexBarPressBackground) {
        pressedBackground = typedArray.getColor(attr, pressedBackground);
      }
    }
    typedArray.recycle();
    indexData = new ArrayList<>();
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setTextSize(textSize);
    paint.setColor(Color.BLACK);
    onIndexPressedListener = new OnIndexPressedListener() {
      @Override
      public void onIndexPressed(final int index, final String text) {
        if (pressedShowTextView != null) {
          pressedShowTextView.setVisibility(View.VISIBLE);
          pressedShowTextView.setText(text);
        }
        if (layoutManager != null) {
          int position = getPosByTag(text);
          if (position != INVALID_INDEX) {
            layoutManager.scrollToPositionWithOffset(position, 0);
          }
        }
      }

      @Override
      public void onMotionEventEnd() {
        if (pressedShowTextView != null) {
          pressedShowTextView.setVisibility(View.GONE);
        }
      }
    };
  }

  @Override
  protected final void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    int measureWidth = 0;
    int measureHeight = 0;
    indexBounds.setEmpty();
    String index;
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    for (int i = 0; i < indexData.size(); i++) {
      index = indexData.get(i);
      paint.getTextBounds(index, 0, index.length(), indexBounds);
      measureWidth = Math.max(indexBounds.width(), measureWidth);
      measureHeight = Math.max(indexBounds.height(), measureHeight);
    }
    measureHeight *= indexData.size();
    switch (widthMode) {
      case MeasureSpec.EXACTLY:
        measureWidth = widthSize;
        break;
      case MeasureSpec.AT_MOST:
        measureWidth = Math.min(measureWidth, widthSize);
        break;
      case MeasureSpec.UNSPECIFIED:
        break;
      default:
        break;
    }
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    switch (heightMode) {
      case MeasureSpec.EXACTLY:
        measureHeight = heightSize;
        break;
      case MeasureSpec.AT_MOST:
        measureHeight = Math.min(measureHeight, heightSize);
        break;
      case MeasureSpec.UNSPECIFIED:
        break;
      default:
        break;
    }
    setMeasuredDimension(measureWidth, measureHeight);
  }


  @Override
  protected final void onDraw(final Canvas canvas) {
    String index;
    for (int i = 0; i < indexData.size(); i++) {
      index = indexData.get(i);
      canvas.drawText(index, width / HALF - paint.measureText(index) / HALF,
          textSize * TEXT_SIZE_LIMIT * i + top, paint);
    }
  }

  /**
   * 处理滑动事件.
   *
   * @param event 滑动事件
   * @return 是否处理完，true处理完，不传递事件；false没处理完，继续传递事件
   */
  @Override
  @SuppressLint("ClickableViewAccessibility")
  public final boolean onTouchEvent(final MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        setBackgroundColor(pressedBackground);
      case MotionEvent.ACTION_MOVE:
        float y = event.getY();
        int pressI = Math.round((y - top) / TEXT_SIZE_LIMIT / textSize);
        if (pressI < 0) {
          pressI = 0;
        } else if (pressI > indexData.size() - 1) {
          pressI = indexData.size() - 1;
        }
        if (null != onIndexPressedListener && pressI > INVALID_INDEX && pressI < indexData.size()) {
          onIndexPressedListener.onIndexPressed(pressI, indexData.get(pressI));
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
      default:
        setBackgroundResource(android.R.color.transparent);
        if (null != onIndexPressedListener) {
          onIndexPressedListener.onMotionEventEnd();
        }
        break;
    }
    return true;
  }

  @Override
  protected final void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    width = w;
    height = h;
    if (null == indexData || indexData.isEmpty()) {
      return;
    }
    computeTop();
  }

  /**
   * 按下监听接口.
   */
  private interface OnIndexPressedListener {
    /**
     * 按下回调方法.
     *
     * @param index 按下位置
     * @param text  按下文本
     */
    void onIndexPressed(int index, String text);

    /**
     * 事件处理完全回调方法.
     */
    void onMotionEventEnd();
  }

  /**
   * 设置数据源.
   *
   * @param sourceData 数据源
   * @return {@link IndexBar}
   */
  public final IndexBar setSourceData(final List<? extends SuspensionHelper> sourceData) {
    this.sourceData = sourceData;
    decoration.setData(sourceData);
    initSourceData();
    return this;
  }

  /**
   * 初始化数据源.
   */
  private void initSourceData() {
    if (null == sourceData || sourceData.isEmpty()) {
      return;
    }
    indexData.clear();
    for (SuspensionHelper bean : sourceData) {
      if (!indexData.contains(bean.getTag())) {
        indexData.add(bean.getTag());
      }
    }
    computeTop();
  }

  /**
   * 计算顶部距离.
   */
  private void computeTop() {
    top = (height - indexData.size() * textSize * TEXT_SIZE_LIMIT) / HALF;
  }

  /**
   * 根据文本标签获取位置.
   *
   * @param tag 文本标签
   * @return 文本位置
   */
  private int getPosByTag(final String tag) {
    if (null == sourceData || sourceData.isEmpty() || TextUtils.isEmpty(tag)) {
      return INVALID_INDEX;
    }
    for (int i = 0; i < sourceData.size(); i++) {
      if (tag.equals(sourceData.get(i).getTag())) {
        return i + headerViewCount;
      }
    }
    return INVALID_INDEX;
  }

  /**
   * 头部悬浮帮助类.
   */
  public interface SuspensionHelper {
    /**
     * 获取悬浮标签.
     *
     * @return 悬浮标签
     */
    String getTag();
  }

  /**
   * 参数构建器.
   */
  public static class Builder {
    /**
     * 标题高度.
     */
    private int titleHeight;
    /**
     * 标题左内边距.
     */
    private int paddingLeft;
    /**
     * 标题背景色.
     */
    private int colorTitleBg = INVALID_COLOR;
    /**
     * 标题颜色.
     */
    private int colorTitleFont = INVALID_COLOR;
    /**
     * RecyclerView头部数量.
     */
    private int headerViewCount;
    /**
     * 按下提示TextView.
     */
    private TextView pressedShowTextView;

    public Builder() {
    }

    /**
     * 设置标题高度.
     *
     * @param titleHeight 标题高度
     * @return {@link Builder}
     */
    public final Builder setTitleHeight(final int titleHeight) {
      this.titleHeight = titleHeight;
      return this;
    }

    /**
     * 设置左内边距.
     *
     * @param paddingLeft 左内边距
     * @return {@link Builder}
     */
    public final Builder setPaddingLeft(final int paddingLeft) {
      this.paddingLeft = paddingLeft;
      return this;
    }

    /**
     * 设置标题背景色.
     *
     * @param colorTitleBg 标题背景色
     * @return {@link Builder}
     */
    public final Builder setColorTitleBg(final int colorTitleBg) {
      this.colorTitleBg = colorTitleBg;
      return this;
    }

    /**
     * 设置标题颜色.
     *
     * @param colorTitleFont 标题颜色
     * @return {@link Builder}
     */
    public final Builder setColorTitleFont(final int colorTitleFont) {
      this.colorTitleFont = colorTitleFont;
      return this;
    }

    /**
     * 设置RecyclerView头部数.
     *
     * @param headerViewCount RecyclerView头部数
     * @return {@link Builder}
     */
    public final Builder setHeaderViewCount(final int headerViewCount) {
      this.headerViewCount = headerViewCount;
      return this;
    }

    /**
     * 设置按下提示TextView.
     *
     * @param pressedShowTextView 按下提示TextView
     * @return {@link Builder}
     */
    public final Builder setPressedShowTextView(final TextView pressedShowTextView) {
      this.pressedShowTextView = pressedShowTextView;
      return this;
    }

    /**
     * 创建头部悬停对象.
     *
     * @return {@link SuspensionDecoration}
     */
    private SuspensionDecoration create() {
      return new SuspensionDecoration(titleHeight, paddingLeft, colorTitleBg, colorTitleFont,
          headerViewCount);
    }
  }

  /**
   * 显示头部类.
   */
  private static final class SuspensionDecoration extends RecyclerView.ItemDecoration {
    /**
     * 头部数据.
     */
    private List<? extends SuspensionHelper> data;
    /**
     * 画笔.
     */
    private final Paint paint;
    /**
     * 头部区间.
     */
    private final Rect bounds;
    /**
     * 标题高度.
     */
    private final int titleHeight;
    /**
     * 标题背景.
     */
    private final int colorTitleBg;
    /**
     * 标题颜色.
     */
    private final int colorTitleFont;
    /**
     * 首部数量.
     */
    private final int headerViewCount;
    /**
     * 左内边距.
     */
    private final int paddingLeft;

    private SuspensionDecoration() {
      this(0, 0, INVALID_COLOR, INVALID_COLOR, 0);
    }

    private SuspensionDecoration(final int titleHeight, final int paddingLeft,
                                 final int colorTitleBg, final int colorTitleFont,
                                 final int headerViewCount) {
      super();
      if (titleHeight == 0) {
        this.titleHeight = getDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TITLE_HEIGHT);
      } else {
        this.titleHeight = titleHeight;
      }
      if (colorTitleBg == INVALID_COLOR) {
        this.colorTitleBg = Color.parseColor("#FFF6F6F6");
      } else {
        this.colorTitleBg = colorTitleBg;
      }
      if (colorTitleFont == INVALID_COLOR) {
        this.colorTitleFont = Color.parseColor("#FF333333");
      } else {
        this.colorTitleFont = colorTitleFont;
      }
      this.paddingLeft = paddingLeft;
      this.headerViewCount = headerViewCount;
      paint = new Paint();
      bounds = new Rect();
      int titleFontSize = getDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TITLE_TEXT_SIZE);
      paint.setTextSize(titleFontSize);
      paint.setAntiAlias(true);
    }

    /**
     * dp，sp转px
     *
     * @param unit  dp,sp单位
     * @param value 数值
     * @return px数值
     */
    private int getDimension(final int unit, final float value) {
      return (int) TypedValue.applyDimension(unit, value, Resources.getSystem()
          .getDisplayMetrics());
    }

    private void setData(final List<? extends SuspensionHelper> data) {
      this.data = data;
    }

    @Override
    public void onDraw(final Canvas c, final RecyclerView parent, final RecyclerView.State state) {
      super.onDraw(c, parent, state);
      final int left = parent.getPaddingLeft();
      final int right = parent.getWidth() - parent.getPaddingRight();
      final int childCount = parent.getChildCount();
      for (int i = 0; i < childCount; i++) {
        final View child = parent.getChildAt(i);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
            .getLayoutParams();
        int position = params.getViewLayoutPosition();
        position -= headerViewCount;
        if (data == null || data.isEmpty() || position > data.size() - 1 || position < 0) {
          continue;
        }
        if (position == 0) {
          drawTitleArea(c, new Rect(left, 0, right, 0), child, params, position);

        } else {
          if (null != data.get(position).getTag()
              && !data.get(position).getTag()
              .equals(data.get(position - 1).getTag())) {
            drawTitleArea(c, new Rect(left, 0, right, 0), child, params, position);
          }
        }
      }
    }

    /**
     * 绘制标题区域.
     *
     * @param c        画面
     * @param rect     左右区间
     * @param child    View
     * @param params   LayoutParams上下区间
     * @param position 文本数据位置
     */
    private void drawTitleArea(final Canvas c, final Rect rect, final View child,
                               final RecyclerView.LayoutParams params, final int position) {
      paint.setColor(colorTitleBg);
      c.drawRect(rect.left, child.getTop() - params.topMargin - titleHeight, rect.right,
          child.getTop() - params.topMargin, paint);
      paint.setColor(colorTitleFont);
      paint.getTextBounds(data.get(position).getTag(), 0,
          data.get(position).getTag().length(), bounds);
      c.drawText(data.get(position).getTag(), child.getPaddingLeft() + paddingLeft,
          child.getTop() - params.topMargin - (titleHeight / HALF - bounds.height() / HALF), paint);
    }

    @Override
    public void onDrawOver(final Canvas c, final RecyclerView parent, final RecyclerView.State state) {
      int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
      pos -= headerViewCount;
      if (data == null || data.isEmpty() || pos < 0 || pos > data.size() - 1) {
        return;
      }
      String tag = data.get(pos).getTag();
      View child = parent.findViewHolderForLayoutPosition(pos + headerViewCount).itemView;
      boolean flag = false;
      if ((pos + 1 < data.size()) && null != tag
          && !TextUtils.equals(tag, data.get(pos + 1).getTag())
          && (child.getHeight() + child.getTop() < titleHeight)) {
        c.save();
        flag = true;
        c.translate(0, child.getHeight() + child.getTop() - titleHeight);
      }
      paint.setColor(colorTitleBg);
      c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight()
          - parent.getPaddingRight(), parent.getPaddingTop() + titleHeight, paint);
      paint.setColor(colorTitleFont);
      if (tag != null) {
        paint.getTextBounds(tag, 0, tag.length(), bounds);
        c.drawText(tag, child.getPaddingLeft() + paddingLeft, parent.getPaddingTop() + titleHeight
            - (titleHeight / HALF - bounds.height() / HALF), paint);
      }
      if (flag) {
        c.restore();
      }
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
                               final RecyclerView.State state) {
      super.getItemOffsets(outRect, view, parent, state);
      int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
      position -= headerViewCount;
      if (data == null || data.isEmpty() || position > data.size() - 1) {
        return;
      }
      SuspensionHelper titleCategoryInterface = data.get(position);
      if (position == 0) {
        outRect.set(0, titleHeight, 0, 0);
      } else {
        if (null != titleCategoryInterface.getTag()
            && !titleCategoryInterface.getTag()
            .equals(data.get(position - 1).getTag())) {
          outRect.set(0, titleHeight, 0, 0);
        }
      }
    }
  }
}
