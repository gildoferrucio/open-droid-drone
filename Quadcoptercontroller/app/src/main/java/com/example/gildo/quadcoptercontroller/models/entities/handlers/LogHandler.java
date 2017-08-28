package com.example.gildo.quadcoptercontroller.models.entities.handlers;

import android.os.Environment;

import com.example.gildo.quadcoptercontroller.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by gildo on 14/06/16.
 */
public class LogHandler {
    private static final String FILENAME_SENSORY = "quadcopterControllerLogSensory.txt";
    private static final String FILENAME_DEVICE_TESTS = "quadcopterControllerLogDeviceTests.txt";
    private static final String FILENAME_RSC = "quadcopterControllerLogRsc.txt";
    private static final String FILENAME_ATC = "quadcopterControllerLogAtt.txt";
    private static final String FILENAME_HRC = "quadcopterControllerLogHrc.txt";
    private static final String FILENAME_ALC = "quadcopterControllerLogAlc.txt";

    private static final int MAX_MEMORY_LOG_LINES = 10000;
//    private static final int MAX_MEMORY_LOG_LINES = 15000;

    private File logFileSensory;
    private File logFileDeviceTests;
    private File logFileRSC;
    private File logFileATC;
    private File logFileHRC;
    private File logFileALC;

    private Queue<String> logLinesSensory;
    private Queue<String> logLinesDeviceTests;
    private Queue<String> logLinesRSC;
    private Queue<String> logLinesATC;
    private Queue<String> logLinesALC;
    private Queue<String> logLinesHRC;


    private TextFileHandler textFileHandler;

    private StringBuilder currentLineLogSensory;
    private StringBuilder currentLineLogDeviceTests;
    private StringBuilder currentLineLogRSC;
    private StringBuilder currentLineLogATC;
    private StringBuilder currentLineLogHRC;
    private StringBuilder currentLineLogALC;

    private SimpleDateFormat simpleDateFormat;

    private boolean logBiggerThanOnePageSensory;
    private boolean logBiggerThanOnePageDeviceTests;
    private boolean logBiggerThanOnePageRSC;
    private boolean logBiggerThanOnePageATC;
    private boolean logBiggerThanOnePageHRC;
    private boolean logBiggerThanOnePageALC;

    private static LogHandler instance;

    public static LogHandler getInstance() {
        if (instance == null) {
            instance = new LogHandler();
        }

        return instance;
    }

    private LogHandler() {
        this.logLinesSensory = new ConcurrentLinkedQueue<>();
        this.logLinesDeviceTests = new ConcurrentLinkedQueue<>();
        this.logLinesRSC = new ConcurrentLinkedQueue<>();
        this.logLinesATC = new ConcurrentLinkedQueue<>();
        this.logLinesHRC = new ConcurrentLinkedQueue<>();
        this.logLinesALC = new ConcurrentLinkedQueue<>();
        StringBuilder filePath = new StringBuilder();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //TODO: distinguish between non-removable and SD card
            filePath.append(Environment.getExternalStorageDirectory().getPath());
            filePath.append("/").append(LogHandler.FILENAME_SENSORY);
            this.logFileSensory = new File(filePath.toString());
            this.logFileSensory.delete();

            filePath.delete(0, filePath.length());
            filePath.append(Environment.getExternalStorageDirectory().getPath());
            filePath.append("/").append(LogHandler.FILENAME_DEVICE_TESTS);
            this.logFileDeviceTests = new File(filePath.toString());
            this.logFileDeviceTests.delete();

            filePath.delete(0, filePath.length());
            filePath.append(Environment.getExternalStorageDirectory().getPath());
            filePath.append("/").append(LogHandler.FILENAME_RSC);
            this.logFileRSC = new File(filePath.toString());
            this.logFileRSC.delete();

            filePath.delete(0, filePath.length());
            filePath.append(Environment.getExternalStorageDirectory().getPath());
            filePath.append("/").append(LogHandler.FILENAME_ATC);
            this.logFileATC = new File(filePath.toString());
            this.logFileATC.delete();

            filePath.delete(0, filePath.length());
            filePath.append(Environment.getExternalStorageDirectory().getPath());
            filePath.append("/").append(LogHandler.FILENAME_HRC);
            this.logFileHRC = new File(filePath.toString());
            this.logFileHRC.delete();

            filePath.delete(0, filePath.length());
            filePath.append(Environment.getExternalStorageDirectory().getPath());
            filePath.append("/").append(LogHandler.FILENAME_ALC);
            this.logFileALC = new File(filePath.toString());
            this.logFileALC.delete();
        }

