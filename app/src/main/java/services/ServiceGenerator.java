package services;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Every field in this class is static as we want to use a single
 * instance of the OkHTTPClient to manage the conections/reuse open
 * sockets. We could do this using DI, but that's a bit of a faff in
 * Android, so we'll use static field instead. Remember, static functions
 * can only access other static functions inside a class!
 */
public class ServiceGenerator {
    private static final String SCORM_API_BASE_URL = "https://192.168.0.37:44369";

    /**
     * Builds the HTTP client that will handle the network communication.
     * See https://square.github.io/okhttp/ for more details.
     */
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    /**
     * Handles the building of requests based on provided schemas provided
     * by the Retrofit interfaces and attributes.
     */
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(SCORM_API_BASE_URL)
            .addConverterFactory(
                    GsonConverterFactory.create());

    /**
     * Retrofit class provides a wrapper for both the OKHttpClient and constructed
     * Retrofit behaviours.
     */
    private static Retrofit retrofit = builder.build();

    /**
     * Dynamically create a Retrofit service by passing in a service interface
     * as the blueprint for the Retrofit builder to create an instance of a
     * service to serve requests to the mapped API.
     *
     * @param serviceClass The class type (service) that the constructed service
     *                     should be.
     *
     * @param <S>          The type that the created service should be constructed
     *                     as.
     *
     * @return             A reference to the service that has been created.
     */
    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
