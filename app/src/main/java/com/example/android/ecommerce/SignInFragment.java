package com.example.android.ecommerce;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class SignInFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignInFragment";
    public static final String INSERT_CUSTOMER_URL = HOST_URL + "scripts/insert-customer.php";

    private EditText email;
    private EditText password;
    private Button loginBtn;

    private NavController navController;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        loginBtn = view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        navController = Navigation.findNavController(view);
    }

    public void login(String email, String password) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                INSERT_CUSTOMER_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int uid = jsonObject.getInt("uid");
                        moveToHomeFragment(uid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "URL response error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d(TAG, "getParams: called");
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("pwd", password);
                return params;
            }
        };

        MySingleton.getInstance(getContext()).enqueueRequest(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                login(email.getText().toString(), password.getText().toString());
                break;
            default:
                Toast.makeText(getContext(), "No functionality assigned yet.", Toast.LENGTH_SHORT).show();
        }
    }

    public void moveToHomeFragment(int uid) {
        if (uid == -1) {
            Toast.makeText(getContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
        } else {
            Bundle args = new Bundle();
            args.putInt("uid", uid);
            navController.navigate(R.id.action_signInFragment_to_homeFragment, args);
        }
    }
}