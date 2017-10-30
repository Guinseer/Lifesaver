package com.guinseer.lifesaver.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.guinseer.lifesaver.R;

public class LockscreenUtils {

	// Member variables
	private OverlayDialog mOverlayDialog=null;
	private OnLockStatusChangedListener mLockStatusChangedListener;
	
	// Interface to communicate with owner activity
	public interface OnLockStatusChangedListener
	{
		void onLockStatusChanged(boolean isLocked);

	}
	
	// Reset the variables
	public LockscreenUtils() {
		Log.d("info", "reset called!");
		reset();
	}

	// Display overlay dialog with a view to prevent home button click
	public void lock(Activity activity) {

		Log.d("info", "lock called!");
		if (mOverlayDialog == null) {
			Log.d("info", "mLockStatusChanged");
			mOverlayDialog = new OverlayDialog(activity);
			mOverlayDialog.show();


		}
	}
	
	// Reset variables
	public void reset() {
		if (mOverlayDialog != null) {
			mOverlayDialog.dismiss();
			mOverlayDialog = null;
		}
	}

	// Unlock the home button and give callback to unlock the screen
	public void unlock() {
		Log.d("info","unlock called!");
		if (mOverlayDialog != null) {
			Log.d("info","mOverlayDialog not null");
			mOverlayDialog.dismiss();
			mOverlayDialog = null;
			if(mLockStatusChangedListener!=null)
			{
				mLockStatusChangedListener.onLockStatusChanged(false);
			}

		}
	}

	// Create overlay dialog for lockedscreen to disable hardware buttons
	private static class OverlayDialog extends AlertDialog {

		public OverlayDialog(Activity activity) {
			super(activity, R.style.OverlayDialog);
			LayoutParams params = getWindow().getAttributes();
			params.type = LayoutParams.TYPE_TOAST;
			params.dimAmount = 0.0F;
			params.width = 0;
			params.height = 0;
			params.gravity = Gravity.BOTTOM;
			getWindow().setAttributes(params);
			getWindow().setFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_NOT_TOUCH_MODAL,
					0xffffff);

			setOwnerActivity(activity);
			setCancelable(false);
		}

		// consume touch events
		public final boolean dispatchTouchEvent(MotionEvent motionevent) {
			return true;
		}

	}
	public void setOnCustomAddListener(OnLockStatusChangedListener onaddListener){
		this.mLockStatusChangedListener = onaddListener;
	}
}
