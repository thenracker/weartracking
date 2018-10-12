package cz.weissar.weartracker.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

import cz.weissar.weartracker.database.Pref;

public class SendFilesService extends IntentService {

    private static final int CACHE_SIZE = 1024 * 10;
    private DataOutputStream request;
    private final String boundary =  "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";


    public SendFilesService() {
        super(SendFilesService.class.getSimpleName());
    }

    public SendFilesService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        for (SensorHandler.Type type : SensorHandler.Type.values()) {
            try {
                handleNewWay(intent, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void handleNewWay(Intent intent, SensorHandler.Type type) throws Exception {
        // creates a unique boundary based on time stamp
        URL url = new URL(type.getUrl());
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);

        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestProperty("Cache-Control", "no-cache");
        httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);

        request = new DataOutputStream(httpConn.getOutputStream());

        String fileName = Environment.getExternalStorageDirectory().toString() + "/WEARTracker/"
                + Pref.getFolderName(getApplicationContext()) + "/"
                + type.name() + ".txt";

        File file = new File(fileName);

        addFilePart("file", file);
        finish(httpConn); //poslat na server

        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile) throws Exception {
        String fileName = uploadFile.getName();
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
                fieldName + "\";filename=\"" +
                fileName + "\"" + this.crlf);
        request.writeBytes(this.crlf);

        byte[] bytes = Files.readAllBytes(uploadFile.toPath());
        request.write(bytes);
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish(HttpURLConnection httpConn) throws IOException {
        String response = "";

        request.writeBytes(this.crlf);
        request.writeBytes(this.twoHyphens + this.boundary +
                this.twoHyphens + this.crlf);

        request.flush();
        request.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = new
                    BufferedInputStream(httpConn.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            response = stringBuilder.toString();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response;
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value

    public void addFormField(String name, String value) throws IOException {
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + this.crlf);
        request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf);
        request.writeBytes(this.crlf);
        request.writeBytes(value + this.crlf);
        request.flush();
    }
     */
}
