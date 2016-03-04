package org.lathanh.demo.android.mvp.adapting_demo.framework.adaptable;

import android.support.annotation.NonNull;
import org.lathanh.android.mvp.adapter.adaptable.AdaptableAdapter;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_BaseFragment;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models;

/**
 * Adapts {@link AdapterFwkDemo_AdaptableViewModel#getDataModel() the
 * data} in {@link AdapterFwkDemo_AdaptableViewModel} into a
 * {@link AdaptingDemo_Models.ViewModel}.
 *
 * <p>Not to be confused an Android Adapter, which does adapting and binding for
 * lists.</p>
 *
 * @author Robert LaThanh 2016-01-27
 */
public class AdapterFwkDemo_AdaptableAdapter
    implements AdaptableAdapter<AdaptingDemo_Models.ViewModel,
                                AdapterFwkDemo_AdaptableViewModel> {

  @NonNull
  @Override
  public AdaptingDemo_Models.ViewModel adapt(
      @NonNull AdapterFwkDemo_AdaptableViewModel adaptable) {
    AdaptingDemo_Models.DataModel dataModel = adaptable.getDataModel();
    return AdaptingDemo_BaseFragment.adaptDataModelToViewModel(dataModel);
  }
}
