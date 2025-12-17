package customview.imagepicker;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;


/**
 * Created by Mickael on 10/10/2016.
 */

public class ImagePickerManager extends PickerManager {

    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;





    public ImagePickerManager(Activity activity) {
        super(activity);

    }

    protected void sendToExternalApp( ){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        activity.startActivityForResult(Intent.createChooser(intent, "Select avatar..."), REQUEST_CODE_SELECT_IMAGE);

        /*ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        // Handle the selected URI (e.g., display the image/video)
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                }
        );


        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build();

        pickMediaLauncher.launch(request);*/



    }


    @Override
    public void setUri(Uri uri)
    {
        mProcessingPhotoUri = uri;
    }

}
