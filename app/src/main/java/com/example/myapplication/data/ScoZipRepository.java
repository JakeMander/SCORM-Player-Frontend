package com.example.myapplication.data;

import android.content.Context;
import android.icu.util.Output;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ScoItemCardBinding;
import com.example.myapplication.ui.scoItems.fragments.ScoItemAdapter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/*
    Repository to handle disk read/write access to SCO Zips
    downloaded from the backend. This repo will handle all
    reading and writing of the necessary zip files to and from
    the hard disk in order to both read and write to the
    session.
 */
public class ScoZipRepository {
    private static volatile ScoZipRepository instance;
    private Executor mFileExecutor;

    public static ScoZipRepository getInstance(Executor fileExecutor) {
        if (instance == null) {
            instance = new ScoZipRepository(fileExecutor);
        }
        return instance;
    }

    private  ScoZipRepository(Executor fileExecutor) {
        mFileExecutor = fileExecutor;
    }

    /**
     *  Method accepts the downloaded bytes, saves these as a ZIP
     *  and then unpackages this as the SCORM files into the app's
     *  local android storage. Binds this to a key consisting of
     *  the SCO Id to assist with identifying which SCO's have
     *  been downloaded. Runs on a new thread so as not to
     *  block the UI.
     * @param context   The context from the currently loaded activity hosting
     *                  the repo. Use to manage current app directory.
     *
     * @param zipBytes  The bytes downloaded from the API constituting the
     *                  downloaded SCORM collection.
     *
     * @param scoId     The ID representing the downloaded SCORM collection.
     *                  Use this as the key to bind to the downloaded
     *                  SCORM data to identify if the SCORM package exists
     *                  locally on disk.
     * @throws IOException
     */
    public void saveZip(Context context, byte[] zipBytes, String scoId,
                        ScoItemCardBinding scoRecord)
            throws IOException {

        //  Use the Android Executor to set up the writing of the zip file
        //  to occur on it's own thread so as not to block the UI.
        mFileExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ByteArrayInputStream byteReader = new ByteArrayInputStream(zipBytes);
                ZipInputStream zipStream = new ZipInputStream(byteReader);
                try {

                    File appRootExtDir = context.getExternalFilesDir(null);
                    String zipDataRootURI = appRootExtDir.getPath() + '/' + scoId;

                    if (!isExternalStorageWritable() || !isExternalStorageReadable()) {
                        String zip_rw_error_msg = context.getString(
                                R.string.sco_unzip_read_write_fail);

                        Log.wtf(null, zip_rw_error_msg);
                        Toast.makeText(context, zip_rw_error_msg, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //  zipDataRootURI represents an individual, unzipped SCO Collection. This
                    //  is stored in the apps persistent storage in the external storage.
                    File zipDataRoot = new File(zipDataRootURI);

                    //  Check to see if the Zip has already been downloaded. If
                    //  it hasn't, create the root directory that will store
                    //  the unzipped data. If it has, something has gone wrong
                    //  (i.e. we shouldn't be downloading if it already exists).
                    //  for now, just log the error so we can investigate further
                    //  and reset the directories if needed.
                    if (!zipDataRoot.exists()) {
                        Log.i(null, "Zip Data Directory Created For " + scoId
                                + " at directory: " + zipDataRootURI);
                        boolean made = zipDataRoot.mkdirs();
                        writeZipToDisk(zipStream, context, zipDataRoot);
                        scoRecord.getScoRecord().setIsScoDownloaded(context);
                    }
                    else {
                        Log.wtf(null, "An Error Has Occurred When Unzipping A SCORM Collection:" +
                                " Directory Already Exists: " + zipDataRootURI);
                    }
                }

                catch (Exception e) {
                    Log.wtf(null, "Failed To Write Zip File To App Memory");
                }

                finally {
                    try {
                        zipStream.close();
                    } catch (IOException e) {
                        String errorMessage = context.getString(
                                R.string.sco_unzip_stream_close_fail);
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     *  Write each file contained within the provided Zip Input Stream
     *  to the disk.
     * @param zipInputStream The Zip File Bytes To Unzip and wite to
     *                       disk.
     */
    private void writeZipToDisk (ZipInputStream zipInputStream, Context context, File storageRoot) {
        try {
            ZipEntry entry = zipInputStream.getNextEntry();

            //  For each embedded element in the file write it to the local
            //  disk.
            while (entry != null) {
                Log.i(null, "New Zip Entry Found: " + entry.getName());

                //  If the entry is a directory, we need to build the directory so
                //  files can be nested beneath it...
                if (entry.isDirectory()) {
                    File newDirectoryPth = new File(storageRoot + "//" + entry.getName());

                    //  Ensure our new directory isn't already available/duplicated.
                    if (!newDirectoryPth.exists()) {
                        newDirectoryPth.mkdir();
                    }
                }

                //  ...Otherwise, write the file to disk.
                else {
                    FileOutputStream fileStream = new FileOutputStream(storageRoot + "//" +
                            entry.getName());

                    int writeLength = 0;
                    byte[] writeBuffer = new byte[4096];

                    writeLength = zipInputStream.read(writeBuffer);

                    while (writeLength > 0) {
                        fileStream.write(writeBuffer, 0, writeLength);
                        writeLength = zipInputStream.read(writeBuffer);
                    }

                    // Release the newly created file stream resources for the
                    // specific zip entry.
                    fileStream.close();
                    zipInputStream.closeEntry();
                }
                entry = zipInputStream.getNextEntry();
            }
        }
        catch (IOException e) {
            Log.wtf(null, "Could Not Write ZIP Entry: " + e);
        }
    }

    // Checks if a volume containing external storage is available
    // for read and write.
    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Checks if a volume containing external storage is available to at least read.
    private boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    /**
     *  Method that recursively iterates over each file an directory within the
     *  unzipped sco's target directory and deletes each child.
     * @param context   The current fragment context.
     * @param scoId     The ID of the sco to remove from disk.
     * @param scoRecord The SCO Record card binding which will have it's UI updated
     *                  regarding the removal of the SCO from disk.
     * @throws IOException
     */
    public void deleteZip(Context context, String scoId,
                          ScoItemCardBinding scoRecord) throws IOException {
        mFileExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try
                {
                    File scoRootDirectory = new File(context.getExternalFilesDir(null).getPath() +
                            "/" + scoId);

                    deleteFilesRecursively(scoRootDirectory);
                    scoRootDirectory.delete();

                    //  After the file has been deleted, update the UI to reflect that the SCO
                    //  should be re-downloaded.
                    scoRecord.getScoRecord().setIsScoDownloaded(context);
                }

                catch (IOException e) {
                    Log.wtf(null, "Sco " + scoId + "Could Not Be Deleted: " +
                            e.getMessage());
                }
            }
        });
    }

    public void deleteFilesRecursively(File parentFile) throws IOException {
        for (File child: parentFile.listFiles()) {
            if (child.isDirectory()) {
                deleteFilesRecursively(child);
            }
            child.delete();
        }
    }
}
