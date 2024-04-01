package gov.cdc.epiinfo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import gov.cdc.epiinfo.analysis.AnalysisMain;
import gov.cdc.epiinfo.cloud.CloudFactory;
import gov.cdc.epiinfo.cloud.LoginActivity;
import gov.cdc.epiinfo.etc.ExtFilter;
import gov.cdc.epiinfo.statcalc.StatCalcMain;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

	private Button btnCollectData;
	private Button btnStatcalc;
	private Button btnAnalyze;
	private MainActivity self;
	private GoogleMap mMap;
	private MapView mMapView;
	private static boolean splashShown;

	private void LoadActivity(Class c)
	{
		startActivity(new Intent(this, c));
	}

	private void LoadActivity(String component, String activity)
	{
		Intent intent = new Intent();
		intent.setClassName(component, activity);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			openOptionsMenu();
		}
		else
		{
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}

	@Override
	public void openOptionsMenu()
	{
		Configuration config = getResources().getConfiguration();

		if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) > Configuration.SCREENLAYOUT_SIZE_LARGE)
		{
			int originalScreenLayout = config.screenLayout;
			config.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;
			super.openOptionsMenu();
			config.screenLayout = originalScreenLayout;
		}
		else
		{
			super.openOptionsMenu();
		}
	}

	private boolean checkPermissions()
	{
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_MEDIA_IMAGES)
				!= PackageManager.PERMISSION_GRANTED) {

			Intent permissions = new Intent(this, Permissions.class);
			permissions.putExtra("PermissionType",Permissions.READ_MEDIA_IMAGES);

			//MainActivity.super.getActivityResultRegistry().onLaunch(Permissions.READ_MEDIA_IMAGES, temp, permissions, null);
			//startActivityForResult(permissions,Permissions.READ_MEDIA_IMAGES);
			permissionsActivityResultLauncher.launch(permissions);
			return false;
		}
		else
        {
            return true;
        }
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//		super.onActivityResult(requestCode, resultCode, intent);
//		SetupFileSystem();
//	}

	ActivityResultContracts.StartActivityForResult temp = new ActivityResultContracts.StartActivityForResult();
	ActivityResultLauncher<Intent> permissionsActivityResultLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					SetupFileSystem();
				}
			});

	private void createNotificationChannel()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			NotificationChannel channel = new NotificationChannel("3034500","Epi Info", NotificationManager.IMPORTANCE_DEFAULT);
			channel.setDescription("Epi Info system notifications");
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

		@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!splashShown)
		{
			LoadActivity(SplashScreen.class);
			splashShown = true;
		}

		boolean havePermission = checkPermissions();
		createNotificationChannel();

		setContentView(R.layout.entry); 

		self = this;

		DeviceManager.Init(this);
		DeviceManager.SetOrientation(this, false);

		mMapView = findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		mMapView.getMapAsync(this);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		if (!sharedPref.getBoolean("ei7", false) && !sharedPref.getBoolean("stacked", false) && !sharedPref.getBoolean("interview", false))
		{
			SharedPreferences.Editor editor = sharedPref.edit();
			if (DeviceManager.IsLargeTablet())
			{
				editor.putBoolean("ei7", true);
			}
			else
			{
				editor.putBoolean("stacked", true);
			}
			editor.putBoolean("sync_up_only", true);
			editor.putBoolean("sample_forms", true);
			editor.apply();
		}
		if (!sharedPref.contains("reverse_order"))
		{
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("reverse_order", true);
			editor.apply();
		}
		if (!sharedPref.contains("cloud_service"))
		{
			if ((!sharedPref.contains("application_key") || sharedPref.getString("application_key", "").equals("")))
			{
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("cloud_service", "Box");
				editor.apply();
			}
			else
			{
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("cloud_service", "Microsoft Azure");
				editor.apply();
			}
		}
		if (!sharedPref.contains("cloud_sync_save"))
		{
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("cloud_sync_save", true);
			editor.apply();
		}
		if (!sharedPref.contains("decimal_symbol"))
		{
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("decimal_symbol", ".");
			editor.apply();
		}
		if (!sharedPref.contains("device_id"))
		{
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("device_id", Secure.getString(getContentResolver(),Secure.ANDROID_ID));
			editor.apply();
		}
		if (!sharedPref.contains("azure_classic"))
		{
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("azure_classic", true);
			editor.apply();
		}

		this.setTheme(R.style.AppTheme);

		btnCollectData = findViewById(R.id.btnCollectData);
		btnCollectData.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment dialog = new ViewDialogFragment();
				dialog.show(getSupportFragmentManager(), "ViewDialogFragment");
			}
		});
		
		btnAnalyze = findViewById(R.id.btnAnalyze);
		btnAnalyze.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment dialog = new ViewDialogAnalysisFragment();
				dialog.show(getSupportFragmentManager(), "ViewDialogAnalysisFragment");
			}
		});

		btnStatcalc = findViewById(R.id.btnStatcalc);
		btnStatcalc.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(self, StatCalcMain.class));
			}
		});

		if (havePermission) {
            SetupFileSystem();
        }
	}

	private void SetupFileSystem()
    {
        try
        {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            path.mkdirs();
            File syncPath = new File(path, "/EpiInfo/SyncFiles/");
            File quesPath = new File(path, "/EpiInfo/Questionnaires/");
            File imgPath = new File(path, "/EpiInfo/Images/");
            File preloadPath = new File(path, "/EpiInfo/Preload/");
            syncPath.mkdirs();
            quesPath.mkdirs();
            imgPath.mkdirs();
            preloadPath.mkdirs();

            File handshakeFile = new File(path, "/EpiInfo/Handshake.xml");
            FileWriter handshakeFileWriter = new FileWriter(handshakeFile);
            BufferedWriter handshakeOut = new BufferedWriter(handshakeFileWriter);
            handshakeOut.write(GetHandshakeContents());
            handshakeOut.close();

        }
        catch (Exception ex)
        {
        	int x=5;
        	x++;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("sample_forms", true))
        {
            GetSampleForm();
        }
        AssetManager assetManager = getAssets();
        try
        {
            String fileName = "EpiGrammar.cgt";
            File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/EpiInfo/" + fileName);
            InputStream in = assetManager.open(fileName);
            FileOutputStream f = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0)
            {
                f.write(buffer, 0, len1);
            }
            f.close();
        }
        catch (Exception e)
        {

        }

        try
        {
            String fileName = "displayMetrics.xml";
            File outputDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File outputFile = new File(outputDirectory + "/EpiInfo/" + fileName);

            if(!outputFile.exists())
            {
                android.util.DisplayMetrics displayMetrics = new android.util.DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

                writer.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n");

                writer.write("<displayMetrics ");

                writer.write("xdpi=\"" + displayMetrics.xdpi + "\" ");
                writer.write("ydpi=\"" + displayMetrics.ydpi + "\" ");
                writer.write("widthPixels=\"" + displayMetrics.widthPixels + "\" ");
                writer.write("heightPixels=\"" + displayMetrics.heightPixels + "\" ");

                writer.write("/>");

                writer.close();
            }
        }
        catch (Exception ignored) { }
        try
        {
            File temp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/EpiInfo/Temp");
            deleteDirectory(temp);
        }
        catch (Exception ignored)
        {

        }

        new Preloader().Load(this);

        loadDefaults();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("ViewName"))
        {
            String viewName = extras.getString("ViewName");
            Intent recordList = new Intent(this, RecordList.class);
            recordList.putExtra("ViewName", viewName);

			if (extras.containsKey("SearchQuery"))
			{
				String searchQuery = extras.getString("SearchQuery");
				recordList.putExtra("SearchQuery", searchQuery);
			}

            startActivity(recordList);
        }
        else if (!AppManager.getDefaultForm().isEmpty())
        {
            Intent recordList = new Intent(this, RecordList.class);
            recordList.putExtra("ViewName", AppManager.getDefaultForm());
            startActivity(recordList);
            finish();
        }
    }


	@Override
	public void onMapReady(GoogleMap googleMap) {

		KmlLoader.Load(googleMap, this);
	}

	private void loadDefaults() {
		AppManager.setDefaultForm("");
		try {
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File file = new File(path, "EpiInfo/defaults.xml");

			InputStream obj_is = null;
			Document obj_doc = null;
			DocumentBuilderFactory doc_build_fact = null;
			DocumentBuilder doc_builder = null;
			obj_is = new FileInputStream(file);
			doc_build_fact = DocumentBuilderFactory.newInstance();
			doc_builder = doc_build_fact.newDocumentBuilder();

			obj_doc = doc_builder.parse(obj_is);
			NodeList obj_nod_list = null;
			if (null != obj_doc) {
				Element feed = obj_doc.getDocumentElement();
				String form = feed.getAttributes().getNamedItem("Form").getNodeValue().replace(".xml", "");
				if (form != null && !form.isEmpty()) {
					AppManager.setDefaultForm(form);
				}
			}
		} catch (Exception ex) {

		}
	}

	private boolean deleteDirectory(File path) 
	{
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return( path.delete() );
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mMapView != null) {
			mMapView.onResume();
		}
	}

	@Override
	public void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (mMapView != null) {
			try {
				mMapView.onDestroy();
			} catch (NullPointerException e) {

			}
		}
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (mMapView != null) {
			mMapView.onLowMemory();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mMapView != null) {
			mMapView.onSaveInstanceState(outState);
		}
	}

	private void ShowSettings()
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(self);
		if (sharedPref.getString("admin_password", "").equals(""))
		{
			startActivity(new Intent(self, AppSettings.class));
		}
		else
		{
			DialogFragment dialog = new PasswordDialogFragment(1);
			dialog.show(getSupportFragmentManager(), "AdminPasswordDialogFragment");
		}
	}

	private void Alert(String message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)       
		.setCancelable(false)       
		.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{           
			public void onClick(DialogInterface dialog, int id) 
			{                
				dialog.cancel();           
			}       
		});
		builder.create();
		builder.show();
	}

	public static class ViewDialogFragment extends DialogFragment {
		public ViewDialogFragment() {
			super(R.layout.view_dialog);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			if (ContextCompat.checkSelfPermission(getActivity(),
					android.Manifest.permission.READ_MEDIA_IMAGES)
					!= PackageManager.PERMISSION_GRANTED) {

				((MainActivity)getActivity()).Alert(getString(R.string.error_storage));
			}
			else {

				File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				File quesPath = new File(basePath + "/EpiInfo/Questionnaires");

				String[] files = quesPath.list(new ExtFilter("xml", "_"));
				if (files != null) {
					String[] spinnerList = new String[files.length];
					for (int x = 0; x < files.length; x++) {
						int idx = files[x].indexOf(".");
						spinnerList[x] = files[x].substring(0, idx);
					}

					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					LayoutInflater inflater = getActivity().getLayoutInflater();
					View view = inflater.inflate(R.layout.view_dialog, null);
					builder.setView(view);

					builder.setTitle(getString(R.string.available_forms));
					builder.setCancelable(true);

					Spinner viewSpinner = view.findViewById(R.id.cbxViewField);
					viewSpinner.setPrompt(getString(R.string.select_form));


					ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, spinnerList);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					viewSpinner.setAdapter(adapter);

					final Spinner mySpinner = viewSpinner;
					final ViewDialogFragment myDialog = this;
					final Intent recordList = new Intent(getContext(), RecordList.class);

					Button btnSet = view.findViewById(R.id.btnSet);
					btnSet.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							recordList.putExtra("ViewName", mySpinner.getSelectedItem().toString());
							startActivity(recordList);

							myDialog.dismiss();
						}
					});

					return builder.create();
				}
			}
			return null;
		}
	}

	public static class ViewDialogAnalysisFragment extends DialogFragment {
		public ViewDialogAnalysisFragment() {
			super(R.layout.view_dialog_for_analysis);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			if (ContextCompat.checkSelfPermission(getActivity(),
					Manifest.permission.READ_MEDIA_IMAGES)
					!= PackageManager.PERMISSION_GRANTED) {

				((MainActivity)getActivity()).Alert(getString(R.string.error_storage));
			}
			else {
				File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				File quesPath = new File(basePath + "/EpiInfo/Questionnaires");

				String[] files = quesPath.list(new ExtFilter("xml", null));
				if (files != null) {
					String[] spinnerList = new String[files.length];
					for (int x = 0; x < files.length; x++) {
						int idx = files[x].indexOf(".");
						spinnerList[x] = files[x].substring(0, idx);
					}

					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					LayoutInflater inflater = getActivity().getLayoutInflater();
					View view = inflater.inflate(R.layout.view_dialog_for_analysis, null);
					builder.setView(view);

					builder.setTitle(getString(R.string.available_forms));
					builder.setCancelable(true);

					Spinner viewSpinner = view.findViewById(R.id.cbxAnalysisViewField);
					viewSpinner.setPrompt(getString(R.string.select_form));


					ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, spinnerList);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					viewSpinner.setAdapter(adapter);

					final Spinner analysisSpinner = viewSpinner;
					final Intent analysis = new Intent(getContext(), AnalysisMain.class);

					Button btnSet = view.findViewById(R.id.btnAnalysisSet);
					btnSet.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							analysis.putExtra("ViewName", analysisSpinner.getSelectedItem().toString());
							startActivity(analysis);

							dismiss();
						}
					});

					return builder.create();
				}
			}
			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem mnuSave = menu.add(8000, 6001, 0, R.string.menu_settings);
		mnuSave.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		mnuSave.setIcon(android.R.drawable.ic_menu_preferences);

		MenuItem mnuLogin = menu.add(8000, 6006, 1, R.string.menu_login);
		mnuLogin.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        MenuItem mnuDownload = menu.add(8000, 6005, 2, R.string.menu_daily_download);
        mnuDownload.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		MenuItem mnuExport = menu.add(8000, 6003, 3, R.string.menu_export_all);
		mnuExport.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		
		MenuItem mnuCloud = menu.add(8000, 6004, 4, R.string.menu_cloud_sync);
		mnuCloud.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		MenuItem mnuHelp = menu.add(8000, 6002, 5, R.string.menu_help);
		mnuHelp.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		return true;
	}	


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 6001:
                ShowSettings();
                return true;
            case 6002:
                Uri uriUrl = Uri.parse("http://epiinfoandroid.codeplex.com/documentation");
                startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
                return true;
            case 6003:
				DialogFragment dialog = new PasswordDialogFragment(2);
				dialog.show(getSupportFragmentManager(), "SyncPasswordDialogFragment");
                return true;
            case 6004:
                doCloudSync();
                return true;
            case 6005:
				Toast.makeText(self, getString(R.string.cloud_download_schedule), Toast.LENGTH_LONG).show();
				new AsyncDailyDownloader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
			case 6006:
				Intent login = new Intent(self, LoginActivity.class);
				//startActivityForResult(new Intent(self, LoginActivity.class),11097);
				permissionsActivityResultLauncher.launch(new Intent(self, LoginActivity.class));
				return true;
        }
        return super.onOptionsItemSelected(item);
    }

	private int GetDailyTasks()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String deviceid = sharedPref.getString("device_id", "");
		return CloudFactory.GetCloudClient("","", null,this).getDailyTasks(this,deviceid);
    }

	private String GetHandshakeContents()
	{
		return "<?xml version=\"1.0\"?><Handshake ClientId=\"90fdc40c-f53d-4e66-930c-261b05a1d84b\"/>";
	}

	private void GetSampleForm()
	{
		AssetManager am = getAssets();
		try 
		{          
			LinkedList<String> fileNames = new LinkedList<String>();
			fileNames.add("Sample_Barcode.xml");
			fileNames.add("Sample_Contact_Investigation.xml");
			fileNames.add("Sample_InterviewMode.xml");
			fileNames.add("_ContactFollowup.xml");

			for (int x=0;x<fileNames.size();x++)
			{
				String fileName = fileNames.get(x);
				File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/EpiInfo/Questionnaires/" + fileName);
				InputStream in = am.open(fileName);         
				FileOutputStream f = new FileOutputStream(destinationFile);          
				byte[] buffer = new byte[1024];         
				int len1 = 0;         
				while ((len1 = in.read(buffer)) > 0) 
				{             
					f.write(buffer, 0, len1);         
				}         
				f.close();     
			}
		} 
		catch (Exception e) 
		{         

		}

		try 
		{          
			LinkedList<String> fileNames = new LinkedList<String>();
			fileNames.add("Sample_Contact_Investigation.csv");

			for (int x=0;x<fileNames.size();x++)
			{
				String fileName = fileNames.get(x);
				File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/EpiInfo/Preload/" + fileName);
				InputStream in = am.open(fileName);         
				FileOutputStream f = new FileOutputStream(destinationFile);          
				byte[] buffer = new byte[1024];         
				int len1 = 0;         
				while ((len1 = in.read(buffer)) > 0) 
				{             
					f.write(buffer, 0, len1);         
				}         
				f.close();     
			}
		} 
		catch (Exception e) 
		{         

		}

		try
		{
			File oldfile1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/EpiInfo/Questionnaires/Sample_Preparedness.xml");
			oldfile1.delete();
			File oldfile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/EpiInfo/Questionnaires/Sample_Ebola_Lab.xml");
			oldfile2.delete();
			File oldfile3 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/EpiInfo/Questionnaires/Sample_Ebola_Site_Monitoring.xml");
			oldfile3.delete();
			File oldfile4 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/EpiInfo/Questionnaires/Sample_Ebola_Lab_ws.xml");
			oldfile4.delete();
		}
		catch (Exception ex)
		{

		}
	}
	
	/*private void SyncAllData(AsyncTask asyncTask)
	{
		File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File quesPath = new File(basePath + "/EpiInfo/Questionnaires");

		String[] files = quesPath.list(new ExtFilter("xml",null));
		if (files != null)
		{
			for (int x=0;x<files.length;x++)
			{
				int idx = files[x].indexOf(".");
				String viewName = files[x].substring(0, idx);
				FormMetadata formMetadata = new FormMetadata("EpiInfo/Questionnaires/"+ viewName +".xml", this);

				if (viewName.startsWith("_"))
				{
					viewName = viewName.toLowerCase();
				}

				EpiDbHelper mDbHelper = new EpiDbHelper(this, formMetadata, viewName);
				mDbHelper.open();

				mDbHelper.SyncWithCloud(asyncTask);
			}
		}
	}*/

	private void ExportAllData(String password)
	{
		File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File quesPath = new File(basePath + "/EpiInfo/Questionnaires");

		String[] files = quesPath.list(new ExtFilter("xml",null));
		if (files != null)
		{
			for (int x=0;x<files.length;x++)
			{
				int idx = files[x].indexOf(".");
				String viewName = files[x].substring(0, idx);
				FormMetadata formMetadata = new FormMetadata("EpiInfo/Questionnaires/"+ viewName +".xml", this);

				if (viewName.startsWith("_"))
				{
					viewName = viewName.toLowerCase();
				}

				EpiDbHelper mDbHelper = new EpiDbHelper(this, formMetadata, viewName);
				mDbHelper.open();

				Cursor syncCursor;
				if (viewName.startsWith("_"))
				{
					syncCursor = mDbHelper.fetchAllRecordsPlusFkey();
				}
				else
				{
					syncCursor = mDbHelper.fetchAllRecords();
				}
				new SyncFileGenerator(self).Generate(formMetadata, password, syncCursor, viewName, mDbHelper);
				try
				{
					Thread.sleep(2000);
				}
				catch (Exception ex)
				{

				}
			}
		}
	}

	public static class PasswordDialogFragment extends DialogFragment {
		int mNum;

		public PasswordDialogFragment(int i) {
			super(R.layout.view_dialog);
			mNum = i;
		}
		public PasswordDialogFragment() {
			new PasswordDialogFragment(1);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			switch (mNum) {
				case 1:
					return NormPassword();
				case 2:
					return SyncPassword();
				default:
					try {
						throw new Exception("Bad number when creating PasswordDialog");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
			}
		}
		private Dialog NormPassword() {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			MainActivity main = (MainActivity) getActivity();
			LayoutInflater inflater = main.getLayoutInflater();
			View view = inflater.inflate(R.layout.admin_password_dialog, null);
			builder.setView(view);
			final EditText txtPassword = (EditText) view.findViewById(R.id.txtPassword);

			Button btnSet = (Button) view.findViewById(R.id.btnSet);
			btnSet.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(main);
					if (sharedPref.getString("admin_password", "").equals(txtPassword.getText().toString()))
					{
						dismiss();
						startActivity(new Intent(main, AppSettings.class));
					}
					else
					{
						main.Alert("Invalid password");
					}
				}
			});
			builder.setTitle(getString(R.string.admin_password));
			builder.setCancelable(true);
			return builder.create();
		}
		private Dialog SyncPassword() {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view = inflater.inflate(R.layout.password_dialog, null);
			builder.setView(view);

			final EditText txtPassword = (EditText) view.findViewById(R.id.txtPassword);
			final EditText txtPasswordConfirm = (EditText) view.findViewById(R.id.txtPasswordConfirm);

			Button btnSet = (Button) view.findViewById(R.id.btnSet);

			btnSet.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					if (txtPassword.getText().toString().equals(txtPasswordConfirm.getText().toString()))
					{
						txtPasswordConfirm.setError(null);
						((MainActivity)getActivity()).AsyncExporterPasswordBackground(txtPassword.getText().toString());
						((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(txtPassword.getWindowToken(), 0);
						Toast.makeText(getActivity(), getString(R.string.sync_file_started), Toast.LENGTH_LONG).show();
						dismiss();
					}
					else
					{
						txtPasswordConfirm.setError(getActivity().getString(R.string.not_match_password));
					}

				}
			});
			builder.setTitle(getString(R.string.sync_file_password));
			builder.setCancelable(false);

			return builder.create();
		}
	}

	public class AsyncDailyDownloader extends AsyncTask<Void,Double, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {

			return GetDailyTasks();
		}


		@Override
		protected void onPostExecute(Integer count) {

			int msgId = new Random().nextInt(Integer.MAX_VALUE);

			if (count > -1) {
				NotificationCompat.Builder builder = new NotificationCompat.Builder(self,"3034500")
						.setSmallIcon(R.drawable.ic_cloud_done)
						.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
						.setContentTitle(String.format(getString(R.string.cloud_download_schedule_complete), count.toString()));

				NotificationManager notificationManager = (NotificationManager) self.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(msgId, builder.build());
			} else {
				NotificationCompat.Builder builder = new NotificationCompat.Builder(self,"3034500")
						.setSmallIcon(R.drawable.ic_sync_problem)
						.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
						.setContentTitle(getString(R.string.cloud_download_schedule_failed));

				NotificationManager notificationManager = (NotificationManager) self.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(msgId, builder.build());
			}
		}
	}

	public class AsyncExporter extends AsyncTask<String,Double, Boolean>
	{

		@Override
		protected Boolean doInBackground(String... password) {

			ExportAllData(password[0]);
			return true;
		}
	}
	private void AsyncExporterPasswordBackground(String s) {
		new AsyncExporter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, s);
	}

	private void doCloudSync() {
		Toast.makeText(self, getString(R.string.cloud_sync_started), Toast.LENGTH_LONG).show();

		File basePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File quesPath = new File(basePath + "/EpiInfo/Questionnaires");

		String[] files = quesPath.list(new ExtFilter("xml", null));
		if (files != null) {
			for (int x = 0; x < files.length; x++) {
				int idx = files[x].indexOf(".");
				String viewName = files[x].substring(0, idx);

				new CloudSynchronizer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,viewName);
			}
		}

	}

	public class CloudSynchronizer extends AsyncTask<String, Integer, Integer> {

		private String formName;
		private ProgressBar progressBar;


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			int contentViewId = (self).getWindow().getDecorView().getRootView().getId();
			String resourceName = self.getResources().getResourceEntryName(contentViewId);

			Log.d("CloudSynchronizer", "Current Content View ID: " + contentViewId);
			Log.d("CloudSynchronizer", "Current Content View Layout Name: " + resourceName);
			// Make the Progress Bar with properties
			//setContentView(R.layout.line_list_row);

			// View rootView = LayoutInflater.from(self).inflate(R.layout.line_list_row, null);
			// progressBar = rootView.findViewById(R.id.progressBar);
			progressBar = MainActivity.this.findViewById(R.id.progressBar);
			if (progressBar != null) {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.setMax(100);
			} else {
				Log.e("CloudSynchronizer", "ProgressBar is null");
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			Log.d("CloudSynchronizer", "Progress update: " + values[0]);
			if (progressBar != null) {
				progressBar.setProgress(values[0]);
			} else {
				Log.e("CloudSynchronizer", "ProgressBar is null");
			}
		}
		@Override
		protected Integer doInBackground(String... params) {

			formName = params[0];
			FormMetadata formMetadata = new FormMetadata("EpiInfo/Questionnaires/" + formName + ".xml", self);

			if (formName.startsWith("_")) {
				formName = formName.toLowerCase();
			}

			//Progress Bar update --> Simulate heavy work
			for (int i = 0; i < 100; i++) {
				publishProgress(i);

				try {
					Thread.sleep(10);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}

			EpiDbHelper mDbHelper = new EpiDbHelper(self, formMetadata, formName);
			mDbHelper.open();

			return mDbHelper.SyncWithCloud(this);

			//return SyncAllData(this);
		}

		@Override
		protected void onPostExecute(Integer status) {

			int msgId = new Random().nextInt(Integer.MAX_VALUE);

			if (status > 0) {

				NotificationCompat.Builder builder = new NotificationCompat.Builder(self, "3034500")
						.setSmallIcon(R.drawable.ic_cloud_done)
						.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
						.setContentTitle(String.format(getString(R.string.cloud_sync_complete), formName))
						.setContentText(getString(R.string.cloud_sync_complete_detail));

				NotificationManager notificationManager = (NotificationManager) self.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(msgId, builder.build());
			} else if (status != -99 && status != 0) {
				NotificationCompat.Builder builder = new NotificationCompat.Builder(self, "3034500")
						.setSmallIcon(R.drawable.ic_sync_problem)
						.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
						.setContentTitle(String.format(getString(R.string.cloud_sync_failed), formName))
						.setContentText(getString(R.string.cloud_sync_failed_detail));

				NotificationManager notificationManager = (NotificationManager) self.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(msgId, builder.build());
			}
		}
	}


}