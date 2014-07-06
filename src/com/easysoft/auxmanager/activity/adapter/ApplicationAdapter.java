package com.easysoft.auxmanager.activity.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.easysoft.auxmanager.R;

import java.util.List;

/**
 * Application List adaptor
 * <p/>
 * <br/><i>Created at 7/5/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */
public class ApplicationAdapter  extends ArrayAdapter<ApplicationModel> {
    private List<ApplicationModel> appsList = null;
    private Context context;
    private PackageManager packageManager;

    public ApplicationAdapter(Context context, int textViewResourceId, List<ApplicationModel> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public ApplicationModel getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.snippet_list_row, null);
        }

        final ApplicationModel applicationModel = appsList.get(position);
        ApplicationInfo data = applicationModel.getApplicationInfo();
        if (null != data) {
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            TextView packageName = (TextView) view.findViewById(R.id.app_paackage);
            ImageView iconView = (ImageView) view.findViewById(R.id.app_icon);
            final CheckBox checkBox =  (CheckBox) view.findViewById(R.id.app_checkbox);
            appName.setText(data.loadLabel(packageManager));
            packageName.setText(data.packageName);
            iconView.setImageDrawable(data.loadIcon(packageManager));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    applicationModel.setSelected(buttonView.isSelected());
                }
            });
            checkBox.setTag(appsList.get(position));
        }
        return view;
    }
}
