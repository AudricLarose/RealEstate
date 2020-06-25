package com.openclassrooms.realestatemanager;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "ShowResult";
    private static final int IMAGE_REQUEST = 102;
    private static final int REQUESTCODEGALLERY = 101;
    private static boolean isItChecked = false;
    private static RealEstate estate;
    private static String dateActuelle, date;
    List<TextInputLayout> editTextContainer = new ArrayList<>();
    private Chip Cecole, Cmagasin, Cmetro, CParc, Cbus;
    private TextInputLayout  ePrixmin, eSurfaceMax, ePrixMax, eSurface, ePiece, eChambre, eSdb;
    private TextView eMarket;
    private Spinner spinerPhoto;
    private Switch switchVendu;
    private Button btnOk, btnReset, btnDate, btnDateSell;
    private List<String> resultsValidatedByUser = new ArrayList<>();
    private List<String> globalResult = new ArrayList<>();
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private Map<String, String> globalResultEstate = new HashMap<>();
    private RelativeLayout relativeLayoutSell;
    private TextView edit_ontheSell;
    private Adaptateur adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private List<String> globaleResulforCompare = new ArrayList<>();
    private List<RealEstate> resultResearchRealEstate = new ArrayList<>();
    private List<String> resultsValidatedByUserForPhotos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.Search);
        resultResearchRealEstate.addAll(listRealEstate);
        actionfleche();
        deployRecyclerView();
        iniatiateAndActivateSwitch();
        editTextContainer = initiateEditText();
        deployeChipes();
        deployButton();
        deployRecyclerView();
        deployRelativeLayout();
        deployeSpinner();
    }

    private void actionfleche() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void deployRecyclerView() {
        adapter = new Adaptateur(resultResearchRealEstate, false, null);
        recyclerView = findViewById(R.id.search_RecyclerSearch);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void deployRelativeLayout() {
        relativeLayoutSell = findViewById(R.id.search_RelativeSellDetails);
    }

    private List<TextInputLayout> initiateEditText() {
        List<TextInputLayout> editTextsContainer = new ArrayList<>();
        eChambre = findViewById(R.id.ChamberMin);
        eMarket = findViewById(R.id.search_edit_onthemarket);
        ePiece = findViewById(R.id.piecemin);
        ePrixmin = findViewById(R.id.minprice);
        ePrixMax = findViewById(R.id.prixmax);
        eSdb = findViewById(R.id.sdbmin);
        eSurface = findViewById(R.id.surfacemin);
        eSurfaceMax = findViewById(R.id.surfacemax);
        edit_ontheSell = findViewById(R.id.search_edit_ontheSell);
        editTextsContainer.add(eChambre);
        editTextsContainer.add(ePrixmin);
        editTextsContainer.add(ePiece);
        editTextsContainer.add(ePrixMax);
        editTextsContainer.add(eSdb);
        editTextsContainer.add(eSurface);
        editTextsContainer.add(eSurfaceMax);
        return editTextsContainer;
    }

    private void saveEntryEditText() {
        globalResultEstate.put("PrixMin", ePrixmin.getEditText().getText().toString());
        globalResultEstate.put("Chambre", eChambre.getEditText().getText().toString());
        globalResultEstate.put("Piece", ePiece.getEditText().getText().toString());
        globalResultEstate.put("PrixMax", ePrixMax.getEditText().getText().toString());
        globalResultEstate.put("SDB", eSdb.getEditText().getText().toString());
        globalResultEstate.put("SurfaceMin", eSurface.getEditText().getText().toString());
        globalResultEstate.put("SurfaceMAx", eSurfaceMax.getEditText().getText().toString());
    }

    private void deployButton() {
        initiateAndActivateDateButton();
        initiateAndActivateOkButton();
        initiateAndActivateDateSellButton();
        initiateAndActivateResetButton();
    }

    private void initiateAndActivateResetButton() {
        btnReset = findViewById(R.id.search_btn_Reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultResearchRealEstate.clear();
                resultResearchRealEstate.addAll(listRealEstate);
                deployRecyclerView();
            }
        });
    }

    private void initiateAndActivateOkButton() {
        btnOk = findViewById(R.id.search_btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEntryEditText();
                addGlobalResult();
                compareListAll();

            }
        });
    }

    private void iniatiateAndActivateSwitch() {
        switchVendu = findViewById(R.id.search_switch_vendu);
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

    private void initiateAndActivateDateButton() {
        btnDate = findViewById(R.id.search_btn_date);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Date Picker2");
            }
        });
    }

    private void initiateAndActivateDateSellButton() {
        btnDateSell = findViewById(R.id.search_btn_date_Sell);
        btnDateSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Date Picker1");
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
        date = Utils.getDateFormat(SearchActivity.this, c);
        FragmentManager fragmanager = getSupportFragmentManager();
        if (fragmanager.findFragmentByTag("Date Picker1") != null) {
            edit_ontheSell.setText(date);
        }
        if (fragmanager.findFragmentByTag("Date Picker2") != null) {
            eMarket.setText(date);
        }
    }


    private void deployeChipes() {
        final List<Chip> ChipesContainer = initiateChipes();
        resultsValidatedByUser = activateChip(ChipesContainer);
    }

    private void deployeSpinner() {
        spinerPhoto= findViewById(R.id.spinnerphotonbr);
        spinerPhoto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                resultsValidatedByUserForPhotos.add(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private List<Chip> initiateChipes() {
        final List<Chip> ChipesContainer = new ArrayList<>();
        Cecole = findViewById(R.id.search_check_ecole);
        Cmagasin = findViewById(R.id.search_check_magasin);
        CParc = findViewById(R.id.search_check_parc);
        Cbus = findViewById(R.id.search_check_bus);
        Cmetro = findViewById(R.id.search_check_metro);
        ChipesContainer.add(Cecole);
        ChipesContainer.add(Cmagasin);
        ChipesContainer.add(CParc);
        ChipesContainer.add(Cbus);
        return ChipesContainer;
    }

    private List<String> activateChip(final List<Chip> ChipesContainer) {
        final List<String> resultList = new ArrayList<>();
        for (int i = 0; i < ChipesContainer.size(); i++) {
            final int finalI = i;
            ChipesContainer.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ChipesContainer.get(finalI).isChecked()) {
                        resultList.add(String.valueOf(ChipesContainer.get(finalI).getText()));
                    } else if (!ChipesContainer.get(finalI).isChecked()) {
                        resultList.remove(String.valueOf(ChipesContainer.get(finalI).getText()));
                    }
                }
            });
        }
        return resultList;
    }


    private void addGlobalResult() {
        globaleResulforCompare.addAll(globalResult);
        globaleResulforCompare.addAll(resultsValidatedByUser);
    }


    private void compareListAll() {
        for (int i = 0; i < listRealEstate.size(); i++) {
            deleteSDBIfResultMatch(i);
            deletePieceIfResultMatch(i);
            deleteChambreIfResultMatch(i);
            deletePriceMinIfResultMatch(i);
            deletePriceMaxIfResultMatch(i);
            deleteSurfaceMinIfResultMatch(i);
            deleteSurfaceMaxIfResultMatch(i);
            deleteNumberPhotosByIfResultMatch(i);
            deleteNearByIfResultMatch(i);
            deleteDateEntryIfResultMatch(i);
            deleteDateSelledIfResultMatch(i);
        }
        deployRecyclerView();
    }

    private void deleteDateEntryIfResultMatch(int i) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date dateSniffed = sdf.parse(eMarket.getText().toString().replace("/", "-"));
            Date dateRealEstate = sdf.parse(listRealEstate.get(i).getMarket().replace("/", "-"));
            if ((dateRealEstate.compareTo(dateSniffed) < 0)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void deleteDateSelledIfResultMatch(int i) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date dateSniffed = sdf.parse(edit_ontheSell.getText().toString().replace("/", "-"));
            Date dateRealEstate = sdf.parse(listRealEstate.get(i).getSelled().replace("/", "-"));
            if ((dateRealEstate.compareTo(dateSniffed) < 0)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void deleteNearByIfResultMatch(int i) {
        if (resultsValidatedByUser != null && resultsValidatedByUser.size() != 0 && !listRealEstate.isEmpty()) {
            if (listRealEstate.get(i).getNearby().contains(resultsValidatedByUser)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteNumberPhotosByIfResultMatch(int i) {
        for (int j = 0; j < resultsValidatedByUserForPhotos.size(); j++) {
            if (resultsValidatedByUserForPhotos.get(j) != null && !listRealEstate.isEmpty()) {
                if (listRealEstate.get(i).getPhotosReal().size() < Integer.valueOf(resultsValidatedByUserForPhotos.get(j))) {
                    resultResearchRealEstate.remove(listRealEstate.get(i));
                }
            }
        }
    }

    private void deletePriceMinIfResultMatch(int i) {
        if ((globalResultEstate.get("PrixMax") != null && !globalResultEstate.get("PrixMax").isEmpty())) {
            if ((Integer.valueOf(globalResultEstate.get("PrixMax")) < Integer.valueOf(listRealEstate.get(i).getPrix()))) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }
    private void deletePriceMaxIfResultMatch(int i) {
        if ((globalResultEstate.get("PrixMin") != null && !globalResultEstate.get("PrixMin").isEmpty())) {
            if ((Integer.valueOf(globalResultEstate.get("PrixMin")) > Integer.valueOf(listRealEstate.get(i).getPrix()))) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteSurfaceMaxIfResultMatch(int i) {
        if ((globalResultEstate.get("SurfaceMax") != null && !globalResultEstate.get("SurfaceMax").isEmpty())) {
            if ((Integer.valueOf(globalResultEstate.get("SurfaceMax")) < Integer.valueOf(listRealEstate.get(i).getSurface()))) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteSurfaceMinIfResultMatch(int i) {
        if ((globalResultEstate.get("SurfaceMin") != null && !globalResultEstate.get("SurfaceMin").isEmpty())) {
            if ((Integer.valueOf(globalResultEstate.get("SurfaceMin")) > Integer.valueOf(listRealEstate.get(i).getSurface()))) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteChambreIfResultMatch(int i) {
        if (globalResultEstate.get("Chambre") != null && !globalResultEstate.get("Chambre").isEmpty()) {
            if ((Integer.valueOf(globalResultEstate.get("Chambre")) <= Integer.valueOf(listRealEstate.get(i).getSurface()))) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deletePieceIfResultMatch(int i) {
        if (globalResultEstate.get("Piece") != null && !globalResultEstate.get("Piece").isEmpty()) {
            if (Integer.valueOf(globalResultEstate.get("Piece")) <= Integer.valueOf(listRealEstate.get(i).getPiece())) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteSDBIfResultMatch(int i) {
        if (globalResultEstate.get("SDB") != null && !globalResultEstate.get("SDB").isEmpty()) {
            if (Integer.valueOf(globalResultEstate.get("SDB")) <= Integer.valueOf(listRealEstate.get(i).getSdb())) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }
}
