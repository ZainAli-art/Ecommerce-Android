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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.utils.Utility;
import com.example.android.ecommerce.viewmodel.CategoryViewModel;
import com.example.android.ecommerce.viewmodel.ProductViewModel;
import com.example.android.ecommerce.viewmodel.UserViewModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddProductFragment extends Fragment implements View.OnClickListener {
    public static final int REQUEST_PERMISSION_READ_EXTERNAL = 1;
    public static final int REQUEST_IMAGE = 2;

    private NavController navController;

    private CategoryViewModel categoryViewModel;
    private ProductViewModel productViewModel;
    private UserViewModel userViewModel;

    private ImageView productImgView;
    private EditText productName;
    private Spinner catSpinner;
    private EditText priceText;


    private Bitmap bitmap;
    private String[] catSpinnerItems;
    private String uid;
    private Map<String, Long> catMap;

    public AddProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(UserViewModel.class);
        categoryViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(CategoryViewModel.class);
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ProductViewModel.class);
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

        navController = NavHostFragment.findNavController(this);

        productImgView = view.findViewById(R.id.productImgView);
        productName = view.findViewById(R.id.productName);
        priceText = view.findViewById(R.id.price);
        view.findViewById(R.id.uploadProductBtn).setOnClickListener(this);

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> uid = user.uid);

        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            catSpinnerItems = new String[categories.size()];
            catMap = new HashMap<>();
            int index = 0;
            for (Category c : categories) {
                catSpinnerItems[index++] = c.name;
                catMap.put(c.name, c.id);
            }

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, catSpinnerItems);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            catSpinner = view.findViewById(R.id.catSpinner);
            catSpinner.setAdapter(spinnerAdapter);
        });

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == FragmentActivity.RESULT_OK) {
            Uri imgUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imgUri);
                Glide.with(requireContext()).load(bitmap).into(productImgView);
            } catch (IOException e) {
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
                    String catName = catSpinner.getSelectedItem().toString();
                    String pName = productName.getText().toString();
                    long catId = catMap.get(catName);
                    String img = Utility.BitmapToString(bitmap);
                    String price = priceText.getText().toString();

                    productViewModel.uploadProduct(uid, pName, catId, img, price);
                    navController.popBackStack();
                }
                break;
        }
    }
}