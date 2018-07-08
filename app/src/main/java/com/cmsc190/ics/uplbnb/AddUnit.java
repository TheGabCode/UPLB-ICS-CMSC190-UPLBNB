package com.cmsc190.ics.uplbnb;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.Toast;


import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static java.security.AccessController.getContext;

public class AddUnit extends AppCompatActivity {
    ImageView unitImagePreview;
    Button uploadImageBtn;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    EditText unitIdentifierText;
    EditText unitRate;
    EditText unitCapacity;
    EditText unitSlotsAvailable;
    Spinner statusSpinner;
    Spinner conditionSpinner;
    Button submitUnitBtn;
    Button addFurnitureBtn;
    Button addPictureBtn;
    Intent intent;
    LinearLayout unitFurnitureContainer;
    LinearLayout dormitoryAttributes;
    LinearLayout apartmentAttributes;
    LinearLayout dormitoryFurnitureAttributes;
    LinearLayout photoList;
    private final int PICK_IMAGE_REQUEST = 71;
    Establishment_Item e;
    Uri filePath;
    List<Uri> uris = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        int establishmentType = intent.getIntExtra("establishmentType", 1);
        if (establishmentType == 1) {
            e = (Apartment_Item) intent.getSerializableExtra("establishment");
        } else {
            e = (Dormitory_Item) intent.getSerializableExtra("establishment");
        }
        setContentView(R.layout.activity_add_unit);
        dormitoryAttributes = (LinearLayout) findViewById(R.id.addUnitDormCapacityContainer);
        apartmentAttributes = (LinearLayout) findViewById(R.id.addUnitApartmentPart);
        dormitoryFurnitureAttributes = (LinearLayout) findViewById(R.id.addUnitDormitoryFurnitureLayout);


