package com.openclassrooms.realestatemanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.chip.Chip;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.EstateViewModel;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.modele.MediaImage;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddInformationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "ShowResult";
    private static final int IMAGE_REQUEST = 102;
    private static final int REQUESTCODEGALLERY = 101;
    private static boolean isItChecked = false;
    private static RealEstate estate;
    private static String dateActuelle;
    List<MaterialTextField> editTextContainer = new ArrayList<>();
    private Chip Cecole, Cmagasin, Cmetro, CParc, Cbus;
    private MaterialTextField eAdress, ePostal, eVille, ePrix, eSurface, ePiece, eChambre, eSdb, eDescr;
    private TextView eMarket, edit_ontheSell;
    private Spinner spinnerChoicce, spinnerNom;
    private Switch switchVendu;
    private Button btnOk, btnCancel, btnLocalPhoto, btnCameraPhoto, btnDate, btnDateSell, btnModify;
    private RelativeLayout relativeLayoutSell;
    private List<String> resultsValidatedByUser = new ArrayList<>();
    private Map<String, String> globalResultEstate = new HashMap<>();
    private List<String> globalResult = new ArrayList<>();
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private List<MediaImage> listRealEstateImage = serviceEstate.getRealEstateImageList();
    private Uri imageURL;
    private Boolean isEstateExist;
    private AdaptateurImage adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Double lattitudeRealEState;
    private Double longitudeRealEState;
    private String url, date;
    private boolean emptyField=true;
    private EstateViewModel estateViewModel;
    private List<String> listPhotoRealistetate = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_information);
        initiateEditText();
        deployeChipes();
        deployeButton();
        deployRelativeLayout();
        deploySpinner();
        iniatiateAndActivateSwitch();
        deployRecyclerView();
        deployModificationAction();
        actionfleche();
    }

    private void deployRelativeLayout() {
        relativeLayoutSell = findViewById(R.id.RelativeSell);
    }

    private void deployModificationAction() {
        isEstateExist = verifyEstateExist();
        if (isEstateExist) {
            giveEstat2ViewsIfNotNull();
            replaceOkButtonByModifyButton();
        }
    }

    private Boolean verifyEstateExist() {
        Intent intent = this.getIntent();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            estate = (RealEstate) extra.getSerializable("RealEstate");
            return true;
        }
        return false;
    }


    private void giveEstat2ViewsIfNotNull() {
        editTextCase();
        spinnerCaseType();
        spinnerCaseAgent();
        ChipCase();
    }

    private void ChipCase() {
        List<Chip> checkForPosition = initiateChipes();
        for (int i = 0; i < checkForPosition.size(); i++) {
            if (checkForPosition.get(i).getText().equals(estate.getNearby().get(0))) {
                checkForPosition.get(i).setChecked(true);
            }
        }

    }

    private void editTextCase() {
        eAdress.getEditText().setText(estate.getAdresse());
        eChambre.getEditText().setText(estate.getChambre());
        eDescr.getEditText().setText(estate.getDescription());
        eMarket.setText(estate.getAdresse());
        ePiece.getEditText().setText(estate.getPiece());
        ePostal.getEditText().setText(estate.getPostal());
        ePrix.getEditText().setText(estate.getPrix());
        eSdb.getEditText().setText(estate.getSdb());
        eSurface.getEditText().setText(estate.getSurface());
        eVille.getEditText().setText(estate.getTown());
    }

    private void replaceOkButtonByModifyButton() {
        btnOk.setVisibility(View.GONE);
        btnModify.setVisibility(View.VISIBLE);
    }

    private void actionfleche() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void iniatiateAndActivateSwitch() {
        switchVendu = findViewById(R.id.switch_vendu);
        switchVendu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    relativeLayoutSell.setVisibility(View.VISIBLE);
                } else {
                    relativeLayoutSell.setVisibility(View.GONE);
                }
            }
        });
    }

    private void deploySpinner() {
        initiateSpinner();
        activeSpinner();
    }

    private void initiateSpinner() {
        spinnerChoicce = findViewById(R.id.type_bien);
        spinnerNom = findViewById(R.id.nom_agent);
    }

    private void activeSpinner() {
        spinnerChoicce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                globalResultEstate.put("TypeEstate", adapterView.getSelectedItem().toString());
                globalResult.add(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerNom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                globalResultEstate.put("nameEstate", adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void spinnerCaseType() {
        ArrayList<String> listForPosition = new ArrayList<String>();
        listForPosition.add("Appartement");
        listForPosition.add("Maison");
        listForPosition.add("Grenier");
        listForPosition.add("Parking");
        listForPosition.add("VIlla");
        for (int i = 0; i < listForPosition.size(); i++) {
            if (estate.getType().equals(listForPosition.get(i))) {
                spinnerChoicce.setSelection(i);
            }
        }
    }

    private void spinnerCaseAgent() {
        ArrayList<String> listForPosition = new ArrayList<String>();
        listForPosition.add("Mickeal Keal");
        listForPosition.add("Jackson Myrtis");
        listForPosition.add("Gilles De Saintpatus");
        listForPosition.add("Audric Richards");
        for (int i = 0; i < listForPosition.size(); i++) {
            if (estate.getNomAgent().equals(listForPosition.get(i))) {
                spinnerNom.setSelection(i);
            }
        }
    }


    private void initiateEditText() {
        eAdress = findViewById(R.id.edit_adresse);
        eChambre = findViewById(R.id.edit_chambre);
        eDescr = findViewById(R.id.edit_descript);
        eMarket = findViewById(R.id.edit_onthemarket);
        ePiece = findViewById(R.id.edit_piece);
        ePostal = findViewById(R.id.edit_codepostal);
        ePrix = findViewById(R.id.edit_prix);
        eSdb = findViewById(R.id.edit_sdb);
        eSurface = findViewById(R.id.edit_surface);
        eVille = findViewById(R.id.edit_ville);
        edit_ontheSell = findViewById(R.id.edit_ontheSell);
    }

    private void saveEntryEditText() {
        globalResultEstate.put("Adresse", eAdress.getEditText().getText().toString());
        globalResultEstate.put("Chambre", eChambre.getEditText().getText().toString());
        globalResultEstate.put("Description", eDescr.getEditText().getText().toString());
        globalResultEstate.put("Postal", ePostal.getEditText().getText().toString());
        globalResultEstate.put("Piece", ePiece.getEditText().getText().toString());
        globalResultEstate.put("Prix", ePrix.getEditText().getText().toString());
        globalResultEstate.put("SDB", eSdb.getEditText().getText().toString());
        globalResultEstate.put("Surface", eSurface.getEditText().getText().toString());
        globalResultEstate.put("Ville", eVille.getEditText().getText().toString());
        globalResultEstate.put("dateSell", edit_ontheSell.getText().toString());
    }

    private void deployeChipes() {
        final List<Chip> ChipesContainer = initiateChipes();
        resultsValidatedByUser = activateChip(ChipesContainer);
    }

    private List<Chip> initiateChipes() {
        final List<Chip> ChipesContainer = new ArrayList<>();
        Cecole = findViewById(R.id.check_ecole);
        Cmagasin = findViewById(R.id.check_magasin);
        CParc = findViewById(R.id.check_parc);
        Cbus = findViewById(R.id.check_bus);
        Cmetro = findViewById(R.id.check_metro);
        ChipesContainer.add(Cecole);
        ChipesContainer.add(Cmagasin);
        ChipesContainer.add(CParc);
        ChipesContainer.add(Cbus);
        ChipesContainer.add(Cmetro);
        return ChipesContainer;
    }

    private List<String> activateChip(final List<Chip> ChipesContainer) {
        final List<String> resultList = new ArrayList<>();
        for (int i = 0; i < ChipesContainer.size(); i++) {
            final int finalI = i;
            ChipesContainer.get(i).setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    if (ChipesContainer.get(finalI).isChecked()) {
                        globalResultEstate.put(ChipesContainer.get(finalI).getText().toString(), String.valueOf(ChipesContainer.get(finalI).getText()));
                        resultList.add(String.valueOf(ChipesContainer.get(finalI).getText()));
                        showResult(resultsValidatedByUser);
                    } else if (!ChipesContainer.get(finalI).isChecked()) {
                        globalResultEstate.remove(ChipesContainer.get(finalI).getText().toString(), String.valueOf(ChipesContainer.get(finalI).getText()));
                        resultList.remove(String.valueOf(ChipesContainer.get(finalI).getText()));
                        showResult(resultsValidatedByUser);
                    }
                }
            });
        }
        return resultList;
    }

    private void showResult(List<String> resultsValidatedByUser) {
        Log.d(TAG, "showResult: " + resultsValidatedByUser);
    }

    private void deployeButton() {
        initiateAndActivateOkButton();
        initiateAndActivateCancelButton();
        initiateAndActivateLocalButton();
        initiateAndActivateCameraButton();
        initiateAndActivateDateButton();
        initiateAndActivateDateSellButton();
        initiateAndActivateModifyButton();
    }

    private void initiateAndActivateDateSellButton() {
        btnDateSell = findViewById(R.id.btn_date_Sell);
        btnDateSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Date Picker1");
            }
        });
    }

    private void initiateAndActivateModifyButton() {
        btnModify = findViewById(R.id.btn_Modifier);
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealEstate estateForModifier = modifyEstate();
                redirectToDetailsActivity(estateForModifier);
            }
        });
    }


    private RealEstate modifyEstate() {
        estate.setAdresse(globalResult.get(0));
        estateViewModel = new EstateViewModel(getApplication());
        estateViewModel.UploadThisData(estate);
        return null;
    }

    private void redirectToDetailsActivity(RealEstate estateForModifier) {
        finish();
    }

    private void initiateAndActivateDateButton() {
        btnDate = findViewById(R.id.btn_date);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Date Picker2");
            }
        });
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        dateActuelle = DateFormat.getDateInstance().format(c.getTime());
        date = Utils.getDateFormat(AddInformationActivity.this, c);
        FragmentManager fragmanager = getSupportFragmentManager();
        if (fragmanager.findFragmentByTag("Date Picker1") != null) {
            edit_ontheSell.setText(date);
        }
        if (fragmanager.findFragmentByTag("Date Picker2") != null) {
            eMarket.setText(date);
        }
    }


    private void initiateAndActivateOkButton() {
        btnOk = findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEntryEditText();
                for (int i = 0; i < globalResultEstate.size(); i++) {
                    if (globalResultEstate.get(i) != null) {
                        if (globalResultEstate.get(i).isEmpty()) {
                            emptyField = true;
                        }
                    } else {
                        emptyField = true;
                    }
                }
                if (!emptyField){
                    showGlobalResult();
                    finish();
                } else{
                    Toast.makeText(AddInformationActivity.this, "There is an Empty Field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initiateAndActivateCancelButton() {
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void findCoordonnée() {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(globalResult.get(2), 1);
            if (addressList != null && addressList.size() > 0) {
                lattitudeRealEState = addressList.get(0).getLatitude();
                longitudeRealEState = addressList.get(0).getLongitude();
                LatLng latLng = new LatLng(lattitudeRealEState, longitudeRealEState);
                url = Utils.linkBuilder(latLng);

            }
        } catch (IOException e) {
            lattitudeRealEState = 0.0;
            longitudeRealEState = 0.0;
            url = "";
        }
    }

    private void showGlobalResult() {
        RealEstate estate = new RealEstate(String.valueOf(true), String.valueOf(isItChecked), globalResultEstate.get("TypeEstate"), globalResultEstate.get("nameEstate"), resultsValidatedByUser, globalResultEstate.get("Adresse"),
                globalResultEstate.get("Chambre"), globalResultEstate.get("Description"), date, globalResultEstate.get("Postal"), globalResultEstate.get("Piece")
                , globalResultEstate.get("Prix"), globalResultEstate.get("SDB"), globalResultEstate.get("Surface"), globalResultEstate.get("Ville"), globalResultEstate.get("dateSell"), lattitudeRealEState, longitudeRealEState, url, listPhotoRealistetate);
        estateViewModel = new EstateViewModel(getApplication());
        estateViewModel.InsertThisData(estate);
    }


    private void initiateAndActivateCameraButton() {
        btnLocalPhoto = findViewById(R.id.btn_photo_on_phone);
        btnLocalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });


    }

    private void initiateAndActivateLocalButton() {
        btnCameraPhoto = findViewById(R.id.btn_take_photo);
        btnCameraPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            listPhotoRealistetate.add(selectedImage.toString());
        }
        if ((requestCode == REQUESTCODEGALLERY) && (resultCode == RESULT_OK)) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    listPhotoRealistetate.add(picturePath);
                    cursor.close();
                }
            }
        }
    }

    private void deployRecyclerView() {
        adapter = new AdaptateurImage(listPhotoRealistetate);
        recyclerView = findViewById(R.id.Recyclerviewphotos);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(AddInformationActivity.this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        deployRecyclerView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        deployRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deployRecyclerView();
    }
}
