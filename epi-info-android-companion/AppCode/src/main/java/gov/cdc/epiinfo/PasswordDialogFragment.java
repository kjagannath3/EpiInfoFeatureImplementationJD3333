//package gov.cdc.epiinfo;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.fragment.app.DialogFragment;
//
//public class PasswordDialogFragment extends DialogFragment {
////		int mNum;
////
////		public static PasswordDialogFragment newInstance(int num) {
////			PasswordDialogFragment f = new PasswordDialogFragment();
////
////			Bundle args = new Bundle();
////			args.putInt("num", num);
////			f.setArguments(args);
////
////			return f;
////		}
////
////		@Override
////		public void onCreate(Bundle savedInstanceState) {
////			super.onCreate(savedInstanceState);
////			mNum = getArguments().getInt("num");
////		}
//
//    public PasswordDialogFragment() {
//        super(R.layout.password_dialog);
//    }
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.password_dialog, null);
//        builder.setView(view);
//
//        final EditText txtPassword = (EditText) view.findViewById(R.id.txtPassword);
//        final EditText txtPasswordConfirm = (EditText) view.findViewById(R.id.txtPasswordConfirm);
//
//        Button btnSet = (Button) view.findViewById(R.id.btnSet);
//        builder.setPositiveButton(btnSet.getText(), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface v, int id) {
//
//                if (txtPassword.getText().toString().equals(txtPasswordConfirm.getText().toString()))
//                {
//                    txtPasswordConfirm.setError(null);
//                    new MainActivity.AsyncExporter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, txtPassword.getText().toString());
//                    ((InputMethodManager)self.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(txtPassword.getWindowToken(), 0);
//                    Toast.makeText(self, getString(R.string.sync_file_started), Toast.LENGTH_LONG).show();
//                    dismiss();
//                }
//                else
//                {
//                    txtPasswordConfirm.setError(self.getString(R.string.not_match_password));
//                }
//
//            }
//        });
//
//        builder.setTitle(getString(R.string.sync_file_password));
//        builder.setCancelable(false);
//
//        return builder.create();
//    }
//}
