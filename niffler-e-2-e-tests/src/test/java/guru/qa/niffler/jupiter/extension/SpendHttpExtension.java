package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Date;

public class SpendHttpExtension extends AbstractSpendExtension {
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://127.0.0.1:8093/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    @Override
    protected SpendJson createSpend(GenerateSpend spend) {
        SpendApi spendApi = retrofit.create(SpendApi.class);

        SpendJson spendJson = new SpendJson(
                null,
                new Date(),
                spend.category(),
                spend.currency(),
                spend.amount(),
                spend.description(),
                spend.username()
        );

        try {
            return spendApi.createSpend(spendJson).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void removeSpend(SpendJson spend) {

    }
}
