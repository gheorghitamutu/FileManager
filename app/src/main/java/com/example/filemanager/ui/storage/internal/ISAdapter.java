package com.example.filemanager.ui.storage.internal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.MainActivity;
import com.example.filemanager.Manager;
import com.example.filemanager.R;
import com.example.filemanager.ui.storage.options.ODialog;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

public class ISAdapter extends RecyclerView.Adapter<ISAdapter.ViewHolder> {
    private List<ISModel> models;

    ISAdapter(List<ISModel> models) {
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mediaView =
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.media_item,
                        parent,
                        false);

        return new ViewHolder(mediaView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ISModel model = models.get(position);
        holder.tvFilename.setText(model.getFilename());

        File file = new File(model.getFilepath());
        if (file.isDirectory()) {
            holder.ivItemIcon.setImageResource(R.drawable.ic_folder);
        } else {
            String ext = model.getFilename().substring(model.getFilename().lastIndexOf(".") + 1);
            switch (ext) {
                case "png":
                case "jpeg":
                case "jpg":
                    final int thumbnailSize = 64;

                    Bitmap imageThumbnail =
                            ThumbnailUtils.extractThumbnail(
                                    BitmapFactory.decodeFile(model.getFilepath()),
                                    thumbnailSize,
                                    thumbnailSize);

                    holder.ivItemIcon.setImageBitmap(imageThumbnail);
                    break;
                case "pdf":
                    holder.ivItemIcon.setImageResource(R.drawable.ic_pdf_file);
                    break;
                case "mp3":
                    holder.ivItemIcon.setImageResource(R.drawable.ic_audio_file);
                    break;
                case "txt":
                    holder.ivItemIcon.setImageResource(R.drawable.ic_text_file);
                    break;
                case "zip":
                case "rar":
                    holder.ivItemIcon.setImageResource(R.drawable.ic_zip_folder);
                    break;
                case "html":
                case "htm":
                case "xml":
                    holder.ivItemIcon.setImageResource(R.drawable.ic_html_file);
                    break;
                case "mp4":
                case "3gp":
                case "wmv":
                case "avi":
                case "mkv":
                    Bitmap videoThumbnail =
                            ThumbnailUtils.createVideoThumbnail(
                                    model.getFilepath(),
                                    MediaStore.Video.Thumbnails.MICRO_KIND);

                    holder.ivItemIcon.setImageBitmap(videoThumbnail);
                    break;
                case "apk":
                    holder.ivItemIcon.setImageResource(R.drawable.ic_apk);
                    break;
                default:
                    holder.ivItemIcon.setImageResource(R.drawable.ic_un_supported_file);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFilename;
        ImageView ivItemIcon;
        OnLongClickListener onLongClickListener;
        OnClickListener onClickListener;

        ViewHolder(View view) {
            super(view);
            tvFilename = view.findViewById(R.id.filename);
            ivItemIcon = view.findViewById(R.id.item_icon);
            onLongClickListener = new OnLongClickListener();
            view.setOnLongClickListener(onLongClickListener);
            onClickListener = new OnClickListener();
            view.setOnClickListener(onClickListener);
        }
    }

    public static class OnLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            TextView tvFilename = v.findViewById(R.id.filename);
            String currentFilename = tvFilename.getText().toString();
            Manager.setCurrentFile(currentFilename);

            //v.setBackgroundColor(
            //        ContextCompat.getColor(
            //                Objects.requireNonNull(v.getContext()), R.color.colorAccent));

            FragmentManager manager = null;
            try {
                MainActivity ma = ((MainActivity) (Manager.getActivity()));
                manager = Objects.requireNonNull(ma).getSupportFragmentManager();
            } catch (ClassNotFoundException |
                    NoSuchMethodException |
                    InvocationTargetException |
                    IllegalAccessException |
                    NoSuchFieldException e) {
                e.printStackTrace();
            }

            Fragment frag = Objects.requireNonNull(manager).findFragmentByTag("fragment_options_dialog");
            if (frag != null) {
                manager.beginTransaction().remove(frag).commit();
            }

            ODialog od = ODialog.getInstance();
            od.show(manager, "fragment_options_dialog");

            //v.setBackgroundColor(
            //        ContextCompat.getColor(
            //                Objects.requireNonNull(v.getContext()), R.color.default_color));

            return true;
        }
    }

    public static class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            TextView tvFilename = v.findViewById(R.id.filename);
            String currentFilename = tvFilename.getText().toString();

            Manager.setCurrentFile(currentFilename);
            boolean treeLevelAdded = Manager.addPathLevel();
            if (!treeLevelAdded) { // filename should be the name of a child directory
                return;
            }

            MainActivity ma = null;
            try {
                ma = (MainActivity) Manager.getActivity();
            } catch (ClassNotFoundException |
                    NoSuchMethodException |
                    InvocationTargetException |
                    IllegalAccessException |
                    NoSuchFieldException e) {
                e.printStackTrace();
            }

            Manager.refreshFragment(Objects.requireNonNull(ma), MainActivity.getCurrentNavigationFragment());
        }
    }
}
