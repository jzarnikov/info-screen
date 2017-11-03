package pi.home.screen.common;

import java.io.IOException;
import java.net.URI;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pi.home.screen.config.ConfigController;
import pi.home.screen.config.data.Config;

public abstract class AbstractTransformingController<I, O> {

    private final ObjectMapper mapper;

    private final ConfigController configController;
    private final TypeReference<I> inputType;

    public AbstractTransformingController(ConfigController configController, TypeReference<I> inputType) {
        this.configController = configController;
        this.inputType = inputType;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    protected Config getConfig() {
        return configController.getConfig();
    }

    protected String getAndTranslate(URI uri) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        ResponseHandler<String> responseHandler = this::handleResponse;
        String response = httpClient.execute(httpGet, responseHandler);
        I input = mapper.readValue(response, inputType);
        O output = translate(input);
        return mapper.writeValueAsString(output);
    }

    private String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    }

    protected abstract O translate(I input) throws IOException;

}
