package waylay.client.activity;

import java.util.ArrayList;
import java.lang.reflect.Field;

import waylay.client.scenario.Node;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.waylay.client.R;

public class NodeAdapter extends ArrayAdapter<Node> {
	
	private final Context context;
	private final ArrayList<Node> values;

	public NodeAdapter(Context context, ArrayList<Node> values) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rowlayout, parent, false);
        }
		TextView textTitle = (TextView) convertView.findViewById(R.id.title);
		TextView textStatus = (TextView) convertView.findViewById(R.id.subtitle);
		TextView textID = (TextView) convertView.findViewById(R.id.rightcorner);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
		Node node = values.get(position);
		//Map<String, Double> nodeStates = node.getStates();
		String name = node.getName(); 
		textTitle.setText(name);
		textStatus.setText(node.getStatesAsString());
		textID.setText(node.getMostLikelyState());
		if (node.getName().equals(ScenariosFragment.selectedTask.getTargetNode())) {
			imageView.setImageResource(R.drawable.trigger);
		} else {
            int resourceID = context.getResources().getIdentifier("node.getSensorName().toLowerCase()", "drawable", context.getPackageName());
			if(resourceID == 0){
                resourceID = R.drawable.sensor;
            }
            imageView.setImageResource(resourceID);
		}

		return convertView;
	}
}