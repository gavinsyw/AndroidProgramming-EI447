package com.example.qrscan;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class QRDecoder extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {
    private SurfaceView surfaceView = null;
    private SurfaceHolder surfaceHolder = null;
    private CameraDevice cameraDevice = null;
    private CameraCaptureSession cameraCaptureSession = null;
    ImageReader imageReader = null;
    ImageView imageView;

    private Handler childHandler;

    // const to control the orientation of the taken figure
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private void initSurfaceView() {
        surfaceView = this.findViewById(R.id.preview_view);
        imageView = this.findViewById(R.id.preview_img);
        surfaceView.setOnClickListener(this);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(QRDecoder.this);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.decoder);

        initSurfaceView();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(getString(R.string.app_name), "SurfaceHolder.Callback: Surface Created.");
        initCameraPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(getString(R.string.app_name), "SurfaceHolder.Callback: Surface Changed.");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(getString(R.string.app_name), "SurfaceHolder.Callback: Surface Destroyed.");
        if (cameraDevice != null) {
            cameraDevice.close();
            QRDecoder.this.cameraDevice = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initCameraPreview() {
        Log.v(getString(R.string.app_name), "Initialize Camera Preview.");
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        Handler mainHandler = new Handler(getMainLooper());
        String cameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;
        imageReader = ImageReader.newInstance(surfaceView.getWidth(), surfaceView.getHeight(), ImageFormat.JPEG, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imgReader) {
                if (cameraDevice != null)
                    cameraDevice.close();
                surfaceView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

                // load image into imageView and display
                Image image = imgReader.acquireNextImage();

                ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    Log.v(getString(R.string.app_name), "Bitmap: "+bitmap.toString());
                    imageView.setImageBitmap(bitmap);

                    // scan QR code in the bitmap
                    // transfer the bitmap into binary one
                    int[] array = new int[bitmap.getWidth()*bitmap.getHeight()];
                    bitmap.getPixels(array, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                    LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), array);
                    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                    Log.v(getString(R.string.app_name), binaryBitmap.toString());

                    Reader reader = new QRCodeReader();
                    // read QR code
                    try {
                        Result result = reader.decode(binaryBitmap);
                        String text = result.getText(); // equivalent to result.toString()

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("result", text);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }

            }
        }, mainHandler);
        CameraManager cameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraManager.openCamera(cameraID, DeviceStateCallBack, mainHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback DeviceStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.v(getString(R.string.app_name), "DeviceStateCallBack: Camera Opened.");
            cameraDevice = camera;
            takePreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.v(getString(R.string.app_name), "DeviceStateCallBack: Camera Disconnected.");
            if (cameraDevice != null) {
                cameraDevice.close();
                QRDecoder.this.cameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.v(getString(R.string.app_name), "DeviceStateCallBack: Return Camera Error.");
            Toast.makeText(QRDecoder.this, "Camera open failed", Toast.LENGTH_SHORT).show();
        }
    };

    private void takePreview() {
        try {
            final CaptureRequest.Builder previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());

            cameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (cameraDevice == null)
                        return;
                    cameraCaptureSession = session;
                    try {
                        // AF mode
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // auto flash light
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        // display preview
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        cameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler);
                    } catch (CameraAccessException e) {
                        Log.e(getString(R.string.app_name), "Camera Access Error.");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(QRDecoder.this, "Camera Preview Failed.", Toast.LENGTH_SHORT).show();
                }
            }, childHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        takePicture();
    }

    private void takePicture() {
        if (cameraDevice == null)
            return;
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageReader.getSurface());
            // AF mode
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // auto flash light
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            // get the orientation of the phone
            int rotation = getWindowManager().getDefaultDisplay().getRotation();


            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            CaptureRequest captureRequest = captureRequestBuilder.build();
            cameraCaptureSession.capture(captureRequest, null, childHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
