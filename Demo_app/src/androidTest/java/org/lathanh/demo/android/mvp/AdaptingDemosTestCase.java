package org.lathanh.demo.android.mvp;

import android.os.Trace;
import androidx.test.uiautomator.*;
import androidx.recyclerview.widget.RecyclerView;
import android.test.InstrumentationTestCase;
import android.util.Log;

/**
 * Simple tests to ensure each of the adapting demo fragments can be navigated
 * to and scrolled.
 *
 * @author Robert LaThanh 2015-10-13
 */
public class AdaptingDemosTestCase extends InstrumentationTestCase {

  public static final String LOG_TAG = "AdaptingDemosTestCase";


  private UiDevice device;
  private InstrumentationUiDevice instrumentedDevice;


  //== 'TestCase' methods =====================================================

  public void setUp() {
    //-- Initialize UiDevice instance
    device = UiDevice.getInstance(getInstrumentation());
    instrumentedDevice = new InstrumentationUiDevice(device, LOG_TAG);

    //-- Get into app if not already
    UiObject appMoreOptions =
        device.findObject(
            new UiSelector()
                .descriptionContains("More options")
                .className("android.widget.ImageView")
                .packageName("org.lathanh.demo.android.mvp"));
    if (!appMoreOptions.exists()) {
      // Start from the home screen
      device.pressHome();

      // Assume app is on home screen and click on it
      BySelector byAppText = By.text("LaThanh.org Android MVP Demos");
      instrumentedDevice.wait(Until.hasObject(byAppText),
                              3000, 5000, "click Home");
      UiObject2 object = device.findObject(byAppText);
      object.click();
      instrumentedDevice.waitForIdle(3000, "clicked on app");
    } else {
      // already in app. go to app's "home"
      chooseMenuItem("Clear");
    }
  }

  public void testAdaptingDemo_StandardFragment() {
    doDemoFragmentTest("Adapting — Standard");
  }

  public void testAdaptingDemo_Improvement1Fragment() {
    doDemoFragmentTest("Adapting — Improvement 1");
  }

  public void testAdaptingDemo_Improvement2Fragment() {
    doDemoFragmentTest("Adapting — Improvement 2");
  }

  public void testDataBindingDemo_StandardFragment() {
    doDemoFragmentTest("Data Binding — Standard");
  }

  public void testDataBindingDemo_Improvement1Fragment() {
    doDemoFragmentTest("Data Binding — Improvement 1");
  }


  //== Private methods ========================================================

  private void doDemoFragmentTest(String menuItemString) {
    Log.v(LOG_TAG, menuItemString + ": start");
    chooseMenuItem(menuItemString);
    instrumentedDevice.wait(
        Until.hasObject(By.clazz(RecyclerView.class)),
        10000, 10000, "fragment start");

    Trace.beginSection(menuItemString + " swipe");
    device.swipe(540, 1600, 540, 400, 2);
    instrumentedDevice.waitForIdle(10000, "fragment swipe");
    Trace.endSection();

    Log.v(LOG_TAG, menuItemString + ": end");
  }

  private void chooseMenuItem(String text) {
    //-- Open menu
    UiObject menuButton =
        device.findObject(
            new UiSelector()
                .descriptionContains("More options")
                .className("android.widget.ImageView"));

    try {
      menuButton.click();
    } catch (UiObjectNotFoundException e) {
      // really?!
      assertTrue("menuButton not found", false);
    }

    //-- Click on the menu item
    UiObject menuItem =
        device.findObject(
            new UiSelector()
                .textContains(text));
    try {
      menuItem.click();
    } catch (UiObjectNotFoundException e) {
      assertTrue("menuItem not found: " + text, false);
    }
  }
}