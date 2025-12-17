package customview;


import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class FLMFlowLayoutManager extends RecyclerView.LayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    public final static int VERTICAL = OrientationHelper.VERTICAL, HORIZONTAL = OrientationHelper.HORIZONTAL;
    public final static int DEFAULT_COUNT_ITEM_IN_LINE = -1;

    private final static String TAG_FIRST_ITEM_ADAPTER_INDEX = "TAG_FIRST_ITEM_ADAPTER_INDEX";
    private final static String TAG_FIRST_LINE_START_POSITION = "TAG_FIRST_LINE_START_POSITION";

    private final static String ERROR_UNKNOWN_ORIENTATION = "Unknown orientation!";
    private final static String ERROR_BAD_ARGUMENT = "Inappropriate field value!";

    private int mGravity;
    private int mOrientation;

    private int mMaxItemsInLine;

    private int mSpacingBetweenItems;
    private int mSpacingBetweenLines;

    private FLMLayoutManagerHelper mLayoutManagerHelper;

    private final ArrayList<Line> mCurrentLines;

    private int mFirstItemAdapterIndex;
    private int mFirstLineStartPosition;

    public FLMFlowLayoutManager(int orientation) {
        this(orientation, Gravity.START, DEFAULT_COUNT_ITEM_IN_LINE, 0, 0);
    }

    public FLMFlowLayoutManager(int orientation, int gravity) {
        this(orientation, gravity, DEFAULT_COUNT_ITEM_IN_LINE, 0, 0);
    }

    public FLMFlowLayoutManager(int orientation, int gravity, int spacingBetweenItems, int spacingBetweenLines) {
        this(orientation, gravity, DEFAULT_COUNT_ITEM_IN_LINE, spacingBetweenItems, spacingBetweenLines);
    }

    public FLMFlowLayoutManager(int orientation, int gravity, int maxItemsInLine, int spacingBetweenItems, int spacingBetweenLines) {
        mCurrentLines = new ArrayList<>();

        mGravity = gravity;

        mFirstItemAdapterIndex = 0;
        mFirstLineStartPosition = -1;

        if (maxItemsInLine == 0 || maxItemsInLine < -1) {
            throw new IllegalArgumentException(ERROR_BAD_ARGUMENT);
        }

        mMaxItemsInLine = maxItemsInLine;

        if (mSpacingBetweenItems < 0) {
            throw new IllegalArgumentException(ERROR_BAD_ARGUMENT);
        }

        mSpacingBetweenItems = spacingBetweenItems;

        if (mSpacingBetweenLines < 0) {
            throw new IllegalArgumentException(ERROR_BAD_ARGUMENT);
        }

        mSpacingBetweenLines = spacingBetweenLines;

        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(ERROR_UNKNOWN_ORIENTATION);
        }
        mOrientation = orientation;

        mLayoutManagerHelper = FLMLayoutManagerHelper.createLayoutManagerHelper(this, orientation, mGravity);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Line currentLine = null;

        if (mFirstLineStartPosition == -1) {
            mFirstLineStartPosition = mLayoutManagerHelper.getStartAfterPadding();
        }

        int topOrLeft = mFirstLineStartPosition;

        detachAndScrapAttachedViews(recycler);
        mCurrentLines.clear();

        for (int i = mFirstItemAdapterIndex; i < getItemCount(); i += currentLine.mItemsCount) {
            currentLine = addLineToEnd(i, topOrLeft, recycler);

            mCurrentLines.add(currentLine);

            topOrLeft = mSpacingBetweenLines + currentLine.mEndValueOfTheHighestItem;

            if (currentLine.mEndValueOfTheHighestItem > mLayoutManagerHelper.getEndAfterPadding()) {
                break;
            }
        }

        if (mFirstItemAdapterIndex > 0 && currentLine != null) {
            int availableOffset = currentLine.mEndValueOfTheHighestItem - mLayoutManagerHelper.getEnd() + mLayoutManagerHelper.getEndPadding();

            if (availableOffset < 0) {
                if (mOrientation == VERTICAL) {
                    scrollVerticallyBy(availableOffset, recycler, state);
                } else {
                    scrollHorizontallyBy(availableOffset, recycler, state);
                }
            }
        }

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle data = (Bundle) state;

        mFirstItemAdapterIndex = data.getInt(TAG_FIRST_ITEM_ADAPTER_INDEX);
        mFirstLineStartPosition = data.getInt(TAG_FIRST_LINE_START_POSITION);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle data = new Bundle();

        data.putInt(TAG_FIRST_ITEM_ADAPTER_INDEX, mFirstItemAdapterIndex);
        data.putInt(TAG_FIRST_LINE_START_POSITION, mFirstLineStartPosition);

        return data;
    }

    /**
     * Change orientation of the layout manager
     *
     * @param orientation New orientation.
     */

    public void setOrientation(int orientation) {

        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(ERROR_UNKNOWN_ORIENTATION);
        }

        if (orientation != mOrientation) {
            assertNotInLayoutOrScroll(null);

            mOrientation = orientation;
            mLayoutManagerHelper = FLMLayoutManagerHelper.createLayoutManagerHelper(this, orientation, mGravity);

            requestLayout();
        }
    }

    public void setMaxItemsInLine(int maxItemsInLine) {

        if (maxItemsInLine <= 0) {
            throw new IllegalArgumentException(ERROR_BAD_ARGUMENT);
        }

        assertNotInLayoutOrScroll(null);

        mMaxItemsInLine = maxItemsInLine;

        requestLayout();
    }

    public void setSpacingBetweenItems(int spacingBetweenItems) {

        if (spacingBetweenItems < 0) {
            throw new IllegalArgumentException(ERROR_BAD_ARGUMENT);
        }

        assertNotInLayoutOrScroll(null);

        mSpacingBetweenItems = spacingBetweenItems;

        requestLayout();
    }

    public void setSpacingBetweenLines(int spacingBetweenLines) {

        if (spacingBetweenLines < 0) {
            throw new IllegalArgumentException(ERROR_BAD_ARGUMENT);
        }

        assertNotInLayoutOrScroll(null);

        mSpacingBetweenLines = spacingBetweenLines;

        requestLayout();
    }

    /**
     * Return current orientation of the layout manager.
     */
    public int getOrientation() {
        return mOrientation;
    }

    /**
     * Change gravity of the layout manager.
     *
     * @param gravity New gravity.
     */
    public void setGravity(int gravity) {
        assertNotInLayoutOrScroll(null);

        if (gravity != mGravity) {
            mGravity = gravity;
            mLayoutManagerHelper.setGravity(gravity);

            requestLayout();
        }
    }

    /**
     * Return current gravity of the layout manager.
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * Add one line to the end of recyclerView.
     *
     * @param startAdapterIndex Adapter index of first item of new line.
     * @param start             Start position(Top - if orientation is VERTICAL or Left - if orientation is HORIZONTAL) of the new line.
     * @return New line.
     */
    @NonNull
    private Line addLineToEnd(int startAdapterIndex, int start, RecyclerView.Recycler recycler) {
        boolean isEndOfLine = false;

        int currentAdapterIndex = startAdapterIndex;
        int currentItemsSize = 0;
        int currentMaxValue = 0;

        Line line = new Line();
        line.mStartValueOfTheHighestItem = start;

        while (!isEndOfLine && currentAdapterIndex < getItemCount()) {
            final View view = recycler.getViewForPosition(currentAdapterIndex);

            addView(view);

            measureChildWithMargins(view, 0, 0);

            final int widthOrHeight = mLayoutManagerHelper.getDecoratedMeasurementInOther(view);
            final int heightOrWidth = mLayoutManagerHelper.getDecoratedMeasurement(view);

            if (line.mItemsCount == mMaxItemsInLine || (currentItemsSize + widthOrHeight) >= mLayoutManagerHelper.getLineSize()) {
                isEndOfLine = true;

                if (currentItemsSize == 0) {
                    currentMaxValue = heightOrWidth;

                    line.mEndValueOfTheHighestItem = line.mStartValueOfTheHighestItem + currentMaxValue;
                    line.mItemsCount++;
                } else {
                    detachAndScrapView(view, recycler);
                    continue;
                }
            } else {
                if (heightOrWidth > currentMaxValue) {
                    currentMaxValue = heightOrWidth;
                    line.mEndValueOfTheHighestItem = line.mStartValueOfTheHighestItem + currentMaxValue;
                }
                line.mItemsCount++;
            }

            currentItemsSize += widthOrHeight + mSpacingBetweenItems;

            currentAdapterIndex++;
        }

        layoutItemsToEnd(currentItemsSize - mSpacingBetweenItems, currentMaxValue, line.mItemsCount, line.mStartValueOfTheHighestItem);

        return line;
    }

    /**
     * Arrange of views from start to end.
     *
     * @param itemsSize                  Size(width - if orientation is VERTICAL or height - if orientation is HORIZONTAL) of all items(include spacing) in line.
     * @param maxItemHeightOrWidth       Max item height(if VERTICAL) or width(if HORIZONTAL) in line.
     * @param itemsInLine                Item count in line.
     * @param startValueOfTheHighestItem Start position(Top - if orientation is VERTICAL or Left - if orientation is HORIZONTAL) of the line.
     */
    private void layoutItemsToEnd(int itemsSize, int maxItemHeightOrWidth, int itemsInLine, int startValueOfTheHighestItem) {
        int currentStart = mLayoutManagerHelper.getStartPositionOfFirstItem(itemsSize);

        int i = itemsInLine;
        int childCount = getChildCount();
        int currentStartValue;

        while (i > 0) {
            final View view = getChildAt(childCount - i);

            final int widthOrHeight = mLayoutManagerHelper.getDecoratedMeasurementInOther(view);
            final int heightOrWidth = mLayoutManagerHelper.getDecoratedMeasurement(view);

            currentStartValue = startValueOfTheHighestItem + mLayoutManagerHelper.getPositionOfCurrentItem(maxItemHeightOrWidth, heightOrWidth);

            if (mOrientation == VERTICAL) {
                layoutDecoratedWithMargins(view, currentStart, currentStartValue,
                        currentStart + widthOrHeight, currentStartValue + heightOrWidth);
            } else {
                layoutDecoratedWithMargins(view, currentStartValue, currentStart,
                        currentStartValue + heightOrWidth, currentStart + widthOrHeight);
            }

            currentStart += widthOrHeight + mSpacingBetweenItems;

            i--;
        }
    }

    /**
     * Add one line to the start of recyclerView.
     *
     * @param startAdapterIndex Adapter index of first item of new line.
     * @param end               End position(Bottom - if orientation is VERTICAL or Right - if orientation is HORIZONTAL) of the new line.
     * @return New line.
     */
    @NonNull
    private Line addLineToStart(int startAdapterIndex, int end, RecyclerView.Recycler recycler) {
        boolean isEndOfLine = false;

        int currentAdapterIndex = startAdapterIndex;
        int currentItemsSize = 0;
        int currentMaxValue = 0;

        Line line = new Line();
        line.mEndValueOfTheHighestItem = end;

        while (!isEndOfLine && currentAdapterIndex >= 0) {
            final View view = recycler.getViewForPosition(currentAdapterIndex);

            addView(view, 0);

            measureChildWithMargins(view, 0, 0);

            final int widthOrHeight = mLayoutManagerHelper.getDecoratedMeasurementInOther(view);
            final int heightOrWidth = mLayoutManagerHelper.getDecoratedMeasurement(view);

            if (line.mItemsCount == mMaxItemsInLine || (currentItemsSize + widthOrHeight) >= mLayoutManagerHelper.getLineSize()) {
                isEndOfLine = true;

                if (currentItemsSize == 0) {
                    currentMaxValue = heightOrWidth;

                    line.mStartValueOfTheHighestItem = line.mEndValueOfTheHighestItem - currentMaxValue;
                    line.mItemsCount++;
                } else {
                    detachAndScrapView(view, recycler);
                    continue;
                }
            } else {
                if (heightOrWidth > currentMaxValue) {
                    currentMaxValue = heightOrWidth;
                    line.mStartValueOfTheHighestItem = line.mEndValueOfTheHighestItem - currentMaxValue;
                }
                line.mItemsCount++;
            }

            currentItemsSize += widthOrHeight + mSpacingBetweenItems;

            currentAdapterIndex--;
        }

        layoutItemsToStart(currentItemsSize - mSpacingBetweenItems, currentMaxValue, line.mItemsCount, line.mStartValueOfTheHighestItem);

        return line;
    }

    /**
     * Arrange of views from end to start.
     *
     * @param itemsSize                  Size(width - if orientation is VERTICAL or height - if orientation is HORIZONTAL) of all items(include spacing) in line.
     * @param maxItemHeightOrWidth       Max item height(if VERTICAL) or width(if HORIZONTAL) in line.
     * @param itemsInLine                Item count in line.
     * @param startValueOfTheHighestItem Start position(Top - if orientation is VERTICAL or Left - if orientation is HORIZONTAL) of the line.
     */
    private void layoutItemsToStart(int itemsSize, int maxItemHeightOrWidth, int itemsInLine, int startValueOfTheHighestItem) {
        int currentStart = mLayoutManagerHelper.getStartPositionOfFirstItem(itemsSize);

        int i = 0;
        int currentStartValue;

        while (i < itemsInLine) {
            final View view = getChildAt(i);

            final int widthOrHeight = mLayoutManagerHelper.getDecoratedMeasurementInOther(view);
            final int heightOrWidth = mLayoutManagerHelper.getDecoratedMeasurement(view);

            currentStartValue = startValueOfTheHighestItem + mLayoutManagerHelper.getPositionOfCurrentItem(maxItemHeightOrWidth, heightOrWidth);

            if (mOrientation == VERTICAL) {
                assert view != null;
                layoutDecoratedWithMargins(view, currentStart, currentStartValue,
                        currentStart + widthOrHeight, currentStartValue + heightOrWidth);

            } else {
                assert view != null;
                layoutDecoratedWithMargins(view, currentStartValue, currentStart,
                        currentStartValue + heightOrWidth, currentStart + widthOrHeight);
            }

            currentStart += widthOrHeight + mSpacingBetweenItems;

            i++;
        }
    }

    /**
     * Adds to start (and delete from end) of the recyclerView the required number of lines depending on the offset.
     *
     * @param offset   Original offset.
     * @return Real offset.
     */
    private int addLinesToStartAndDeleteFromEnd(int offset, RecyclerView.Recycler recycler) {
        Line line = mCurrentLines.get(0);

        final int availableOffset = line.mStartValueOfTheHighestItem - mLayoutManagerHelper.getStartAfterPadding();

        int currentOffset = Math.max(availableOffset, offset);
        int adapterViewIndex = getPosition(Objects.requireNonNull(getChildAt(0))) - 1;

        int startValueOfNewLine = line.mStartValueOfTheHighestItem - mSpacingBetweenLines;

        while (adapterViewIndex >= 0) {

            if (currentOffset <= offset) {
                deleteLinesFromEnd(offset, recycler);
                break;
            } else {
                deleteLinesFromEnd(currentOffset, recycler);
            }

            line = addLineToStart(adapterViewIndex, startValueOfNewLine, recycler);
            mCurrentLines.add(0, line);

            startValueOfNewLine = line.mStartValueOfTheHighestItem - mSpacingBetweenLines;

            currentOffset = line.mStartValueOfTheHighestItem;
            adapterViewIndex -= line.mItemsCount;
        }

        return Math.max(currentOffset, offset);
    }

    /**
     * Removes lines from the end. The number of deleted lines depends on the offset.
     *
     * @param offset   Current offset.
     */
    private void deleteLinesFromEnd(int offset, RecyclerView.Recycler recycler) {
        Line lineToDel = mCurrentLines.get(mCurrentLines.size() - 1);

        while (lineToDel != null) {
            if (lineToDel.mStartValueOfTheHighestItem - offset >
                    mLayoutManagerHelper.getEndAfterPadding()) {
                for (int i = 0; i < lineToDel.mItemsCount; i++) {
                    removeAndRecycleView(Objects.requireNonNull(getChildAt(getChildCount() - 1)), recycler);
                }
                mCurrentLines.remove(lineToDel);
                lineToDel = mCurrentLines.get(mCurrentLines.size() - 1);
            } else {
                lineToDel = null;
            }
        }
    }

    /**
     * Adds to end (and delete from start) of the recyclerView the required number of lines depending on the offset.
     *
     * @param offset   Original offset.
     * @return Real offset.
     */
    private int addLinesToEndAndDeleteFromStart(int offset, RecyclerView.Recycler recycler) {
        Line line = mCurrentLines.get(mCurrentLines.size() - 1);

        int availableOffset = line.mEndValueOfTheHighestItem - mLayoutManagerHelper.getEnd() + mLayoutManagerHelper.getEndPadding();

        int currentOffset = Math.min(availableOffset, offset);
        int adapterViewIndex = getPosition(Objects.requireNonNull(getChildAt(getChildCount() - 1))) + 1;

        int startValueOfNewLine = line.mEndValueOfTheHighestItem + mSpacingBetweenLines;

        while (adapterViewIndex < getItemCount()) {

            if (currentOffset >= offset) {
                deleteLinesFromStart(offset, recycler);
                break;
            } else {
                deleteLinesFromStart(currentOffset, recycler);
            }

            line = addLineToEnd(adapterViewIndex, startValueOfNewLine, recycler);
            mCurrentLines.add(line);

            startValueOfNewLine = line.mEndValueOfTheHighestItem + mSpacingBetweenLines;

            currentOffset = line.mEndValueOfTheHighestItem - mLayoutManagerHelper.getEnd();
            adapterViewIndex += line.mItemsCount;
        }


        return Math.min(currentOffset, offset);
    }

    /**
     * Removes lines from the start. The number of deleted lines depends on the offset.
     *
     * @param offset   Current offset.
     */
    private void deleteLinesFromStart(int offset, RecyclerView.Recycler recycler) {
        Line lineToDel = mCurrentLines.get(0);

        while (lineToDel != null) {
            if (lineToDel.mEndValueOfTheHighestItem - offset <
                    mLayoutManagerHelper.getStartAfterPadding()) {
                for (int i = 0; i < lineToDel.mItemsCount; i++) {
                    removeAndRecycleView(Objects.requireNonNull(getChildAt(0)), recycler);
                }
                mCurrentLines.remove(lineToDel);
                // mItemsInLines.add(lineToDel.mItemsCount);

                lineToDel = mCurrentLines.get(0);
            } else {
                lineToDel = null;
            }
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int offset = 0;

        if (getChildCount() > 0 && dy != 0) {

            if (dy > 0) {
                offset = addLinesToEndAndDeleteFromStart(dy, recycler);
            } else {
                offset = addLinesToStartAndDeleteFromEnd(dy, recycler);
            }

            if (offset != 0) {
                for (int i = 0; i < mCurrentLines.size(); i++) {
                    mCurrentLines.get(i).offset(-offset);
                }
                offsetChildrenVertical(-offset);
            }

            final View firstView = getChildAt(0);

            mFirstLineStartPosition = mLayoutManagerHelper.getDecoratedStart(firstView);
            assert firstView != null;
            mFirstItemAdapterIndex = getPosition(firstView);
        }

        return offset;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int offset = 0;

        if (getChildCount() > 0 && dx != 0) {

            if (dx > 0) {
                offset = addLinesToEndAndDeleteFromStart(dx, recycler);
            } else {
                offset = addLinesToStartAndDeleteFromEnd(dx, recycler);
            }

            if (offset != 0) {
                for (int i = 0; i < mCurrentLines.size(); i++) {
                    mCurrentLines.get(i).offset(-offset);
                }
                offsetChildrenHorizontal(-offset);
            }

            final View firstView = getChildAt(0);

            mFirstLineStartPosition = mLayoutManagerHelper.getDecoratedStart(firstView);
            assert firstView != null;
            mFirstItemAdapterIndex = getPosition(firstView);
        }

        return offset;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == VERTICAL;
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == HORIZONTAL;
    }

    @Override
    public void scrollToPosition(int position) {
        if (position >= 0 && position <= getItemCount() - 1) {
            mFirstItemAdapterIndex = position;
            mFirstLineStartPosition = -1;
            requestLayout();
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(position);

        startSmoothScroll(linearSmoothScroller);
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }

        final int firstChildPos = getPosition(Objects.requireNonNull(getChildAt(0)));
        final int direction = targetPosition < firstChildPos ? -1 : 1;

        if (mOrientation == HORIZONTAL) {
            return new PointF(direction, 0);
        } else {
            return new PointF(0, direction);
        }
    }

    /**
     * Representation of line in RecyclerView.
     */
    private final static class Line {

        int mStartValueOfTheHighestItem;
        int mEndValueOfTheHighestItem;

        int mItemsCount;

        void offset(int offset) {
            mStartValueOfTheHighestItem += offset;
            mEndValueOfTheHighestItem += offset;
        }
    }

    /**
     * Orientation and gravity helper.
     */
    private static abstract class FLMLayoutManagerHelper {

        RecyclerView.LayoutManager mLayoutManager;
        int mGravity;

        private FLMLayoutManagerHelper(RecyclerView.LayoutManager layoutManager, int gravity) {
            mLayoutManager = layoutManager;
            mGravity = gravity;
        }

        public void setGravity(int gravity) {
            mGravity = gravity;
        }

        public abstract int getEnd();

        public abstract int getEndPadding();

        public abstract int getLineSize();

        public abstract int getEndAfterPadding();

        public abstract int getStartAfterPadding();

        public abstract int getDecoratedStart(View view);

        public abstract int getDecoratedMeasurement(View view);

        public abstract int getDecoratedMeasurementInOther(View view);

        public abstract int getStartPositionOfFirstItem(int itemsSize);

        public abstract int getPositionOfCurrentItem(int itemMaxSize, int itemSize);

        public static FLMLayoutManagerHelper createLayoutManagerHelper(RecyclerView.LayoutManager layoutManager, int orientation, int gravity) {
            return switch (orientation) {
                case VERTICAL -> createVerticalLayoutManagerHelper(layoutManager, gravity);
                case HORIZONTAL -> createHorizontalLayoutManagerHelper(layoutManager, gravity);
                default -> throw new IllegalArgumentException(ERROR_UNKNOWN_ORIENTATION);
            };
        }

        private static FLMLayoutManagerHelper createVerticalLayoutManagerHelper(final RecyclerView.LayoutManager layoutManager, int gravity) {
            return new FLMLayoutManagerHelper(layoutManager, gravity) {
                @Override
                public int getEnd() {
                    return mLayoutManager.getHeight();
                }

                @Override
                public int getEndPadding() {
                    return mLayoutManager.getPaddingBottom();
                }

                @Override
                public int getLineSize() {
                    return mLayoutManager.getWidth() - mLayoutManager.getPaddingLeft() - mLayoutManager.getPaddingRight();
                }

                @Override
                public int getEndAfterPadding() {
                    return mLayoutManager.getHeight() - mLayoutManager.getPaddingBottom();
                }

                @Override
                public int getStartAfterPadding() {
                    return mLayoutManager.getPaddingTop();
                }

                @Override
                public int getDecoratedStart(View view) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

                    return this.mLayoutManager.getDecoratedTop(view) - params.topMargin;
                }

                @Override
                public int getDecoratedMeasurement(View view) {
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

                    return mLayoutManager.getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
                }

                @Override
                public int getDecoratedMeasurementInOther(View view) {
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

                    return mLayoutManager.getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
                }

                @Override
                public int getStartPositionOfFirstItem(int itemsSize) {
                    int horizontalGravity = GravityCompat.getAbsoluteGravity(mGravity, mLayoutManager.getLayoutDirection());

                    return switch (horizontalGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                        case Gravity.RIGHT ->
                                mLayoutManager.getWidth() - mLayoutManager.getPaddingRight() - itemsSize;
                        case Gravity.CENTER_HORIZONTAL ->
                                (mLayoutManager.getWidth() - mLayoutManager.getPaddingLeft() - mLayoutManager.getPaddingRight()) / 2
                                        - itemsSize / 2;
                        default -> mLayoutManager.getPaddingLeft();
                    };
                }

                @Override
                public int getPositionOfCurrentItem(int itemMaxSize, int itemSize) {
                    int verticalGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;

                    return switch (verticalGravity) {
                        case Gravity.CENTER_VERTICAL -> itemMaxSize / 2 - itemSize / 2;
                        case Gravity.BOTTOM -> itemMaxSize - itemSize;
                        default -> 0;
                    };
                }

            };
        }

        private static FLMLayoutManagerHelper createHorizontalLayoutManagerHelper(RecyclerView.LayoutManager layoutManager, int gravity) {
            return new FLMLayoutManagerHelper(layoutManager, gravity) {

                @Override
                public int getEnd() {
                    return mLayoutManager.getWidth();
                }

                @Override
                public int getEndPadding() {
                    return mLayoutManager.getPaddingRight();
                }

                @Override
                public int getLineSize() {
                    return mLayoutManager.getHeight() - mLayoutManager.getPaddingTop() - mLayoutManager.getPaddingBottom();
                }

                @Override
                public int getEndAfterPadding() {
                    return mLayoutManager.getWidth() - mLayoutManager.getPaddingRight();
                }

                @Override
                public int getStartAfterPadding() {
                    return mLayoutManager.getPaddingLeft();
                }

                @Override
                public int getDecoratedStart(View view) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

                    return this.mLayoutManager.getDecoratedLeft(view) - params.leftMargin;
                }

                @Override
                public int getDecoratedMeasurement(View view) {
                    return mLayoutManager.getDecoratedMeasuredWidth(view);
                }

                @Override
                public int getDecoratedMeasurementInOther(View view) {
                    return mLayoutManager.getDecoratedMeasuredHeight(view);
                }

                @Override
                public int getStartPositionOfFirstItem(int itemsSize) {
                    int verticalGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;
                    int startPosition;
                    if (verticalGravity == Gravity.CENTER_VERTICAL) {
                        startPosition = (mLayoutManager.getHeight() - mLayoutManager.getPaddingTop() - mLayoutManager.getPaddingBottom()) / 2
                                - itemsSize / 2;
                    } else if (verticalGravity == Gravity.BOTTOM) {
                        startPosition = mLayoutManager.getHeight() - mLayoutManager.getPaddingBottom() - itemsSize;
                    } else {
                        startPosition = mLayoutManager.getPaddingTop();
                    }

                    return startPosition;
                }

                @Override
                public int getPositionOfCurrentItem(int itemMaxSize, int itemSize) {
                    int horizontalGravity = GravityCompat.getAbsoluteGravity(mGravity, mLayoutManager.getLayoutDirection());
                    int currentPosition;

                    if ((horizontalGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
                        currentPosition = itemMaxSize / 2 - itemSize / 2;
                    } else if ((horizontalGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT) {
                        currentPosition = itemMaxSize - itemSize;
                    } else {
                        currentPosition = 0;
                    }
                    return currentPosition;
                }
            };
        }
    }
}