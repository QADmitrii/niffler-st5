package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.CategoryApi;
import guru.qa.niffler.jupiter.annotation.GenerateCategory;
import guru.qa.niffler.model.CategoryJson;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class CategoryHttpExtension extends AbstractCategoryExtension{
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://127.0.0.1:8093/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    @Override
    protected CategoryJson createCategory(GenerateCategory category) {
        CategoryApi categoryApi = retrofit.create(CategoryApi.class);

        CategoryJson categoryJson = new CategoryJson(
                null,
                category.category(),
                category.username()
        );

        try {
            return categoryApi.createCategory(categoryJson).execute().body();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }


    @Override
    protected void removeCategory(CategoryJson category) {

    }
}
