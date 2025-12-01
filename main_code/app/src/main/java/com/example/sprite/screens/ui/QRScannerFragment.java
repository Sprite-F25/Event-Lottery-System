package com.example.sprite.screens.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Event;
import com.example.sprite.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Collections;

/**
 * Fragment that scans an event QR code and navigates to the event details page.
 *
 * Assumes the QR encodes the event's eventId as plain text.
 */
public class QRScannerFragment extends Fragment {

    private DecoratedBarcodeView barcodeView;
    private DatabaseService databaseService;

    // Runtime permission launcher for CAMERA
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the permission launcher once
        cameraPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                // Start / resume camera once permission is granted
                                if (barcodeView != null) {
                                    barcodeView.resume();
                                }
                            } else {
                                Toast.makeText(requireContext(),
                                        "Camera permission is required to scan QR codes",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);

        barcodeView = view.findViewById(R.id.barcode_scanner);
        Button scanButton = view.findViewById(R.id.scan_button);

        databaseService = new DatabaseService();

        // Configure ZXing to only look for QR codes
        barcodeView.getBarcodeView().setDecoderFactory(
                new DefaultDecoderFactory(Collections.singletonList(BarcodeFormat.QR_CODE))
        );

        scanButton.setOnClickListener(v -> startScan());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // As soon as the fragment is visible, make sure we have camera permission
        checkCameraPermissionAndStartPreview();
    }

    /**
     * Checks camera permission; if granted, starts the preview.
     * If not, launches the permission dialog.
     */
    private void checkCameraPermissionAndStartPreview() {
        if (barcodeView == null || !isAdded()) {
            return;
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Starts a single-scan operation. Assumes the camera preview is already running.
     * On first successful scan, we look up the event and navigate to its details fragment.
     */
    private void startScan() {
        if (barcodeView == null) return;

        Toast.makeText(requireContext(),
                "Point the camera at the QR code",
                Toast.LENGTH_SHORT).show();

        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result == null || result.getText() == null) {
                    Toast.makeText(requireContext(),
                            "No QR data found", Toast.LENGTH_SHORT).show();
                    return;
                }

                String eventId = result.getText().trim();
                fetchEventAndNavigate(eventId);
            }
        });
    }

    /**
     * Fetches the event from Firestore using the scanned eventId,
     * then navigates to the regular EventDetailsFragment.
     */
    private void fetchEventAndNavigate(String eventId) {
        databaseService.getEvent(eventId, (Task<DocumentSnapshot> task) -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(requireContext(),
                        "Could not load event for this QR code",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Event event = task.getResult().toObject(Event.class);
            if (event == null) {
                Toast.makeText(requireContext(),
                        "Event not found", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isAdded()) return;

            NavController navController = Navigation.findNavController(
                    requireActivity(), R.id.nav_host_fragment_content_main);

            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedEvent", event);
            navController.navigate(R.id.fragment_event_details, bundle);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Whenever we come back to this fragment, re-check permission & preview
        checkCameraPermissionAndStartPreview();
    }

    @Override
    public void onPause() {
        if (barcodeView != null) {
            barcodeView.pause();
        }
        super.onPause();
    }
}
