/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.levelup.jiemimoshengren.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class IndexScroller {
	
	private float mIndexbarWidth;
	private float mIndexbarMargin;
	private float mPreviewPadding;
	private float mDensity;
	private float mScaledDensity;
	private int mState = STATE_SHOWN;
	private int mListViewWidth;
	private int mListViewHeight;
	private int mCurrentSection = -1;
	private boolean mIsIndexing = false;
	private ListView mListView = null;
	private SectionIndexer mIndexer = null;
	private String[] mSections = null;
	private RectF mIndexbarRect;
	
	private static final int STATE_HIDDEN = 0;
	private static final int STATE_SHOWN = 1;

    private static final int ALPHA_INDEXBAR = 64; //indexbar的透明度
    private static final int ALPHA_PREVIEW = 96; //预览字母的透明度

    private Paint indexbarPaint;
    private Paint indexPaint;
    private Paint previewPaint;
    private Paint previewTextPaint;
	
	public IndexScroller(Context context, ListView lv) {
		mDensity = context.getResources().getDisplayMetrics().density;
		mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		mListView = lv;
		setAdapter(mListView.getAdapter());
		
		mIndexbarWidth = 20 * mDensity;
		mIndexbarMargin = 1 * mDensity;
		mPreviewPadding = 0 * mDensity;

        setupPaint();
	}

    private void setupPaint(){
        //indexbar Paint
        indexbarPaint = new Paint();
        indexbarPaint.setColor(Color.BLACK);
        indexbarPaint.setAlpha(ALPHA_INDEXBAR);
        indexbarPaint.setAntiAlias(true);

        //index Paint
        indexPaint = new Paint();
        indexPaint.setColor(Color.WHITE);
        indexPaint.setAlpha(255);
        indexPaint.setAntiAlias(true);
        indexPaint.setTextSize(12 * mScaledDensity);

        //preview Paint
        previewPaint = new Paint();
        previewPaint.setColor(Color.BLACK);
        previewPaint.setAlpha(ALPHA_PREVIEW);
        previewPaint.setAntiAlias(true);
        previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

        //Preview Paint
        previewTextPaint = new Paint();
        previewTextPaint.setColor(Color.WHITE);
        previewTextPaint.setAntiAlias(true);
        previewTextPaint.setTextSize(50 * mScaledDensity);

    }

	public void draw(Canvas canvas) {
		if (mState == STATE_HIDDEN) {
            return;
        }
        canvas.drawRoundRect(mIndexbarRect, 5 * mDensity, 5 * mDensity, indexbarPaint);

        if (mSections != null && mSections.length > 0) {
			// Preview is shown when mCurrentSection is set
			if (mCurrentSection >= 0) {
				float previewTextWidth = previewTextPaint.measureText(mSections[mCurrentSection]);
				float previewSize = 2 * mPreviewPadding + previewTextPaint.descent() - previewTextPaint.ascent();
				RectF previewRect = new RectF((mListViewWidth - previewSize) / 2
						, (mListViewHeight - previewSize) / 2
						, (mListViewWidth - previewSize) / 2 + previewSize
						, (mListViewHeight - previewSize) / 2 + previewSize);
				
				canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint);
				canvas.drawText(mSections[mCurrentSection], previewRect.left + (previewSize - previewTextWidth) / 2 - 1
						, previewRect.top + mPreviewPadding - previewTextPaint.ascent() + 1, previewTextPaint);
			}

			float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length;
			float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint.ascent())) / 2;
			for (int i = 0; i < mSections.length; i++) {
				float paddingLeft = (mIndexbarWidth - indexPaint.measureText(mSections[i])) / 2;
				canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft
						, mIndexbarRect.top + mIndexbarMargin + sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);
			}
		}
	}
	
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// If down event occurs inside index bar region, start indexing
			if (mState == STATE_SHOWN && contains(ev.getX(), ev.getY())) {
				// It demonstrates that the motion event started from index bar
				mIsIndexing = true;
				mCurrentSection = getSectionByPoint(ev.getY());
				mListView.setSelection(mIndexer.getPositionForSection(mCurrentSection));
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsIndexing) {
				// If this event moves inside index bar
				if (contains(ev.getX(), ev.getY())) {
					mCurrentSection = getSectionByPoint(ev.getY());
					mListView.setSelection(mIndexer.getPositionForSection(mCurrentSection));
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsIndexing) {
				mIsIndexing = false;
				mCurrentSection = -1;
                if (mState == STATE_SHOWN)
                    mListView.invalidate();
                return true;
			}
            break;
		}
		return false;
	}
	
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		mListViewWidth = w;
		mListViewHeight = h;
		mIndexbarRect = new RectF(w - mIndexbarMargin - mIndexbarWidth
				, mIndexbarMargin
				, w - mIndexbarMargin
				, h - mIndexbarMargin);
	}
	
	public void show() {
		if (mState == STATE_HIDDEN) {
            mState = STATE_SHOWN;
            mListView.invalidate();
        }
	}
	
	public void hide() {
		if (mState == STATE_SHOWN) {
            mState = STATE_HIDDEN;
            mListView.invalidate();
        }
	}
	
	public void setAdapter(Adapter adapter) {
		if (adapter instanceof SectionIndexer) {
			mIndexer = (SectionIndexer) adapter;
			mSections = (String[]) mIndexer.getSections();
		}
	}
	
	private boolean contains(float x, float y) {
		// Determine if the point is in index bar region, which includes the right margin of the bar
		return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top + mIndexbarRect.height());
	}
	
	private int getSectionByPoint(float y) {
		if (mSections == null || mSections.length == 0)
			return 0;
		if (y < mIndexbarRect.top + mIndexbarMargin)
			return 0;
		if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
			return mSections.length - 1;
		return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length));
	}

}
