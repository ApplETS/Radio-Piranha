package ca.etsmtl.applets.radio.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import ca.etsmtl.applets.radio.R;
import ca.etsmtl.applets.radio.models.CurrentCalendar;
import ca.etsmtl.applets.radio.views.CalendarTextView;
import ca.etsmtl.applets.radio.views.NumGridView;
import ca.etsmtl.applets.radio.views.NumGridView.OnCellTouchListener;

public class CalendarFragment extends Fragment implements OnCellTouchListener {

    protected CurrentCalendar currentCalendar;
    private NumGridView currentGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View v = inflater.inflate(R.layout.calendar_view, null, false);
	// set the gridview containing the day names
	final String[] day_names = getResources().getStringArray(R.array.day_names);
	//
	final GridView grid = (GridView) v.findViewById(R.id.gridDayNames);
	grid.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.day_name, day_names));

	// set next and previous buttons
	final ImageButton btn_previous = (ImageButton) v.findViewById(R.id.btn_previous);
	final ImageButton btn_next = (ImageButton) v.findViewById(R.id.btn_next);

	btn_previous.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		currentCalendar.previousMonth();
	    }
	});
	btn_next.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		currentCalendar.nextMonth();
	    }
	});

	// set the calendar view
	currentGridView = (NumGridView) v.findViewById(R.id.calendar_view);

	currentGridView.setOnCellTouchListener(this);

	// assignation des session déja en mémoire
	// currentGridView.setSessions(ETSMobileApp.getInstance().getSessions());

	// Affiche le mois courant
	final CalendarTextView txtcalendar_title = (CalendarTextView) v
		.findViewById(R.id.calendar_title);

	currentCalendar = new CurrentCalendar();
	// initialisation des observers

	currentCalendar.addObserver(currentGridView);
	currentCalendar.addObserver(txtcalendar_title);

	currentCalendar.setChanged();
	currentCalendar.notifyObservers(currentCalendar.getCalendar());

	currentGridView.getCurrentCell().setChanged();
	currentGridView.getCurrentCell().notifyObservers();

	return v;

    }

    @Override
    public void onCellTouch(NumGridView v, int x, int y) {

    }
}
