package gov.cdc.epiinfo.statcalc;

import gov.cdc.epiinfo.DeviceManager;
import gov.cdc.epiinfo.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class StatCalcMain extends Activity {

	private void LoadActivity(Class c) {
		startActivity(new Intent(this, c));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DeviceManager.SetOrientation(this, false);
		this.setTheme(R.style.AppThemeNoBar);

		setContentView(R.layout.statcalc_main);

		Spinner activitiesSpinner = findViewById(R.id.activitySpinner); // Make sure your spinner id matches
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.activity_names, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		activitiesSpinner.setAdapter(adapter);

		//added a dropdown setting to the StatCalc so that UI is cleaner
		//Included the spinner class to create this dropdown feature
		activitiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// Avoid triggering on initial spinner load
				if (position > 0) {
					switch (position) {
						case 1: // Assuming this is 'Population Survey' based on your array
							LoadActivity(PopulationSurveyActivity.class);
							break;
						case 2: // 'Unmatched Case Control'
							LoadActivity(UnmatchedActivity.class);
							break;
						case 3: // 'Cohort'
							LoadActivity(CohortActivity.class);
							break;
						case 4: // 'Two by Two'
							LoadActivity(TwoByTwoActivity.class);
							break;
						case 5: // 'Matched Pair'
							LoadActivity(MatchedPairActivity.class);
							break;
						case 6: // 'Chi Square'
							LoadActivity(ChiSquareActivity.class);
							break;
						case 7: // 'Poisson'
							LoadActivity(PoissonActivity.class);
							break;
						case 8: // 'Binomial'
							LoadActivity(BinomialActivity.class);
							break;
						// Add more cases as necessary
						default:
							break;
					}
					//set the default dropdown selection to 'Population Survey'
					activitiesSpinner.setSelection(0);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// Another interface callback
			}
		});
	}
}
