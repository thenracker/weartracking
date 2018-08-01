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

        //handleOldWay(intent);

        try {
            handleNewWay(intent, "http://imitgw.uhk.cz:59729/uhkhelper/file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void handleOldWay(Intent intent) {
        try {
            //TODO - todo for all files in folders here
            String fileName = Environment.getExternalStorageDirectory().toString() + "/WEARTracker/MEASURE_2/"
                    //+ Pref.getFolderName(getBaseContext()) + "/"
                    + intent.getExtras().getString("NAME") + ".txt";
            File file = new File(fileName);

            URL url = new URL("http://imitgw.uhk.cz:59729/uhkhelper/file");
            HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.addRequestProperty("Cache-Control", "no-cache");
            connection.addRequestProperty("Content-length", file.length() + "");
            String boundary = "*****";
            connection.addRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            connection.connect();

            InputStream input = new FileInputStream(file);
            OutputStream output = connection.getOutputStream();

            // cache
            byte[] cache = new byte[CACHE_SIZE];

            int count;
            while ((count = input.read(cache)) != -1) {
                output.write(cache, 0, count);
            }

            output.flush();

            output.close();
            input.close();

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void handleNewWay(Intent intent, String requestURL) throws Exception {
        // creates a unique boundary based on time stamp
        URL url = new URL(requestURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);

        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestProperty("Cache-Control", "no-cache");
        httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);

        request = new DataOutputStream(httpConn.getOutputStream());

        String fileName = Environment.getExternalStorageDirectory().toString() + "/WEARTracker/MEASURE_2/"
                //+ Pref.getFolderName(getBaseContext()) + "/"
                + intent.getExtras().getString("NAME") + ".txt";
        File file = new File(fileName);

        addFilePart("file", file);
        finish(httpConn);
    }


    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) throws IOException {
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + this.crlf);
        request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf);
        request.writeBytes(this.crlf);
        request.writeBytes(value + this.crlf);
        request.flush();
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
}