        /*unitImagePreview = (ImageView)findViewById(R.id.addUnitUploadPhotoPreview);
        uploadImageBtn = (Button)findViewById(R.id.addUnitUploadImageBtn);
        */
        unitIdentifierText = (EditText) findViewById(R.id.addUnitIdentifier);
        unitRate = (EditText) findViewById(R.id.addUnitPrice);
        unitCapacity = (EditText) findViewById(R.id.addUnitCapacity);
        addPictureBtn = findViewById(R.id.addUnitPictureBtn);
        addPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        unitSlotsAvailable = (EditText) findViewById(R.id.addUnitDormitoryAvailableSlots);
        statusSpinner = (Spinner) findViewById(R.id.apartmentStatusSpinner);
        statusSpinner.setSelection(1);
        conditionSpinner = (Spinner) findViewById(R.id.conditionSpinner);
        unitFurnitureContainer = findViewById(R.id.addUnitFurnitureContainer);
        submitUnitBtn = (Button) findViewById(R.id.addUnitSubmitButton);
        submitUnitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUnit();
            }
        });
        addFurnitureBtn = (Button) findViewById(R.id.addFurnitureBtn);
        addFurnitureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFurniture(view);
            }
        });
        if (establishmentType == 1) {
            dormitoryAttributes.setVisibility(View.GONE);
            dormitoryFurnitureAttributes.setVisibility(View.GONE);
        } else {
            apartmentAttributes.setVisibility(View.GONE);
        }
        if (establishmentType == 1) {

        } else {
            unitRate.setText(e.getPrice());
            unitCapacity.setText(((Dormitory_Item) e).getCapacityPerUnit() + "");
            initializeFurniture(e);
        }
        photoList = (LinearLayout) findViewById(R.id.uploadedPhotoContainer);
    }

    public void initializeFurniture(Establishment_Item e) {
        HashMap<String, Integer> furniture = (HashMap<String, Integer>) ((Dormitory_Item) e).getAvailableFurniture();
        if (furniture != null) {

            Iterator entries = furniture.entrySet().iterator();
            while (entries.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                String key = (String) entry.getKey();
                Integer value = (Integer) entry.getValue();
                addFurniture(key, value);
            }
        } else {

        }
    }

    public void addFurniture(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View furnitureView = inflater.inflate(R.layout.furniture_layout, null);
        unitFurnitureContainer.addView(furnitureView, unitFurnitureContainer.getChildCount() - 1);
    }

    public void addFurniture(String item, int quantity) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View furnitureView = inflater.inflate(R.layout.furniture_layout, null);
        EditText itemName = (EditText) furnitureView.findViewById(R.id.dormitoryFurnitureItem);
        EditText itemQty = (EditText) furnitureView.findViewById(R.id.dormitoryFurnitureQty);
        itemName.setText(item);
        itemQty.setText("" + quantity);
        unitFurnitureContainer.addView(furnitureView, unitFurnitureContainer.getChildCount() - 1);
    }

    public void deleteFurniture(View v) {
        unitFurnitureContainer.removeView((View) v.getParent());
    }


    public void addUnit() {
        final DatabaseReference databaseReference;
        DatabaseReference furnitureRef;
        int establishmentType = intent.getIntExtra("establishmentType", 1);
        String establishmentId = intent.getStringExtra("establishmentId");
        String unitIdentifier = unitIdentifierText.getText().toString().trim();
        int intUnitCondition;
        int condition = 0;
        int intUnitStatus = 1;
        int slotsAvailable = 0; //for dorms
        int capacity = 0; //for dorms;
        int open = 1;
        String id;
        HashMap<String, Integer> furniture = new HashMap<>();
        double rate;

        boolean ratePerHead = false;
        if (TextUtils.isEmpty(unitIdentifierText.getText().toString().trim())) {
            Toast.makeText(getApplication(), "Please fill in the unit identifier.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (establishmentType == 1) {
            intUnitCondition = conditionSpinner.getSelectedItemPosition(); //for apartment
            slotsAvailable = -1;
            intUnitStatus = statusSpinner.getSelectedItemPosition();
            if (intUnitStatus == 0) {
                open = 1;
            } else {
                open = 0;
            }
            if (intUnitCondition == 0) {
                condition = 1;
            } else {
                condition = 0;
            }

            if (TextUtils.isEmpty(unitRate.getText().toString().trim())) {
                Toast.makeText(getApplication(), "Please fill in unit rate.", Toast.LENGTH_SHORT).show();
                return;
            }

            rate = Double.parseDouble(unitRate.getText().toString().trim());
        } else {
            if (TextUtils.isEmpty(unitRate.getText().toString().trim())) {
                Toast.makeText(getApplication(), "Please fill in unit rate.", Toast.LENGTH_SHORT).show();
                return;
            }
            rate = Double.parseDouble(unitRate.getText().toString().trim());

            if (TextUtils.isEmpty(unitCapacity.getText().toString().trim())) {
                Toast.makeText(getApplication(), "Please fill in unit capacity.", Toast.LENGTH_SHORT).show();
                return;
            }
            capacity = Integer.parseInt(unitCapacity.getText().toString().trim());
            if (TextUtils.isEmpty(unitSlotsAvailable.getText().toString().trim())) {
                Toast.makeText(getApplication(), "Please fill in unit slots available.", Toast.LENGTH_SHORT).show();
                return;
            }
            slotsAvailable = Integer.parseInt(unitSlotsAvailable.getText().toString().trim());
            ratePerHead = intent.getBooleanExtra("ratePerHead", false);

            if (slotsAvailable > 0) {
                open = 1;
            } else {
                open = 0;
            }

            for (int i = 1; i < unitFurnitureContainer.getChildCount() - 1; i++) {
                View v = unitFurnitureContainer.getChildAt(i);
                EditText itemQuantity = (EditText) v.findViewById(R.id.dormitoryFurnitureQty);
                EditText itemName = (EditText) v.findViewById(R.id.dormitoryFurnitureItem);
                int qty = 0;
                String item;
                if (TextUtils.isEmpty(itemQuantity.getText().toString().trim())) {
                    Toast.makeText(getApplication(), "Please fill in the furniture fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                qty = Integer.parseInt(itemQuantity.getText().toString().trim());
                if (TextUtils.isEmpty(itemName.getText().toString().trim())) {
                    Toast.makeText(getApplication(), "Please fill in the furniture fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                item = itemName.getText().toString().trim();
                furniture.put(item, qty);

            }


        }


        databaseReference = FirebaseDatabase.getInstance().getReference("establishment").child(establishmentId).child("unit").push();
        id = databaseReference.getKey();
        HashMap<String, String> pictures = new HashMap<String, String>();

        Unit_Item unit = new Unit_Item(unitIdentifier, open, slotsAvailable, rate, id, condition, furniture, ratePerHead, capacity, pictures);
        databaseReference.setValue(unit);
        DatabaseReference unitRef = FirebaseDatabase.getInstance().getReference("establishment").child(establishmentId).child("numUnitsAvailable");
        unitRef.setValue(countOpenUnits(e));
        if(uris.size() > 0){
            saveImages(id);
            finish();
        }
        else{
            finish();
        }



    }

    public int countOpenUnits(Establishment_Item e) {
        int totalOpen = 0;
        if (e.unit != null) {
            Iterator entries = e.unit.entrySet().iterator();
            while (entries.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) entries.next();
                Unit_Item value = (Unit_Item) entry.getValue();
                if (value.getStatus() == 1) {
                    totalOpen++;
                }
            }
        }
        return totalOpen;
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); //creates upload previews
            View uploadedPictureView = inflater.inflate(R.layout.preview_uploaded_image, null);
            ImageView retrievedImage = (ImageView) uploadedPictureView.findViewById(R.id.establishmentAddPhoto);
            ImageButton deletePhotoBtn = (ImageButton) uploadedPictureView.findViewById(R.id.deletePhoto);
            deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePhoto(v);
                }
            });
            filePath = data.getData();
            //uriList.add(filePath);
            try {
                Bitmap bitmap = decodeUri(getApplicationContext(), filePath, 200); //compression
/*                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();*/
                /*retrievedImage.setImageBitmap(bitmap);*/
                GlideApp.with(getApplicationContext()) //load compressed image to image views for preview
                        .load(bitmap)
                        .centerCrop()
                        .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                        .into(retrievedImage);

                photoList.addView(uploadedPictureView);
                uris.add(filePath); //add compressed bitmap to array list for firebase storage upload
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    public void saveUrisToDatabase(String id) {
        DatabaseReference imageRef;
        for (int i = 0; i < uris.size(); i++) {
            imageRef = FirebaseDatabase.getInstance().getReference("establishment").child(e.getId()).child("unit").child(id).child("pictures").push();
            imageRef.setValue(uris.get(i).getLastPathSegment());
        }
    }

    public void deletePhoto(View v) {
        int remove = photoList.indexOfChild((View) v.getParent());
        photoList.removeView((View) v.getParent());
        uris.remove(remove);

    }

    public void saveImages(String id) {
        //new ImageUploader(id).execute(id, null, null);
        if(uris.size() > 0){
            StorageReference ref;
            ByteArrayOutputStream baos;
            byte[] data1 = null;
            String uploadId;

            for(int i = 0; i < uris.size(); i++){
                baos = new ByteArrayOutputStream();
                try{
                    Bitmap bitmap = decodeUri(getApplicationContext(),uris.get(i),400);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data1 = baos.toByteArray();
                }catch (IOException e){
                    e.printStackTrace();
                }


                final int cnt = i;
                ref = storageReference.child("units/"+id+"/"+uris.get(i).getLastPathSegment());
                ref.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //        Toast.makeText(getApplicationContext(),"added "+ cnt + " " + uris.get(cnt).getLastPathSegment(),Toast.LENGTH_SHORT).show();

                    }
                });

            }

            saveUrisToDatabase(id);
        }


    }


    private class ImageUploader extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog;
        String unitId;
        public ImageUploader(String unitId){
            this.unitId = unitId;
        }

        @Override
        protected Void doInBackground(String[] objects) {
            if (uris.size() > 0) {
                StorageReference ref;
                ByteArrayOutputStream baos;
                byte[] data1 = null;
                String id = objects[0];
                for (int i = 0; i < uris.size(); i++) {
                    baos = new ByteArrayOutputStream();
                    try {
                        Bitmap bitmap = decodeUri(getApplication(), uris.get(i), 400);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        data1 = baos.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    final int cnt = i;
                    ref = storageReference.child("units/" + id + "/" + uris.get(i).getLastPathSegment());
                    ref.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(getContext(),"added "+ cnt + " " + uris.get(cnt).getLastPathSegment(),Toast.LENGTH_SHORT).show();


                        }
                    });

                }


            }

            return null;
        }

        @Override
        protected void onPreExecute() {
/*
            progressDialog = new ProgressDialog(AddUnit.this);
            progressDialog.setMessage("Uploading");
            progressDialog.show();
*/

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try{
/*
                progressDialog.dismiss();
                progressDialog = null;
*/
                saveUrisToDatabase(this.unitId);

            }catch (Exception e){

            }
        }

    }
}
