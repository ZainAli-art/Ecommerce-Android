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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.ecommerce.model.Category;
import com.example.android.ecommerce.utils.ByteUtil;
import com.example.android.ecommerce.viewmodel.CategoryViewModel;
import com.example.android.ecommerce.viewmodel.ProductViewModel;

import java.io.IOException;

public class AddProductFragment extends Fragment implements View.OnClickListener {
    public static final int REQUEST_PERMISSION_READ_EXTERNAL = 1;
    public static final int REQUEST_IMAGE = 2;

    private ImageView productImgView;
    private EditText productName;
    private Spinner catSpinner;
    private EditText priceText;
    private NavController navController;
    private CategoryViewModel categoryViewModel;
    private ProductViewModel productViewModel;

    private Bitmap bitmap;
    String[] catSpinnerItems;

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
        priceText = view.findViewById(R.id.price);
        view.findViewById(R.id.uploadProductBtn).setOnClickListener(this);
        categoryViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(CategoryViewModel.class);
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(ProductViewModel.class);


        categoryViewModel.getCategories().observe(requireActivity(), categories -> {
            catSpinnerItems = new String[categories.size()];
            int index = 0;
            for (Category c : categories) catSpinnerItems[index++] = c.getName();
        });

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, catSpinnerItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner = view.findViewById(R.id.catSpinner);
        catSpinner.setAdapter(spinnerAdapter);

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
        if (requestCode == REQUEST_IMAGE && resultCode == requireActivity().RESULT_OK) {
            Uri imgUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imgUri);
                Glide.with(getContext()).load(bitmap).into(productImgView);
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
                    String pName = productName.getText().toString();
                    String catId = String.valueOf(categoryViewModel.getCatIdByName(catSpinner.getSelectedItem().toString()));
                    String img = ByteUtil.BitmapToString(bitmap);
                    String price = priceText.getText().toString();

                    productViewModel.uploadProduct(pName, catId, img, price);
                    navController.popBackStack();
                }
                break;
        }
    }
}