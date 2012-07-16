package com.swifi.al;



import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

public class ListViewOverscrollBasic extends ListView {

	private static final int MAX_Y_OVERSCROLL_DISTANCE = 100;
    
    private Context mContext;
	private int mMaxYOverscrollDistance;
//	private NewestFragment mFragment = null;
//	private boolean canSendNewRequest = true;
//	private boolean newRequestReady = false;
//	private boolean moreRequestReady = false;

	public ListViewOverscrollBasic(Context context) 
	{
		super(context);
		mContext = context;
		initBounceListView();
	}
	
	public ListViewOverscrollBasic(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mContext = context;
		initBounceListView();
	}
	
	public ListViewOverscrollBasic(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		mContext = context;
		initBounceListView();
	}
	
//	public void setFragment(NewestFragment fragment) {
//		mFragment = fragment;
//	}
//	
//	public void enableNewRequests() {
//		canSendNewRequest = true;
//	}
	
	private void initBounceListView()
	{
		//get the density of the screen and do some math with it on the max overscroll distance
		//variable so that you get similar behaviors no matter what the screen size
		final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;
		mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
	}
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) 
	{ 
		//This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance; 
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);  
	}
	
//	@Override
//	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
//		//enable requests for newest
//		if(clampedY && scrollY < 0) {
//			newRequestReady = true;
//		}
//		//enable requests for loading more results
//		if(clampedY && scrollY > 0) {
//			moreRequestReady = true;
//		}
//		
//		//if we reached the overscroll maximum, list is used in a fragment, the last request is processed and the list is scrolling back to original state  
//		if(mFragment != null && canSendNewRequest && newRequestReady && Math.abs(scrollY) == 0) {
//			//forbid new requests until server has a response or the user overscrolls again
//			canSendNewRequest = false;
//			newRequestReady = false;
//			//this is pull down to refresh
//			mFragment.executeNewServerTask(String.format(Constants.API_MAIN_NEWEST, "0"));
//		}
//		//if we reached the overscroll maximum, list is used in a fragment, the last request is processed and the list is scrolling back to original state 
//		else if(mFragment != null && canSendNewRequest && moreRequestReady && Math.abs(scrollY) == 0) {
//			//forbid new requests until server has a response or the user overscrolls again
//			canSendNewRequest = false;
//			moreRequestReady = false;
//			//this is pull up to load more
//			mFragment.executeNewServerTask(null);
//		}
//		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
//	}
}
