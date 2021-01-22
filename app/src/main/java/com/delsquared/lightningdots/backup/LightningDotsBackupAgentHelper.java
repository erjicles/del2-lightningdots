package com.delsquared.lightningdots.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.database.GameSQLiteHelper;
import com.delsquared.lightningdots.utilities.LightningDotsApplication;

import java.io.IOException;

public class LightningDotsBackupAgentHelper extends BackupAgentHelper {

    @SuppressWarnings("FieldCanBeLocal")
    private final String BACKUP_KEY_SHARED_PREFERENCES = "com.delsquared.lightningdots.backup.SharedPreferences";
    @SuppressWarnings("FieldCanBeLocal")
    private final String BACKUP_KEY_DATABASE = "com.delsquared.lightningdots.backup.Database";

    @Override
    public void onCreate() {

        // Create the backup helper for the shared preferences
        String fileNameSharedPreferences = getString(R.string.preferences_file_name);
        SharedPreferencesBackupHelper backupHelperSharedPreferences =
                new SharedPreferencesBackupHelper(this, fileNameSharedPreferences);
        addHelper(BACKUP_KEY_SHARED_PREFERENCES, backupHelperSharedPreferences);

        // Create the backup helper for the database
        String databaseName = getString(R.string.database_name);
        FileBackupHelper databaseBackupHelper = new FileBackupHelper(this, "../databases/" + databaseName);
        addHelper(BACKUP_KEY_DATABASE, databaseBackupHelper);

        Log.d(LightningDotsApplication.logTag, "Backup agent helper created.");
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        Log.d(LightningDotsApplication.logTag, "onBackup called");
        // Hold the lock while the FileBackupHelper performs backup
        synchronized (GameSQLiteHelper.sDataLock) {
            Log.d(LightningDotsApplication.logTag, "...in backup synchronized block");
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        Log.d(LightningDotsApplication.logTag, "onRestore called");
        // Hold the lock while the FileBackupHelper restores the file
        synchronized (GameSQLiteHelper.sDataLock) {
            Log.d(LightningDotsApplication.logTag, "...in restore synchronized block");
            super.onRestore(data, appVersionCode, newState);
        }
    }

}
