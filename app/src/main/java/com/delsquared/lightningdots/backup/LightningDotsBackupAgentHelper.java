package com.delsquared.lightningdots.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;

import com.delsquared.lightningdots.R;
import com.delsquared.lightningdots.database.GameSQLiteHelper;
import com.delsquared.lightningdots.utilities.UtilityFunctions;

import java.io.IOException;

public class LightningDotsBackupAgentHelper extends BackupAgentHelper {
    private static final String CLASS_NAME = LightningDotsBackupAgentHelper.class.getSimpleName();

    @SuppressWarnings("FieldCanBeLocal")
    private final String BACKUP_KEY_SHARED_PREFERENCES = "com.delsquared.lightningdots.backup.SharedPreferences";
    @SuppressWarnings("FieldCanBeLocal")
    private final String BACKUP_KEY_DATABASE = "com.delsquared.lightningdots.backup.Database";

    @Override
    public void onCreate() {
        String methodName = CLASS_NAME + ".onCreate";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Create the backup helper for the shared preferences
        String fileNameSharedPreferences = getString(R.string.preferences_file_name);
        SharedPreferencesBackupHelper backupHelperSharedPreferences =
                new SharedPreferencesBackupHelper(this, fileNameSharedPreferences);
        addHelper(BACKUP_KEY_SHARED_PREFERENCES, backupHelperSharedPreferences);

        // Create the backup helper for the database
        String databaseName = getString(R.string.database_name);
        FileBackupHelper databaseBackupHelper = new FileBackupHelper(this, "../databases/" + databaseName);
        addHelper(BACKUP_KEY_DATABASE, databaseBackupHelper);

        UtilityFunctions.logDebug(methodName,"Backup agent helper created.");
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        String methodName = CLASS_NAME + ".onBackup";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Hold the lock while the FileBackupHelper performs backup
        synchronized (GameSQLiteHelper.sDataLock) {
            UtilityFunctions.logDebug(methodName, "...in sDataLock synchronized block");
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        String methodName = CLASS_NAME + ".onRestore";
        UtilityFunctions.logDebug(methodName, "Entered");

        // Hold the lock while the FileBackupHelper restores the file
        synchronized (GameSQLiteHelper.sDataLock) {
            UtilityFunctions.logDebug(methodName, "...in sDataLock synchronized block");
            super.onRestore(data, appVersionCode, newState);
        }
    }

}
