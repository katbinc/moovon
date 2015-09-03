package by.katbinc.moovon.api;

import by.katbinc.moovon.model.NavigationModel;
import by.katbinc.moovon.model.PlayerModel;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created on 01.09.15.
 * (c)
 */
public interface Api {

    String URL_API = "http://app-link.to/clients/app-backend";
    String KEY_PLAYER = "musicPlayer";

    @GET("/?id=29031&method=navigation&version=1.0&app=com.wewant.tankrast&device=phone&live=0&os=android&debug=0&language=de&fe_typo_user=39bb09045a09d0ab62e9064efae4de12")
    void getNavigationData(Callback<NavigationModel> response);

    @GET("/?type=777")
    void getPlayerData(@Query("id") int id, Callback<PlayerModel> response);
}
