package org.lathanh.demo.android.mvp.adapting_demo.framework.adaptable;

import android.databinding.Bindable;
import android.support.annotation.NonNull;
import org.lathanh.android.mvp.adapter.adaptable.AbstractAdaptableViewModel;
import org.lathanh.android.mvp.adapter.adaptable.AdaptableViewModel;
import org.lathanh.demo.android.mvp.BR;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models;

/**
 * An {@link AdaptableViewModel} that has
 * a {@link AdaptingDemo_Models.DataModel} that can be adapted into a
 * {@link AdaptingDemo_Models.ViewModel}.
 *
 * @author Robert LaThanh 2016-01-27
 */
public class AdapterFwkDemo_AdaptableViewModel
    extends AbstractAdaptableViewModel<AdaptingDemo_Models.ViewModel> {

  //-- Operating fields -------------------------------------------------------

  private long onBindTimeNanos;
  private @NonNull AdaptingDemo_Models.DataModel dataModel;


  //== Constructors ===========================================================

  public AdapterFwkDemo_AdaptableViewModel(
      @NonNull AdaptingDemo_Models.DataModel dataModel) {
    this.dataModel = dataModel;
  }

  //== Instance methods =======================================================

  @Override
  public void setViewModel(@NonNull AdaptingDemo_Models.ViewModel viewModel) {
    super.setViewModel(viewModel);
    notifyPropertyChanged(BR.viewModel);
    notifyPropertyChanged(BR.string);
  }


  //== 'AdapterFwkDemo_AdaptableViewModel' methods ============================

  public AdaptingDemo_Models.DataModel getDataModel() {
    return dataModel;
  }

  public void setOnBindTimeNanos(long onBindTimeNanos) {
    this.onBindTimeNanos = onBindTimeNanos;
  }

  @NonNull
  @Bindable
  public String getBindTime() {
    long elapsed = System.nanoTime() - onBindTimeNanos;
    return String.format("%.2f ms", elapsed / 1000000f);
  }

}