        this.textFileHandler = TextFileHandler.getInstance();
        this.currentLineLogSensory = new StringBuilder();
        this.currentLineLogDeviceTests = new StringBuilder();
        this.currentLineLogRSC = new StringBuilder();
        this.currentLineLogATC = new StringBuilder();
        this.currentLineLogHRC = new StringBuilder();
        this.currentLineLogALC = new StringBuilder();
//        this.simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss.SSS");
        this.simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

        this.logBiggerThanOnePageSensory = false;
        this.logBiggerThanOnePageDeviceTests = false;
        this.logBiggerThanOnePageRSC = false;
        this.logBiggerThanOnePageATC = false;
        this.logBiggerThanOnePageHRC = false;
        this.logBiggerThanOnePageALC = false;
    }

    public void clearLog(final int logPreferenceId) {
        switch (logPreferenceId) {
            case R.string.pref_key_enable_device_tests_logs:
                this.logLinesDeviceTests.clear();
                break;
            case R.string.pref_key_enable_sensory_logs:
                this.logLinesSensory.clear();
                break;
            case R.string.pref_key_enable_rsc_logs:
                this.logLinesRSC.clear();
                break;
            case R.string.pref_key_enable_atc_logs:
                this.logLinesATC.clear();
                break;
            case R.string.pref_key_enable_hrc_logs:
                this.logLinesHRC.clear();
                break;
            case R.string.pref_key_enable_alc_logs:
                this.logLinesALC.clear();
                break;
        }
    }

    public void appendDataToLog(final int logPreferenceId, final CharSequence data) {
        String dateString = this.simpleDateFormat.format(new java.util.Date());

        switch (logPreferenceId) {
            case R.string.pref_key_enable_device_tests_logs:
                this.currentLineLogDeviceTests.delete(0, this.currentLineLogDeviceTests.length());
                this.currentLineLogDeviceTests.append(dateString).append(",");
                this.currentLineLogDeviceTests.append(data);

                if (this.logLinesDeviceTests.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageDeviceTests = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesDeviceTests = new ConcurrentLinkedQueue<>();
                }
                this.logLinesDeviceTests.add(this.currentLineLogDeviceTests.toString());
                break;
            case R.string.pref_key_enable_sensory_logs:
                this.currentLineLogSensory.delete(0, this.currentLineLogSensory.length());
                this.currentLineLogSensory.append(dateString).append(",");
                this.currentLineLogSensory.append(data);

                if (this.logLinesSensory.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageSensory = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesSensory = new ConcurrentLinkedQueue<>();
                }
                this.logLinesSensory.add(this.currentLineLogSensory.toString());
                break;
            case R.string.pref_key_enable_rsc_logs:
                this.currentLineLogRSC.delete(0, this.currentLineLogRSC.length());
                this.currentLineLogRSC.append(dateString).append(",");
                this.currentLineLogRSC.append(data);

                if (this.logLinesRSC.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageRSC = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesRSC = new ConcurrentLinkedQueue<>();
                }
                this.logLinesRSC.add(this.currentLineLogRSC.toString());
                break;
            case R.string.pref_key_enable_atc_logs:
                this.currentLineLogATC.delete(0, this.currentLineLogATC.length());
                this.currentLineLogATC.append(dateString).append(",");
                this.currentLineLogATC.append(data);

                if (this.logLinesATC.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageATC = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesATC = new ConcurrentLinkedQueue<>();
                }
                this.logLinesATC.add(this.currentLineLogATC.toString());
                break;
            case R.string.pref_key_enable_hrc_logs:
                this.currentLineLogHRC.delete(0, this.currentLineLogHRC.length());
                this.currentLineLogHRC.append(dateString).append(",");
                this.currentLineLogHRC.append(data);

                if (this.logLinesHRC.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageHRC = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesHRC = new ConcurrentLinkedQueue<>();
                }
                this.logLinesHRC.add(this.currentLineLogHRC.toString());
                break;
            case R.string.pref_key_enable_alc_logs:
                this.currentLineLogALC.delete(0, this.currentLineLogALC.length());
                this.currentLineLogALC.append(dateString).append(",");
                this.currentLineLogALC.append(data);

                if (this.logLinesALC.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageALC = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesALC = new ConcurrentLinkedQueue<>();
                }
                this.logLinesALC.add(this.currentLineLogALC.toString());
                break;
        }
    }

    public void appendValueToLog(final int logPreferenceId, final CharSequence dataOrigin, final CharSequence value) {
        String dateString = this.simpleDateFormat.format(new java.util.Date());

        switch (logPreferenceId) {
            case R.string.pref_key_enable_device_tests_logs:
                this.currentLineLogDeviceTests.delete(0, this.currentLineLogDeviceTests.length());
                this.currentLineLogDeviceTests.append(dateString).append(",");
                this.currentLineLogDeviceTests.append(dataOrigin).append(",");
                this.currentLineLogDeviceTests.append(value);

                if (this.logLinesDeviceTests.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageDeviceTests = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesDeviceTests = new ConcurrentLinkedQueue<>();
                }
                this.logLinesDeviceTests.add(this.currentLineLogDeviceTests.toString());
                break;
            case R.string.pref_key_enable_sensory_logs:
                this.currentLineLogSensory.delete(0, this.currentLineLogSensory.length());
                this.currentLineLogSensory.append(dateString).append(",");
                this.currentLineLogSensory.append(dataOrigin).append(",");
                this.currentLineLogSensory.append(value);

                if (this.logLinesSensory.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageSensory = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesSensory = new ConcurrentLinkedQueue<>();
                }
                this.logLinesSensory.add(this.currentLineLogSensory.toString());
                break;
            case R.string.pref_key_enable_rsc_logs:
                this.currentLineLogRSC.delete(0, this.currentLineLogRSC.length());
                this.currentLineLogRSC.append(dateString).append(",");
                this.currentLineLogRSC.append(dataOrigin).append(",");
                this.currentLineLogRSC.append(value);

                if (this.logLinesRSC.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageRSC = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesRSC = new ConcurrentLinkedQueue<>();
                }
                this.logLinesRSC.add(this.currentLineLogRSC.toString());
                break;
            case R.string.pref_key_enable_atc_logs:
                this.currentLineLogATC.delete(0, this.currentLineLogATC.length());
                this.currentLineLogATC.append(dateString).append(",");
                this.currentLineLogATC.append(dataOrigin).append(",");
                this.currentLineLogATC.append(value);

                if (this.logLinesATC.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageATC = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesATC = new ConcurrentLinkedQueue<>();
                }
                this.logLinesATC.add(this.currentLineLogATC.toString());
                break;
            case R.string.pref_key_enable_hrc_logs:
                this.currentLineLogHRC.delete(0, this.currentLineLogHRC.length());
                this.currentLineLogHRC.append(dateString).append(",");
                this.currentLineLogHRC.append(dataOrigin).append(",");
                this.currentLineLogHRC.append(value);

                if (this.logLinesHRC.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageHRC = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesHRC = new ConcurrentLinkedQueue<>();
                }
                this.logLinesHRC.add(this.currentLineLogHRC.toString());
                break;
            case R.string.pref_key_enable_alc_logs:
                this.currentLineLogALC.delete(0, this.currentLineLogALC.length());
                this.currentLineLogALC.append(dateString).append(",");
                this.currentLineLogALC.append(dataOrigin).append(",");
                this.currentLineLogALC.append(value);

                if (this.logLinesALC.size() >= MAX_MEMORY_LOG_LINES) {
                    this.logBiggerThanOnePageALC = true;
                    saveLogFile(logPreferenceId);
                    this.logLinesALC = new ConcurrentLinkedQueue<>();
                }
                this.logLinesALC.add(this.currentLineLogALC.toString());
                break;
        }
    }

    public void appendValueToLog(final int logPreferenceId, final String dataOrigin, final Float value) {
        appendValueToLog(logPreferenceId, dataOrigin, value.toString());
    }

    public void saveLogFile(final int logPreferenceId) {
        switch (logPreferenceId) {
            case R.string.pref_key_enable_device_tests_logs:
                this.textFileHandler.saveFile(this.logFileDeviceTests, this.logLinesDeviceTests, this.logBiggerThanOnePageDeviceTests);
                break;
            case R.string.pref_key_enable_sensory_logs:
                this.textFileHandler.saveFile(this.logFileSensory, this.logLinesSensory, this.logBiggerThanOnePageSensory);
                break;
            case R.string.pref_key_enable_rsc_logs:
                this.textFileHandler.saveFile(this.logFileRSC, this.logLinesRSC, this.logBiggerThanOnePageRSC);
                break;
            case R.string.pref_key_enable_atc_logs:
                this.textFileHandler.saveFile(this.logFileATC, this.logLinesATC, this.logBiggerThanOnePageATC);
                break;
            case R.string.pref_key_enable_hrc_logs:
                this.textFileHandler.saveFile(this.logFileHRC, this.logLinesHRC, this.logBiggerThanOnePageHRC);
                break;
            case R.string.pref_key_enable_alc_logs:
                this.textFileHandler.saveFile(this.logFileALC, this.logLinesALC, this.logBiggerThanOnePageALC);
                break;
        }
    }
}
