package org.lathanh.demo.android.mvp;

import androidx.test.uiautomator.SearchCondition;
import androidx.test.uiautomator.UiDevice;
import android.util.Log;

/**
 * Wraps some of UiDevice's calls with debug logging.
 *
 * @author Robert LaThanh 2015-10-16
 */
public class InstrumentationUiDevice {

  private final UiDevice device;

  private final String logTag;


  //== Constructor ============================================================

  public InstrumentationUiDevice(UiDevice device, String logTag) {
    this.device = device;
    this.logTag = logTag;
  }


  //== Instance methods =======================================================

  /** {@link UiDevice#wait(long, int)}, and log how long the call took. */
  public <T> T wait(SearchCondition<T> condition, long timeoutMillis,
                    String reason) {
    return wait(condition, timeoutMillis, 0, reason);
  }

  /**
   * {@link #waitForIdle(long, String)} and {@link UiDevice#wait(long, int)},
   * and log how long each call took.
   */
  public <T> T wait(SearchCondition<T> condition, long timeoutMs,
                    long firstWaitForIdleMs, String reason) {
    if (firstWaitForIdleMs > 0) {
      waitForIdle(firstWaitForIdleMs, "waitForIdle before: " + reason);
    }

    long startTime = System.currentTimeMillis();
    T result = device.wait(condition, timeoutMs);
    long elapsed = System.currentTimeMillis() - startTime;
    Log.v(logTag, "wait: " + elapsed + " (" + reason + ")");
    return result;
  }

  /**
   * {@link UiDevice#waitForIdle(long)}, and log how long the call took.
   */
  public void waitForIdle(long millis, String reason) {
    long startTime = System.currentTimeMillis();
    device.waitForIdle(millis);
    long elapsed = System.currentTimeMillis() - startTime;
    Log.v(logTag, "waitForIdle: " + elapsed + " (" + reason + ")");
  }

}
