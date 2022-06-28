

import android.graphics.Bitmap;


import com.mlkit.sample.camera.FrameMetadata;
import com.mlkit.sample.views.overlay.GraphicOverlay;

import java.nio.ByteBuffer;

public interface ImageTransactor {

    void process(ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay);

    void process(Bitmap bitmap, GraphicOverlay graphicOverlay);

    void stop();

    boolean isFaceDetection();
}