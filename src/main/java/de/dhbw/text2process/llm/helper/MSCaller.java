package de.dhbw.text2process.llm.helper;

import java.net.ConnectException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class MSCaller {
    public void callPythonService(String textToSend) throws NullPointerException, ConnectException{
        try {
            // URL of the Python microservice endpoint
            String pythonMicroserviceURL = "http://127.0.0.1:5000/process_text";
            
            // Create an HttpClient instance
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(pythonMicroserviceURL);

            // Set the content type and text to send
            httpPost.addHeader("Content-Type", "text/plain");
            StringEntity entity = new StringEntity(textToSend);
            httpPost.setEntity(entity);

            // Execute the request and retrieve the response
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            // Print the response received from the microservice
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response from Python microservice: " + responseBody);
            } else{
                throw new NullPointerException("The API of OpenAI is not available.");
            }

            // Close the response and HttpClient
            response.close();
            httpClient.close();

        } catch(ConnectException e){
            System.out.println("Miroservice not available: " + e.getMessage());
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
