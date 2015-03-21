package com.jelcaf.pacomf.patealapalma.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jelcaf.pacomf.patealapalma.R;
import com.jelcaf.pacomf.patealapalma.SenderosConstants;
import com.jelcaf.pacomf.patealapalma.fragment.RecommendSenderoFragment;
import com.jelcaf.pacomf.patealapalma.fragment.SenderoDetailFragment;
import com.jelcaf.pacomf.patealapalma.fragment.SenderoListFragment;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


/**
 * An activity representing a list of Senderos. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link com.jelcaf.pacomf.patealapalma.activity.SenderoDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link com.jelcaf.pacomf.patealapalma.fragment.SenderoListFragment} and the item details
 * (if present) is a {@link com.jelcaf.pacomf.patealapalma.fragment.SenderoDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link com.jelcaf.pacomf.patealapalma.fragment.SenderoListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class SenderosSwipeActivity extends ActionBarActivity
      implements SenderoListFragment.Callbacks, MaterialTabListener {

   /**
    * Whether or not the activity is in two-pane mode, i.e. running on a tablet
    * device.
    */
   private boolean mTwoPane;

   private SenderoListFragment senderoListFragment;

   MaterialTabHost tabHost;
   ViewPager pager;
   ViewPagerAdapter adapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_dashboard_with_tabs);

      // Toolbar Support
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
      pager = (ViewPager) this.findViewById(R.id.pager);

      // init view pager
      adapter = new ViewPagerAdapter(getSupportFragmentManager());
      pager.setAdapter(adapter);
      pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
         @Override
         public void onPageSelected(int position) {
            // when user do a swipe the selected tab change
            tabHost.setSelectedNavigationItem(position);

         }
      });

      // insert all tabs from pagerAdapter data
      for (int i = 0; i < adapter.getCount(); i++) {
         tabHost.addTab(
               tabHost.newTab()
                     .setText(adapter.getPageTitle(i))
                     .setTabListener(this)
         );

      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.principal_menu, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle item selection
      switch (item.getItemId()) {
         case R.id.setting:
            callSettingsActivity();
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }

   private void callSettingsActivity() {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
   }


   /**
    * Callback method from {@link com.jelcaf.pacomf.patealapalma.fragment.SenderoListFragment.Callbacks}
    * indicating that the item with the given ID was selected.
    */
   @Override
   public void onItemSelected(Long id) {
      if (mTwoPane) {
         // In two-pane mode, show the detail view in this activity by
         // adding or replacing the detail fragment using a
         // fragment transaction.
         Bundle arguments = new Bundle();
         arguments.putLong(SenderoDetailFragment.ARG_ITEM_ID, id);
         SenderoDetailFragment fragment = new SenderoDetailFragment();
         fragment.setArguments(arguments);
         getSupportFragmentManager().beginTransaction()
               .replace(R.id.sendero_detail_container, fragment)
               .commit();

      } else {
         // In single-pane mode, simply start the detail activity
         // for the selected item ID.
         Intent detailIntent = new Intent(this, SenderoDetailWithImageActivity.class);
         detailIntent.putExtra(SenderoDetailFragment.ARG_ITEM_ID, id);
         startActivity(detailIntent);
      }
   }

   @Override
   public void onTabSelected(MaterialTab tab) {
      pager.setCurrentItem(tab.getPosition());
   }

   @Override
   public void onTabReselected(MaterialTab tab) {

   }

   @Override
   public void onTabUnselected(MaterialTab tab) {

   }

   private class ViewPagerAdapter extends FragmentStatePagerAdapter {

      public static final int NUM_TABS = 3;

      public ViewPagerAdapter(FragmentManager fm) {
         super(fm);

      }

      public Fragment getItem(int num) {
         if (num == 0) {
            RecommendSenderoFragment recommendSenderoFragment = new RecommendSenderoFragment();
            return recommendSenderoFragment;
         }
         SenderoListFragment senderoListFragment = new SenderoListFragment();
         if (num == 1) {
            Bundle args = new Bundle();
            args.putBoolean(SenderosConstants.Arguments.RECOMMENDED, true);

            senderoListFragment.setArguments(args);
         }
         return senderoListFragment;
      }

      @Override
      public int getCount() {
         return NUM_TABS;
      }

      @Override
      public CharSequence getPageTitle(int position) {
         switch (position) {
            case SenderosConstants.Tabs.GROUP_RECOMMEND_SENDEROS: return getResources().getString(R.string.group_recommend_senderos);
            case SenderosConstants.Tabs.RECOMMENDED_SENDEROS: return getResources().getString(R.string.recommend_senderos);
            case SenderosConstants.Tabs.ALL_SENDEROS: return getResources().getString(R.string
                  .all_senderos);
         }
         return "undefined";
      }

   }

}
