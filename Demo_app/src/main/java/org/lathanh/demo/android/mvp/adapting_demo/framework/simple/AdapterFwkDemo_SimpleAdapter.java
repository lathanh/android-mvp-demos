package org.lathanh.demo.android.mvp.adapting_demo.framework.simple;

import android.support.annotation.NonNull;
import org.lathanh.android.mvp.adapter.simple.SimpleAdapter;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_BaseFragment;
import org.lathanh.demo.android.mvp.adapting_demo.AdaptingDemo_Models;

/**
 * Adapts {@link AdaptingDemo_Models.DataModel}s into
 * {@link AdaptingDemo_Models.ViewModel}s.
 *
 * <p>Not to be confused an Android Adapter, which does adapting and binding for
 * lists.</p>
 *
 * @author Robert LaThanh 2016-03-25
 */
public class AdapterFwkDemo_SimpleAdapter
    implements SimpleAdapter<AdaptingDemo_Models.ViewModel,
                             AdaptingDemo_Models.DataModel> {

  @NonNull
  @Override
  public AdaptingDemo_Models.ViewModel adapt(
      @NonNull AdaptingDemo_Models.DataModel dataModel) {
    return AdaptingDemo_BaseFragment.adaptDataModelToViewModel(dataModel);
  }
}
