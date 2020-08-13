package com.example.android.ecommerce;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.android.ecommerce.utils.ByteUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.android.ecommerce.MySingleton.HOST_URL;

public class AddProductFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AddProductFragment";
    public static final int REQUEST_PERMISSION_READ_EXTERNAL = 1;
    public static final int REQUEST_IMAGE = 2;
    public static final String IMAGE_URL = HOST_URL + "scripts/upload-product.php";

    private ImageView productImgView;
    private EditText productName;
    private Button uploadProductBtn;
    private Spinner catSpinner;
    private NavController navController;

    private Bitmap bitmap;

    public AddProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        productImgView = view.findViewById(R.id.productImgView);
        productName = view.findViewById(R.id.productName);
        uploadProductBtn = view.findViewById(R.id.uploadProductBtn);
        uploadProductBtn.setOnClickListener(this);

        String[] categories = new String[]{"1", "2", "3", "4"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner = view.findViewById(R.id.catSpinner);
        catSpinner.setAdapter(spinnerAdapter);

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL);
        Log.d(TAG, "onViewCreated: permission requested");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: read external permission granted");

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_IMAGE);
            Log.d(TAG, "onRequestPermissionsResult: gallery activity started for result");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: called");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == requireActivity().RESULT_OK) {
            Log.d(TAG, "onActivityResult: result returned OK for REQUEST_IMAGE");
            Uri imgUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imgUri);
                Glide.with(getContext()).load(bitmap).into(productImgView);

                Log.d(TAG, "onActivityResult: bitmap is being loaded into imageView");
            } catch (IOException e) {
                Toast.makeText(getContext(), "Image Not Loaded", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uploadProductBtn:
                if (bitmap == null) {
                    Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
                } else {
                    String img = ByteUtil.BitmapToString(bitmap);
                    String catId = catSpinner.getSelectedItem().toString();
                    String pName = productName.getText().toString();

                    uploadProduct(pName, catId, img);
                    Log.d(TAG, "onClick: Product is being uploaded");
                }
                break;
        }
    }

    public void uploadProduct(String pName, String catId, String img) {
        Log.d(TAG, "uploadProduct: called");

        StringRequest request = new StringRequest(
                Request.Method.POST,
                IMAGE_URL,
                response -> {
                    Toast.makeText(getContext(), "Product Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                    navController.popBackStack();
                },
                error -> {
                    Toast.makeText(getContext(), "Response Error.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d(TAG, "getParams: called");

                Map<String, String> params = new HashMap<>();
                params.put("image", img);
                params.put("pname", pName);
                params.put("cat_id", catId);
                return params;
            }
        };

        MySingleton.getInstance(getContext()).enqueueRequest(request);
    }
}