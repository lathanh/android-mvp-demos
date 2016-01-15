package org.lathanh.demo.android.mvp.adapting_demo;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import org.lathanh.demo.android.mvp.R;

/**
 * This class simply contains (as inner classes) the various models that will
 * be used in the various implementations of this demo.
 *
 * Normally they would each be their own top-level classes, but putting them
 * all in one place should make them easier to see how they relate to each
 * other.
 */
public class AdaptingDemo_Models {

  /**
   * The model of the data as it comes from the data source.
   * In other words, this is a Java object that closely matches the data
   * source's model, and simply holds that data.
   */
  public static class DataModel {
    private final String name;
    private final int delayMs;

    public DataModel(int position, int delayMs) {
      this.name = Integer.toString(position);
      this.delayMs = delayMs;
    }

    public String getName() {
      return name;
    }

    public int getDelayMs() {
      return delayMs;
    }
  }

  /**
   * The data as it should be displayed.
   * While it often has a strong resemblance to a data model, it may also often:
   * <ul>
   *   <li>transform/format the data for display,</li>
   *   <li>contain data from multiple data models, and</li>
   *   <li>contain data/state not found in and data model.</li>
   * </ul>
   *
   * The {@code @Bindable} annotations are used by the DataBinding demos and
   * have no effect on the other demos.
   */
  public static class ViewModel extends BaseObservable {
    @Bindable
    private String string;
    @Bindable
    private String delay;

    public ViewModel(String string, String delay) {
      this.string = string;
      this.delay = delay;
    }

    public String getString() {
      return string;
    }

    public String getDelay() {
      return delay;
    }
  }

  /**
   * The LoadingViewModel is a ViewModel that allows the view to know when to
   * show a loading indicator until the actual ViewModel is ready.
   *
   * The {@link BaseObservable} class that this extends and the {@code Bindable}
   * annotations are used by the DataBinding demos and have no effect on the
   * other demos.
   */
  public static class LoadingViewModel
      extends BaseObservable {

    protected final DataModel dataModel;
    protected ViewModel viewModel;

    public LoadingViewModel(DataModel dataModel) {
      this.dataModel = dataModel;
    }

    /**
     * @return {@code null} if the ViewModel hasn't been created (from the
     *         {@link #getDataModel() DataModel} yet.
     */
    @Bindable
    @Nullable
    public ViewModel getViewModel() {
      return viewModel;
    }

    public void setViewModel(ViewModel viewModel) {
      this.viewModel = viewModel;
    }

    public DataModel getDataModel() {
      return dataModel;
    }
  }

  /** A ViewHolder for the adapting_demo_list_item layout. */
  public static class ViewHolder extends RecyclerView.ViewHolder {

    public final TextView label;
    public final TextView delay;
    public final TextView bind;

    public ViewHolder(View itemView) {
      super(itemView);
      this.label = (TextView) itemView.findViewById(R.id.label);
      this.delay = (TextView) itemView.findViewById(R.id.delay);
      this.bind = (TextView) itemView.findViewById(R.id.bind);
    }
  }
}
