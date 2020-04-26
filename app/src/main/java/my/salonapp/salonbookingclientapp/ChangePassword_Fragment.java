package my.salonapp.salonbookingclientapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class ChangePassword_Fragment extends Fragment implements OnClickListener {

    // URLs
    private static final String ASPX_UPDATE_PASSWORD = "UpdateClientPassword.aspx";

    // Control fields
    private View view;
    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private static FloatingActionButton updatePasswordBtn;
    private static FloatingActionButton backPasswordBtn;

    // Variables
    private Utils.Task task;
    private Boolean status, isNetworkConnected;
    private ClientDbHelper db;

    public ChangePassword_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.changepassword_layout, container, false);
        task = null;

        initViews();
        setListeners();

        return view;
    }

    // Initialize all the views
    private void initViews() {
        currentPassword = view.findViewById(R.id.currentPassword);
        newPassword = view.findViewById(R.id.newPassword);
        confirmPassword = view.findViewById(R.id.confirmPassword);

        updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);
        backPasswordBtn = view.findViewById(R.id.backPasswordBtn);
    }

    // Set event listeners
    private void setListeners() {
        updatePasswordBtn.setOnClickListener(this);
        backPasswordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updatePasswordBtn:
                // Update password
                task = Utils.Task.Update;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    doSubmission(getString(R.string.confirmation_update_password),
                            Utils.Task.Update.toString());
                }
                break;

            case R.id.backPasswordBtn:
                // Back to setting page
                FragmentManager fragment = getActivity().getSupportFragmentManager();
                fragment
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new More_Fragment(),
                                Utils.More_Fragment)
                        .commit();
                break;
        }
    }

    // Check validation method
    private Boolean checkValidation() {
        db = new ClientDbHelper(getContext());
        Client client = db.getClient();

        Boolean validate = Boolean.TRUE;

        // Get all edit text values
        String currPassword = currentPassword.getText().toString();
        String password = newPassword.getText().toString();
        String retypePassword = confirmPassword.getText().toString();

        // Check if all the strings are null or not
        if (currPassword.equals("") || password.equals("")
                || retypePassword.equals("")) {
            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.all_fields_are_required));
        }
        // Check if current password equal to database
        else if (!currPassword.equals(client.getClientPassword())) {
            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.password_not_match_database));
        }
        // Check if both password should be equal
        else if (!password.equals(retypePassword)) {
            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.new_password_not_match));
        }

        return validate;
    }

    private void doSubmission(String message, final String taskSelection) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.confirmation))
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                taskSelection);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void updatePassword() {
        db = new ClientDbHelper(getContext());
        String strNewPassword = newPassword.getText().toString();

        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s&password=%5$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_UPDATE_PASSWORD,
                    db.getClientId(),
                    strNewPassword);

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);

            if (status == Boolean.TRUE) {
                ClientDbHelper db = new ClientDbHelper(getContext());
                int id = db.getClientId();
                db.updateClientPasswordById(id, strNewPassword);
            }
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            isNetworkConnected = Utils.isNetworkAvailable(getContext());

            if (isNetworkConnected == Boolean.TRUE) {
                if (param[0].equals(Utils.Task.Update.toString())) {
                    updatePassword();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (isNetworkConnected == Boolean.TRUE) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Utils.Bundle_Task, task);
                bundle.putBoolean(Utils.Bundle_Status, status);

                More_Fragment fragment = new More_Fragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, fragment, Utils.More_Fragment)
                        .commit();
            } else {
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
            }
        }
    }
}
